package com.servicelibre.repositories.security;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.security.Consommateur;

public interface ConsommateurRepository extends CrudRepository<Consommateur, Long> {

	Consommateur findByNom(String nom);


}
