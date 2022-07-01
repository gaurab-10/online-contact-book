package com.smart.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
	@RequestMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request) {
		System.out.println("Session is::" + request.getSession());
		if (request.isUserInRole("ROLE_ADMIN")) {
			return "redirect:/admin/dashboard";
		}
		return "redirect:/user/dashboard";
	}

	/*
	 * @RequestMapping(value= {"/default"}, method = RequestMethod.GET) public
	 * String defaultAfterLogin() { Collection<? extends GrantedAuthority> authorities; 
	 * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	 * authorities = auth.getAuthorities(); 
	 * String myRole = authorities.toArray()[0].toString();
	 * String admin = "admin"; 
	 * if (myRole.equals(admin)) {
	 *  return "redirect:/admin/"; 
	 *  }
	 *  return "redirect:/r/"; 
	 *  }
	 */
}
