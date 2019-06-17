package fi.jonix.hkmyynti.operator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.util.SellerExtractor;

@Controller
@RequestMapping("/operator/main")
public class DefaultOperatorViewController {

	@Autowired
	private SellerExtractor sellerExtractor;
	
	@RequestMapping
	public ModelAndView showMain(HttpServletRequest req) {
		ModelAndView mav = new ModelAndView("operator/index");
		mav.addObject("whichPage", "main");
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}
	
}
