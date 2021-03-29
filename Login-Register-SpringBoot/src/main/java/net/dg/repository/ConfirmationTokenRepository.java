package net.dg.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import net.dg.model.ConfirmationToken;

@Repository("confirmationTokenRepository")
public interface ConfirmationTokenRepository 
		extends CrudRepository<ConfirmationToken, String> {
	
	ConfirmationToken findByConfirmationToken(String confirmationToken);
	

}
