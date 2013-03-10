package com.servicelibre.repositories.corpus;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;

public interface ListeMotRepository extends CrudRepository<ListeMot, Long> {

	List<ListeMot> findByListe(Liste liste);
	List<ListeMot> findByMot(Mot mot);
	
	ListeMot findByListeAndMot(Liste liste, Mot mot);
	
	List<ListeMot> findAll();
	
}
