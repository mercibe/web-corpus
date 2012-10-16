package com.servicelibre.repositories.ui;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.ui.Onglet;
import com.servicelibre.entities.ui.Paramètre;

public interface ParamètreRepository extends CrudRepository<Paramètre, Long> {

	//List<Paramètre> findAll(Sort sort);
	Paramètre findByNom(String nom);
}
