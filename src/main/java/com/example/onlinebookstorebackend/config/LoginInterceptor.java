package com.example.onlinebookstorebackend.config;

import com.example.onlinebookstorebackend.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashSet;
import java.util.Set;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 白名单接口
    private static final Set<String> WHITELIST_URIS = new HashSet<>() {{
        add("/api/user/login");
        add("/api/user/register");
        add("/api/user/public");
        add("/error");
        add("/swagger-ui");
        add("/v3/api-docs");
    }};

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 检查是否在白名单中
        if (isWhitelist(uri)) {
            return true;
        }

        // 检查Session中是否有用户信息
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // 返回401未授权
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            ApiResponse.error(401, "未登录，请先登录")
                    )
            );
            return false;
        }

        return true;
    }

    private boolean isWhitelist(String uri) {
        // 直接匹配
        if (WHITELIST_URIS.contains(uri)) {
            return true;
        }

        // 检查是否以白名单开头
        for (String whiteUri : WHITELIST_URIS) {
            if (uri.startsWith(whiteUri)) {
                return true;
            }
        }

        return false;
    }
}