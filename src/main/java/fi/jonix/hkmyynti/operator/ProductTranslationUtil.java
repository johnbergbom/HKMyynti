package fi.jonix.hkmyynti.operator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.commandobject.AdTemplateInfo;
import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.hkmyynti.validator.NewAdTemplateValidator;
import fi.jonix.huutonet.domain.model.AdTemplate;
import fi.jonix.huutonet.domain.model.Image;
import fi.jonix.huutonet.domain.model.Product;
import fi.jonix.huutonet.domain.model.Provider;
import fi.jonix.huutonet.domain.model.Seller;
import fi.jonix.huutonet.domain.model.Translation;
import fi.jonix.huutonet.domain.model.User;
import fi.jonix.huutonet.domain.model.dao.AdTemplateDAO;
import fi.jonix.huutonet.domain.model.dao.ProductDAO;
import fi.jonix.huutonet.domain.model.dao.ProviderDAO;
import fi.jonix.huutonet.domain.model.dao.SellerDAO;
import fi.jonix.huutonet.domain.model.dao.TranslationDAO;
import fi.jonix.huutonet.domain.model.dao.TranslationDAOImpl;

@Component(value = "productTranslationUtil")
public class ProductTranslationUtil {

	public static final Logger logger = Logger.getLogger(ProductTranslationUtil.class);

	public enum TranslationMode {
		ALL, HEADLINE_AND_DETAILS
	};

	private static String LOCATION = "Postin kautta kotiinkuljetettuna";
	private static String SLOGAN = "";
	private static String SHIPPING_DESCRIPTION = "Keskitetyn tilaus- ja toimitusprosessimme vuoksi, tilausten käsittely maksun saapumisesta postittamiseen on noin 5-8 arkipäivää, joten tuote tulee postitse noin 10:n arkipäivän kuluttua maksun saapumisesta.";
	
	/** Default translation mode is just headline and details. */
	//TranslationMode translationMode = TranslationMode.ALL;
	TranslationMode translationMode = TranslationMode.HEADLINE_AND_DETAILS;

	public static String DEFAULT_PROVIDER = "FocalPrice";
	static public String FINNISH_LANGUAGE = "fi_FI";
	
	@Autowired
	private TranslationDAO translationDAO;

	@Autowired
	private SellerDAO sellerDAO;

	@Autowired
	private ProviderDAO providerDAO;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private AdTemplateDAO adTemplateDAO;

	@Autowired
	private SellerExtractor sellerExtractor;
	
	/**
	 * @return Returns the loggen on user (taken from the session).
	 */
	private User getUser(HttpServletRequest req) {
		return ((UserSession) req.getSession().getAttribute("userSession")).getUser();
	}
	
	private Seller getSeller(User user) {
		Seller seller = null;
		List<Seller> sellers = sellerDAO.getSellersByUser(user);
		if (sellers.isEmpty() || sellers.size() > 1) {
			//if the user has no sellers or several seller, then use default seller with id 2
			logger.debug("User " + user.getLoginName() + " has " + sellers.size() + " , using default.");
			seller = sellerDAO.get(2L);
			if (seller == null) {
				throw new RuntimeException("Default seller not found.");
			}
		} else {
			logger.debug("User " + user.getLoginName() + " has one seller.");
			seller = sellers.get(0);
		}
		logger.debug("Using seller " + seller.getName());
		return seller;
	}

	// TODO: this method is basically just copy'n'pasted from FocalPriceProvider.productExistsAtProvider - refactor!
	/*public boolean shouldTranslateProduct(Product product) {
		HttpClient client = new HttpClient();
		String url = "http://www.focalprice.com/" + product.getProviderProdId() + "/something.html";
		GetMethod gm = new GetMethod(url);
		// Sometimes FocalPrice returns an error if the Referer header is missing, so let's add it here.
		gm.setRequestHeader("Referer", url);
		try {
			client.executeMethod(gm);
			String body = gm.getResponseBodyAsString();
			if (body.indexOf("Parameter is incorrect.") >= 0) {
				logger.info("Item not available");
				// Delete product
				this.productDAO.delete(product);
				return false;
			} else if ((body.indexOf("Access control configuration prevents your request") >= 0
					&& body.indexOf("allowed at this time") >= 0
					&& body.indexOf("Please contact your service") >= 0
					&& body.indexOf("feel this is incorrect") >= 0)
					|| body.indexOf("ERR_ACCESS_DENIED") >= 0) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}*/

