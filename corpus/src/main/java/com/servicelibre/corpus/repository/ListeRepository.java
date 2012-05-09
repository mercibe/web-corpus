package com.servicelibre.corpus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.servicelibre.corpus.entity.CatégorieListe;
import com.servicelibre.corpus.entity.Liste;

public interface ListeRepository extends CrudRepository<Liste, Long> {

	Liste findByNom(String nom);
	
	List<Liste> findByCatégorie(CatégorieListe catégorieListe);

	@Query("select MAX(l.ordre) from Liste l")
	Number findMaxOrdre();

	@Query("select l from Liste l where l.corpus.id = :corpus_id order by l.ordre, l.nom")
	List<Liste> findByCorpusId(@Param("corpus_id") int corpus_id);

}
