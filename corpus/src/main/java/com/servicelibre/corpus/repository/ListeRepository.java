package com.servicelibre.corpus.repository;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.corpus.entity.Liste;

public interface ListeRepository extends CrudRepository<Liste, Long> {

	Liste findByNom(String nom);

}