	public boolean shouldTranslateProduct(Product product) {
		if (product.getCompensation() || product.getProductState().equals(Product.STATE_NO_LONGER_FOR_SALE)) {
			return false;
		} else {
			return true;
		}
	}
	
	/* TODO: this method is copy'n'pasted from StringUtilities.escapeString2HTML - refactor! */
	private String escapeString2HTML(String text) {
		String returnText = text.replaceAll("å", "&aring;").replaceAll("Å", "&Aring;");
		returnText = returnText.replaceAll("ä", "&auml;").replaceAll("Ä", "&Auml;");
		returnText = returnText.replaceAll("ö", "&ouml;").replaceAll("Ö", "&Ouml;");
		returnText = returnText.replaceAll("€", "&euro;").replaceAll("£", "&pound;");
		return returnText;
	}

	private List<String> getSplitters(String originalString) {
		List<String> splitters = new ArrayList<String>();
		
		Pattern pattern = Pattern
				.compile(TranslationDAOImpl.TRANSLATION_PART_SEPARATORS);
		Matcher matcher = pattern.matcher(originalString);

		while (matcher.find()) {
			String splitter = matcher.group();
			if (matcher.start() > 0) {
				for (int i = matcher.start() - 1; i >= 0; i--) {
					if (Character.isWhitespace(originalString.charAt(i))) {
						splitter = originalString.charAt(i) + splitter;
					} else {
						break;
					}
				}
			}
			for (int i = matcher.end(); i < originalString.length(); i++) {
				if (Character.isWhitespace(originalString.charAt(i))) {
					splitter = splitter + originalString.charAt(i);
				} else {
					break;
				}
			}
			splitters.add(splitter);
		}
		return splitters;
	}

	private String getTranslation(String text, List<String> explanations, Map<String,String> memoryMap) {
		//logger.debug("text = \"" + text + "\"");
		if (text == null || text.length() == 0)
			return "";
		String translatedText = "";
		String[] sentences = text.split(": ");
		int sentenceIndex = 0;
		String explanation = "";
		for (String sentence : sentences) {
			//logger.debug("sentence = \"" + sentence + "\"");
			String[] parts2 = sentence
					.split(TranslationDAOImpl.TRANSLATION_PART_SEPARATORS);
			String[] parts = new String[parts2.length];
			for (int i = 0; i < parts.length; i++) {
				parts[i] = parts2[i].trim();
			}
			List<String> splitters = this.getSplitters(sentence);
			int index = 0;
			for (String part : parts) {
				//logger.debug("part = \"" + part + "\"");
				if (!part.trim().equals("")) {
					if (memoryMap != null && memoryMap.get(part.trim()) != null) {
						explanation += "*memory exact*> " + part.trim() + " -> " + memoryMap.get(part.trim()) + "  ";
						translatedText += memoryMap.get(part.trim());
					} else {
						Translation translation = this.translationDAO.getTranslation(
								part.trim(), FINNISH_LANGUAGE);
						if (translation != null) {
							//logger.debug("aaa = \"" + translation.getTranslatedText() + "\"");
							//mav.addObject(explanationPrefix + "Explanation",
								//	"*old exact*> " + part.trim() + " -> " + translation.getTranslatedText());
							explanation += "*old exact*> " + part.trim() + " -> " + translation.getTranslatedText() + "  ";
							translatedText += translation.getTranslatedText();
						} else {
							translation = this.translationDAO.getCloseTranslation(
									part.trim(), FINNISH_LANGUAGE);
							if (translation != null) {
								//logger.debug("bbb = \"" + translation.getTranslatedText() + "\"");
								//mav.addObject(explanationPrefix + "Explanation",
									//	"*old close*> " + part.trim() + " -> " + translation.getTranslatedText());
								explanation += "*old close*> " + part.trim() + " -> " + translation.getTranslatedText() + "  ";
								translatedText += translation.getTranslatedText();
							} else {
								/* Skip using google translator because it's nowadays a paid service. */
								/*try {
									Translate.setHttpReferrer("http://www.sellstar.fi");
									String googleTranslation = Translate.execute(part,
											Language.ENGLISH, Language.FINNISH);
									translatedText += googleTranslation;
									//logger.debug("ddd = \"" + googleTranslation + "\"");
									//mav.addObject(explanationPrefix + "Explanation",
										//	"*google*   > " + part + " -> " + googleTranslation);
									explanation += "*google*   > " + part + " -> " + googleTranslation + "  ";
								} catch (Exception e) {
									logger.debug("Google translation failed for text " + part + ": " + e.getMessage());
								}*/
							}
						}
					}
				}
				if (splitters.size() > index) {
					translatedText += splitters.get(index);
				}
				index++;
			}
			if (sentences.length > 1 && sentenceIndex % 2 == 0) {
				translatedText += ": ";
			}
			sentenceIndex++;
		}
		//mav.addObject(explanationPrefix + "Explanation",explanation);
		explanations.add(explanation);
		return translatedText.trim().replaceAll("  ", " ");
	}

