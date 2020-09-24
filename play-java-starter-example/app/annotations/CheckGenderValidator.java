package annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckGenderValidator implements ConstraintValidator<CheckGender, String> {

    @Override
    public void initialize(CheckGender constraintAnnotation) {
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }
        return Gender.FEMALE.getGenderCn().equals(object) || Gender.MALE.getGenderCn().equals(object);
    }
}