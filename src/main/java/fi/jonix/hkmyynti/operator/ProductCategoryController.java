package fi.jonix.hkmyynti.operator;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.huutonet.domain.model.AdTemplate;
import fi.jonix.huutonet.domain.model.Category;
import fi.jonix.huutonet.domain.model.dao.AdTemplateDAO;
import fi.jonix.huutonet.domain.model.dao.CategoryDAO;

@Controller
@RequestMapping("/operator/listProductsInCategory")
public class ProductCategoryController {

	public static final Logger logger = Logger.getLogger(ProductCategoryController.class);

	@Autowired
	private AdTemplateDAO adTemplateDAO;
	
	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	public class SimpleAdTemplateInfo {
		private long id;
		private String headline;
		
		public SimpleAdTemplateInfo(long id, String headline) {
			this.id = id;
			this.headline = headline;
		}

		public void setId(long id) {
			this.id = id;
		}
		public long getId() {
			return id;
		}
		public void setHeadline(String headline) {
			this.headline = headline;
		}
		public String getHeadline() {
			return headline;
		}
		
	}
	
	@RequestMapping
	public ModelAndView showForm(@RequestParam(required = true) long categoryId,
			HttpServletRequest req) {
		//ModelAndView mav = new ModelAndView("operator/index");
		//mav.addObject("whichPage", "productsInCategoryList");
		ModelAndView mav = new ModelAndView("operator/productsInCategoryList");
		logger.debug("categoryId = " + categoryId);
		Category category = categoryDAO.get(categoryId);
		logger.debug("category = " + category);
		String path = CategoryAssignmentController.extractPath(CategoryAssignmentController.getWholePathFromRootFor(category));
		mav.addObject("path", path);
		List<SimpleAdTemplateInfo> adTemplateInfoList = new ArrayList<SimpleAdTemplateInfo>();
		for (AdTemplate adTemplate : adTemplateDAO.findBySellStarCategory(category)) {
			adTemplateInfoList.add(new SimpleAdTemplateInfo(adTemplate.getId(), adTemplate.getHeadline()));
		}
		mav.addObject("adTemplateInfoList", adTemplateInfoList);
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}
	
}
