package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String adminPage(Model model, Principal principal) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));
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
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam(value = "password", required = false) String password,
                           @RequestParam(value = "role", required = false) String role) {

        if (user.getId() != null) {
            User existingUser = userService.findById(user.getId());
            if (existingUser != null) {
                user.setPassword(password != null && !password.isEmpty() ? password : existingUser.getPassword());
            }
        }

        Set<Role> roles = new HashSet<>();
        if ("ADMIN".equals(role)) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            if (adminRole != null) roles.add(adminRole);
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole != null) roles.add(userRole);
        } else {
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole != null) roles.add(userRole);
        }
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/user/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));
        return "admin/user-form";
    }

    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}