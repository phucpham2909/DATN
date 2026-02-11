package com.ht_cinema.controller;

import com.ht_cinema.model.Account;
import com.ht_cinema.repository.AccountRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChangePasswordController {

	@Autowired
	private AccountRepository accountRepository;

	@GetMapping("/change-password")
	public String changePasswordForm() {
		return "account/change-password";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			HttpSession session, Model model) {

		Account currentUser = (Account) session.getAttribute("currentUser");

		if (currentUser == null) {
			return "redirect:/login/form";
		}

		if (!currentUser.getPassword().equals(currentPassword)) {
			model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
			return "account/change-password";
		}
		if (newPassword.trim().equals(currentPassword.trim())) {
			model.addAttribute("error", "Mật khẩu mới không được trùng mật khẩu hiện tại.");
			return "account/change-password";
		}

		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
			return "account/change-password";
		}

		currentUser.setPassword(newPassword);
		accountRepository.save(currentUser);
		session.invalidate();

		model.addAttribute("success", "Đổi mật khẩu thành công!");
		return "account/login";
	}
}
