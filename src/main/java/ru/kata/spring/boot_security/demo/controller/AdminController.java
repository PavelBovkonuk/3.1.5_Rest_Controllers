package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.UserService;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserRepository userRepository, UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String user(Principal principal, Model model) {
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        model.addAttribute("roles", userService.findByUsername(principal.getName()).getAuthorities());
        return "showUser";
    }

    @GetMapping("/show")
    public String show(Principal principal, Model model) {
        List<User> users = userService.getAllUsers();
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        return "usersTable";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        User user = new User();
        user.setAge(0);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "editUser";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", roleRepository.findAll());
            return "editUser";
        }
        try {
            userService.saveUser(user);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("roles", roleRepository.findAll());
            return "editUser";
        }
        return "redirect:/admin/show";
    }

    @GetMapping("/update")
    public String showUpdateForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getUser(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "editUser";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", roleRepository.findAll());
            return "redirect:/admin/show?error=true&userId=" + user.getId();
        }
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (!user.getPassword().equals(existingUser.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            try {
                userService.updateUser(user);
            } catch (RuntimeException e) {
                model.addAttribute("errorMessage", e.getMessage());
                model.addAttribute("user", user);
                model.addAttribute("roles", roleRepository.findAll());
                return "usersTable";
            }
            return "redirect:/admin/show";
        }
        return "redirect:/admin/show";
    }

    @PostMapping("/delete")
    public String deleteUser (@RequestParam("id") Long id){
        userService.deleteUser(id);
        return "redirect:/admin/show";
    }
}
