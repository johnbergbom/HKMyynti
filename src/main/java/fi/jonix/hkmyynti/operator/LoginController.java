package fi.jonix.hkmyynti.operator;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
@RequestMapping("/operator/login")
public class LoginController {

	public static final Logger logger = Logger.getLogger(LoginController.class);

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private SellerExtractor sellerExtractor;
	
	@RequestMapping
	public ModelAndView login(@RequestParam(required = false) String redirect,
			HttpServletRequest req
			/*@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "password", required = false) String password*/) {
		//logger.debug("userName = " + userName + ", password = " + password);
		ModelAndView mav = new ModelAndView("operator/index");
		//ModelAndView mav = new ModelAndView(sellerExtractor.getBaseUrl(req) + "/operator/index");
		//mav.addObject("title", "refund");
		mav.addObject("whichPage", "login");
		//mav.addObject("information", information);
		logger.debug("redirect = " + redirect);
		LoginInfo li = new LoginInfo();
		li.setRedirect(redirect);
		mav.addObject("commandObj", li);
		sellerExtractor.addSellerInfo(req, mav);
		return mav;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(@ModelAttribute("commandObj") LoginInfo commandObj,
			BindingResult result, HttpServletRequest req/*, HttpServletResponse response*/) /*throws IOException*/ {
		//logger.info("LoginController.post");
		//logger.debug("userName = " + commandObj.getUserName() + ", password = " + commandObj.getPassword()
			//	+ " md5 = " + getMD5(commandObj.getPassword()));
		logger.debug("redirect = " + commandObj.getRedirect());
		User user = userDAO.getByLoginName(commandObj.getUserName(),commandObj.getPassword());
		if (user != null) {
			logger.info("Login for user " + user.getLoginName() + " accepted.");
			UserSession userSession = new UserSession(new Date(),user);
			req.getSession().setAttribute("userSession", userSession);
		} else {
			logger.debug("Faulty password for user " + commandObj.getUserName() + ", redirecting to logon page.");
			result.reject("faultyUserNameOrPassword");
			ModelAndView mav = new ModelAndView("operator/index","commandObj",commandObj);
			//ModelAndView mav = new ModelAndView(sellerExtractor.getBaseUrl(req) + "/operator/index","commandObj",commandObj);
			mav.addObject("whichPage", "login");
			sellerExtractor.addSellerInfo(req, mav);
			return mav;
		}

		String redirect;
		if (commandObj.getRedirect() == null || commandObj.getRedirect().trim().equals("")) {
			//redirect = "main";
			redirect = sellerExtractor.getBaseUrl(req) + "/operator/main";
		} else {
			redirect = commandObj.getRedirect();
		}
		ModelAndView mav = new ModelAndView("redirect:" + redirect);
		return mav;
	}
	
}
