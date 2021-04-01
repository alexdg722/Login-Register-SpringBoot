package net.dg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import net.dg.model.ConfirmationToken;
import net.dg.model.User;
import net.dg.repository.ConfirmationTokenRepository;
import net.dg.repository.UserRepository;
import net.dg.service.EmailService;
import net.dg.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;
	
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/login")
	public String UserLogin(Model model) {
	
		return "login";
	}
	
	@GetMapping("/profile")
	public String UserHomePage() {
		return "profile";
	}
	
	
	
	@RequestMapping("/register")
	public String viewHomePage(ModelAndView modelAndView, User user) {
		
		modelAndView.addObject("user", user);
		modelAndView.setViewName("register");
		return "register";
	}

	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView registerUser(ModelAndView modelAndView, User user) {
		
		User existingUser = userRepository.findByEmail(user.getEmail());
		if(existingUser != null) {
			modelAndView.addObject("message", "This email already exists!");
			modelAndView.setViewName("register");
		}
		else {
		
			userService.saveUser(user);
			ConfirmationToken confirmationToken = new ConfirmationToken(user);
			confirmationTokenRepository.save(confirmationToken);
			
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setTo(user.getEmail());
			mailMessage.setSubject("Complete Registration");
			mailMessage.setFrom("javaprojects1999@gmail.com");
			mailMessage.setText("To confirm your account, please click here: "
					+ "http://localhost:8080/confirm-account?token=" 
					+ confirmationToken.getConfirmationToken());
			
			emailService.sendEmail(mailMessage);
			modelAndView.addObject("email", user.getEmail());
			modelAndView.setViewName("succesfulRegistration");
		}
		
		return modelAndView;
		
	}
	
	@RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String confirmationToken) {
		
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
		
		if(token != null) {
			User user = userRepository.findByEmail(token.getUser().getEmail());
			user.setEnabled(true);
			userRepository.save(user);
			modelAndView.setViewName("accountVerified");
		}
		else {
			modelAndView.addObject("message","true");
            modelAndView.setViewName("accountVerified");
		}
		
		return modelAndView;
	}
	
	
}
