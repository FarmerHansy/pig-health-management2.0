package com.linyi.pig.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.linyi.pig.config.OllamaConfig;
import com.linyi.pig.entity.Conversation;
import com.linyi.pig.entity.vo.conversation.ConversationAddVo;
import com.linyi.pig.entity.vo.conversation.ConversationQueryVo;
import com.linyi.pig.entity.vo.conversation.ConversationUpdateVo;
import com.linyi.pig.exception.LinyiException;
import com.linyi.pig.mapper.ConversationMapper;
import com.linyi.pig.service.ConversationService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import com.linyi.pig.common.model.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
                implements ConversationService {

        @Autowired
        private ConversationMapper conversationMapper;

        @Resource
        private OllamaConfig ollamaConfig;

        @Value("${ai.ollama.chat.options.model}")
        private String defaultChatOptionsModel;

        @Value("${ai.provider.local-enabled:true}")
        private boolean localEnabled;

        @Value("${ai.provider.openapi-enabled:false}")
        private boolean openapiEnabled;

        @Value("${ai.provider.fallback-enabled:false}")
        private boolean fallbackEnabled;

        @Value("${ai.openapi.base-url:https://api.deepseek.com}")
        private String openapiBaseUrl;

        @Value("${ai.openapi.api-key:}")
        private String openapiApiKey;

        @Value("${ai.openapi.chat.model:deepseek-chat}")
        private String openapiChatModel;

        @Value("${ai.openapi.chat.temperature:0.4}")
        private double openapiChatTemperature;

        @Autowired
        private ChatClient chatClient;

        @Autowired
        private VectorStore vectorStore;

        private final OkHttpClient httpClient = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .build();

        @Override
        public PageResult<Conversation> conversationPage(ConversationQueryVo conversationQueryVo) {
                LambdaQueryWrapper<Conversation> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Optional.ofNullable(conversationQueryVo.getId()).isPresent(), Conversation::getId,
                                conversationQueryVo.getId());
                queryWrapper.eq(Optional.ofNullable(conversationQueryVo.getUserId()).isPresent(),
                                Conversation::getUserId,
                                conversationQueryVo.getUserId());
                queryWrapper.eq(StringUtils.isNotBlank(conversationQueryVo.getUserInput()), Conversation::getUserInput,
                                conversationQueryVo.getUserInput());
                queryWrapper.eq(StringUtils.isNotBlank(conversationQueryVo.getAiResponse()),
                                Conversation::getAiResponse,
                                conversationQueryVo.getAiResponse());
                queryWrapper.eq(Optional.ofNullable(conversationQueryVo.getAiResponse()).isPresent(),
                                Conversation::getAiResponse, conversationQueryVo.getAiResponse());
                queryWrapper.gt(Optional.ofNullable(conversationQueryVo.getStartConversationTime()).isPresent(),
                                Conversation::getConversationTime, conversationQueryVo.getStartConversationTime());
                queryWrapper.lt(Optional.ofNullable(conversationQueryVo.getEndConversationTime()).isPresent(),
                                Conversation::getConversationTime, conversationQueryVo.getEndConversationTime());
                queryWrapper.eq(StringUtils.isNotBlank(conversationQueryVo.getModelName()), Conversation::getModelName,
                                conversationQueryVo.getModelName());
                queryWrapper.ge(Optional.ofNullable(conversationQueryVo.getResponseTime()).isPresent(),
                                Conversation::getResponseTime, conversationQueryVo.getResponseTime());

                // 分页数据
                Page<Conversation> page = new Page<>(conversationQueryVo.getPageNum(),
                                conversationQueryVo.getPageSize());
                // 查询数据
                Page<Conversation> pageNew = conversationMapper.selectPage(page, queryWrapper);
                // 返回分页数据
                return new PageResult<>(pageNew.getRecords(), pageNew.getTotal(), pageNew.getPages(),
                                conversationQueryVo.getPageNum(), conversationQueryVo.getPageSize());
        }

        @Override
        public Boolean conversationAdd(ConversationAddVo conversationAddVo) {
                // 创建实体对象
                Conversation conversation = new Conversation();
                // 复制属性
                BeanUtils.copyProperties(conversationAddVo, conversation);
                // 插入数据
                return conversationMapper.insert(conversation) > 0 ? true : false;
        }

        @Override
        public Boolean conversationUpdate(ConversationUpdateVo conversationUpdateVo) {
                // 根据ID查询数据
                Conversation byId = this.getById(conversationUpdateVo.getId());
                // 判断数据是否存在
                if (Optional.ofNullable(byId).isEmpty()) {
                        log.error("数据不存在");
                        return false;
                }
                // 复制属性
                BeanUtils.copyProperties(conversationUpdateVo, byId);
                // 修改数据
                return conversationMapper.updateById(byId) > 0 ? true : false;
        }

        @Override
        public List<Conversation> getHistoryNum(Integer num) {
                // 查询数据
                LambdaQueryWrapper<Conversation> queryWrapper = new LambdaQueryWrapper<>();
                // 根据用户ID查询历史记录
                queryWrapper.eq(Conversation::getUserId, StpUtil.getLoginId())
                                .orderByDesc(Conversation::getConversationTime).last("limit " + num);
                return conversationMapper.selectList(queryWrapper);
        }

        @Override
        public Conversation getOllama(String msg) {
                return getOllama(msg, null);
        }

        @Override
        public List<Conversation> listBySessionId(Long sessionId) {
                LambdaQueryWrapper<Conversation> qw = new LambdaQueryWrapper<>();
                qw.eq(Conversation::getUserId, StpUtil.getLoginId())
                                .eq(Conversation::getSessionId, sessionId)
                                .orderByAsc(Conversation::getId);
                return conversationMapper.selectList(qw);
        }

        @Override
        public Conversation getOllama(String msg, Long sessionId) {
                if (!localEnabled && openapiEnabled) {
                        log.info("AI provider: OpenAPI (baseUrl={}, model={})", openapiBaseUrl, openapiChatModel);
                        return getApiLLM(msg, sessionId);
                }
                if (!localEnabled && !openapiEnabled) {
                        throw new LinyiException("AI服务未启用");
                }
                if (StringUtils.isBlank(msg)) {
                        log.error("请输入chat内容");
                        throw new LinyiException("请输入chat内容");
                }

                try {
                        log.info("AI provider: Ollama (model={})", defaultChatOptionsModel);
                        return callLocalModel(msg, sessionId);
                } catch (Exception ex) {
                        if (openapiEnabled && fallbackEnabled) {
                                log.warn("本地模型调用失败，切换到OpenAPI: {}", ex.getMessage());
                                return getApiLLM(msg, sessionId);
                        }
                        throw ex;
                }
        }

        @Override
        public Conversation getApiLLM(String prompt) {
                return getApiLLM(prompt, null);
        }

        @Override
        public Conversation getApiLLM(String prompt, Long sessionId) {
                if (!openapiEnabled) {
                        throw new LinyiException("OpenAPI未启用");
                }
                if (StringUtils.isBlank(prompt)) {
                        throw new LinyiException("请输入chat内容");
                }
                if (StringUtils.isBlank(openapiApiKey)) {
                        throw new LinyiException("OpenAPI Key未配置");
                }

                long startTime = System.nanoTime();
                String finalPrompt = buildKnowledgePrompt(prompt);
                String content = callOpenApi(finalPrompt);
                long endTime = System.nanoTime();

                String formattedTimeTaken = String.format("%.2f", (endTime - startTime) / 1e9);

                Conversation conversation = Conversation.builder()
                                .userId(Integer.valueOf(StpUtil.getLoginId().toString()))
                                .sessionId(sessionId)
                                .userInput(prompt)
                                .aiResponse(content)
                                .modelName(openapiChatModel)
                                .responseTime(new BigDecimal(formattedTimeTaken))
                                .build();
                conversationMapper.insert(conversation);
                return conversation;
        }

        private String buildKnowledgePrompt(String msg) {
                var similar = vectorStore.similaritySearch(msg);
                StringBuilder context = new StringBuilder();
                for (var d : similar) {
                        context.append("\n---\n");
                        context.append(d.getContent());
                }
                return "你是一名猪病AI医生。先基于以下本地知识回答，如果知识未涵盖再综合自身知识作答，并标注依据来源要点。" +
                                "\n本地知识: " + context +
                                "\n问题: " + msg +
                                "\n请给出清晰、权威且可执行的回答。";
        }

        private String callOpenApi(String finalPrompt) {
                String baseUrl = StringUtils.removeEnd(openapiBaseUrl, "/");
                JSONObject payload = new JSONObject();
                payload.put("model", openapiChatModel);
                payload.put("temperature", openapiChatTemperature);
                payload.put("stream", false);

                JSONArray messages = new JSONArray();
                JSONObject systemMsg = new JSONObject();
                systemMsg.put("role", "system");
                systemMsg.put("content", "You are a helpful assistant.");
                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", finalPrompt);
                messages.add(systemMsg);
                messages.add(userMsg);
                payload.put("messages", messages);

                RequestBody body = RequestBody.create(payload.toJSONString(), MediaType.parse("application/json"));
                Request request = new Request.Builder()
                                .url(baseUrl + "/chat/completions")
                                .addHeader("Authorization", "Bearer " + openapiApiKey)
                                .addHeader("Content-Type", "application/json")
                                .post(body)
                                .build();

                try (Response response = httpClient.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                                String errorBody = response.body() != null ? response.body().string() : "";
                                throw new LinyiException("OpenAPI调用失败: " + response.code() + " " + errorBody);
                        }
                        String json = response.body() != null ? response.body().string() : "";
                        JSONObject parsed = JSONObject.parseObject(json);
                        JSONArray choices = parsed.getJSONArray("choices");
                        if (choices == null || choices.isEmpty()) {
                                throw new LinyiException("OpenAPI无有效响应");
                        }
                        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                        String content = message != null ? message.getString("content") : null;
                        if (StringUtils.isBlank(content)) {
                                throw new LinyiException("OpenAPI响应为空");
                        }
                        return content.trim();
                } catch (Exception ex) {
                        log.error("OpenAPI调用异常", ex);
                        throw new LinyiException("OpenAPI调用异常: " + ex.getMessage());
                }
        }

        private Conversation callLocalModel(String msg, Long sessionId) {
                // 记录接收到的问题消息
                log.info("问题是:{}", msg);

                long startTime = System.nanoTime();
                String finalPrompt = buildKnowledgePrompt(msg);
                String content = chatClient
                                .prompt()
                                .user(finalPrompt)
                                .options(OllamaOptions.builder()
                                                .withModel(defaultChatOptionsModel)
                                                .withTemperature(0.4)
                                                .build())
                                .call()
                                .content();
                long endTime = System.nanoTime();

                if (StringUtils.isBlank(content)) {
                        throw new LinyiException("本地模型响应为空");
                }

                String formattedTimeTaken = String.format("%.2f", (endTime - startTime) / 1e9);
                Conversation conversation = Conversation.builder()
                                .userId(Integer.valueOf(StpUtil.getLoginId().toString()))
                                .sessionId(sessionId)
                                .userInput(msg)
                                .aiResponse(content)
                                .modelName(defaultChatOptionsModel)
                                .responseTime(new BigDecimal(formattedTimeTaken))
                                .build();
                conversationMapper.insert(conversation);
                return conversation;
        }
}
