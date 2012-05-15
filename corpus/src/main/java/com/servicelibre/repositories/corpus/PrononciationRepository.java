package com.servicelibre.repositories.corpus;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.Prononciation;

public interface PrononciationRepository extends CrudRepository<Prononciation, Long> {

	Prononciation findByPrononciation(String prononciation);

	List<Prononciation> findAll();
}
