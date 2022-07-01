package com.smart.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dto.OTPDto;
import com.smart.entity.OTP;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.OTPRepository;
import com.smart.repository.UserRepository;
import com.smart.service.OTPGeneration;
import com.smart.util.EmailUtilImpl;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private OTPRepository otpRepo;

	@Autowired
	private EmailUtilImpl emailUtilImpl;

	@Autowired
	private OTPGeneration otpGeneration;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private static Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

//```in request mapping by default it is getMapping`
	@RequestMapping(value = "/")
	public String goToHomePage(Model model) {
		model.addAttribute("title", "Home-Smart Contact Manager");
		return "home";
	} 

	// `
	@RequestMapping(value = "/about")
	public String aboutPage(Model model) {
		model.addAttribute("title", "About-Smart Contact Manager");
		return "about";
	}

	@RequestMapping(value = "/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register-Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

//~`
	@PostMapping("/doRegister")
	public String doRegister(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean isChecked, HttpSession session) {

		try {
			// System.out.println("isChecked " + isChecked); `````
			if (isChecked == false) {
				System.out.println("is checked:::: " + isChecked);
				throw new Exception("You havenot agreed the terms and conditions");
			}
			if (bindingResult.hasErrors()) {
				for (ObjectError er : bindingResult.getAllErrors()) {
					LOGGER.info("errors :::" + er);
				}
				return "signup";
			}

			user.setRole("ROLE_NORMAL");
			// System.out.println("set role");
			user.setEnabled(true);
			// System.out.println("set enabled");``
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			// System.out.println("encoder error");
			User savedUser = userRepo.save(user);
 
			LOGGER.info("user details are--->{}", savedUser);
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
			return "login";

		} catch (Exception e) {

			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-danger"));
			e.printStackTrace();
			return "signup";
		}
	}

	@RequestMapping(value = "/login")
	public String login(Model model) {
		model.addAttribute("title", "Login-Smart Contact Manager");
		return "login";
	}

	/*
	 * After clicking on the forgot password link
	 */
	@GetMapping("/forgot-Password")
	public String forgotPassword() {
		return "forgot_password";
	}

	@PostMapping("/doGenerateOTP")
	public String sendOTP(@RequestParam("username") String username, HttpSession session, Model model) {

		if (username != null) {
			User user = userRepo.findByUsername(username);

			// If the user is valid
			if (user != null) {

				Integer otp = otpGeneration.generateOTP(1200, 9900);// generate otp and send it to the entered email.
				System.out.println("Generated OTP is:::" + otp);

				// Sending the OTP in the mail..
				emailUtilImpl.sendEmail(username, "Your OTP for changing the password", "OTP is::" + otp);

				// !!!!!!!! deleting the previous OTP if present. !!!!!!
				// find the OTP for the user...
				OTP previousOTP = otpRepo.findByUser(user);
				{
					if (previousOTP != null) {
						// delete the oTP
						otpRepo.deleteById(previousOTP.getId());

					}
				}
				// saving the OTP to the database.
				OTP savedOTP = otpRepo.save(new OTP(user, otp));
				System.out.println("Saved OTP is:: " + savedOTP);

				model.addAttribute("otpDto", new OTPDto());
				System.out.println("OTP DTO IS::: " + new OTPDto());
				model.addAttribute("otpID", savedOTP.getId());
				return "verify-otp";

			}
			session.setAttribute("message", new Message("Username doesnot exist !!", "alert-danger"));
			return "forgot_password";

		} else {
			session.setAttribute("message", new Message("Please Enter the field !!", "alert-danger"));
			return "forgot_password";
		}

	}

	/*
	 * This method verifies whether the user input OTP is correct or not by
	 * comparing with the OTP saved for the specific ID
	 */
	@PostMapping("/do-verify-OTP")
	public String verifyOTP(@Valid @ModelAttribute("otpDto") OTPDto otpDto, BindingResult bindingResult,
			@RequestParam("otpID") String otpID, Model model) throws ParseException {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LocalDateTime presentDate = LocalDateTime.now();
		if (bindingResult.hasErrors()) {
			System.out.println("-------Binding error has occured------");
			return "verify-otp";
		}
		// verify the OTP by comparing it with the database....
		OTP otp = otpRepo.findById(Long.valueOf(otpID)).get();
		int savedOTP = otp.getOTP();
		int enteredOTP = otpDto.getOtp();

		if (savedOTP == enteredOTP) {

			/*
			 * ------------------MAIN LOGIC--- --------------------------------------
			 * 
			 * Convert different form of dates to same form Date using sdf.parse()
			 * sdf.parse(string) --> takes String as a input
			 * 
			 * _____________We are using parse function so that it becomes easier while
			 * comparing two dates using same format.__________________________________
			 * 
			 * We can easily convert any date format easily to string using .toString()
			 * method.
			 * 
			 * 
			 */
			String cd = dtf.format(presentDate);
			Date currentDate = sdf.parse(cd); // Sat Mar 19 10:23:10 NPT 2022
			Date createdDate = otp.getCreatedDate(); // 2022-03-19 10:23:01.85

			long difference_In_Time = currentDate.getTime() - createdDate.getTime();

			long difference_In_Minutes = TimeUnit.MILLISECONDS.toMinutes(difference_In_Time) % 60;

			long difference_In_Seconds =TimeUnit.MILLISECONDS.toSeconds(difference_In_Time) % 60;
			// System.out.println("difference_In_Seconds " + difference_In_Seconds);

			
			
			// _________________________ Now we can write logic that_____________
			// IF the time is greater than 5 minute
			// ((( to be more accurate we can also use time difference in second )))
			// THEN, delete the otp from the database
			// & send the message stating you've exceed the time limit please re-request to send
			// otp.
			// ELSE , 
			// REDIRECT TO THE CREATE_NEW_PASSWORD

			model.addAttribute("userId", otp.getUser().getId());
			otpRepo.deleteById(Long.parseLong(otpID));
			return "create-new-password";
		}
		model.addAttribute("otpID", otpID);
		model.addAttribute("error", "!!!!!! Invalid OTP !!!!");
		return "verify-otp";

	}

	@PostMapping("/addNewPassword")
	public String changePassword(@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, @RequestParam("userId") Long userID,
			HttpSession session, Model model) {

		if (newPassword != null && confirmPassword != null) {
			System.out.println("Yohohho not nulll");
			if (newPassword.equals(confirmPassword)) {

				System.out.println("Your password has been changed");
				model.addAttribute("message", new Message("Your password is changed", "success"));
				System.out.println(userID);
				User user = userRepo.findById(userID).get();
				user.setPassword(passwordEncoder.encode(newPassword));
				this.userRepo.save(user);

				return "redirect:/login";

			} else {
				model.addAttribute("userId", userID);
				session.setAttribute("message", new Message("Your new passwords didnot match !!!", "danger"));
				System.out.println(" You entered incorrect new password to change");
				return "create-new-password";
			}

		} else {
			model.addAttribute("userId", userID);
			session.setAttribute("message", new Message("Enter all the filed", "danger"));
			return "create-new-password";
		}
	}

}
