package com.ivay.ivay_app.valid;

import com.ivay.ivay_app.utils.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 密码校验规则类
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    private Password password;

    @Override
    public void initialize(Password password) {
        this.password = password;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.length() == password.size() && StringUtil.isNumeric(s);
    }
}
