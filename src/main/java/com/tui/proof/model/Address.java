package com.tui.proof.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses") 
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_generator")
	@SequenceGenerator(name = "order_id_generator", sequenceName = "order_id_sequence", allocationSize = 1)
	Long idAddress;
	
	private String street;
	private String postcode;
	private String city;
	private String country;
	
	@ManyToOne(cascade = { CascadeType.ALL, CascadeType.PERSIST })
	@JoinColumn(name = "id")
	private Client client;

	public Address(String city, String street, String country, String postcode) {
		this.city = city;
		this.street = street;
		this.country = country;
		this.postcode = postcode;
	}

	
}
