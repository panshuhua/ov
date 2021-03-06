package com.ivay.ivay_common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 验证密码合法性的注解
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {
    String message() default "validated.password.error";
    
    String type() default "0";

    int size() default 6;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