	private String getFormattedSentenceWithoutDot(String row) {
		if (row == null || row.length() < 2)
			return row;
		String sentence = Character.toUpperCase(row.charAt(0))
				+ row.substring(1);
		return sentence;
	}

	/* Sometimes there can be several very similar products in a row, for example
	 * the same product, just in different colors. These products normally have
	 * many things in common with the current product in question, for example
	 * it might have an identical technicalSpecs. This can be used for providing
	 * a good base for the translation of the current product in question and
	 * decrease manual work. This method finds such sibling products. */
	private AdTemplate getSibling(Product product) {
		Translation translation = this.translationDAO.getCloseTranslation(
				product.getName(), FINNISH_LANGUAGE);
		if (translation == null) {
			return null;
		}
		String siblingHeadline = translation.getTranslatedText();
		List<AdTemplate> siblingAdTemplates = adTemplateDAO
				.findByHeadline(siblingHeadline);
		if (siblingAdTemplates == null || siblingAdTemplates.isEmpty()) {
			return null;
		}
		return siblingAdTemplates.get(0);
	}

	private List<String> getOriginalDetails(Product product, AdTemplate sibling) {
		ModelAndView mav = new ModelAndView();
		handleDetails(mav,product,sibling,null,null);
		return (List<String>) mav.getModel().get("detailsOrig");
	}
	
	private List<String> getOriginalTechSpecs(Product product, AdTemplate sibling) {
		ModelAndView mav = new ModelAndView();
		handleTechnicalSpecs(mav,product,sibling,null);
		return (List<String>) mav.getModel().get("techSpecsOrig");
	}
	
