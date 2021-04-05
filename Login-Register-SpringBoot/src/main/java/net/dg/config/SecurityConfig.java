package net.dg.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import net.dg.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private UserService userService;

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService);
		auth.setPasswordEncoder(passwordEncoder());
		return auth;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http.authorizeRequests()
	      .antMatchers("/register", "/confirm-account",
				  		"/forgot-password", "/confirm-reset/**",
				  		"/reset-password").permitAll()
	      .antMatchers("/profile/**").hasAuthority("USER")
	      .anyRequest().authenticated()
	      .and().formLogin()
	      .loginPage("/login")
	      .defaultSuccessUrl("/profile", true)
	      .permitAll()
	      .and()
	      .logout()
	      .invalidateHttpSession(true)
	      .clearAuthentication(true)
	      .deleteCookies("JSESSIONID")
	      .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	      .logoutSuccessUrl("/login?logout")
	      .permitAll();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{

		auth.authenticationProvider(authenticationProvider());
	}
}
