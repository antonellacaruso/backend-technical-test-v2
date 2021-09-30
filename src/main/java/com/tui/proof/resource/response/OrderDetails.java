package com.tui.proof.resource.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderDetails {
	private String id;
	private String number;
	private double orderTotal;
	private LocalDateTime orderPlacedTime;
	private String OrderStatus;

}
