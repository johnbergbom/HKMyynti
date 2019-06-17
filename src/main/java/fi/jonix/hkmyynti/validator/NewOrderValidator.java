package fi.jonix.hkmyynti.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import fi.jonix.hkmyynti.commandobject.OrderInfo;

public class NewOrderValidator implements Validator {

	public boolean supports(Class clazz) {
    	return OrderInfo.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		AddressValidator.validate(obj, errors);
		EmailValidator.validate(obj, errors);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "marketSalesId", "marketSalesIdIsRequired");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount", "amountIsRequired");
		OrderInfo newOrder = (OrderInfo) obj;
		if (newOrder.getAmount() < 1) {
			errors.rejectValue("amount","amountTooLow");
		} else if (newOrder.getAmount() > 10) {
			errors.rejectValue("amount","amountTooHigh");
		}
	}

}
