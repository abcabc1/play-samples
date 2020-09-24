package annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = NameIncludeValidator.class)
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameInclude {

    String message(); //报错返回的信息

    Class<?>[] groups() default {}; //被哪个组校验

    String type(); //自己定义的

    Class<? extends Payload>[] payload() default {};
}
