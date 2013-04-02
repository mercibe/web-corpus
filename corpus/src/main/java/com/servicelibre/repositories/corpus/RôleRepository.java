package com.servicelibre.repositories.corpus;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.Rôle;

public interface RôleRepository extends CrudRepository<Rôle, Long> {

	Rôle findByNom(String nom);
}
