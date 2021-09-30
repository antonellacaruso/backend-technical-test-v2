package com.tui.proof.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.google.i18n.phonenumbers.NumberParseException;
import com.tui.proof.enums.OrderStatus;
import com.tui.proof.model.Client;
import com.tui.proof.model.Order;
import com.tui.proof.resource.mapper.OrderMapper;
import com.tui.proof.resource.request.OrderResource;
import com.tui.proof.resource.response.OrderDetails;
import com.tui.proof.service.OrderServiceImpl;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/pilotes/orders")
public class OrderController {

	@Autowired
	private OrderServiceImpl orderService;
	@Autowired
	private OrderMapper mapperService;

	@RequestMapping(value = "/place", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderDetails> placeorder(@Valid @RequestBody OrderResource orderResource)
			throws NumberParseException {
		Order placedOrder = orderService.createOrder(orderResource);
		if (!placedOrder.getStatus().equals(OrderStatus.PLACED.toString())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(mapperService.mapOrderDetails(placedOrder), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderDetails> updateOrder(@Valid @RequestBody OrderResource orderResource)
			throws NumberParseException {
		Order placedOrder = orderService.updateOrder(orderResource);
		if (!placedOrder.getStatus().equals(OrderStatus.UPDATED.toString())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(mapperService.mapOrderDetails(placedOrder), HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderDetails> deleteOrder(@Valid @RequestBody OrderResource orderResource)
			throws NumberParseException {
		Order order = orderService.delete(orderResource);
		if (!order.getStatus().equals(OrderStatus.DELETED.toString())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(mapperService.mapOrderDetails(order), HttpStatus.OK);
	}

	@RequestMapping(value = "/getOrders", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OrderResource>> getOrders() {
		List<Order> orders = orderService.getOrders();
		if (orders.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(mapperService.mapOrderListDetails(orders), HttpStatus.OK);
	}

	@RequestMapping(value = "/getOrdersByClient", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OrderResource>> getOrdersByClient(@RequestBody Client client) {
		List<Order> orders = orderService.getOrdersByClient(client);
		return new ResponseEntity<>(mapperService.mapOrderListDetails(orders), HttpStatus.OK);
	}

	@RequestMapping(value = "/getOrderbyId/{order_number}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderResource> getOrder(@Valid @PathVariable("order_number") String orderNumber) {
		Order order = orderService.getOrder(orderNumber);
		if (order == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(mapperService.mapOrderResourceDetails(order), HttpStatus.OK);
	}

}
