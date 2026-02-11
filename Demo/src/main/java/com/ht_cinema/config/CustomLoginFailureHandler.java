package com.ht_cinema.config;

import com.ht_cinema.model.Account;
import com.ht_cinema.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private final AccountRepository accountRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String email = request.getParameter("username");
        Optional<Account> acc = accountRepository.findByEmail(email);

        String errorMessage;

        if (acc.isEmpty()) {
            errorMessage = "Email không tồn tại";
        } else if (!acc.get().isStatus()) {
            errorMessage = "Tài khoản đã bị khóa";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Mật khẩu không đúng";
        } else {
            errorMessage = "Đăng nhập thất bại";
        }

        request.getSession().setAttribute("LOGIN_ERROR", errorMessage);

        response.sendRedirect("/login/form?error=true");
    }
}