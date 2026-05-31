package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String adminPage(Model model, Principal principal) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("currentUserEmail", principal.getName());
        return "admin";
    }

    @GetMapping("/user/new")
    public String createUserForm(Model model, Principal principal) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));
        return "admin/user-form";
    }

    @PostMapping("/user/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/user/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));
        return "admin/user-form";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}