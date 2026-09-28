package com.quynhtadinh.finalexample.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.quynhtadinh.finalexample.entity.User;
import com.quynhtadinh.finalexample.service.UserService;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty", "Vui lòng nhập tên đăng nhập");
        if (user.getUsername().length() < 4 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username", "Tên đăng nhập phải từ 4 đến 32 ký tự");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.userForm.username", "Tên đăng nhập đã tồn tại");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty", "Vui lòng nhập mật khẩu");
        if (user.getPassword().length() < 4 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password", "Mật khẩu phải từ 4 đến 32 ký tự");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm", "Mật khẩu nhập lại không khớp");
        }
    }
}