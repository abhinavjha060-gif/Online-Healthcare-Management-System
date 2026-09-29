package com.hms.controller;

import com.hms.model.Role;
import com.hms.model.User;
import com.hms.repository.UserRepository;
import com.hms.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** "My Account" page for any logged-in user, including deleting their own account. */
@Controller
@RequestMapping("/account")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountService userAccountService;

    @Autowired
    public AccountController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              UserAccountService userAccountService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAccountService = userAccountService;
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Account not found"));
    }

    @GetMapping
    public String account(Authentication auth, Model model) {
        model.addAttribute("user", currentUser(auth));
        return "account";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam String password,
                          Authentication auth,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          RedirectAttributes ra) {
        User user = currentUser(auth);

        // Admins can't self-delete: the system must always keep at least one admin.
        if (user.getRole() == Role.ADMIN) {
            ra.addFlashAttribute("error", "Admin accounts can't be self-deleted. Ask another admin to remove it.");
            return "redirect:/account";
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            ra.addFlashAttribute("error", "Incorrect password. Your account was not deleted.");
            return "redirect:/account";
        }

        try {
            userAccountService.deleteAccount(user.getId());
        } catch (Exception ex) {
            log.error("Failed to delete account {}", user.getEmail(), ex);
            ra.addFlashAttribute("error", "Could not delete your account. Check the server log for details.");
            return "redirect:/account";
        }

        new SecurityContextLogoutHandler().logout(request, response, auth);
        return "redirect:/login?deleted";
    }
}