	private void handleDetails(ModelAndView mav, Product product,
			AdTemplate sibling, AdTemplateInfo adTemplateInfo, Map<String,String> memoryMap) {
		String[] origRows = null;
		if (product.getContents() != null && !product.getContents().trim().equals("")) {
			origRows = product.getContents().split("\n");
		} else if (sibling != null/* && sibling.getDetails() != null*/
				&& sibling.getProduct().getContents() != null && !sibling.getProduct().getContents().trim().equals("")) {
			/*if ((sibling.getProduct().getContents() == null || sibling.getProduct().getContents().trim().equals(""))
					&& sibling.getDetails() != null
					&& (adTemplateInfo.getDetails() == null || adTemplateInfo.getDetails().size() == 0)) {
				/* If we don't get any details in the original language and the sibling product
				 * contains translated details, then use those. /
				String[] trRows = sibling.getDetails().split("\n");
			} else {*/
				origRows = sibling.getProduct().getContents().split("\n");
			//}
		}
		if (origRows == null) {
			origRows = new String[0];
		}
		List<String> detailsOrig = new ArrayList<String>();
		List<String> detailsTransl = new ArrayList<String>();
		List<String> explanations = new ArrayList<String>();
		//List<String> detailsTranslated = new ArrayList<String>();
		//String detailsTranslated = "";
		for (int i = 0; i < origRows.length; i++) {
			String row = origRows[i];
			if (row.startsWith("* ") && row.length() > 2) {
				row = "1 x " + row.substring(2);
			}
			String translatedRow = getTranslation(row,explanations,memoryMap);
			detailsOrig.add(row);
			//detailsTranslated.add(getTranslation(row,mav,"headline"));
			//detailsTranslated += translatedRow;
			if (translatedRow != null && !translatedRow.trim().equals("")) {
				detailsTransl.add(translatedRow); //TODO: if this row is uncommented, then the translations are used for the details (=product.contents)
				////detailsTransl.add(row); //TODO: if this row is commented out, then the translations are used for the details (=product.contents)
			} else {
				detailsTransl.add(row);
			}
			/*if (i < (origRows.length - 1)) {
				detailsTranslated += "\n";
			}*/
		}
		//mav.addObject("detailsRows", origRows.length);
		mav.addObject("detailsOrig", detailsOrig);
		mav.addObject("detailsExplanation", explanations);
		//mav.addObject("detailsTranslated", detailsTranslated);
		//return detailsTranslated;
		//adTemplateInfo.setDetails(detailsTranslated);
		if (adTemplateInfo != null && (adTemplateInfo.getDetails() == null || adTemplateInfo.getDetails().size() == 0)) {
			adTemplateInfo.setDetails(detailsTransl);
		}
	}

	private void handleTechnicalSpecs(ModelAndView mav, Product product,
			AdTemplate sibling, AdTemplateInfo adTemplateInfo) {
		String[] origRows = null;
		/*String[] trRows = null;
		if (adTemplateInfo.getTechnicalSpecs() != null && adTemplateInfo.getTechnicalSpecs().size() > 0) {
			trRows = (String[]) adTemplateInfo.getTechnicalSpecs().toArray(); //.split("\n");
		} else if (sibling != null && sibling.getTechnicalSpecs() != null) {
			//TODO: this one isn't used - fix
			trRows = sibling.getTechnicalSpecs().split("\n");
		}*/
		if (product.getTechnicalSpecs() != null && !product.getTechnicalSpecs().trim().equals("")) {
			origRows = product.getTechnicalSpecs().split("\n");
		} else if (sibling != null && sibling.getProduct().getTechnicalSpecs() != null
				&& !sibling.getProduct().getTechnicalSpecs().trim().equals("")) {
			origRows = sibling.getProduct().getTechnicalSpecs().split("\n");
		}
		if (origRows == null) {
			origRows = new String[0];
		}
		/*if (trRows == null) {
			trRows = new String[0];
		}*/
		
		List<String> techSpecsOrig = new ArrayList<String>();
		List<String> techSpecsTransl = new ArrayList<String>();
		List<String> explanations = new ArrayList<String>();
		//String techSpecsOrigTranslated = "";
		//String techSpecsOrigOneString = "";
		for (int i = 0; i < origRows.length; i++) {
			String row = origRows[i];
			String translatedRow = getTranslation(row,explanations,null);
			techSpecsOrig.add(row);
			//techSpecsOrigOneString += row;
			if (translationMode == TranslationMode.ALL) {
				//techSpecsOrigTranslated += translatedRow;
				techSpecsTransl.add(translatedRow);
			} else {
				//techSpecsOrigTranslated += row;
				techSpecsTransl.add(row);
			}
			/*if (i < (origRows.length - 1)) {
				//techSpecsOrigOneString += "\n";
				techSpecsOrigTranslated += "\n";
			}*/
		}
		//mav.addObject("techSpecsRows", origRows.length);
		mav.addObject("techSpecsOrig", techSpecsOrig);
		//mav.addObject("techSpecsOrigOneString", techSpecsOrigOneString);
		mav.addObject("techSpecsExplanation", explanations);
		/*if (adTemplateInfo.getTechnicalSpecs() == null) {
			adTemplateInfo.setTechnicalSpecs(techSpecsOrigTranslated);
		}*/
		if (adTemplateInfo != null && (adTemplateInfo.getTechnicalSpecs() == null
				|| adTemplateInfo.getTechnicalSpecs().size() == 0)) {
			adTemplateInfo.setTechnicalSpecs(techSpecsTransl);
		}
	}

