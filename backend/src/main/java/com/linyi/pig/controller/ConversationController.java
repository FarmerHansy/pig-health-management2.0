package com.linyi.pig.controller;

import com.linyi.pig.common.model.PageResult;
import com.linyi.pig.common.model.Result;
import com.linyi.pig.entity.Conversation;
import com.linyi.pig.entity.vo.conversation.ConversationAddVo;
import com.linyi.pig.entity.vo.conversation.ConversationQueryVo;
import com.linyi.pig.entity.vo.conversation.ConversationUpdateVo;
import com.linyi.pig.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



@Tag(name = "对话管理模块")
@Slf4j
@Validated
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Value("${ai.provider.local-enabled:true}")
    private boolean localEnabled;

    @Value("${ai.provider.openapi-enabled:false}")
    private boolean openapiEnabled;

    @Value("${ai.openapi.base-url:https://api.deepseek.com}")
    private String openapiBaseUrl;

    @Value("${ai.openapi.chat.model:deepseek-chat}")
    private String openapiChatModel;

    @Value("${ai.ollama.chat.options.model:qwen2.5:7b}")
    private String ollamaChatModel;

    /**
     * 分页查询对话
     *
     * @param conversationQueryVo 分页查询实体
     * @return Result<PageResult<Conversation>> 返回分页数据
     */
    @Operation(summary = "分页查询对话")
    @PostMapping("/conversationPage")
    public Result<PageResult<Conversation>> conversationPage(@RequestBody ConversationQueryVo conversationQueryVo) {
        return Result.success(conversationService.conversationPage(conversationQueryVo));
    }

    /**
     * 新增对话
     *
     * @param conversationAddVo 新增实体
     * @return Result<Boolean> 返回结果(true/false)
     */
    @Operation(summary = "新增对话")
    @PostMapping("/conversationAdd")
    public Result<Boolean> conversationAdd(@RequestBody ConversationAddVo conversationAddVo) {
        return Result.success(conversationService.conversationAdd(conversationAddVo));
    }

    /**
     * 根据主键ID删除对话
     *
     * @param id 主键id
     * @return Result<Boolean> 返回结果(true/false)
     */
    @Operation(summary = "根据主键ID删除对话")
    @DeleteMapping("conversationDelete")
    public Result<Boolean> conversationDelete(@RequestParam Serializable id) {
        return Result.success(conversationService.removeById(id));
    }

    /**
     * 根据主键ID批量删除对话
     *
     * @param ids 主键id集合
     * @return Result<Boolean> 返回结果(true/false)
     */
    @Operation(summary = "根据主键ID批量删除对话")
    @DeleteMapping("conversationListDelete")
    public Result<Boolean> conversationListDelete(@RequestParam List<Serializable> ids) {
        return Result.success(conversationService.removeByIds(ids));
    }

    /**
     * 根据主键ID修改对话
     *
     * @param conversationUpdateVo 修改实体
     * @return Result<Boolean> 返回结果(true/false)
     */
    @Operation(summary = "根据主键ID修改对话")
    @PutMapping("conversationUpdate")
    public Result<Boolean> conversationUpdate(@RequestBody ConversationUpdateVo conversationUpdateVo) {
        return Result.success(conversationService.conversationUpdate(conversationUpdateVo));
    }

    /**
     * 根据主键ID查询对话
     *
     * @param id 主键id
     * @return Result<Conversation> 返回对话
     */
    @Operation(summary = "根据主键ID查询对话")
    @GetMapping("/getInfo")
    public Result<Conversation> conversationUpdate(@RequestParam Serializable id) {
        return Result.success(conversationService.getById(id));
    }

    /**
     * 根据几次历史对话记录
     * 
     * @param num 次数
     * @return
     */
    @Operation(summary = "要根据几次历史对话记录")
    @GetMapping("/getHistory")
    public Result<List<Conversation>> getHistoryNum(@RequestParam Integer num) {
        return Result.success(conversationService.getHistoryNum(num));
    }

    /**
     * 根据会话ID获取历史记录
     */
    @Operation(summary = "根据会话ID获取历史记录")
    @GetMapping("/listBySession")
    public Result<List<Conversation>> listBySession(@RequestParam Long sessionId) {
        return Result.success(conversationService.listBySessionId(sessionId));
    }

    /**
     * 通过ollama调用大模型
     * 
     * @param prompt
     * @return
     */
    @Operation(summary = "通过ollama调用大模型")
    @GetMapping("/getOllama")
    public Result<Conversation> getOllama(@RequestParam String prompt, @RequestParam(required = false) Long sessionId) {
        return Result.success(conversationService.getOllama(prompt, sessionId));
    }

    /**
     * 通过API调用大模型
     * 
     * @param prompt
     * @return
     */
    @Operation(summary = "调用大模型API")
    @GetMapping("/getApiLLM")
    public Result<Conversation> getApiLLM(@RequestParam String prompt, @RequestParam(required = false) Long sessionId) {
        return Result.success(conversationService.getApiLLM(prompt, sessionId));
    }

    /**
     * 返回当前启用的AI提供方与模型信息
     */
    @Operation(summary = "获取当前AI提供方与模型")
    @GetMapping("/provider")
    public Result<Map<String, Object>> getProviderInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        if (localEnabled) {
            info.put("provider", "ollama");
            info.put("model", ollamaChatModel);
        } else if (openapiEnabled) {
            info.put("provider", "openapi");
            info.put("model", openapiChatModel);
            info.put("baseUrl", openapiBaseUrl);
        } else {
            info.put("provider", "disabled");
        }
        return Result.success(info);
    }

}
