package org.qinfeng.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 认证入口点
 * 处理未认证请求，返回统一的 JSON 响应格式
 *
 * @author qinfeng
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> body = new HashMap<>();
        body.put("code", 401);
        body.put("message", getErrorMessage(authException));
        body.put("data", null);

        logger.warn("认证失败: {} - {}", request.getRequestURI(), authException.getMessage());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    /**
     * 根据异常类型获取友好的错误信息
     */
    private String getErrorMessage(AuthenticationException authException) {
        String message = authException.getMessage();
        if (message != null) {
            if (message.contains("Token")) {
                return "登录已过期，请重新登录";
            } else if (message.contains("Invalid")) {
                return "登录状态无效，请重新登录";
            } else if (message.contains("expired")) {
                return "登录已过期，请重新登录";
            }
        }
        return "未登录或登录已过期，请重新登录";
    }
}