package net.dg.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "confirmationToken")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConfirmationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "token_id")
	private long tokenid;

	@Column(name = "confirmation_token")
	private String confirmationToken;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;


	public ConfirmationToken(User user) {
		this.user = user;
		createDate = new Date();
		confirmationToken = UUID.randomUUID().toString();
	}


}
