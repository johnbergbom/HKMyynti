package fi.jonix.hkmyynti.operator;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import fi.jonix.hkmyynti.util.SellerExtractor;

public class AuthInterceptor extends HandlerInterceptorAdapter {

	public static final Logger logger = Logger.getLogger(AuthInterceptor.class);

	@Autowired
	private SellerExtractor sellerExtractor;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (request.getPathInfo().indexOf("operator") > 0 && !request.getPathInfo().equals("/operator/login")) {
			/*logger.debug("In preHandle2");
			logger.debug("getContextPath = " + request.getContextPath());
			logger.debug("getLocalAddr = " + request.getLocalAddr());
			logger.debug("getLocalName = " + request.getLocalName());
			logger.debug("getMethod = " + request.getMethod());
			logger.debug("getPathInfo = " + request.getPathInfo());
			logger.debug("getPathTranslated = " + request.getPathTranslated());
			logger.debug("getProtocol = " + request.getProtocol());
			logger.debug("getQueryString = " + request.getQueryString());
			//logger.debug(" = " + request.getRealPath(arg0));
			logger.debug("getRemoteAddr = " + request.getRemoteAddr());
			logger.debug("getRemoteHost = " + request.getRemoteHost());
			logger.debug("getRequestedSessionId = " + request.getRequestedSessionId());
			logger.debug("getRequestURI = " + request.getRequestURI());
			logger.debug("getRequestURL = " + request.getRequestURL());
			logger.debug("getScheme = " + request.getScheme());
			logger.debug("getServerName = " + request.getServerName());
			logger.debug("getServletPath = " + request.getServletPath());
			logger.debug("getSession.getServletContext.getServerInfo = " + request.getSession().getServletContext().getServerInfo());
			logger.debug("getSession.getServletContext.getServletContextName = " + request.getSession().getServletContext().getServletContextName());
			*/

			HttpSession session = request.getSession(false);
			UserSession userSession = null;
			if (session != null) {
				//logger.debug("session found");
				userSession = (UserSession) session.getAttribute("userSession");
				if (userSession != null) {
					/* Check if the session is still valid. */
					//logger.debug("userSession found");
					Calendar oneHourAgo = Calendar.getInstance();
					oneHourAgo.add(Calendar.HOUR_OF_DAY,-1);
					if (userSession.getStartTime().getTime() > oneHourAgo.getTime().getTime()) {
						//logger.debug("userSession still valid");
					} else {
						logger.debug("userSession no longer valid - invalidating session");
						session.invalidate();
						userSession = null;
					}
				} else {
					logger.debug("userSession not found");
				}
			} else {
				//logger.debug("session not found");
			}
			
			if (userSession != null) {
				//TODO: hur ska man hantera principals här för att fixa authentication dao-tasolla?
				return super.preHandle(request, response, handler);
			} else {
				logger.debug("Redirecting to logon page (pathInfo = " + request.getPathInfo() + ").");
				//String redirectUrl = request.getContextPath() + "/seller/operator/login";
				String redirectUrl = sellerExtractor.getBaseUrl(request) + "/operator/login";
				if (!request.getPathInfo().equals("/operator/logout")) {
					//redirectUrl += "?redirect=" + request.getServletPath() + request.getPathInfo()
						//+ (request.getQueryString() != null ? "?" + request.getQueryString() : "");
					redirectUrl += "?redirect=" + sellerExtractor.getBaseUrl(request) + request.getPathInfo()
					+ (request.getQueryString() != null ? "?" + request.getQueryString() : "");
				}
				logger.debug("redirectUrl = " + redirectUrl);
				response.sendRedirect(redirectUrl);
				return false;
			}
		} else {
			return super.preHandle(request, response, handler);
		}
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//logger.debug("In postHandle2");
		// TODO Auto-generated method stub
		super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//logger.debug("In afterCompletion2");
		// TODO Auto-generated method stub
		super.afterCompletion(request, response, handler, ex);
	}
}
