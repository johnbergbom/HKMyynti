package fi.jonix.hkmyynti.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import fi.jonix.hkmyynti.commandobject.OrderInfo;

public class AccountNumberValidator {

	//public static String ACCOUNT_VALIDATION_PATTERN = "(\\d+-\\d+)|(FI\\d{16}+)";
	public static String ACCOUNT_VALIDATION_PATTERN = "(FI\\d{2}[ ]\\d{4}[ ]\\d{4}[ ]\\d{4}[ ]\\d{2})|(FI\\d{16})";
	
	public static void validate(Object obj, Errors errors) {
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountNumber", "accountNumberIsRequired");
	    OrderInfo orderInfo = (OrderInfo) obj;
	    validateAccountNumber(orderInfo, errors);
	}

	private static void validateAccountNumber(OrderInfo orderInfo, Errors errors) {
		String accountNumber = orderInfo.getAccountNumber();
		if (accountNumber.indexOf(" ") >= 0) {
			accountNumber = accountNumber.replaceAll(" ","");
		}
		if (!accountNumber.matches(ACCOUNT_VALIDATION_PATTERN)) {
			errors.rejectValue("accountNumber", "cancelationConfirmation.invalidAccountNumber");
		}
	}

}
