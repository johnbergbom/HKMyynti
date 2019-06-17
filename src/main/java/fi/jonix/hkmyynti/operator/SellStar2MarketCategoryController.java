package fi.jonix.hkmyynti.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.commandobject.CategoryChoice;
import fi.jonix.hkmyynti.commandobject.CategoryChoices;
import fi.jonix.hkmyynti.operator.CategoryAssignmentController.CategoryRadioButtonEntry;
import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.huutonet.domain.model.AdTemplate;
import fi.jonix.huutonet.domain.model.Category;
import fi.jonix.huutonet.domain.model.CategoryMapping;
import fi.jonix.huutonet.domain.model.Market;
import fi.jonix.huutonet.domain.model.Product;
import fi.jonix.huutonet.domain.model.SellerMarket;
import fi.jonix.huutonet.domain.model.dao.AdTemplateDAO;
import fi.jonix.huutonet.domain.model.dao.CategoryDAO;
import fi.jonix.huutonet.domain.model.dao.CategoryMappingDAO;
import fi.jonix.huutonet.domain.model.dao.MarketDAO;
import fi.jonix.huutonet.domain.model.dao.SellerMarketDAO;

@Controller
@RequestMapping("/operator/sellStar2MarketCategories")
public class SellStar2MarketCategoryController {

	public static final Logger logger = Logger.getLogger(SellStar2MarketCategoryController.class);

	public static Map<String,List<CategoryRadioButtonEntry>> allCategories = new HashMap<String,List<CategoryRadioButtonEntry>>();
	public static Map<String,Long> allCategoriesTimestamp = new HashMap<String,Long>();
	public static Map<String,List<Long>> missingSellStarCategories = new HashMap<String,List<Long>>();
	//public static List<Long> missingSellStarCategories = new ArrayList<Long>();
	public static long missingSellStarCategoriesTimestamp = -1;

	@Autowired
	private SellerMarketDAO sellerMarketDAO;
	
	@Autowired
	private AdTemplateDAO adTemplateDAO;
	
	@Autowired
	private CategoryMappingDAO categoryMappingDAO;
	
	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private MarketDAO marketDAO;
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	private void generateCategoryList(Category root, List<CategoryRadioButtonEntry> list) {
		List<Category> children = categoryDAO.getChildren(root);
		if (children.size() > 0) {
			for (Category category : children) {
				generateCategoryList(category, list);
			}
		} else {
			String path = CategoryAssignmentController.extractPath(CategoryAssignmentController.getWholePathFromRootFor(root));
			/* Remove all K-18 categories from the list.
			 * TODO: fix access rights so that certain admins can access these categories. */
			if (path.indexOf("/K-18") < 0) {
				list.add(new CategoryRadioButtonEntry(root.getId(),path,null,null));
			}
		}
	}