	private void addProduct(Product product, Integer nbrProducts,
			String language, Long sellerId, AdTemplateInfo adTemplateInfo, ModelAndView mav) {
		if (nbrProducts != null) {
			mav.addObject("nbrProducts", nbrProducts);
		}
		AdTemplate sibling = getSibling(product);
		if (sibling == null) {
			logger.debug("sibling = null");
		} else {
			logger.debug("sibling = " + sibling.getId());
		}
		if (sellerId != null) {
			mav.addObject("sellerId", sellerId);
		}
		if (language != null) {
			mav.addObject("adTemplateLanguage", language);
		}
		mav.addObject("productId", product.getId());
		mav.addObject("headlineOrig", product.getName());
		mav.addObject("translationMode", translationMode);
		/*String details = */handleDetails(mav,product,sibling,adTemplateInfo,null);
		List<String> explanation = new ArrayList<String>();
		String translated = getFormattedSentenceWithoutDot(getTranslation(product.getName(),explanation,null)); //TODO: if this row is uncommented, then the translations are used for the headline
		//String translated = getFormattedSentenceWithoutDot(product.getName()); //TODO: if this row is commented out, then the translations are used for the headline
		mav.addObject("headlineExplanation", explanation);
		if (adTemplateInfo.getHeadline() == null) {
			if (translated != null && !translated.trim().equals("")) {
				adTemplateInfo.setHeadline(translated);
			} else {
				adTemplateInfo.setHeadline(getFormattedSentenceWithoutDot(product.getName()));
			}
		}
		//adTemplateInfo.setDetails(details);
		/*if (translationMode == TranslationMode.ALL) {
			throw new RuntimeException("Implement support for translating technicalSpecs");
		} else {*/
			handleTechnicalSpecs(mav,product,sibling,adTemplateInfo);
		//}
		mav.addObject("commandObj", adTemplateInfo);
		addImages(product,mav);
	}

