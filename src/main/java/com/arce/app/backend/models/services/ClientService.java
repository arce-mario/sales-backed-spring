package com.arce.app.backend.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arce.app.backend.models.dao.ClientDaoInterface;
import com.arce.app.backend.models.entities.Client;

@Service
public class ClientService implements ClientServiceInterface {
	@Autowired
	private ClientDaoInterface clientDao;

	@Override
	@Transactional(readOnly = true)
	public List<Client> findAll() {
		return (List<Client>) clientDao.findAll();
	}

	@Override
	public Client save(Client client) {
		return clientDao.save(client);
	}

	@Override
	public void delete(long id) {
		clientDao.deleteById(id);
	}

	@Override
	public Client findByID(long id) {
		return clientDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Client> findAll(Pageable pageable) {
		return clientDao.findAll(pageable);
	}
}
