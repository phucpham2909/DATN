package com.ht_cinema.config;

import com.ht_cinema.config.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        if (user.getAccount().isRole()) {
            // ✅ Admin
            try { response.sendRedirect("/admin/index"); } catch (Exception ignored) {}
        } else {
            // ✅ User
            try { response.sendRedirect("/"); } catch (Exception ignored) {}
        }
    }
}
