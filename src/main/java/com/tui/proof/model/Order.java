package com.tui.proof.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	private String number;
	private int pilotes;
	private double orderTotal;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_generator")
	@SequenceGenerator(name = "order_id_generator", sequenceName = "order_id_sequence", allocationSize = 1)
	Long id;

	private LocalDateTime placedAt;
	private String status;
	@ManyToOne(cascade = { CascadeType.ALL, CascadeType.PERSIST })
	@JoinColumn(name = "client_id")
	private Client client;

	public Order(String number, int pilotes, double orderTotal, Client client) {
		this.number = number;
		this.pilotes = pilotes;
		this.orderTotal = orderTotal;
		this.client = client;
	}

}