	@RequestMapping
	public ModelAndView showForm(HttpServletRequest req) {
		ModelAndView mav = new ModelAndView("operator/index");
		mav.addObject("whichPage", "sellStar2MarketCategories");
		sellerExtractor.addSellerInfo(req, mav);

		/* Extract the SellStar categories that are not mapped to any market specific categories. */
		if (missingSellStarCategoriesTimestamp == -1
				|| (System.currentTimeMillis() - (1000*3600)) > missingSellStarCategoriesTimestamp) {
			logger.debug("Finding used and unmapped SellStar categories.");
			missingSellStarCategories.clear();
			/* Find products in such SellStar categories that aren't mapped to any
			 * market specific categories. */
			List<SellerMarket> sellerMarkets = sellerMarketDAO.loadAll();
			for (SellerMarket sellerMarket : sellerMarkets) {
				if (!sellerMarket.getDoListing()) {
					continue;
				}
				String marketName = sellerMarket.getMarket().getName();
				List<AdTemplate> adTemplates = adTemplateDAO.findBySeller(sellerMarket.getSeller());
				for (AdTemplate adTemplate : adTemplates) {
					if (Boolean.FALSE.equals(adTemplate.getActive())) {
						continue;
					}
					Category sellStarCategory = adTemplate.getProduct().getCategory();
					if (sellStarCategory == null || (missingSellStarCategories.get(marketName) != null
							&& missingSellStarCategories.get(marketName).contains(sellStarCategory.getId()))) {
						continue;
					}
					List<Category> marketCategoryList =
						categoryMappingDAO.getCategoryMappingsForMarket(sellStarCategory, sellerMarket.getMarket());
					if (marketCategoryList.isEmpty()) {
						logger.debug("SellStar category " + sellStarCategory.getId() + " isn't mapped to any"
								+ " category at " + marketName);
						if (missingSellStarCategories.get(marketName) == null) {
							missingSellStarCategories.put(marketName, new ArrayList<Long>());
						}
						missingSellStarCategories.get(marketName).add(sellStarCategory.getId());
					}
				}
			}
			missingSellStarCategoriesTimestamp = System.currentTimeMillis();
			logger.debug("Done finding used and unmapped SellStar categories.");
		}
		
		/* Pick the next missing mapping. */
		Iterator<String> iter = missingSellStarCategories.keySet().iterator();
		while (iter.hasNext()) {
			String marketName = iter.next();
			List<Long> list = missingSellStarCategories.get(marketName);
			if (list.size() > 0) {
				Category sellStarCategory = categoryDAO.get(list.get(0));
				String path = CategoryAssignmentController.extractPath
					(CategoryAssignmentController.getWholePathFromRootFor(sellStarCategory));
				mav.addObject("sellStarCategoryPath", path);
				mav.addObject("sellStarCategoryId", sellStarCategory.getId());
				mav.addObject("marketName", marketName);
				Category rootCategory = categoryDAO.getChild(null, marketName);
				List<CategoryRadioButtonEntry> categoryList = new ArrayList<CategoryRadioButtonEntry>();
				if (allCategoriesTimestamp.get(marketName) == null
						|| (System.currentTimeMillis() - (1000*3600)) > allCategoriesTimestamp.get(marketName)) {
					logger.debug("Fetching all categories of " + marketName + " from database.");
					generateCategoryList(rootCategory,categoryList);
					allCategoriesTimestamp.put(marketName,System.currentTimeMillis());
					allCategories.put(marketName,categoryList);
					logger.debug("Done fetching from database.");
				} else {
					logger.debug("Fething cached categories.");
					categoryList = allCategories.get(marketName);
					logger.debug("Done fetching cached categories.");
				}
				mav.addObject("categories", categoryList);
				CategoryChoices cc = new CategoryChoices();
				mav.addObject("commandObj", cc);
				return mav;
			}
		}
		
		logger.debug("No unmapped categories found.");
		mav.addObject("sellStarCategoryPath", "No unmapped categories found.");
		return mav;
	}
	
