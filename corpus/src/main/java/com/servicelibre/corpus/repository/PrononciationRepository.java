package com.servicelibre.corpus.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.corpus.entity.Prononciation;

public interface PrononciationRepository extends CrudRepository<Prononciation, Long> {

	Prononciation findByPrononciation(String prononciation);

	List<Prononciation> findAll();
}
