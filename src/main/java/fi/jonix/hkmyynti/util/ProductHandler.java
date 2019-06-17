package fi.jonix.hkmyynti.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.huutonet.domain.model.Ad;
import fi.jonix.huutonet.domain.model.Image;
import fi.jonix.huutonet.domain.model.Market;
import fi.jonix.huutonet.domain.model.dao.AdDAO;
import fi.jonix.huutonet.domain.model.dao.MarketDAO;

@Component(value = "productHandler")
public class ProductHandler {

	public static final Logger logger = Logger.getLogger(ProductHandler.class);

	@Autowired
	private AdDAO adDAO;
	
	@Autowired
	private MarketDAO marketDAO;
	
	public void addOrderInfoDataForProduct(ModelAndView mav, String marketSalesId, Long marketId) {
		Market market = marketDAO.get(marketId);
		Ad ad = adDAO.getByMarketSalesIdWithoutSeller(marketSalesId, market);
		if (ad != null) {
			logger.info("ad.id = " + ad.getId() + " found");
			//TODO: remove this hardcoding and start using MarketRobot.getUrlForMarketSalesId instead
			if (market.getId().intValue() == 1) {
				mav.addObject("adUrl", "http://www.huuto.net/kohteet/" + ad.getMarketSalesId());
			} else if (market.getId().intValue() == 4) {
				mav.addObject("adUrl", "http://www.mikko.fi/kohde/" + ad.getMarketSalesId());
			}
			//String headline = ad.getAdTemplate().getHeadline();
			//logger.info("Setting headline: " + headline);
			mav.addObject("adTemplateHeadline", ad.getAdTemplate().getHeadline());
			List<Image> imageList = ad.getAdTemplate().getProduct().getImagesInOrder();
			if (imageList != null && imageList.size() > 0) {
				Image image = imageList.get(0);
				mav.addObject("imageUrl", image.getImageHostUrl());
			}
		}
	}

}
