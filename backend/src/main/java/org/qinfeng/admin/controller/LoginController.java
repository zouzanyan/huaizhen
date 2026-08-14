package org.qinfeng.admin.controller;

import jakarta.validation.Valid;
import org.qinfeng.admin.dto.LoginRequest;
import org.qinfeng.admin.dto.LoginResponse;
import org.qinfeng.admin.security.JwtUser;
import org.qinfeng.admin.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.username(),
                    loginRequest.password()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            JwtUser userDetails = (JwtUser) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getUserId());

            LoginResponse response = new LoginResponse(token, "登录成功", userDetails.getUsername());

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("用户名或密码错误");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("退出成功");
    }
}