package com.tui.proof.resource.request;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResource {
	@NotNull(message = "number is required")
	@Size(min = 1, max = 30)
	String number;
	@NotNull(message = "number is required")
	// @Pattern(regexp = "^([5]|[10]|[15])")
	private int pilotes;
	Date orderPlacedTime;
	@NotNull(message = "cost is required")
	@DecimalMin(value = "0.1")
	double orderTotal;

	@NotBlank(message = "client name is required")
	String firstName;
	@NotBlank(message = "client surname is required")
	String lastName;
	@NotBlank(message = "telephone is required")
	private String telephone;
	@NotBlank(message = "email is required")
	@Email(message = "Email should be valid")
	private String email;

	@NotBlank(message = "street is required")
	private String street;
	@NotBlank(message = "postcode is required")
	private String postcode;
	@NotBlank(message = "city is required")
	private String city;
	private String country;

}