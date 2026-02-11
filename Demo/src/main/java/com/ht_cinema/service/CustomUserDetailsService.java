package com.ht_cinema.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ht_cinema.config.CustomUserDetails;
import com.ht_cinema.model.Account;
import com.ht_cinema.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + email)
                );

        if (!account.isStatus()) {
            throw new UsernameNotFoundException("Tài khoản của bạn đã bị khóa");
        }

        return new CustomUserDetails(account);
    }
}
