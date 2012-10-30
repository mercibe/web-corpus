package com.servicelibre.repositories.ui;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.ui.Rôle;

public interface RôleRepository extends CrudRepository<Rôle, Long> {

	Rôle findByNom(String nom);
}
