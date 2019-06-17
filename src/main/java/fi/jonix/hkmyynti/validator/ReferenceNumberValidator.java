package fi.jonix.hkmyynti.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import fi.jonix.hkmyynti.commandobject.OrderInfo;

public class ReferenceNumberValidator {

	public static void validate(Object obj, Errors errors) {
		OrderInfo orderInfo = (OrderInfo) obj;
		validateSecurityCode(orderInfo, errors);
	}

	private static void validateSecurityCode(OrderInfo orderInfo, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "referenceNumber", "referenceNumberIsRequired");
		if (orderInfo.getReferenceNumber() != null && orderInfo.getReferenceNumber().length() > 0) {
			String refNbr = orderInfo.getReferenceNumber();
			if (refNbr.endsWith("_only")) {
				refNbr = refNbr.substring(0, refNbr.length()-5);
			}
			String part = refNbr.substring(0,
					refNbr.length() - 1);
			try {
				if (!getSecurityCode(part).equals(refNbr)) {
					errors.rejectValue("referenceNumber", "addressChange.invalidReferenceNumber");
				}
			} catch (NumberFormatException e) {
				errors.rejectValue("referenceNumber", "addressChange.invalidReferenceNumber");
			}
		}
	}

	private static String getSecurityCode(String code) throws NumberFormatException {
		int sum = 0;
		int[] multiplier = new int[] { 7, 3, 1 };
		for (int i = 0; i < code.length(); i++) {
			sum += Integer.parseInt(code.charAt(code.length() - i - 1) + "") * multiplier[i % 3];
		}
		int checkNumber = ((sum % 10) - 10) % 10 * -1;
		return code + checkNumber;
	}

}
