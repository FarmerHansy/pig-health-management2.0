import axios from "axios";
import { ElMessage } from 'element-plus';

const service = axios.create({
    // 基础URL
    baseURL: 'http://127.0.0.1:19999',
    // 请求超时60秒
    timeout: 60000,
    // 允许发送和接收 Cookie
    withCredentials: true
});

//请求拦截器
service.interceptors.request.use(
    config => {
        // 对请求参数做点什么
        const token = localStorage.getItem('token')  //拿到登录后存储的token
        if(token){
            //后端要求请求头里有个satoken
            config.headers['satoken'] = token
        }
        return config;
    },
    err => Promise.reject(err)
)

//响应拦截器
service.interceptors.response.use(
    res => {
        // 500 错误可能是 token 失效
        if (res.data?.code === 500 && res.data?.message?.includes('token')) {
            ElMessage.warning('登录已过期，请重新登录');
            localStorage.clear();
            setTimeout(() => {
                location.replace('/login');
            }, 1500);
            return Promise.reject(new Error('token expired'));
        }
        return res.data;
    },
    err => {
        console.error('请求失败:', err.response?.data || '请求失败');

        // 未授权或 token 失效
        if(err.response?.status === 401 ||
           (err.response?.data?.code === 500 && err.response?.data?.message?.includes('token'))) {
            ElMessage.warning('登录已过期，请重新登录');
            localStorage.clear();
            setTimeout(() => {
                location.replace('/login');
            }, 1500);
        } else if (err.response?.data?.message) {
            // 其他错误显示具体信息
            ElMessage.error(err.response.data.message || '请求失败');
        } else {
            ElMessage.error('网络请求失败，请检查网络连接');
        }

        return Promise.reject(err);
    }
)

export default service;