	private void addImages(Product product, ModelAndView mav) {
		List<String> imageUrlList = new ArrayList<String>();
		boolean nonThumbnailsFound = false;
		List<Image> images = product.getImagesInOrder();
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
	
	private void setHardCodedStuff(AdTemplate adTemplate, String addedBy) {
		adTemplate.setActive(true);
		adTemplate.setLocation(LOCATION);
		adTemplate.setBeanName("templateGenerator");
		//adTemplate.setMinBidIncreaseInEuro(new BigDecimal("0.1"));
		//adTemplate.setMinBidIncreaseInMarketCurrency(new BigDecimal("0.1"));
		adTemplate.setOpenDays(5);
		adTemplate.setPaymentWay(1);
		adTemplate.setQuantity(1);
		adTemplate.setSlogan(SLOGAN);
		adTemplate.setShippingDescription(escapeString2HTML(SHIPPING_DESCRIPTION));
		if (adTemplate.getSummary() == null) {
			adTemplate.setSummary(""); // cannot be null
		}
		if (adTemplate.getDetails() == null) {
			adTemplate.setDetails(""); // cannot be null
		}
		adTemplate.setShippingWay(1);
		adTemplate.setCreatedTimestamp(new Date());
		adTemplate.setAddedBy(addedBy);
	}

	private void updateTranslationToMemory(String originalText, String translatedText, Seller seller, Map<String,String> toMemoryOnly) {
		updateTranslationInternal(originalText, translatedText, seller, toMemoryOnly);
	}
	
	private void updateTranslationToDB(String originalText, String translatedText, Seller seller) {
		updateTranslationInternal(originalText, translatedText, seller, null);
	}
	
	private void updateTranslationInternal(String originalText, String translatedText, Seller seller, Map<String,String> toMemoryOnly) {
		if (translatedText == null) {
			return;
		}
		translatedText = translatedText.trim();
		originalText = originalText.trim();
		//String returnText = "";
		String[] originalTextParts = originalText
				.split(TranslationDAOImpl.TRANSLATION_PART_SEPARATORS);
		String[] parts2 = translatedText
				.split(TranslationDAOImpl.TRANSLATION_PART_SEPARATORS);
		String[] translatedTextParts = new String[parts2.length];
		for (int i = 0; i < translatedTextParts.length; i++) {
			translatedTextParts[i] = parts2[i].trim();
		}
		// TODO : Later you may want to save full sentences also
		if (originalTextParts.length != translatedTextParts.length) {
			// store nothing to the database if the number of parts don't agree
			return;
		}
		//List<String> translatedTextSplitters = getSplitters(translatedText);
		int index = 0;
		for (int i = 0; i < originalTextParts.length; i++) {
			if (i == translatedTextParts.length) {
				break;
			}
			translatedTextParts[i] = translatedTextParts[i].trim();
			originalTextParts[i] = originalTextParts[i].trim();
			if (originalTextParts[i] != null
					&& originalTextParts[i].length() > 1
					&& translatedTextParts[i] != null
					&& translatedTextParts[i].length() > 1) {
				translatedTextParts[i] = Character
						.toUpperCase(translatedTextParts[i].charAt(0))
						+ translatedTextParts[i].substring(1);
				if (toMemoryOnly != null) {
					toMemoryOnly.put(originalTextParts[i],translatedTextParts[i]);
				} else {
					Translation translation = this.translationDAO.getTranslation(
							originalTextParts[i], FINNISH_LANGUAGE);
					if (translation != null) {
						translation.setTranslatedText(translatedTextParts[i]);
						this.translationDAO.save(translation);
					} else {
						translation = new Translation(originalTextParts[i],
								translatedTextParts[i], FINNISH_LANGUAGE, seller);
						this.translationDAO.save(translation);
					}
				}
				//logger.debug("Storing original text = " + originalTextParts[i] + ", translation = " + translatedTextParts[i]);
			}
			/*returnText += translatedTextParts[i];
			if (translatedTextSplitters.size() > index) {
				returnText += translatedTextSplitters.get(index);
			}*/
			index++;
		}
		//return returnText.trim().replaceAll("  ", " ");
	}

	private void storeNewAdTemplateToDB(Product product, String adTemplateLanguage, Seller seller,
			AdTemplateInfo adTemplateInfo, User user) {
		AdTemplate adTemplate = new AdTemplate();
		adTemplate.setSeller(seller);
		adTemplate.setLanguage(adTemplateLanguage);
		adTemplate.setProduct(product);
		adTemplate.setHeadline(adTemplateInfo.getHeadline());
		adTemplate.setVisibilityType(0);
		updateTranslationToDB(product.getName(), adTemplateInfo.getHeadline(), seller);
		
		String details = "";
		AdTemplate sibling = getSibling(product);
		List<String> orig = getOriginalDetails(product, sibling);
		boolean storeTranslationToDB = (orig.size() == adTemplateInfo.getDetails().size());
		for (int i = 0; i < adTemplateInfo.getDetails().size(); i++) {
			String row = adTemplateInfo.getDetails().get(i);
			details += row;
			if (i < (adTemplateInfo.getDetails().size() - 1)) {
				details += "\n";
			}
			if (storeTranslationToDB) {
				updateTranslationToDB(orig.get(i), row, seller);
			}
		}
		adTemplate.setDetails(details);
		
		/* adTemplateInfo.getTechnicalSpecs() can be empty here even if translationMode == ALL
		 * if the product in question didn't contain any technicalSpecs. */
		if (translationMode == TranslationMode.ALL && adTemplateInfo.getTechnicalSpecs() != null
				&& adTemplateInfo.getTechnicalSpecs().size() > 0) {
			orig = getOriginalTechSpecs(product, sibling);
			storeTranslationToDB = (orig.size() == adTemplateInfo.getTechnicalSpecs().size());
			String techSpecs = "";
			for (int i = 0; i < adTemplateInfo.getTechnicalSpecs().size(); i++) {
				String row = adTemplateInfo.getTechnicalSpecs().get(i);
				techSpecs += row;
				if (i < (adTemplateInfo.getTechnicalSpecs().size() - 1)) {
					techSpecs += "\n";
				}
				if (storeTranslationToDB) {
					updateTranslationToDB(orig.get(i), row, seller);
				}
			}
			adTemplate.setTechnicalSpecs(techSpecs);
		}
		
		this.setHardCodedStuff(adTemplate, user.getLoginName());
		logger.info("Storing translated ad template to database.");
		adTemplateDAO.save(adTemplate);
		adTemplateDAO.flush();
	}

	public ModelAndView getStartPage(HttpServletRequest req) {
		//ModelAndView mav = new ModelAndView("operator/productTranslation");
		ModelAndView mav = new ModelAndView("operator/index");
		mav.addObject("whichPage", "productTranslation");
		User user = getUser(req);
		Seller seller = getSeller(user);
		Provider provider = providerDAO.getProviderByName(DEFAULT_PROVIDER);
		List<Product> products = productDAO.getProductsWithoutAdTemplate(provider);
		int nbrProducts = products.size();
		logger.debug("nbrProducts = " + nbrProducts);
		AdTemplateInfo adTemplateInfo = new AdTemplateInfo();
		for (Product product : products) {
			if (shouldTranslateProduct(product)) {
				logger.debug("Translating product " + product.getId() + ".");
				addProduct(product,nbrProducts,FINNISH_LANGUAGE,seller.getId(),adTemplateInfo,mav);
				break;
			}
		}
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}

	public ModelAndView updateDetailsWithAjax(long productId, String headline,
			long sellerId) {
		Map<String,String> translationMap = new HashMap<String,String>();
		Product product = productDAO.get(productId);
		AdTemplate sibling = getSibling(product);
		updateTranslationToMemory(product.getName(), headline, sellerDAO.get(sellerId), translationMap);
		List<String> orig = getOriginalDetails(product, sibling);
		String translatedRow = getTranslation(orig.get(0),new ArrayList<String>(),translationMap);
		ModelAndView mav = new ModelAndView("operator/detailsUpdate");
		mav.addObject("updatedDetailsFirstRow", translatedRow);
		return mav;
	}

	public ModelAndView handleResult(AdTemplateInfo adTemplateInfo,
			BindingResult result, HttpServletRequest req, long sellerId,
			String adTemplateLanguage, long productId, int nbrProducts) {
		new NewAdTemplateValidator().validate(adTemplateInfo, result);
		if (result.hasErrors()) {
			logger.info("ProductTranslationController.post has errors.");
			//ModelAndView mav = new ModelAndView("operator/productTranslation");
			ModelAndView mav = new ModelAndView("operator/index");
			mav.addObject("whichPage", "productTranslation");
			addProduct(productDAO.get(productId),nbrProducts,adTemplateLanguage,sellerId,adTemplateInfo,mav);
			sellerExtractor.addSellerInfo(req, mav);
			return mav;
		}
		logger.info("ProductTranslationController.post doesn't have errors.");
		User user = getUser(req);
		storeNewAdTemplateToDB(productDAO.get(productId),adTemplateLanguage,
				sellerDAO.get(sellerId),adTemplateInfo,user);
		//ModelAndView mav = new ModelAndView("redirect:translateProducts");
		ModelAndView mav = new ModelAndView("redirect:" + sellerExtractor.getBaseUrl(req) + "/operator/translateProducts");
		return mav;
	}

}
