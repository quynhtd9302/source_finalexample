package com.quynhtadinh.finalexample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.quynhtadinh.finalexample.config.SocialLoginProperties;
import com.quynhtadinh.finalexample.entity.User;
import com.quynhtadinh.finalexample.repository.ProductRepository;
import com.quynhtadinh.finalexample.repository.StoreRepository;
import com.quynhtadinh.finalexample.security.SecurityService;
import com.quynhtadinh.finalexample.service.UserService;
import com.quynhtadinh.finalexample.validator.UserValidator;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private SocialLoginProperties socialLoginProperties;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Tên người dùng và mật khẩu của bạn không hợp lệ.");

        if (logout != null)
            model.addAttribute("message", "Bạn đã đăng xuất thành công.");

        model.addAttribute("googleEnabled", socialLoginProperties.isGoogleEnabled());
        model.addAttribute("facebookEnabled", socialLoginProperties.isFacebookEnabled());

        return "login";
    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        model.addAttribute("username", securityService.findLoggedInUsername());
        model.addAttribute("featuredProducts", productRepository.findAll(PageRequest.of(0, 6)).getContent());
        model.addAttribute("stores", storeRepository.findByActiveTrue());
        return "index";
    }

}
