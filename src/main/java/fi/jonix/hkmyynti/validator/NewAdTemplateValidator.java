package fi.jonix.hkmyynti.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import fi.jonix.hkmyynti.commandobject.AdTemplateInfo;

public class NewAdTemplateValidator implements Validator {

	public boolean supports(Class clazz) {
    	return AdTemplateInfo.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "headline", "headlineIdIsRequired");
		/*AdTemplateInfo adTemplateInfo = (AdTemplateInfo) obj;
		if (adTemplateInfo.getDetails() == null || adTemplateInfo.getDetails().size() == 0) {
			errors.rejectValue("details", "headlineIdIsRequired");
		} else {
			boolean allRowsEmpty = true;
			for (String detailRow : adTemplateInfo.getDetails()) {
				if (detailRow != null && !detailRow.trim().equals("")) {
					allRowsEmpty = false;
				}
			}
			if (allRowsEmpty) {
				errors.rejectValue("details", "headlineIdIsRequired");
			}
		}
		if (adTemplateInfo.getTechnicalSpecs() == null || adTemplateInfo.getTechnicalSpecs().size() == 0) {
			errors.rejectValue("technicalSpecs", "headlineIdIsRequired");
		} else {
			boolean allRowsEmpty = true;
			for (String techSpecsRow : adTemplateInfo.getTechnicalSpecs()) {
				if (techSpecsRow != null && !techSpecsRow.trim().equals("")) {
					allRowsEmpty = false;
				}
			}
			if (allRowsEmpty) {
				errors.rejectValue("technicalSpecs", "headlineIdIsRequired");
			}
		}*/
		//errors.reject("generalError");
	}

}
