package net.dg.service;

import net.dg.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
	
	List<User> getAllUsers();
	User saveUser(User user);
	List<User> findByKeyboard(String keyboard);
	User getUserById(long id);
	void deleteUserById(long id);

}
