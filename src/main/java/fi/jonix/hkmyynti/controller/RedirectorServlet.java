package fi.jonix.hkmyynti.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import fi.jonix.huutonet.domain.model.Ad;
import fi.jonix.huutonet.domain.model.AdTemplate;
import fi.jonix.huutonet.domain.model.Market;
import fi.jonix.huutonet.domain.model.Seller;
import fi.jonix.huutonet.domain.model.dao.AdDAO;
import fi.jonix.huutonet.domain.model.dao.AdTemplateDAO;
import fi.jonix.huutonet.domain.model.dao.MarketDAO;
import fi.jonix.huutonet.domain.model.dao.MessageDAO;
import fi.jonix.huutonet.domain.model.dao.SellerDAO;
import fi.jonix.huutonet.tools.Statics;

public class RedirectorServlet extends HttpServlet {

	public static final Logger logger = Logger.getLogger(RedirectorServlet.class);
	
	private ApplicationContext applicationContext;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.debug("Initiating RedirectorServlet");
		applicationContext = ContextLoader.getCurrentWebApplicationContext();
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req,resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.debug("Redirect for pathInfo \"" + req.getPathInfo() + "\" (contextPath = " + req.getContextPath() + ").");
		//SellerExtractor sellerExtractor = (SellerExtractor) applicationContext.getBean("sellerExtractor");
		//logger.debug("sellerExtractor = " + sellerExtractor);
		String[] split = req.getPathInfo().split("/");
		String type = split[1];
		Long sellerId = Long.parseLong(split[2]);
		Long marketId = Long.parseLong(split[3]);
		Long adTemplateId = Long.parseLong(split[4]);
		logger.debug("type = " + type + ", sellerId = " + sellerId + ", marketId = " + marketId + ", adTemplateId = " + adTemplateId);

		/* Find out the latest ad for this adTemplate. */
		MarketDAO marketDAO = (MarketDAO) applicationContext.getBean("marketDAO");
		Market market = marketDAO.get(marketId);
		AdTemplateDAO adTemplateDAO = (AdTemplateDAO) applicationContext.getBean("adTemplateDAO");
		AdTemplate adTemplate = adTemplateDAO.get(adTemplateId);
		AdDAO adDAO = (AdDAO) applicationContext.getBean("adDAO");
		SellerDAO sellerDAO = (SellerDAO) applicationContext.getBean("sellerDAO");
		Seller seller = sellerDAO.get(sellerId);
		Ad ad = null;
		if (adTemplate != null) {
			ad = adDAO.getLatestAdByProductAndSellerAndMarket(adTemplate.getProduct(),market,seller);
		}
		
		boolean forwarded = false;
		if (ad != null) {
			if (type.equals("newOrder")) {
				logger.debug("Forwarding (marketSalesId = " + ad.getMarketSalesId() + ")");
				//resp.sendRedirect(req.getContextPath() + "/seller/newOrder?marketSalesId=" + ad.getMarketSalesId() + "&marketId=" + market.getId());
				resp.sendRedirect(getBaseUrl(req) + "/newOrder?marketSalesId=" + ad.getMarketSalesId() + "&marketId=" + market.getId());
				forwarded = true;
			} else if (type.equals("bounceToMarket")) {
				logger.debug("Bouncing to latest ad for adTemplate " + adTemplate.getId()
						+ " (marketSalesId = " + ad.getMarketSalesId() + ")");
				if (market.getName().equals(Market.HUUTONET_MARKET_NAME)) {
					resp.sendRedirect("http://www.huuto.net/kohteet/" + ad.getMarketSalesId());
					forwarded = true;
				} else {
					logger.error("Cannot do bouncing for market " + market.getName() + ".");
				}
			} else {
				logger.warn("Unknown type, cannot redirect (" + type + ").");
			}
		} else {
			logger.debug("Cannot redirect because ad is null" + (adTemplate != null ? " (adTemplate " + adTemplate.getId() + ")." : "."));
		}
		if (!forwarded) {
			logger.debug("Wrong parameters, forwarding to root");
			//resp.sendRedirect(req.getContextPath() + "/seller/");
			resp.sendRedirect(getBaseUrl(req) + "/");
		}
	}
	
	//TODO: this is just copy'n'pasted from SellerExtractor. The servlet configuration
	//should be changed so that this servlet can access the SellerExtractor straight
	//from the application context. Or even better: change the redirector to a normal
	//controller and skip the servlet stuff altogether. => This cannot be turned into
	//a normal controller, because huutonet doesn't allow parameters to be passed
	//through links on the ad. That's the whole point of passing them as path info
	//information instead (e.g. ".../newOrder/2/1/3376") - only a servlet can handle
	//this.
	public Seller getSeller(HttpServletRequest req) {
		String backendServerName = "http://" + req.getServerName();
		logger.debug("backendServerName = \"" + backendServerName + "\"");
		SellerDAO sellerDAO = (SellerDAO) applicationContext.getBean("sellerDAO");
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
	
	public String getBaseUrl(HttpServletRequest req) {
		Seller seller = getSeller(req);
		MessageDAO messageDAO = (MessageDAO) applicationContext.getBean("messageDAO");
		String contextPath = messageDAO.getText("contextPath", seller,Statics.FINNISH_LANGUAGE);
		return seller.getStoreUrl() + contextPath;
	}
	
}
