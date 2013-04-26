package com.servicelibre.repositories.corpus;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;

public interface ListeMotRepository extends CrudRepository<ListeMot, Long> {

	List<ListeMot> findByListe(Liste liste);
	List<ListeMot> findByMot(Mot mot);
	
	ListeMot findByListeAndMot(Liste liste, Mot mot);
	
	List<ListeMot> findAll();
	
	@Modifying
	@Transactional
	@Query("delete from ListeMot lm where lm.liste=:liste")
	void deleteByListe(@Param("liste")Liste liste);
	
}
