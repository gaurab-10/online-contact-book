package com.smart.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@RestController
public class SearchRestController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	// search handler
	@GetMapping("/search/{pattern}")
	public ResponseEntity<?> search(@PathVariable("pattern") String pattern, Principal principal) {
		
		System.out.println("Pattern is::" + pattern);
		User user = userRepository.findByUsername(principal.getName());
		//User user = userRepository.findByUsername("john2334@gmail.com");
		List<Contact> contacts=this.contactRepository.findByNameContainingAndUser(pattern, user);
		return ResponseEntity.ok(contacts);

	}

}
