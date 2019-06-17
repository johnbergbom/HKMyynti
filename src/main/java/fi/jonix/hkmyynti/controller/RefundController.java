package fi.jonix.hkmyynti.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.commandobject.OrderInfo;
import fi.jonix.hkmyynti.util.EmailUtils;
import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.hkmyynti.util.XmlUtils;
import fi.jonix.hkmyynti.validator.RefundValidator;
import fi.jonix.huutonet.domain.model.Seller;

@Controller
@RequestMapping("/refund")
public class RefundController {

	public static final Logger logger = Logger.getLogger(RefundController.class);
	
	//@Autowired  
	//private Validator validator;  
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(@RequestParam(required = false) String confirmationCode,
			@RequestParam(required = false) String accountNumber,
			HttpServletRequest req) {
		logger.debug("RefundController.showForm");
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("title", "refund");
		mav.addObject("whichPage", "refund");
		OrderInfo oi = new OrderInfo();
		oi.setConfirmationCode(confirmationCode);
		oi.setAccountNumber(accountNumber);
		mav.addObject("commandObj", oi);
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute("commandObj") OrderInfo commandObj,
			BindingResult result, HttpServletRequest req) {
		logger.debug("RefundController.post");
		new RefundValidator().validate(commandObj, result);
		Seller seller = sellerExtractor.getSeller(req);
		if (result.hasErrors() || !sendEmail(commandObj, result, seller)) {
			logger.debug("RefundController.post has errors.");
			ModelAndView mav = new ModelAndView("index","commandObj",commandObj);
			mav.addObject("whichPage", "refund");
			sellerExtractor.addSellerInfo(req, mav);
			return mav;
		} else {
			logger.debug("RefundController.post doesn't have errors.");
			//ModelAndView mav = new ModelAndView("redirect:/seller/");
			ModelAndView mav = sellerExtractor.redirectToBase(req);
			mav.addObject("information", "refund.received");
			return mav;
		}
	}
	
	private boolean sendEmail(OrderInfo commandObj, Errors errors, Seller seller) {
		String accountNumber = commandObj.getAccountNumber();
		if (accountNumber.indexOf(" ") >= 0) {
			accountNumber = accountNumber.replaceAll(" ","");
		}
		String body = XmlUtils.getXmlTag("ACCOUNT_NUMBER", accountNumber);
		body += XmlUtils.getXmlTag("CONFIRMATION_CODE", commandObj.getConfirmationCode());
		//logger.debug(body);
		try {
			EmailUtils.sendEmail(body, "GAE_REFUND_FOR_ORDER " + commandObj.getConfirmationCode(), seller);
		} catch (Exception e) {
			e.printStackTrace();
			errors.reject("emailSendingFailed");
			return false;
		}
		return true;
	}
	
}
