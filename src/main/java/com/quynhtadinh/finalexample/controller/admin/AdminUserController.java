package com.quynhtadinh.finalexample.controller.admin;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quynhtadinh.finalexample.entity.Role;
import com.quynhtadinh.finalexample.entity.User;
import com.quynhtadinh.finalexample.repository.RoleRepository;
import com.quynhtadinh.finalexample.repository.UserRepository;
import com.quynhtadinh.finalexample.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @GetMapping
    public String list(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) String keyword) {
        Page<User> users = userService.searchUsers(Optional.of(keyword != null ? keyword : ""), PageRequest.of(page, 10));
        model.addAttribute("activeMenu", "users");
        model.addAttribute("title", "Người dùng");
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "admin/users";
    }

    @GetMapping("/{id}/toggle-admin")
    public String toggleAdmin(@PathVariable long id) {
        User user = userService.findById(id);
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (user != null && adminRole != null) {
            HashSet<Role> roles = new HashSet<>(user.getRoles());
            boolean hasAdmin = roles.stream().anyMatch(r -> r.getId().equals(adminRole.getId()));
            if (hasAdmin) {
                roles.removeIf(r -> r.getId().equals(adminRole.getId()));
            } else {
                roles.add(adminRole);
            }
            user.setRoles(roles);
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}
