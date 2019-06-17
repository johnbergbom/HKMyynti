package fi.jonix.hkmyynti.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.util.SellerExtractor;

@Controller
@RequestMapping("/")
public class MainController {

	public static final Logger logger = Logger.getLogger(MainController.class);

	@Autowired
	private SellerExtractor sellerExtractor;
	
	@RequestMapping
	public ModelAndView showMain(@RequestParam(required = false) String information,
			HttpServletRequest req) {
		//Seller seller = sellerExtractor.getSeller(req);
		//logger.debug("seller = " + seller.getId());

		ModelAndView mav = new ModelAndView("index");
		//mav.addObject("title", "menu.title");
		mav.addObject("whichPage", "main");
		//mav.addObject("sellerName", seller.getSignature());
		//mav.addObject("sellerUrl", seller.getStoreUrl());
		sellerExtractor.addSellerInfo(req, mav);
		mav.addObject("information", information);
		return mav;
	}
	
}
