package com.tui.proof.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource;
import com.tui.proof.enums.OrderStatus;
import com.tui.proof.model.Client;
import com.tui.proof.model.Order;
import com.tui.proof.repository.ClientRepository;
import com.tui.proof.repository.OrderRepository;
import com.tui.proof.resource.mapper.OrderMapper;
import com.tui.proof.resource.request.OrderResource;
import com.tui.proof.utils.OrderUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Optional;

import javax.validation.ValidationException;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Value("#{'${pilotes.number}'.split(',')}")
	private List<Integer> pilotesOrdereable;

	@Value("${order.updateable.minutes}")
	private long orderUpdateableMinutes;

	@Autowired
	private OrderMapper orderMapper;

	@Override
	public Order createOrder(OrderResource orderResource) throws NumberParseException {
		Order order = orderMapper.orderResourceToOrder(orderResource);
		String orderNumber = orderResource.getNumber();
		if (validateOrder(order)) {
			order.setPlacedAt(LocalDateTime.now());
			order.setStatus(OrderStatus.PLACED.toString());
			log.info("Created order with id {} and number {}", order.getId(), orderNumber);
			return orderRepository.save(order);
		}
		order.setStatus(OrderStatus.INVALID.toString());

		log.error("Order: {} not created", orderNumber);
		return order;
	}

	@Override
	public Order updateOrder(OrderResource orderResource) {
		String orderNumber = orderResource.getNumber();
		Optional<Order> orderToFound = Optional.ofNullable(getOrder(orderNumber));
		Order orderFound = new Order();
		if (orderToFound.isPresent()) {
			orderFound = orderToFound.get();
			if (isUpdateable(orderFound) && validateOrder(orderFound)) {
				orderFound.setPlacedAt(LocalDateTime.now());
				orderFound.setStatus(OrderStatus.UPDATED.toString());
				orderFound = orderRepository.save(orderFound);
				log.info("Updated order with id {} and number {}", orderFound.getId(), orderNumber);
				return orderFound;

			} else {
				orderFound.setStatus(OrderStatus.INVALID.toString());
				log.error("Order: {} not updateable", orderNumber);
				throw new ValidationException("Order: " + orderNumber + " not updateable");
			}
		} else {
			orderFound.setStatus(OrderStatus.INVALID.toString());
			log.error("Order: {} not found", orderNumber);
			throw new ValidationException("Order: " + orderNumber + " not found");
		}
	}

	@Override
	public Order delete(OrderResource orderResource) {
		String orderNumber = orderResource.getNumber();
		Optional<Order> orderToFound = Optional.ofNullable(getOrder(orderNumber));
		Order orderFound = new Order();
		if (orderToFound.isPresent()) {
			orderFound = orderToFound.get();
			if (isUpdateable(orderFound)) {
				orderFound.setStatus(OrderStatus.DELETED.toString());
				orderRepository.deleteOrderById(orderFound.getId());
				log.info("Order: {} deleted", orderNumber);
				return orderFound;

			} else {
				orderFound.setStatus(OrderStatus.INVALID.toString());
				log.error("Order: {} not removeable", orderNumber);
				throw new ValidationException("Order: " + orderNumber + " cannot be removed - time over five minutes");
			}
		} else {
			orderFound.setStatus(OrderStatus.INVALID.toString());
			log.error("Order: {} not found", orderNumber);
			throw new ValidationException("Order: " + orderNumber + " not found");
		}
	}

	@Override
	public Order getOrder(String orderNumber) {
		Order order = orderRepository.findOrderByNumber(orderNumber);
		log.info("Found order with id {} and number {}", order.getId(), order.getNumber());
		return order;
	}

	@Override
	public List<Order> getOrders() {
		List<Order> orders = orderRepository.findAll();
		if (orders.isEmpty()) {
			log.error("Orders list not found");
			throw new ValidationException("No orders retrieved");
		}
		return orders;
	}

	@Override
	public List<Order> getOrdersByClient(Client client) {
		List<Order> orders = new ArrayList<Order>();
		log.info("Retrieving orders by {}", client);
		GenericPropertyMatcher containingMatcher = GenericPropertyMatcher.of(StringMatcher.CONTAINING);

		ExampleMatcher orderMatcher = ExampleMatcher.matchingAll().withMatcher("firstName", containingMatcher)
				.withMatcher("lastName", containingMatcher).withMatcher("telephone", containingMatcher)
				.withMatcher("email", containingMatcher).withIgnorePaths("id").withIgnorePaths("order")
				.withIgnorePaths("address");

		List<Client> clients = clientRepository.findAll(Example.of(client, orderMatcher));

		for (Client clientRetrieved : clients) {
			orders.add(orderRepository.findAllByClientId(clientRetrieved.getId()));
		}
		return orders;
	}

	private <T> boolean validateOrder(Order order) {
		String phone = order.getClient().getTelephone();
		PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
		PhoneNumber phoneNumber = null;

		if (!pilotesOrdereable.contains(order.getPilotes())) {
			log.error("Pilotes {} number not allowed", order.getPilotes());
			throw new ValidationException("Pilotes number not allowed");
		}
		try {
			phoneNumber = numberUtil.parse(phone, CountryCodeSource.UNSPECIFIED.name());
			if (!numberUtil.isValidNumber(phoneNumber)) {
				log.error("Telephone: {} not valid", phone);
				throw new ValidationException("Order phone {} not valid");
			}
		} catch (NumberParseException e) {
			log.error("Telephone {} not valid, error: {}", phoneNumber, e);
			throw new ValidationException(e.getMessage(), e.getCause());
		}
		return true;
	}

	private boolean isUpdateable(Order actualOrder) {
		LocalDateTime orderPlacedTime = actualOrder.getPlacedAt();
		long currentTime = new Date().getTime();
		long updateAllowedTime = OrderUtil.convertToDateViaInstant(orderPlacedTime).getTime()
				+ Duration.ofMinutes(orderUpdateableMinutes).toMillis();
		if (updateAllowedTime < currentTime) {
			return false;
		}
		return true;
	}
}