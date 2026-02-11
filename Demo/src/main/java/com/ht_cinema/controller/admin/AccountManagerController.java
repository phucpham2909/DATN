package com.ht_cinema.controller.admin;

import com.ht_cinema.config.CustomUserDetails;
import com.ht_cinema.model.Account;
import com.ht_cinema.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/account-manager")
@RequiredArgsConstructor
public class AccountManagerController {

    private final AccountRepository accountRepository;

    // ✅ Lấy user từ Spring Security
    private Account getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
            return user.getAccount();
        }
        return null;
    }

    @GetMapping
    public String showUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {

        Account currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login/form";
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending());

        Page<Account> accountsPage =
                keyword.isBlank()
                        ? accountRepository.findAll(pageable)
                        : accountRepository.findByKeyword(keyword.trim(), pageable);

        List<Account> filteredList = accountsPage.getContent()
                .stream()
                .filter(acc -> !acc.getId().equals(currentUser.getId())) // bỏ chính mình
                .filter(acc -> !acc.isRole()) // bỏ admin
                .toList();

        Page<Account> filteredPage = new PageImpl<>(
                filteredList,
                pageable,
                filteredList.size()
        );

        model.addAttribute("accounts", filteredPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentUser", currentUser);

        model.addAttribute("activeParent", "account-manager");
        model.addAttribute("activePage", "account-manager");

        return "admin/account-manager";
    }

    @PostMapping
    public String toggleUserStatus(
            @RequestParam("id") Integer id,
            @RequestParam("newStatus") boolean newStatus) {

        Account currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login/form";
        }

        Account targetAccount = accountRepository.findById(id).orElse(null);

        if (targetAccount != null &&
            !targetAccount.getId().equals(currentUser.getId()) && // không khóa chính mình
            !targetAccount.isRole()) { // không khóa admin

            targetAccount.setStatus(newStatus);
            accountRepository.save(targetAccount);
        }

        return "redirect:/admin/account-manager";
    }
}
