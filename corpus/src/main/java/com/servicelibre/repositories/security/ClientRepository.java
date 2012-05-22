package com.servicelibre.repositories.security;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.security.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {

	Client findByNom(String nom);


}
