package fi.jonix.hkmyynti.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.commandobject.OrderInfo;
import fi.jonix.hkmyynti.util.EmailUtils;
import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.hkmyynti.util.XmlUtils;
import fi.jonix.hkmyynti.validator.AddressChangeValidator;
import fi.jonix.huutonet.domain.model.Seller;

@Controller
@RequestMapping("/addressChange")
public class AddressChangeController {
	
	public static final Logger logger = Logger.getLogger(AddressChangeController.class);

	//@Autowired  
	//private Validator validator;  
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(@RequestParam(required = false) String referenceNumber,
			@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName,
			@RequestParam(required = false) String address,
			@RequestParam(required = false) String postCode,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String emailAddress,
			HttpServletRequest req) {
		logger.debug("AddressChangeController.showForm");
		//Seller seller = sellerExtractor.getSeller(req);
		//logger.debug("seller = " + seller.getId());
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("title", "addressChange");
		mav.addObject("whichPage", "addressChange");
		OrderInfo oi = new OrderInfo();
		oi.setReferenceNumber(referenceNumber);
		oi.setFirstName(firstName);
		oi.setLastName(lastName);
		oi.setAddress(address);
		oi.setPostCode(postCode);
		oi.setCity(city);
		oi.setEmailAddress(emailAddress);
		mav.addObject("commandObj", oi);
		//mav.addObject("sellerName", seller.getSignature());
		//mav.addObject("sellerUrl", seller.getStoreUrl());
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute("commandObj") OrderInfo commandObj,
			BindingResult result, HttpServletRequest req) {
		logger.debug("AddressChangeController.post");
		new AddressChangeValidator().validate(commandObj, result);
		Seller seller = sellerExtractor.getSeller(req);
		if (result.hasErrors() || !sendEmail(commandObj, result, seller)) {
			logger.debug("AddressChangeController.post has errors.");
			ModelAndView mav = new ModelAndView("index","commandObj",commandObj);
			mav.addObject("whichPage", "addressChange");
			sellerExtractor.addSellerInfo(req, mav);
			return mav;
		} else {
			logger.debug("AddressChangeController.post doesn't have errors.");
			//ModelAndView mav = new ModelAndView("redirect:/seller/");
			ModelAndView mav = sellerExtractor.redirectToBase(req);
			mav.addObject("information", "addressChange.received");
			return mav;
		}
	}
	
	private boolean sendEmail(OrderInfo commandObj, Errors errors, Seller seller) {
		String body = XmlUtils.getXmlTag("FIRST_NAME", commandObj.getFirstName());
		body += XmlUtils.getXmlTag("LAST_NAME", commandObj.getLastName());
		body += XmlUtils.getXmlTag("ADDRESS", commandObj.getAddress());
		body += XmlUtils.getXmlTag("POST_CODE", commandObj.getPostCode());
		body += XmlUtils.getXmlTag("CITY", commandObj.getCity());
		body += XmlUtils.getXmlTag("EMAIL_ADDRESS", commandObj.getEmailAddress());
		body += XmlUtils.getXmlTag("REFERENCE_NUMBER", commandObj.getReferenceNumber());
		//logger.debug(body);
		try {
			EmailUtils.sendEmail(body, "GAE_ADDRESS_CHANGE " + commandObj.getReferenceNumber(), seller);
		} catch (Exception e) {
			e.printStackTrace();
			errors.reject("emailSendingFailed");
			return false;
		}
		return true;
	}
	
}
