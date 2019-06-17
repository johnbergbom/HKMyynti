package fi.jonix.hkmyynti.operator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.huutonet.domain.model.AdTemplate;
import fi.jonix.huutonet.domain.model.Category;
import fi.jonix.huutonet.domain.model.Image;
import fi.jonix.huutonet.domain.model.Market;
import fi.jonix.huutonet.domain.model.Product;
import fi.jonix.huutonet.domain.model.dao.AdTemplateDAO;
import fi.jonix.huutonet.domain.model.dao.CategoryDAO;
import fi.jonix.huutonet.domain.model.dao.ProductDAO;

@Controller
@RequestMapping("/operator/assignCategories")
public class CategoryAssignmentController {

	public static final Logger logger = Logger.getLogger(CategoryAssignmentController.class);

	public static long NONE_OF_THESE = -1;
	public static long DECIDE_LATER = -2;
	public static long SHOW_MORE_ALTERNATIVES = -3;
	public static long SHOW_ALL_ALTERNATIVES = -4;

	public static int VIEW_TYPE_FEW = 1;
	public static int VIEW_TYPE_MORE = 2;
	public static int VIEW_TYPE_ALL = 3;
	
	public static String HARDNESS_FIRST = "FIRST";
	public static String HARDNESS_SECOND = "SECOND";
	public static String HARDNESS_THIRD = "THIRD";
	
	public static List<CategoryRadioButtonEntry> allCategories = new ArrayList<CategoryRadioButtonEntry>();
	public static long allCategoriesTimestamp = -1;

	//@Autowired
	//private ProductDAO productDAO;
	
