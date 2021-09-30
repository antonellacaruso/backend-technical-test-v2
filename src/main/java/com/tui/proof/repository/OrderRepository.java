package com.tui.proof.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tui.proof.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

	Order findAllByClientId(Long id);

	Long findClientByNumber(Long number);

	Order findOrderByNumber(String number);

	int deleteOrderById(Long id);
}
