package com.ht_cinema.controller;

import com.ht_cinema.model.Account;
import com.ht_cinema.repository.AccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/form")
    public String registerForm(Model model) {
        model.addAttribute("account", new Account());
        return "account/register";
    }

    @PostMapping("/save")
    public String registerSave(
            @Valid @ModelAttribute("account") Account account,
            BindingResult result,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (result.hasErrors()) {
            return "account/register";
        }

        if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email đã được sử dụng");
            return "account/register";
        }

        if (!account.getPassword().equals(confirmPassword)) {
            model.addAttribute("confirmError", "Mật khẩu xác nhận không khớp");
            return "account/register";
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        account.setRole(false);   
        account.setStatus(true);  

        accountRepository.save(account);

        return "redirect:/login/form?registered=true";
    }
}
