package net.dg.service;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import net.dg.model.User;

public interface UserService extends UserDetailsService {
	
	List<User> getAllUsers();
	User saveUser(User user);
	List<User> findByKeyboard(String keyboard);
	User getUserById(long id);
	void deleteUserById(long id);

}
