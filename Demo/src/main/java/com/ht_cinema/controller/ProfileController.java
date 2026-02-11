package com.ht_cinema.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ht_cinema.config.CustomUserDetails;
import com.ht_cinema.model.Account;
import com.ht_cinema.repository.AccountRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class ProfileController {
	private Account getCurrentAccount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user) {
			return user.getAccount();
		}
		return null;
	}
    private final AccountRepository accountRepository;

    public ProfileController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/account/profileForm")
    public String profileForm(HttpSession session, Model model) {
    	Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}
        model.addAttribute("user", account);
        return "account/profileForm";
    }

    @PostMapping("/account/profile/save")
    public String profileFormSave(@RequestParam("fullname") String fullname,
                                  @RequestParam("phone") String phone,
                                  @RequestParam(value = "gender", required = false) Boolean gender,
                                  @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                                  HttpSession session) throws IOException {

    	Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}

        account.setFullname(fullname);
        account.setPhone(phone);
        if (gender != null) {
            account.setGender(gender);
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String filename = account.getId() + "_" + avatarFile.getOriginalFilename();
            File dest = Paths.get(System.getProperty("user.dir"), "uploads", filename).toFile();
            dest.getParentFile().mkdirs();
            avatarFile.transferTo(dest);

            account.setAvatar("/uploads/" + filename);
        }

        accountRepository.save(account);
        session.setAttribute("currentUser", account);

        return "redirect:/account/profileForm?success";
    }
}
