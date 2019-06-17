package fi.jonix.hkmyynti.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import fi.jonix.hkmyynti.commandobject.OrderInfo;
import fi.jonix.huutonet.order.ContactInfoChecker;

public class EmailValidator {

	public static void validate(Object obj, Errors errors) {
	       //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "emailAddressIsRequired");
	       OrderInfo newOrder = (OrderInfo) obj;
	       String error = ContactInfoChecker.checkEmail(newOrder.getEmailAddress());
	       if (error != null) {
	    	   errors.rejectValue("emailAddress",error);
	       }
	       /*if (!ContactInfoChecker.correctEmailAddress(newOrder.getEmailAddress())) {
	    	   errors.rejectValue("emailAddress","illegalEmailAddress");
	       }*/
	}

}
