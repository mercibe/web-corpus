package com.servicelibre.corpus.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.ListeMot;

public interface ListeMotRepository extends CrudRepository<ListeMot, Long> {

	List<ListeMot> findByListe(Liste liste);

}
