package com.ht_cinema.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ht_cinema.model.Account;
import com.ht_cinema.repository.AccountRepository;

@Controller
public class LoginController {
	@Autowired
	AccountRepository accountRepository;
	
	@GetMapping("/login/form")
	public String loginForm(Model model, @RequestParam(value = "error", required = false) String error) {
	    if (error != null) {
	        model.addAttribute("message", "Sai tài khoản hoặc mật khẩu");
	    }

	    return "account/login";
	}

    @GetMapping("/access-denied")
    public String denied() {
        return "home/index";
    }
}
