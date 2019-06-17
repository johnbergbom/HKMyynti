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
import fi.jonix.hkmyynti.validator.OrderStatusValidator;
import fi.jonix.huutonet.domain.model.Seller;

@Controller
@RequestMapping("/orderStatus")
public class OrderStatusController {

	public static final Logger logger = Logger.getLogger(OrderStatusController.class);

	//@Autowired  
	//private Validator validator;  
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(@RequestParam(required = false) String emailAddress,
			HttpServletRequest req) {
		logger.debug("OrderStatusController.showForm");
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("title", "orderStatus");
		mav.addObject("whichPage", "status");
		OrderInfo oi = new OrderInfo();
		oi.setEmailAddress(emailAddress);
		mav.addObject("commandObj", oi);
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute("commandObj") OrderInfo commandObj,
			BindingResult result, HttpServletRequest req) {
		logger.debug("OrderStatusController.post");
		new OrderStatusValidator().validate(commandObj, result);
		Seller seller = sellerExtractor.getSeller(req);
		if (result.hasErrors() || !sendEmail(commandObj, result, seller)) {
			logger.debug("OrderStatusController.post has errors.");
			ModelAndView mav = new ModelAndView("index","commandObj",commandObj);
			mav.addObject("whichPage", "status");
			sellerExtractor.addSellerInfo(req, mav);
			return mav;
		} else {
			logger.debug("OrderStatusController.post doesn't have errors.");
			//ModelAndView mav = new ModelAndView("redirect:/seller/");
			ModelAndView mav = sellerExtractor.redirectToBase(req);
			mav.addObject("information", "orderStatus.received");
			return mav;
		}
	}
	
	private boolean sendEmail(OrderInfo commandObj, Errors errors, Seller seller) {
		String body = XmlUtils.getXmlTag("EMAIL_ADDRESS", commandObj.getEmailAddress());
		//logger.debug(body);
		try {
			EmailUtils.sendEmail(body, "GAE_STATUS " + commandObj.getEmailAddress(), seller);
		} catch (Exception e) {
			e.printStackTrace();
			errors.reject("emailSendingFailed");
			return false;
		}
		return true;
	}
	
}
