package com.ivay.ivay_app.valid;

import com.ivay.ivay_app.utils.AESEncryption;
import com.ivay.ivay_app.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 身份证校验规则类
 */
public class IdentityCardValidator implements ConstraintValidator<IdentityCard, String> {
    private IdentityCard identityCard;

    @Value("${needDecrypt}")
    private boolean needDecrypt;

    @Override
    public void initialize(IdentityCard identityCard) {
        this.identityCard = identityCard;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println(needDecrypt);
        if (needDecrypt) {
            s = AESEncryption.decrypt(s);
        }
        return StringUtil.isCharacter(s) && s.length() >= identityCard.min() && s.length() <= identityCard.max();
    }
}
