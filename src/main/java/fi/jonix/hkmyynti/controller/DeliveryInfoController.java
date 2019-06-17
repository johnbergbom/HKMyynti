package fi.jonix.hkmyynti.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.huutonet.domain.model.Seller;
import fi.jonix.huutonet.domain.model.dao.SellerDAO;

@Controller
@RequestMapping("/deliveryInfo")
public class DeliveryInfoController {

	public static final Logger logger = Logger.getLogger(DeliveryInfoController.class);

	@Autowired
	private SellerDAO sellerDAO;
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(HttpServletRequest req) {
		logger.debug("AddressChangeController.showForm");
		/*Seller seller = sellerDAO.get(2L);
		if (!seller.getName().equals("HK-myynti")) {
			throw new RuntimeException("Internal error");
		}*/
		Seller seller = sellerExtractor.getSeller(req);
		logger.debug("seller = " + seller.getId());
		ModelAndView mav = new ModelAndView("showSimpleText");
		mav.addObject("text", seller.getDeliveryText());
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}
	
}
