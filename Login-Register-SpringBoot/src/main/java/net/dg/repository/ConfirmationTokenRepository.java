package net.dg.repository;

import net.dg.model.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("confirmationTokenRepository")
public interface ConfirmationTokenRepository 
		extends CrudRepository<ConfirmationToken, String> {
	
	ConfirmationToken findByConfirmationToken(String confirmationToken);
	

}