	private boolean contains(Long[] longArray, long item) {
		for (Long id : longArray) {
			if (id.equals(item)) {
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute("commandObj") CategoryChoices commandObj,
			BindingResult result, HttpServletRequest req,
			@RequestParam(value = "sellStarCategoryId", required = true) long sellStarCategoryId,
			@RequestParam(value = "marketName", required = true) String marketName) {
		logger.debug("CategoryAssignmentController.post");
		logger.debug("sellStarCategoryId = " + sellStarCategoryId);
		logger.debug("marketName = " + marketName);
		logger.debug("commandObj.getCategoryIds() = " + commandObj.getCategoryIds());
		logger.debug("commandObj.getCategoryIds().length = " + commandObj.getCategoryIds().length);
		for (Long categoryId : commandObj.getCategoryIds()) {
			logger.debug("categoryId = " + categoryId);
		}
		Category sellStarCategory = categoryDAO.get(sellStarCategoryId);
		String path = CategoryAssignmentController.extractPath
			(CategoryAssignmentController.getWholePathFromRootFor(sellStarCategory));
		Market market = marketDAO.getByName(marketName);
		logger.debug("Storing mappings for " + path + " and " + marketName);

		/* Remove deleted mappings. */
		List<CategoryMapping> mappings = categoryMappingDAO.getCategoryMappings(sellStarCategory);
		for (CategoryMapping mapping : mappings) {
			if (mapping.getCategory1().getMarket().getId().equals(market.getId())
					&& contains(commandObj.getCategoryIds(),mapping.getCategory1().getId())) {
				String p = CategoryAssignmentController.extractPath
					(CategoryAssignmentController.getWholePathFromRootFor(mapping.getCategory1()));
				logger.debug("Mapping " + path + " to " + p + " already exists (" + sellStarCategoryId
						+ "<->" + mapping.getCategory1().getId() + ").");
			} else if (mapping.getCategory2().getMarket().getId().equals(market.getId())
					&& contains(commandObj.getCategoryIds(),mapping.getCategory2().getId())) {
				String p = CategoryAssignmentController.extractPath
					(CategoryAssignmentController.getWholePathFromRootFor(mapping.getCategory2()));
				logger.debug("Mapping " + path + " to " + p + " already exists (" + sellStarCategoryId
						+ "<->" + mapping.getCategory2().getId() + ").");
			} else if ((mapping.getCategory1().getMarket().getId().equals(market.getId())
					&& !contains(commandObj.getCategoryIds(),mapping.getCategory1().getId()))
					|| (mapping.getCategory2().getMarket().getId().equals(market.getId())
							&& !contains(commandObj.getCategoryIds(),mapping.getCategory2().getId()))) {
				String p = null;
				if (mapping.getCategory1().getMarket().getId().equals(market.getId())) {
					p = CategoryAssignmentController.extractPath
						(CategoryAssignmentController.getWholePathFromRootFor(mapping.getCategory1()));
				} else {
					p = CategoryAssignmentController.extractPath
						(CategoryAssignmentController.getWholePathFromRootFor(mapping.getCategory2()));
				}
				logger.debug("Removing mapping " + mapping.getId() + " (" + path + " to " + p + ").");
				categoryMappingDAO.delete(mapping);
			}
		}

		/* Add new mappings. */
		for (Long categoryId : commandObj.getCategoryIds()) {
			boolean exists = false;
			for (CategoryMapping mapping : mappings) {
				if (mapping.getCategory1().getMarket().getId().equals(market.getId())
						&& categoryId.equals(mapping.getCategory1().getId())) {
					exists = true;
				} else if (mapping.getCategory2().getMarket().getId().equals(market.getId())
						&& categoryId.equals(mapping.getCategory2().getId())) {
					exists = true;
				}
			}
			Category category = categoryDAO.get(categoryId);
			String p = CategoryAssignmentController.extractPath
			(CategoryAssignmentController.getWholePathFromRootFor(category));
			if (!exists) {
				CategoryMapping cm = new CategoryMapping();
				cm.setCategory1(sellStarCategory);
				cm.setCategory2(category);
				categoryMappingDAO.save(cm);
				logger.debug("Added mapping " + cm.getId() + " (" + path + " to " + p + ").");
			} else {
				logger.debug("Mapping " + path + " to " + p + " already exists (" + sellStarCategoryId
						+ "<->" + categoryId + ").");
			}
		}
		List<Long> list = missingSellStarCategories.get(marketName);
		if (list.contains(sellStarCategoryId)) {
			list.remove(sellStarCategoryId);
		}
		
		//String redirect = "redirect:sellStar2MarketCategories";
		String redirect = "redirect:" + sellerExtractor.getBaseUrl(req) + "/operator/sellStar2MarketCategories";
		logger.debug("SellStar2MarketCategoryController.post - redirecting to " + redirect);
		ModelAndView mav = new ModelAndView(redirect);
		return mav;
	}
	
}
