package fi.jonix.hkmyynti.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import fi.jonix.hkmyynti.commandobject.OrderInfo;

public class RefundValidator implements Validator {

	public boolean supports(Class clazz) {
    	return OrderInfo.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmationCode", "confirmationCodeIsRequired");
		AccountNumberValidator.validate(obj, errors);
	}
	
}
