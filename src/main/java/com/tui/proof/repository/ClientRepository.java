package com.tui.proof.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tui.proof.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

	List<Client> findAllByFirstName(Example<Client> of);
}
