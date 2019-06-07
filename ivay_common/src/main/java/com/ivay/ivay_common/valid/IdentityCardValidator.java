package com.ivay.ivay_common.valid;

import com.ivay.ivay_common.utils.AESEncryption;
import com.ivay.ivay_common.utils.StringUtil;
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
        if (needDecrypt) {
            s = AESEncryption.decrypt(s);
        }
        s = StringUtil.replaceBlank(s);
        return StringUtil.isCharacter(s) && s.length() >= identityCard.min() && s.length() <= identityCard.max();
    }
}
