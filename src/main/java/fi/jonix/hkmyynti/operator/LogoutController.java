package fi.jonix.hkmyynti.operator;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fi.jonix.hkmyynti.commandobject.LoginInfo;
import fi.jonix.hkmyynti.util.SellerExtractor;
import fi.jonix.huutonet.domain.model.User;
import fi.jonix.huutonet.domain.model.dao.UserDAO;

@Controller
@RequestMapping("/operator/logout")
public class LogoutController {

	public static final Logger logger = Logger.getLogger(LogoutController.class);

	@Autowired
	private SellerExtractor sellerExtractor;
	
	@RequestMapping
	public ModelAndView logout(HttpServletRequest req) {
		boolean userSessionFound = false;
		HttpSession session = req.getSession(false);
		if (session != null) {
			UserSession userSession = (UserSession) session.getAttribute("userSession");
			if (userSession != null) {
				userSessionFound = true;
				logger.info("Logging out user " + userSession.getUser().getLoginName());
				session.invalidate();
			}
		}
		if (!userSessionFound) {
			logger.info("Logging out user (no user session found)");
		}
		//return new ModelAndView("redirect:/seller/");
		return sellerExtractor.redirectToBase(req);
	}

}
