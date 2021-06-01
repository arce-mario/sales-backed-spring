package com.arce.app.backend.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arce.app.backend.models.entities.Client;

public interface ClientDaoInterface extends JpaRepository<Client, Long> {
	
}
