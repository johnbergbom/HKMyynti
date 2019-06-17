package fi.jonix.hkmyynti.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.huutonet.domain.model.Seller;
import fi.jonix.huutonet.domain.model.dao.MessageDAO;
import fi.jonix.huutonet.domain.model.dao.SellerDAO;
import fi.jonix.huutonet.tools.Statics;

@Component(value = "sellerExtractor")
public class SellerExtractor {

	public static final Logger logger = Logger.getLogger(ProductHandler.class);

	@Autowired
	private SellerDAO sellerDAO;
	
	@Autowired
	private MessageDAO messageDAO;
	
	public Seller getSeller(HttpServletRequest req) {
		/* NOTE: This code requires apache to be configured with "ProxyPreserveHost On" (Another
		 * possibility would be to use the header "x-forwarded-server" or "x-forwarded-host"
		 * (apache adds these, see also http://httpd.apache.org/docs/2.2/mod/mod_proxy.html#x-headers).
		 * 
		 * This code has a problem:
		 * 1.) if the customer goes to "http://gardenia.fi/webstore/hkmyynti/seller/", then
		 *     req.getServerName() = "gardenia.fi" (same goes for "x-forwarded-host", however
		 *     "x-forwarded-server" shows "www.gardenia.fi".
		 * 2.) if the customer goes to "http://www.hk-myynti.fi/webstore/hkmyynti/seller/", then
		 *     req.getServerName() = "gardenia.fi" (same goes for "x-forwarded-host" and "x-forwarded-server") */
		String backendServerName = "http://" + req.getServerName();
		logger.debug("backendServerName = \"" + backendServerName + "\"");
		
		List<Seller> sellers = sellerDAO.loadAll();
		logger.debug("sellers.size = " + sellers.size());
		for (Seller seller : sellers) {
			logger.debug("seller.store = \"" + seller.getStoreUrl() + "\", backendServerName = \"" + backendServerName + "\"");
			if (seller.getStoreUrl().startsWith(backendServerName)) {
				return seller;
			}
		}
		logger.error("Cannot figure out seller for " + backendServerName);
		throw new RuntimeException("Cannot figure out seller.");
		
		/* For some reason version tags/prod20120702 (which uses domain version 0.0.20) stops working
		 * after some time. Right after deployment it works fine, but then after some time the above
		 * call sellerDAO.loadAll() only returns ONE item, namely the first seller (TeamLauber). I haven't
		 * figured out what this comes from. We could possibly try to revert to using version 0.0.19
		 * of domain and see if that works better. For the time being we resort to the following workaround.
		 * => The problem might have been that version tags/prod20120702 contained a dependency to
		 * domain 0.0.20 and logic 0.0.3 which in turn contained a dependency to domain 0.0.18, so that
		 * two versions of domain was in use. I published a new version of logic (0.0.4) which has a
		 * dependency to domain 0.0.20 and if this problem doeesn't come any more, then we can conclude
		 * that the problem is solved.
		 * => UPDATE: I THINK the problem was that version 0.0.20 of domain used "hibernateTemplate.setMaxResults(oldMaxResults)"
		 * which caused problems when two requests were received in rapid succession. On
		 * 21.7.2012 I released a new version of domain (0.0.21) + logic 0.0.5 (which depends on
		 * domain 0.0.21). The new version of domain rather sets setMaxResults(0). Hopefully
		 * this solves the problem. */
		/*Seller seller = sellerDAO.get(2L);
		if (seller == null) {
			throw new RuntimeException("Cannot figure out seller.");
		} else {
			logger.warn("Using default seller 2 (" + seller.getStoreUrl() + ")");
			return seller;
		}*/
	}
	
	public void addSellerInfo(HttpServletRequest req, ModelAndView mav) {
		Seller seller = getSeller(req);
		mav.addObject("sellerName", seller.getSignature());
		mav.addObject("sellerUrl", seller.getStoreUrl());
		String contextPath = messageDAO.getText("contextPath", seller,Statics.FINNISH_LANGUAGE);
		mav.addObject("sellerContextPath", contextPath);
	}
	
	public ModelAndView redirectToBase(HttpServletRequest req) {
		Seller seller = getSeller(req);
		String contextPath = messageDAO.getText("contextPath", seller,Statics.FINNISH_LANGUAGE);
		ModelAndView mav = new ModelAndView("redirect:" + seller.getStoreUrl() + contextPath + "/");
		return mav;
	}
	
	public String getBaseUrl(HttpServletRequest req) {
		Seller seller = getSeller(req);
		String contextPath = messageDAO.getText("contextPath", seller,Statics.FINNISH_LANGUAGE);
		return seller.getStoreUrl() + contextPath;
	}
	
}
