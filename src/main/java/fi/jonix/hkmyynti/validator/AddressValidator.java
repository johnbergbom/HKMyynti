package fi.jonix.hkmyynti.validator;

import org.springframework.validation.Errors;

import fi.jonix.hkmyynti.commandobject.OrderInfo;
import fi.jonix.huutonet.order.ContactInfoChecker;

public class AddressValidator {

	public static void validate(Object obj, Errors errors) {
	       /*ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstNameIsRequired");
	       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastNameIsRequired");
	       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "addressIsRequired");
	       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postCode", "postCodeIsRequired");
	       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "cityIsRequired");*/

		OrderInfo orderInfo = (OrderInfo) obj;
	       String error = ContactInfoChecker.checkFirstName(orderInfo.getFirstName());
	       if (error != null) {
	    	   errors.rejectValue("firstName",error);
	       }
	       error = ContactInfoChecker.checkLastName(orderInfo.getLastName());
	       if (error != null) {
	    	   errors.rejectValue("lastName",error);
	       }
	       error = ContactInfoChecker.checkAddress(orderInfo.getAddress(),orderInfo.getPostCode());
	       if (error != null) {
	    	   errors.rejectValue("address",error);
	       }
	       error = ContactInfoChecker.checkPostCode(orderInfo.getPostCode());
	       if (error != null) {
	    	   errors.rejectValue("postCode",error);
	       }
	       error = ContactInfoChecker.checkCity(orderInfo.getCity());
	       if (error != null) {
	    	   errors.rejectValue("city",error);
	       }
	}

}
