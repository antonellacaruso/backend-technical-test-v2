package com.tui.proof.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "clients")
@AllArgsConstructor
@NoArgsConstructor
public class Client {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_generator")
	@SequenceGenerator(name = "client_id_generator", sequenceName = "client_id_sequence", allocationSize = 1)
	private Long id;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "id", referencedColumnName = "id")
	private List<Address> addresses;
	
	private String firstName;
	private String lastName;
	private String telephone;
	private String email;	

	@OneToMany(mappedBy = "client", cascade = { CascadeType.ALL, CascadeType.PERSIST })
	private Set<Order> orders;

	public Client(String firsName, String lastName, String telephone, String email, Address deliveryAddress) {
		this.firstName = firsName;
		this.lastName = lastName;
		this.email = email;
		this.telephone = telephone;
		addresses = new ArrayList<Address>();
		addresses.add(deliveryAddress);
	}

	public Client(String firstName, String lastName, String telephone, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.telephone = telephone;

	}

	

}
