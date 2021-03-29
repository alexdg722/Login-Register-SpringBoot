package net.dg.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.dg.model.User;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {

	User findByEmail(String email);
}
