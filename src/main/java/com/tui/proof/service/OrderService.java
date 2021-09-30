package com.tui.proof.service;

import java.util.List;

import com.google.i18n.phonenumbers.NumberParseException;
import com.tui.proof.model.Client;
import com.tui.proof.model.Order;
import com.tui.proof.resource.request.OrderResource;

public interface OrderService {
	Order createOrder(OrderResource orderResource) throws NumberParseException;

	Order updateOrder(OrderResource orderResource);

	Order delete(OrderResource orderResource);

	Order getOrder(String orderNumber);

	List<Order> getOrders();

	List<Order> getOrdersByClient(Client client);

}