package com.arce.app.backend.controlles;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arce.app.backend.models.entities.Client;
import com.arce.app.backend.models.services.ClientServiceInterface;
@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClientController {

	@Autowired
	private ClientServiceInterface clientService;
	
	@GetMapping("/clients")
	public ResponseEntity<?> get(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size){
		
		if(page != null && size != null) {
			return new ResponseEntity<Page<Client>>(clientService.findAll(PageRequest.of(page, size)),HttpStatus.OK);
		}else {
			return new ResponseEntity<List<Client>>(clientService.findAll(),HttpStatus.OK);
		}
	}
	
	@GetMapping("clients/{id}")
	public ResponseEntity<?> getByID(@PathVariable Long id){
		Client client;
		Map<String, Object> response = new HashMap<>();
		
		try {
			client = clientService.findByID(id);
		} catch (DataAccessException exception) {
			response.put("message", "Ocurrió un error a nivel de base de datos");
			response.put("errors", String.format("%s: %s", exception.getMessage(), exception.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(client == null) {
			response.put("message", "Usuario no encontrado");
			response.put("error", "null");
		
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<Client>(client,HttpStatus.OK);
		}
	}
	
	@PostMapping("/clients")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<?> post(@Valid @RequestBody Client client, BindingResult result) {
		Client newClient;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			response.put("message", "Campos incorrectos");
			
			response.put("errors", result.getFieldErrors()
					.stream()
					.map(err -> String.format("El campo \"%s\" %s", err.getField(),err.getDefaultMessage()))
					.collect(Collectors.toList()));
			
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		try {
			client.setUpdateAt(new Date());
			newClient = clientService.save(client);
		} catch (DataAccessException exception) {
			response.put("message", "Ocurrió un error a nivel de base de datos");
			response.put("error", String.format("%s: %s", exception.getMessage(), exception.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("message", "Usuario creado");
		response.put("client", newClient);
		
		return new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	
	@PutMapping("/clients/{id}")
	public ResponseEntity<?> update(@PathVariable long id, @Valid @RequestBody Client client, BindingResult result ) {
		Client oldClient;
		
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			response.put("message", "Campos incorrectos");
			
			response.put("error", result.getFieldErrors()
					.stream()
					.map(err -> String.format("El campo \"%s\" %s", err.getField(),err.getDefaultMessage()))
					.collect(Collectors.toList()));
			
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		try {
			oldClient = clientService.findByID(id);
			
			if(oldClient == null) {
				response.put("message", "Usuario no encontrado");
				response.put("error", "null");
			
				return new ResponseEntity<Map<String, Object>>(response,HttpStatus.NOT_FOUND);
			}else {
				oldClient.setEmail(client.getEmail());
				oldClient.setFirstname(client.getFirstname());
				oldClient.setLastname(client.getLastname());
				oldClient.setPhone(client.getPhone());
				
				oldClient = clientService.save(oldClient);
				
				response.put("message", "Usuario actualizado");
				response.put("client", oldClient);
				
				return new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
			}
			
		} catch (DataAccessException exception) {
			response.put("message", "Ocurrió un error a nivel de base de datos");
			response.put("error", String.format("%s: %s", exception.getMessage(), exception.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/clients/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			clientService.delete(id);
			response.put("message", "Cliente eliminado");
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
			
		} catch (DataAccessException exception) {
			response.put("message", "No se logró eliminar el cliente");
			response.put("error", String.format("%s: %s", exception.getMessage(), exception.getMostSpecificCause().getMessage()));
			
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}