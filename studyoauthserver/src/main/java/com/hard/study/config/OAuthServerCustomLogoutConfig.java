//package com.hard.study.config;
//
//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//
//@Configuration
//public class OAuthServerCustomLogoutConfig implements LogoutSuccessHandler {
//	
//	@Autowired
//	private Environment env;
//	
//	@Override
//	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//		System.out.println("onLogoutSuccess");
//		request.getSession().invalidate();
//		authentication = null;
//		response.sendRedirect(env.getProperty("config.oauth2.logout-redirect-url"));
//		
//	}
//
//}
