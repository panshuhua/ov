package com.ivay.ivay_app.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 验证身份证合法性的注解
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IdentityCardValidator.class)
public @interface IdentityCard {
    String message() default "validated.identityCard.error";

    int min() default 8;

    int max() default 11;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
