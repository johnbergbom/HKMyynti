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
import fi.jonix.hkmyynti.util.ProductHandler;
import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.hkmyynti.util.XmlUtils;
import fi.jonix.hkmyynti.validator.NewOrderValidator;
import fi.jonix.huutonet.domain.model.Ad;
import fi.jonix.huutonet.domain.model.Market;
import fi.jonix.huutonet.domain.model.Seller;
import fi.jonix.huutonet.domain.model.dao.AdDAO;
import fi.jonix.huutonet.domain.model.dao.MarketDAO;

@Controller
@RequestMapping("/newOrder")
public class NewOrderController {

	public static final Logger logger = Logger.getLogger(NewOrderController.class);

	/*private static String MARKET_NAME_HUUTONET = "Huutonet";
	private static String MARKET_NAME_MIKKO = "Mikko";
	private static String PROVIDER_NAME_DEAL_EXTREME = "DealExtreme";
	private static String PROVIDER_NAME_FOCAL_PRICE = "FocalPrice";
	private static String mikkoExpression = "\\d{7,8}";
	private static String huutonetExpression = "\\d{9}";
	private static String dealExtremeExpression = "sku.\\d+";
	private static String focalPriceExpression = "fc.[\\d\\w]+";*/

	@Autowired
	private ProductHandler productHandler;
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	@Autowired
	private AdDAO adDAO;
	
	@Autowired
	private MarketDAO marketDAO;
	
	@RequestMapping
	public ModelAndView showForm(@RequestParam(value = "marketSalesId", required = false) String marketSalesId,
			@RequestParam(value = "marketId", required = false) Long marketId,
			HttpServletRequest req) {
		logger.debug("marketSalesId = " + marketSalesId + ", marketId = " + marketId);
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("title", "placeNewOrder");
		OrderInfo oi = new OrderInfo();
		oi.setAmount(1);
		oi.setMarketSalesId(marketSalesId);
		if (marketSalesId != null && marketId != null) {
			productHandler.addOrderInfoDataForProduct(mav, marketSalesId, marketId);
			mav.addObject("marketId", marketId);
		}
		if (mav.getModel().get("adTemplateHeadline") != null/*oi.getAdTemplateHeadline() != null*/) {
			mav.addObject("whichPage", "newOrderShowProduct");
		} else {
			mav.addObject("whichPage", "newOrder");
		}
		mav.addObject("commandObj", oi);
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute("commandObj") OrderInfo commandObj,
			BindingResult result, @RequestParam(value = "marketId", required = false) Long marketId,
			HttpServletRequest req) {
		logger.info("NewOrderController.post1 = " + marketId);
		//logger.info("NewOrderController.post2 = åäöÅÄÖ");
		logger.info("NewOrderController.firstName = " + commandObj.getFirstName());
		logger.info("NewOrderController.lastName = " + commandObj.getLastName());
		logger.info("NewOrderController.address = " + commandObj.getAddress());
		logger.info("NewOrderController.postCode = " + commandObj.getPostCode());
		logger.info("NewOrderController.city = " + commandObj.getCity());
		logger.info("NewOrderController.emailAddress = " + commandObj.getEmailAddress());
		logger.info("NewOrderController.amount = " + commandObj.getAmount());
		new NewOrderValidator().validate(commandObj, result);
		//String market = this.getMarket(commandObj.getMarketSalesId(), result);
		//String provider = this.getProvider(commandObj.getMarketSalesId(), result);
		Market marketObj = marketDAO.get(marketId);
		Ad ad = adDAO.getByMarketSalesIdWithoutSeller(commandObj.getMarketSalesId(), marketObj);
		String market = ad.getMarket().getName();
		//String provider = ad.getAdTemplate().getProduct().getProvider().getName();
		Seller seller = sellerExtractor.getSeller(req);
		if (result.hasErrors() || !sendEmail(commandObj,market,null,result, seller)) {
			logger.info("NewOrderController.post has errors.");
			ModelAndView mav = new ModelAndView("index","commandObj",commandObj);
			//mav.addObject("whichPage", "newOrder");
			if (commandObj.getMarketSalesId() != null && marketId != null) {
				productHandler.addOrderInfoDataForProduct(mav, commandObj.getMarketSalesId(), marketId);
				mav.addObject("marketId", marketId);
			}
			if (mav.getModel().get("adTemplateHeadline") != null/*oi.getAdTemplateHeadline() != null*/) {
				mav.addObject("whichPage", "newOrderShowProduct");
			} else {
				mav.addObject("whichPage", "newOrder");
			}
			sellerExtractor.addSellerInfo(req, mav);
			return mav;
		} else {
			logger.info("NewOrderController.post doesn't have errors.");
			//ModelAndView mav = new ModelAndView("redirect:/seller/");
			ModelAndView mav = sellerExtractor.redirectToBase(req);
			mav.addObject("information", "newOrder.sent");
			return mav;
		}
	}
	
	private boolean sendEmail(OrderInfo commandObj, String market, String provider, Errors errors, Seller seller) {
		String body = XmlUtils.getXmlTag("FIRST_NAME", commandObj.getFirstName());
		body += XmlUtils.getXmlTag("LAST_NAME", commandObj.getLastName());
		body += XmlUtils.getXmlTag("ADDRESS", commandObj.getAddress());
		body += XmlUtils.getXmlTag("POST_CODE", commandObj.getPostCode());
		body += XmlUtils.getXmlTag("CITY", commandObj.getCity());
		body += XmlUtils.getXmlTag("EMAIL_ADDRESS", commandObj.getEmailAddress());
		body += XmlUtils.getXmlTag("COUNT", ""+commandObj.getAmount());
		
		String marketSalesId = commandObj.getMarketSalesId();
		if(marketSalesId.startsWith("fc.")){
			marketSalesId = marketSalesId.substring("fc.".length());
		}
		
		body += XmlUtils.getXmlTag("MARKET_SALES_ID", marketSalesId);
		if (market != null) {
			body += XmlUtils.getXmlTag("MARKET", market);
		}
		if (provider != null) {
			body += XmlUtils.getXmlTag("PROVIDER", provider);
		}
		//logger.info(body);
		try {
			EmailUtils.sendEmail(body, "GAE_NEW_ORDER " + commandObj.getMarketSalesId(), seller);
		} catch (Exception e) {
			e.printStackTrace();
			errors.reject("emailSendingFailed");
			return false;
		}
		return true;
	}
	
	/*private String getMarket(String marketSalesId, Errors errors) {
		if (marketSalesId.matches(huutonetExpression)) {
			return MARKET_NAME_HUUTONET;
		} else if (marketSalesId.matches(dealExtremeExpression)) {
		} else if (marketSalesId.matches(focalPriceExpression)) {
		} else if(marketSalesId.matches(mikkoExpression)){
			return MARKET_NAME_MIKKO;
		} else {
			errors.rejectValue("marketSalesId", "newOrder.invalidMarketSalesId");
		}
		return null;
	}*/
	
	/*private String getProvider(String marketSalesId, Errors errors) {
		if (marketSalesId.matches(huutonetExpression)) {
		} else if (marketSalesId.matches(dealExtremeExpression)) {
			return PROVIDER_NAME_DEAL_EXTREME;
		} else if (marketSalesId.matches(focalPriceExpression)) {
			return PROVIDER_NAME_FOCAL_PRICE;
		} else if(marketSalesId.matches(mikkoExpression)){
		} else {
			errors.rejectValue("marketSalesId", "newOrder.invalidMarketSalesId");
		}
		return null;
	}*/

}