	@Autowired
	private AdTemplateDAO adTemplateDAO;
	
	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private ProductDAO productDAO;
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	public static class CategoryRadioButtonEntry {
		private long category;
		private String path;
		private Double score;
		private String cssClass;
		
		public CategoryRadioButtonEntry(long category, String path, Double score, String cssClass) {
			this.category = category;
			this.path = path;
			this.score = score;
			if (cssClass == null) {
				this.cssClass = "";
			} else {
				this.cssClass = cssClass;
			}
		}
		public void setCategory(long category) {
			this.category = category;
		}
		public long getCategory() {
			return category;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String getPath() {
			return path;
		}
		public void setScore(Double score) {
			this.score = score;
		}
		public Double getScore() {
			return score;
		}
		public void setCssClass(String cssClass) {
			this.cssClass = cssClass;
		}
		public String getCssClass() {
			return cssClass;
		}
		public String getLabel() {
			if (score == null) {
				return path;
			} else {
				DecimalFormat df = new DecimalFormat("0.##");
				return path + " [score: " + df.format(score) + "]";
			}
		}
	}
	
	private List<CategoryRadioButtonEntry> generateCategoryList(Product product, ModelAndView mav, int viewType, CategoryChoice cc) {
		/* Start by parsing the category suggestions. */
		List<Long> listHeadLineAndProvCatForLocalized = new ArrayList<Long>();
		List<Long> listHeadlineAndSellStarCat = new ArrayList<Long>();
		List<Long> listHeadLineAndProvCatForInternational = new ArrayList<Long>();
		List<Long> listAnyAdTemplateHeadlineForLocalized = new ArrayList<Long>();
		List<Long> listAnyAdTemplateHeadlineForInternational = new ArrayList<Long>();
		List<Long> providerCategory2SellstarCategory = new ArrayList<Long>();
		String suggestions = product.getCategorySuggestion();
		String hardness = suggestions.substring(0,suggestions.indexOf(";"));
		mav.addObject("hardness", "hardness." + hardness.toLowerCase());
		suggestions = suggestions.substring(hardness.length() + 1);
		StringTokenizer typeST = new StringTokenizer(suggestions,";");
		while (typeST.hasMoreElements()) {
			String type = typeST.nextToken();
			//logger.debug("type = " + type);
			String klass = type.substring(0,type.indexOf(":"));
			//logger.debug("klass = " + klass);
			String data = type.substring(klass.length()+1);
			//logger.debug("data = " + data);
			List<Long> list = new ArrayList<Long>();
			StringTokenizer catST = new StringTokenizer(data,",");
			while (catST.hasMoreElements()) {
				String catId = catST.nextToken();
				//logger.debug("catId = " + catId);
				//list.add(categoryDAO.get(Long.parseLong(catId)));
				list.add(Long.parseLong(catId));
			}
			if (klass.equals("listHeadLineAndProvCatForLocalized")) {
				listHeadLineAndProvCatForLocalized.addAll(list);
			} else if (klass.equals("listHeadLineAndProvCatForInternational")) {
				listHeadLineAndProvCatForInternational.addAll(list);
			} else if (klass.equals("providerCategory2SellstarCategory")) {
				providerCategory2SellstarCategory.addAll(list);
			} else if (klass.equals("listHeadlineAndSellStarCat")) {
				listHeadlineAndSellStarCat.addAll(list);
			} else if (klass.equals("listAnyAdTemplateHeadlineForLocalized")) {
				listAnyAdTemplateHeadlineForLocalized.addAll(list);
			} else if (klass.equals("listAnyAdTemplateHeadlineForInternational")) {
				listAnyAdTemplateHeadlineForInternational.addAll(list);
			} else {
				throw new RuntimeException("Unknown class " + klass);
			}
		}
		
		/*logger.info("listHeadLineAndProvCatForLocalized.size = " + listHeadLineAndProvCatForLocalized.size());
		logger.info("listHeadLineAndProvCatForInternational.size = " + listHeadLineAndProvCatForInternational.size());
		logger.info("providerCategory2SellstarCategory.size = " + providerCategory2SellstarCategory.size());
		logger.info("listHeadlineAndSellStarCat.size = " + listHeadlineAndSellStarCat.size());
		logger.info("listAnyAdTemplateHeadlineForLocalized.size = " + listAnyAdTemplateHeadlineForLocalized.size());
		logger.info("listAnyAdTemplateHeadlineForInternational.size = " + listAnyAdTemplateHeadlineForInternational.size());*/

		/* Then assign scores to different alternatives. Relatively, the goodness of different classes is probably as listed (from good to bad):
		 * 1.) listHeadLineAndProvCatForLocalized
		 * 2.) listHeadlineAndSellStarCat
		 * 3.) listHeadLineAndProvCatForInternational
		 * 4.) listAnyAdTemplateHeadlineForLocalized
		 * 5.) listAnyAdTemplateHeadlineForInternational
		 * 6.) providerCategory2SellstarCategory
		 * 
		 * More points are given to better lists + more points for categories that are found
		 * in several lists + more points are given to categories that are in the beginning
		 * of each list. */
		Map<Long,Double> categoryPoints = new HashMap<Long,Double>();
		for (int i = 0; i < listHeadLineAndProvCatForLocalized.size()/* && i < 10*/; i++) {
			Long id = listHeadLineAndProvCatForLocalized.get(i);
			double extra = 0.6 + (10 - i) / 10;
			categoryPoints.put(id, extra);
		}
		for (int i = 0; i < listHeadlineAndSellStarCat.size()/* && i < 10*/; i++) {
			Long id = listHeadlineAndSellStarCat.get(i);
			double extra = 0.5 + (10 - i) / 10;
			if (categoryPoints.get(id) == null) {
				categoryPoints.put(id, extra);
			} else {
				categoryPoints.put(id, categoryPoints.get(id) + extra);
			}
		}
		for (int i = 0; i < listHeadLineAndProvCatForInternational.size()/* && i < 10*/; i++) {
			Long id = listHeadLineAndProvCatForInternational.get(i);
			double extra = 0.4 + (10 - i) / 10;
			if (categoryPoints.get(id) == null) {
				categoryPoints.put(id, extra);
			} else {
				categoryPoints.put(id, categoryPoints.get(id) + extra);
			}
		}
		for (int i = 0; i < listAnyAdTemplateHeadlineForLocalized.size()/* && i < 10*/; i++) {
			Long id = listAnyAdTemplateHeadlineForLocalized.get(i);
			double extra = 0.3 + (10 - i) / 10;
			if (categoryPoints.get(id) == null) {
				categoryPoints.put(id, extra);
			} else {
				categoryPoints.put(id, categoryPoints.get(id) + extra);
			}
		}
		for (int i = 0; i < listAnyAdTemplateHeadlineForInternational.size()/* && i < 10*/; i++) {
			Long id = listAnyAdTemplateHeadlineForInternational.get(i);
			double extra = 0.2 + (10 - i) / 10;
			if (categoryPoints.get(id) == null) {
				categoryPoints.put(id, extra);
			} else {
				categoryPoints.put(id, categoryPoints.get(id) + extra);
			}
		}
		for (int i = 0; i < providerCategory2SellstarCategory.size()/* && i < 10*/; i++) {
			Long id = providerCategory2SellstarCategory.get(i);
			double extra = 0.1 + (10 - i) / 10;
			if (categoryPoints.get(id) == null) {
				categoryPoints.put(id, extra);
			} else {
				categoryPoints.put(id, categoryPoints.get(id) + extra);
			}
		}


		List<CategoryRadioButtonEntry> list = new ArrayList<CategoryRadioButtonEntry>();
		if (viewType == VIEW_TYPE_ALL) {
			if (allCategoriesTimestamp == -1 || (System.currentTimeMillis() - (1000*3600)) > allCategoriesTimestamp) {
				logger.debug("Fetching all categories from the database");
				Category root = categoryDAO.getChild(null, Market.SELLSTAR_MARKET_NAME);
				fetchAllCategoriesFromDB(root,list,categoryPoints);
				allCategoriesTimestamp = System.currentTimeMillis();
				allCategories.clear();
				allCategories.addAll(list);
				logger.debug("End of fetching all categories from the database");
			} else {
				logger.debug("Using cached categories.");
				list.addAll(allCategories);
				updateCategoryScores(list,categoryPoints);
				logger.debug("Done updating scores of cached categories.");
			}
			list.add(new CategoryRadioButtonEntry(NONE_OF_THESE,"None of these!",null,null));
			cc.setCategoryId(DECIDE_LATER);
		} else {
			/* Then create the list to return from this method, ordered according to the above scores. */
			while (categoryPoints.size() > 0) {
				Iterator<Long> iter = categoryPoints.keySet().iterator();
				Long maxScoreKey = -1L;
				while (iter.hasNext()) {
					Long key = iter.next();
					double score = categoryPoints.get(key);
					if (maxScoreKey == -1 || score > categoryPoints.get(maxScoreKey)) {
						maxScoreKey = key;
					}
				}
				Category category = categoryDAO.get(maxScoreKey);
				String path = extractPath(getWholePathFromRootFor(category));
				//logger.debug("Adding " + maxScoreKey + " (score = " + categoryPoints.get(maxScoreKey) + ").");
				double points = categoryPoints.get(maxScoreKey);
				list.add(new CategoryRadioButtonEntry(maxScoreKey,path,points,"categoryHasScore"));
				categoryPoints.remove(maxScoreKey);

				/* Print also sibling categories in the following cases:
				 * 1.) viewType == VIEW_TYPE_MORE
				 * 2.) hardness == HARDNESS_FIRST and it's the first alternative in the list OR points >= 2
				 * 3.) it's the first alternative in the list AND points >= 2 */
				if (viewType == VIEW_TYPE_MORE
						|| (hardness.equals(HARDNESS_FIRST) && (list.size() == 1 || points >= 2))
						|| (list.size() == 1 && points >= 2)) {
					for (Category cat : categoryDAO.getChildren(category.getParentCategory())) {
						if (!cat.getId().equals(category.getId())) {
							path = extractPath(getWholePathFromRootFor(cat));
							Double score = categoryPoints.get(cat.getId());
							/* For hardness = HARDNESS_THIRD only add siblings if the score of
							 * the sibling is >= 0.3 */
							if (viewType == VIEW_TYPE_MORE
								|| hardness.equals(HARDNESS_FIRST)
								|| hardness.equals(HARDNESS_SECOND)
								|| (hardness.equals(HARDNESS_THIRD) && score != null && score >= 0.3)) {
								//logger.debug("Adding sibling " + cat.getId() + score);
								if (score != null) {
									list.add(new CategoryRadioButtonEntry(cat.getId(),path,score,"categoryHasScore"));
								} else {
									list.add(new CategoryRadioButtonEntry(cat.getId(),path,score,null));
								}
								categoryPoints.remove(cat.getId());
							}
						}
					}
				}
			}
			//logger.debug("Score for categoryPoints.get(2722) = " + categoryPoints.get(2722));
			//logger.info("returnList.size = " + returnList.size());
			if (viewType == VIEW_TYPE_FEW) {
				list.add(new CategoryRadioButtonEntry(SHOW_MORE_ALTERNATIVES,"Show more alternatives!",null,null));
				cc.setCategoryId(SHOW_MORE_ALTERNATIVES);
			} else {
				cc.setCategoryId(SHOW_ALL_ALTERNATIVES);
			}
			list.add(new CategoryRadioButtonEntry(SHOW_ALL_ALTERNATIVES,"Show all alternatives!",null,null));
		}
		list.add(new CategoryRadioButtonEntry(DECIDE_LATER,"Decide later.",null,null));

		/* Get some statistics of the scores. */
		double bestScore = -98;
		double secondBestScore = -99;
		for (CategoryRadioButtonEntry entry : list) {
			if (entry.getScore() != null && entry.getScore().doubleValue() > bestScore) {
				secondBestScore = bestScore;
				bestScore = entry.getScore();
			} else if (entry.getScore() != null && entry.getScore().doubleValue() == bestScore) {
				secondBestScore = bestScore;
			} else if (entry.getScore() != null && entry.getScore().doubleValue() > secondBestScore) {
				secondBestScore = entry.getScore();
			}
		}
		logger.debug("bestScore = " + bestScore + ", secondBestScore = " + secondBestScore);
		
		/* Calculate a lower limit for what alternatives to list (only applies for
		 * viewType = VIEW_TYPE_FEW). */
		double limit = -99;
		if (hardness.equals(HARDNESS_FIRST)) {
			limit = 0.2;
		} else {
			limit = 0.21;
		}
		/* If at least two alternatives have a score greater than three or if one alternative has
		 * a score greater than five, then remove even more alternatives from the list. */
		if (bestScore > 5 || secondBestScore > 3) {
			limit += 0.3;
		}
		logger.debug("limit = " + limit);

		/* Possibly remove some of the alternatives from the list. */
		double alternativesWithScore = 0;
		List<CategoryRadioButtonEntry> tempList = new ArrayList<CategoryRadioButtonEntry>();
		List<CategoryRadioButtonEntry> tempLimitedList = new ArrayList<CategoryRadioButtonEntry>();
		for (CategoryRadioButtonEntry entry : list) {
			boolean addToTempList = true;
			boolean addToTempLimitedList = true;
			/* Remove all K-18 categories from the list.
			 * TODO: fix access rights so that certain admins can access these categories. */
			if (entry.getPath().startsWith("/SellStar/K-18")) {
				addToTempList = false;
				addToTempLimitedList = false;
			}
			/* Remove entries that have less than a certain number of points for the first
			 * view (that shows the fewest amount of alternatives). */
			if (viewType == VIEW_TYPE_FEW) {
				/* entry.getCategory() < 0 here for SHOW_MORE_ALTERNATIVES, SHOW_ALL_ALTERNATIVES and DECIDE_LATER
				 * entry.getScore() can be null here for extra added sibling entries */
				if (entry.getCategory() >= 0 && entry.getScore() != null) {
					if (entry.getScore() < limit) {
						//logger.debug("Remove entry " + entry.getCategory() + " because its score is just " + entry.getScore());
						addToTempLimitedList = false;
					}
					if (entry.getScore() < 0.2) {
						addToTempList = false;
					}
				}
			}
			if (addToTempLimitedList) {
				tempList.add(entry);
				tempLimitedList.add(entry);
			} else if (addToTempList) {
				tempList.add(entry);
			}
			if (addToTempLimitedList && entry.getScore() != null) {
				alternativesWithScore++;
			}
		}
		
		/* If we have very few alternatives and this one isn't a hardness = HARDNESS_THIRD,
		 * then use the list where not so many alternatives are removed. */
		List<CategoryRadioButtonEntry> returnList = null;
		if (alternativesWithScore < 4 && (hardness.equals(HARDNESS_FIRST) || hardness.equals(HARDNESS_SECOND))) {
			logger.debug("Use the longer list because we only had " + alternativesWithScore + " alternatives with a score.");
			returnList = tempList; //don't remove too much if we have very few alternatives
		} else {
			logger.debug("Use the more limited list ( " + alternativesWithScore + " alternatives with a score).");
			returnList = tempLimitedList;
		}

		/* Set the default choice to the first alternative if it has a score of at least
		 * four AND the second best alternative has a score of at least three points less. */
		if (viewType == VIEW_TYPE_FEW && returnList.size() >= 4) {
			//Double firstScore = returnList.get(0).getScore();
			//Double secondScore = returnList.get(1).getScore();
			if (bestScore >= 4 && (bestScore - secondBestScore) >= 3) {
				cc.setCategoryId(returnList.get(0).getCategory());
			}
		}

		return returnList;
	}
	
	private void fetchAllCategoriesFromDB(Category root, List<CategoryRadioButtonEntry> list, Map<Long,Double> categoryPoints) {
		List<Category> children = categoryDAO.getChildren(root);
		if (children.size() > 0) {
			for (Category category : children) {
				fetchAllCategoriesFromDB(category, list, categoryPoints);
			}
		} else {
			String path = extractPath(getWholePathFromRootFor(root));
			Double score = categoryPoints.get(root.getId());
			//logger.debug("Adding sibling " + root.getId() + score);
			//logger.debug("All: categoryPoints.get " + root.getId() + " = " + categoryPoints.get(root.getId()));
			//logger.debug("All: score = " + score);
			if (categoryPoints.get(root.getId()) != null) {
				list.add(new CategoryRadioButtonEntry(root.getId(),path,score,"categoryHasScore"));
			} else {
				list.add(new CategoryRadioButtonEntry(root.getId(),path,score,null));
			}
		}
	}
	
	private void updateCategoryScores(List<CategoryRadioButtonEntry> list, Map<Long,Double> categoryPoints) {
		//public CategoryRadioButtonEntry(long category, String label, String cssClass) {
		for (CategoryRadioButtonEntry entry : list) {
			Double score = categoryPoints.get(entry.getCategory());
			//logger.debug("Update: categoryPoints.get " + entry.getCategory() + " = " + categoryPoints.get(entry.getCategory()));
			//logger.debug("Update: score = " + score);
			if (categoryPoints.get(entry.getCategory()) != null) {
				entry.setCssClass("categoryHasScore");
			} else {
				entry.setCssClass(null);
			}
			entry.setScore(score);
		}
	}
	
	//TODO: this method is just copy'n'pasted from CategoryHelper.java - refactor!
	public static List<Category> getWholePathFromRootFor(Category category) {
		List<Category> list = new ArrayList<Category>();
		Category p = category;
		list.add(p);
		while (p.getParentCategory() != null) {
			p = p.getParentCategory();
			list.add(p);
		}
		Collections.reverse(list);
		return list;
		
	}
	
	//TODO: this method is just copy'n'pasted from CategoryHelper.java - refactor!
	public static String extractPath(List<Category> path) {
		StringBuffer strBuf = new StringBuffer();
		if (path.isEmpty()) {
			strBuf.append("/");
		} else {
			for (Category category : path) {
				strBuf.append("/" + category.getName());
			}
		}
		return strBuf.toString();
	}

	private List<String> array2List(String stringWithNewLines) {
		List<String> list = new ArrayList<String>();
		if (stringWithNewLines != null) {
			for (String oneLine : stringWithNewLines.split("\n")) {
				list.add(oneLine);
			}
		}
		return list;
	}
	
	/**
	 * @param number Number starting from 1 (and not 0).
	 * @return
	 */
	@RequestMapping
	public ModelAndView showForm(@RequestParam(required = false) Integer number,
			@RequestParam(required = false) Integer viewType,
			HttpServletRequest req) {
		ModelAndView mav = new ModelAndView("operator/index");
		sellerExtractor.addSellerInfo(req, mav);
		mav.addObject("whichPage", "categoryAssignment");
		mav.addObject("information", "CategoryAssignmentController");
		logger.debug("showForm.number = " + number);
		logger.debug("showForm.viewType = " + viewType);
		if (viewType == null) {
			// By default show only the categories in the suggestions
			viewType = VIEW_TYPE_FEW;
		}

		/* Find out how many products there are for which categories can be defined. */
		//TODO: place this in a lower level for which a transaction is defined
		List<AdTemplate> adTemplateList = new ArrayList<AdTemplate>();
		for (AdTemplate adTemplate : adTemplateDAO.getAdTemplatesWithAssignableCategory()) {
			if (!adTemplate.getProduct().getCategorySuggestion().startsWith("TOO_HARD;")) {
				adTemplateList.add(adTemplate);
			}
		}
		mav.addObject("nbrProducts", adTemplateList.size());
		if (number == null) {
			number = 1;
		}
		if (adTemplateList.size() >= number) {
			AdTemplate adTemplate = adTemplateList.get(number-1);
			mav.addObject("adTemplateId", adTemplate.getId());
			mav.addObject("productId", adTemplate.getProduct().getId());
			mav.addObject("headline", adTemplate.getHeadline());
			mav.addObject("headlineOrig", adTemplate.getProduct().getName());
			mav.addObject("technicalSpecs", array2List(adTemplate.getTechnicalSpecs()));
			mav.addObject("technicalSpecsOrig", array2List(adTemplate.getProduct().getTechnicalSpecs()));
			mav.addObject("details", adTemplate.getDetails());
			mav.addObject("detailsOrig", adTemplate.getProduct().getContents());
			mav.addObject("providerCategory", adTemplate.getProduct().getProviderCategory());
			mav.addObject("providerProductUrl", adTemplate.getProduct().getProviderUrl());
			CategoryChoice cc = new CategoryChoice();
			mav.addObject("number", number);
			mav.addObject("viewType", viewType);
			//cc.setCategoryId(SHOW_MORE_ALTERNATIVES);
			mav.addObject("commandObj", cc);
			List<CategoryRadioButtonEntry> categoryList = generateCategoryList(adTemplate.getProduct(),mav,viewType,cc);
			mav.addObject("categories", categoryList);
			List<String> imageUrlList = new ArrayList<String>();
			boolean nonThumbnailsFound = false;
			List<Image> images = adTemplate.getProduct().getImagesInOrder();
			for (Image image : images) {
				if (image.getThumbnailImageId() != null) { //don't use thumbnails
					imageUrlList.add(image.getImageHostUrl());
					nonThumbnailsFound = true;
				}
			}
			if (!nonThumbnailsFound) {
				/* Use the thumbnails if no big images were found. */
				for (Image image : images) {
					imageUrlList.add(image.getImageHostUrl());
				}
			}
			mav.addObject("imageUrlList", imageUrlList);
		}
		return mav;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute("commandObj") CategoryChoice commandObj,
			BindingResult result, HttpServletRequest req, @RequestParam(value = "adTemplateId", required = true) long adTemplateId,
			@RequestParam(value = "number", required = true) int number,
			@RequestParam(required = true) int viewType) {
		logger.debug("CategoryAssignmentController.post");
		logger.debug("categoryId = " + commandObj.getCategoryId());
		logger.debug("adTemplateId = " + adTemplateId);
		logger.debug("number = " + number);
		//String redirect = "redirect:assignCategories";
		String redirect = "redirect:" + sellerExtractor.getBaseUrl(req) + "/operator/assignCategories";
		AdTemplate adTemplate = adTemplateDAO.get(adTemplateId);
		Product product = adTemplate.getProduct();
		if (commandObj.getCategoryId() == DECIDE_LATER) {
			number++;
			logger.info("Skipping product " + product.getId() + " (adTemplateId = " + adTemplateId + ") for the moment.");
			redirect += "?number=" + number;
		} else if (commandObj.getCategoryId() == SHOW_MORE_ALTERNATIVES) {
			if (viewType == VIEW_TYPE_FEW) {
				redirect += "?number=" + number + "&viewType=" + VIEW_TYPE_MORE;
			} else { //viewType == VIEW_TYPE_MORE
				redirect += "?number=" + number + "&viewType=" + VIEW_TYPE_ALL;
			}
		} else if (commandObj.getCategoryId() == SHOW_ALL_ALTERNATIVES) {
			redirect += "?number=" + number + "&viewType=" + VIEW_TYPE_ALL;
		} else if (commandObj.getCategoryId() == NONE_OF_THESE) {
			logger.info("Marking product " + product.getId() + " (adTemplateId = " + adTemplateId + ") as hard.");
			String categorySuggestion = adTemplate.getProduct().getCategorySuggestion();
			product.setCategorySuggestion("TOO_HARD;" + categorySuggestion);
			productDAO.update(product);
			redirect += "?number=" + number;
		} else {
			logger.info("Assigning category " + commandObj.getCategoryId() + " for product " + product.getId()
					+ " (adTemplateId = " + adTemplateId + ").");
			Category category = categoryDAO.get(commandObj.getCategoryId());
			product.setCategory(category);
			product.setCategorySuggestion(null);
			productDAO.update(product);
			redirect += "?number=" + number;
		}
		logger.debug("CategoryAssignmentController.post - redirecting to " + redirect);
		ModelAndView mav = new ModelAndView(redirect);
		return mav;
	}
	
}
