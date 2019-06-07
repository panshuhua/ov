package com.ivay.ivay_common.valid;

import com.ivay.ivay_common.utils.StringUtil;

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
    	boolean flag;
    	if("0".equals(password.type())){
    		flag = s.length() == password.size() && StringUtil.isNumeric(s);
    	}else{
    		flag = StringUtil.valiPassword(s);
    	}
    	return flag;
    }
    
}
