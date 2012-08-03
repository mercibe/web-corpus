package com.servicelibre.repositories.ui;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.ui.Onglet;

public interface OngletRepository extends CrudRepository<Onglet, Long> {

	List<Onglet> findAll(Sort sort);

}
