package net.dg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import net.dg.model.ConfirmationToken;
import net.dg.model.User;
import net.dg.repository.ConfirmationTokenRepository;
import net.dg.repository.UserRepository;
import net.dg.service.EmailService;
import net.dg.service.UserService;

import javax.validation.Valid;

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

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
    public ModelAndView registerUser(ModelAndView modelAndView, @Valid final User user,
                                     BindingResult bindingResult) {

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            modelAndView.addObject("message", "This email already exists!");
            modelAndView.setViewName("register");
        }

        if (bindingResult.hasErrors()) {

            modelAndView.setViewName("register");
        } else {

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

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {

        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            user.setEnabled(true);
            user.setConfirmPassword(user.getPassword());
            user.setConfirmEmail(user.getEmail());
            userRepository.save(user);
            modelAndView.setViewName("accountVerified");
        } else {
            modelAndView.addObject("message", "true");
            modelAndView.setViewName("accountVerified");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("forgotPassword");
        return modelAndView;
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public ModelAndView forgotUserPassword(ModelAndView modelAndView, User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null) {

            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);
            confirmationTokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setSubject("Complete Password Reset");
            mailMessage.setFrom("javaprojects1999@gmail.com");
            mailMessage.setText("Tom complete the password reset, please click here: "
                    + "http://localhost:8080/confirm-reset?token=" + confirmationToken.getConfirmationToken());

            emailService.sendEmail(mailMessage);

            modelAndView.addObject("succes", "Request to reset password received" +
                    ", check your inbox for the reset link.");
            modelAndView.setViewName("forgotPassword");
        } else {
            modelAndView.addObject("error", "This email does not exist!");
            modelAndView.setViewName("forgotPassword");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/confirm-reset", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView,
                                           @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            user.setEnabled(true);

            userRepository.save(user);
            modelAndView.addObject("user", user);
            modelAndView.addObject("email", user.getEmail());
            modelAndView.setViewName("resetPassword");
        } else {
            modelAndView.addObject("error", "The link is invalid or broken!");
            modelAndView.setViewName("resetPassword");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {
        if (user.getEmail() != null) {
            User tokenUser = userRepository.findByEmail(user.getEmail());
            tokenUser.setEnabled(true);
            tokenUser.setPassword(encoder.encode(user.getPassword()));
            tokenUser.setConfirmPassword(tokenUser.getPassword());
            tokenUser.setConfirmEmail(user.getEmail());

            userRepository.save(tokenUser);
            modelAndView.addObject("succes", "Password succesfully reseted." +
                    "You can now log in with the new credentials.");
            modelAndView.setViewName("resetPassword");
        } else {
            modelAndView.addObject("error", "The link is invalid or broken!");
            modelAndView.setViewName("resetPassword");
        }
        return modelAndView;
    }


}
