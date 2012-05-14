package com.servicelibre.corpus.repository;

import java.util.List;

import javax.persistence.OrderBy;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.servicelibre.corpus.entity.CatégorieListe;
import com.servicelibre.corpus.entity.Liste;

public interface ListeRepository extends CrudRepository<Liste, Long> {

	Liste findByNom(String nom);

	@OrderBy("ordre")
	List<Liste> findByCatégorie(CatégorieListe catégorieListe);

	// List<Liste> findByCorpus(Corpus corpus);
	// List<Liste> findByCorpus(Corpus corpus, Sort sort);

	@Query("select MAX(l.ordre) from Liste l")
	Number findMaxOrdre();

	@Query("select l from Liste l where l.catégorie.corpus.id = :corpus_id order by l.ordre, l.nom")
	List<Liste> findByCorpusId(@Param("corpus_id") int corpus_id);

}
