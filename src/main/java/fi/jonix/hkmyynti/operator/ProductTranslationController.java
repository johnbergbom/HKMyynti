package fi.jonix.hkmyynti.operator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.commandobject.AdTemplateInfo;

@Controller
@RequestMapping("/operator/translateProducts")
public class ProductTranslationController {

	public static final Logger logger = Logger.getLogger(ProductTranslationController.class);

	@Autowired
	private ProductTranslationUtil productTranslationUtil;

	@RequestMapping
	public ModelAndView showForm(HttpServletRequest req) {
		return productTranslationUtil.getStartPage(req);
	}

	/**
	 * The suggested translation of the details might possibly change after the
	 * headline is translated. This method returns an updated first row of the
	 * details.
	 */
	@RequestMapping(method = RequestMethod.POST, params="ajax")
	public ModelAndView post(@RequestParam(required = true) boolean ajax,
			@RequestParam(value = "productId", required = true) long productId,
			@RequestParam(value = "headline", required = true) String headline,
			@RequestParam(value = "sellerId", required = true) long sellerId) {
		logger.debug("ajaxanrop = " + ajax);
		logger.debug("productId = " + productId);
		logger.debug("headline = " + headline);
		logger.debug("sellerId = " + sellerId);
		return productTranslationUtil.updateDetailsWithAjax(productId,headline,sellerId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute("commandObj") AdTemplateInfo adTemplateInfo,
			BindingResult result, HttpServletRequest req,
			@RequestParam(value = "sellerId", required = true) long sellerId,
			@RequestParam(value = "adTemplateLanguage", required = true) String adTemplateLanguage,
			@RequestParam(value = "productId", required = true) long productId,
			@RequestParam(value = "nbrProducts", required = true) int nbrProducts) {
		logger.debug("ProductTranslationController.post");
		logger.debug("sellerId = " + sellerId);
		logger.debug("adTemplateLanguage = " + adTemplateLanguage);
		logger.debug("productId = " + productId);
		logger.debug("translatedHeadline = " + adTemplateInfo.getHeadline());
		return productTranslationUtil.handleResult(adTemplateInfo,result,req,sellerId,adTemplateLanguage,productId,nbrProducts);
	}

}
