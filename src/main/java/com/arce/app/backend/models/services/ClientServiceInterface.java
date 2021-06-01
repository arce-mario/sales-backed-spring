package com.arce.app.backend.models.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.arce.app.backend.models.entities.Client;

public interface ClientServiceInterface {
	public List<Client> findAll();
	public Page<Client> findAll(Pageable pageable);
	public Client save(Client client);
	public void delete(long id);
	public Client findByID(long id);
}