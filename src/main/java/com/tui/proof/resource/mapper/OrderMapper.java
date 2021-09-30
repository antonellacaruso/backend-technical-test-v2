package com.tui.proof.resource.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.Order;
import com.tui.proof.resource.request.OrderResource;
import com.tui.proof.resource.response.OrderDetails;
import com.tui.proof.utils.OrderUtil;

@Service
public class OrderMapper {

	public Order orderResourceToOrder(OrderResource orderResource) {
		Address deliveryAddress = new Address(orderResource.getCity(), orderResource.getStreet(),
				orderResource.getCountry(), orderResource.getPostcode());
		Client client = new Client(orderResource.getFirstName(), orderResource.getLastName(),
				orderResource.getTelephone(), orderResource.getEmail(), deliveryAddress);
		return new Order(orderResource.getNumber(), orderResource.getPilotes(), orderResource.getOrderTotal(), client);
	}

	public OrderDetails mapOrderDetails(Order order) {
		OrderDetails orderResponse = new OrderDetails();
		orderResponse.setOrderTotal(order.getOrderTotal());
		orderResponse.setId(order.getId().toString());
		orderResponse.setNumber(order.getNumber());
		orderResponse.setOrderStatus(order.getStatus());
		orderResponse.setOrderPlacedTime(order.getPlacedAt());
		return orderResponse;
	}

	public OrderResource mapOrderResourceDetails(Order order) {
		OrderResource orderResource = new OrderResource();
		Client client = order.getClient();
		(new ArrayList<Address>(client.getAddresses())).stream().forEach(address -> {
			orderResource.setStreet(address.getStreet());
			orderResource.setPostcode(address.getPostcode());
			orderResource.setCity(address.getCity());
			orderResource.setCountry(address.getCountry());
		});
		orderResource.setNumber(order.getNumber());
		orderResource.setPilotes(order.getPilotes());
		orderResource.setOrderTotal(order.getOrderTotal());
		orderResource.setFirstName(client.getFirstName());
		orderResource.setLastName(client.getLastName());
		orderResource.setEmail(client.getEmail());
		orderResource.setTelephone(client.getTelephone());
		orderResource.setOrderPlacedTime(OrderUtil.convertToDateViaInstant(order.getPlacedAt()));
		return orderResource;
	}

	public List<OrderResource> mapOrderListDetails(List<Order> orders) {
		List<OrderResource> lists = new ArrayList<OrderResource>();
		orders.stream().forEach(order -> lists.add(mapOrderResourceDetails(order)));
		return lists;
	}
}
