package com.smart.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@Controller
@RequestMapping("/user/")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/*
	 * Dashboard home
	 */
	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		return "normalUser/user_dashboard";
	}

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		User user = userRepo.findByUsername(userName);
		model.addAttribute("user", user);

	}

	/*
	 * Open add contact form handler
	 */
	@GetMapping("/addContact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		// System.out.println("Contact is::"+new Contact());
		// System.out.println("Contact image is::"+new Contact().getImg()); ---null
		// System.out.println("Contact id is::"+new Contact().getId()); --- null
		model.addAttribute("heading", "Register Here");
		return "normalUser/add_contact_form";
	}

	@PostMapping("/processContacts")
	public String processContact(@ModelAttribute("contact") Contact contact, @RequestParam("userId") String id,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session) {

		try {
			Optional<User> user = userRepo.findById(Long.parseLong(id));
			if (user.isPresent()) {
				contact.setUser(user.get());
				System.out.println(contact);

				// If the user is registering the new contact.
				if (file.isEmpty()) {
					contact.setImg("profile.png");

				} else {
					// Destination-file =
					// C:\Users\gaura\OneDrive\Desktop\eclipse\smart_contact_manager\smart_contact_manager\target\classes\static\img
					File destinationFile = new ClassPathResource("static/img").getFile();

					// Path =
					// C:\Users\gaura\OneDrive\Desktop\eclipse\smart_contact_manager\smart_contact_manager\target\classes\static\img\signature
					// (7).png
					Path path = Paths
							.get(destinationFile.getAbsoluteFile() + File.separator + file.getOriginalFilename());

					// it transfers the image to the path..
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					// System.out.println("Image is uploadeed");

					// Stores the image name in database.
					contact.setImg(file.getOriginalFilename()); // signature (7).png
				}
			}
			Contact savedContact = contactRepo.save(contact);
			session.setAttribute("message", new Message("Your contact is added", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			// error message.
			session.setAttribute("message", new Message("Something went wrong ", "danger"));
		}
		// return "redirect:/user/addContact";
		return "normalUser/add_contact_form";

	}

	/*
	 * Show contact handler Per page show 5 items only. Current page. 0,1,2
	 */
	@GetMapping("/showContacts/{page}")
	public String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		String username = principal.getName();
		User user = userRepo.findByUsername(username);
		PageRequest pageReqeust = PageRequest.of(page, 5, Sort.by("name").ascending());
		Page<Contact> contacts = contactRepo.findAllByUser(user, pageReqeust);

		/*
		 * -----------1 List<Contact> contacts =
		 * contactRepo.findAllWithUserId(user.getId()); ---------- 2 List<Contact>
		 * contacts = user.getContacts();
		 */
		model.addAttribute("title", "View Contacts");
		model.addAttribute("contacts", contacts.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		System.out.println(contacts);
		return "normalUser/show_contact";
	}

	/*
	 * This function deletes the contact of particular user from the database......
	 */
	@GetMapping("/delete/{contactId}/{pageNo}")
	public String deleteContact(@PathVariable("contactId") Long id, @PathVariable("pageNo") Integer pageNo,
			Model model) {

		Contact contact = contactRepo.findById(id).orElseThrow(() -> new EntityNotFoundException());

		try {
			// delete old photo update the new photo...
			String oldImageName = contact.getImg();
			// IF the image is the default one then there is no need to delete that image.
			if (!oldImageName.equals("profile.png")) {
				File deleteFileLocation = new ClassPathResource("static/img").getFile();
				File file00 = new File(deleteFileLocation, oldImageName);
				file00.delete();
			}
			contactRepo.deleteById(id);
			System.out.println("iam inside the delete function");
			return "redirect:/user/showContacts/{pageNo}";

		} catch (Exception e) {
			model.addAttribute("errorMessage", "Sorry!!" + e.getMessage());
			return "normalUser/exception_handler";
		}
	}

	/*
	 * This method shows the details of the contact when click in the email.
	 */
	@GetMapping("/contact/{id}")
	public String showContactDetails(@PathVariable("id") Long id, Model model, Principal principal) {

		Contact contactDetails = contactRepo.findById(id).orElseThrow(() -> new EntityNotFoundException());
		String contactUsername = contactDetails.getUser().getUsername();
		String loggedUsername = principal.getName();
		// Checking whether the logged username is equal to the contact username or not
		// to secure that the logged user cannot view contact details of other user.
		if (contactUsername.equals(loggedUsername)) {
			model.addAttribute("contact", contactDetails);
			return "normalUser/contact_detail";
		}
		model.addAttribute("errorMessage", "Sorry!! Contact Not found");
		return "normalUser/exception_handler";
	}

	/*
	 * This method updates the existing contact.
	 * 
	 */
	@GetMapping("/update/{contactId}/{pageNo}")
	public String updateData(@PathVariable("contactId") Long id, Model model, Principal principal) {

		Contact contact = contactRepo.findById(id).orElseThrow(() -> new EntityNotFoundException());
		String contactUsername = contact.getUser().getUsername();
		String loggedUsername = principal.getName();

		// to check whether the request contact is of logged user or not.
		if (contactUsername.equals(loggedUsername)) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", "Update Contact");
			return "normalUser/update_contact";

		}
		// If the client gives the random contact no which is in the database but
		// Doesn't belong the logged user.
		model.addAttribute("errorMessage", "Sorry!! Contact Not found");
		return "normalUser/exception_handler";
	}

	// This method updates the contact.
	@PostMapping("/updateContacts")
	public String updateContact(@ModelAttribute("contact") Contact contact, @RequestParam("userId") String id,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session) {

		Contact oldContact = contactRepo.findById(contact.getId()).get();
		String oldImgName = oldContact.getImg();

		try {
			Optional<User> user = userRepo.findById(Long.parseLong(id));

			if (user.isPresent()) {
				contact.setUser(user.get());

				// If the user is registering the new contact.
				if (file.isEmpty()) {
					System.out.println(" !!!!!!! EMpty file !!!!!");
					contact.setImg(oldImgName);

				} else {
					// delete old photo update the new photo...
					File deleteFileLocation = new ClassPathResource("static/img").getFile();
					// If the image name is profile.png(i.e. default image) then there is no need to
					// delete the default image.
					if (!oldImgName.equals("profile.png")) {
						File file00 = new File(deleteFileLocation, oldImgName);
						file00.delete();

					}
					// Stores the image name in database.
					File destinationFile = new ClassPathResource("static/img").getFile();
					Path path = Paths
							.get(destinationFile.getAbsoluteFile() + File.separator + file.getOriginalFilename());
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					contact.setImg(file.getOriginalFilename()); // signature (7).png
				}

			}
			contactRepo.save(contact);
			session.setAttribute("message", new Message("Your contact is updated!!", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong ", "danger")); // error message.
		}
		// redirecting it to the showContactDetails Page..
		return "redirect:/user/contact/" + contact.getId();
	}

	@GetMapping("/profile")
	public String yourProfile() {
		return "normalUser/user_profile";
	}

	/*
	 * This function opens the jsp page for settings.
	 */
	@GetMapping("/settings")
	public String openSettings() {
		return "normalUser/setting";
	}

	/*
	 * This function process the update activity. if i want to redirect user to
	 * login page after changing the password ---> return "redirect:/logout";
	 */
	@PostMapping("/updatePassword")
	public String changePassword(@RequestParam("newPassword") String newPassword,
			@RequestParam("oldPassword") String oldPassword, @RequestParam("confirmPassword") String confirmPassword,
			Principal principal, HttpSession session) {

		User currentUser = userRepo.findByUsername(principal.getName());

		if (newPassword != null && oldPassword != null && confirmPassword != null) {

			// if the user enters the correct old password`
			if (passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
				System.out.println("previous password matched");

				if (newPassword.equals(confirmPassword)) {

					System.out.println("Your password has been changed");
					session.setAttribute("message", new Message("Your password is updated", "success"));
					currentUser.setPassword(passwordEncoder.encode(newPassword));
					this.userRepo.save(currentUser);
					return "redirect:/user/dashboard";

				} else {

					session.setAttribute("message", new Message("Your new passwords didnot match !!!", "danger"));
					System.out.println(" You entered incorrect new password to change");
					return "normalUser/setting";
				}
			}

			else {
				session.setAttribute("message", new Message("Please enter correct old password!!!", "danger"));
				System.out.println("Incorrect oldl password");
				return "normalUser/setting";
			}
		} else {
			session.setAttribute("message", new Message("Enter all the filed", "danger"));
			return "normalUser/setting";
		}
	}
}
