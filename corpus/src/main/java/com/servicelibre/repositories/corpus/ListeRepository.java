package com.servicelibre.repositories.corpus;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Liste;

public interface ListeRepository extends CrudRepository<Liste, Long> {

	Liste findByNom(String nom);

	List<Liste> findByCatégorie(CatégorieListe catégorieListe, Sort sort);

	// List<Liste> findByCorpus(Corpus corpus);
	// List<Liste> findByCorpus(Corpus corpus, Sort sort);

	@Query("select MAX(l.ordre) from Liste l")
	Number findMaxOrdre();

	@Query("select l from Liste l where l.catégorie.corpus.id = :corpus_id order by l.ordre, l.nom")
	List<Liste> findByCorpusId(@Param("corpus_id") int corpus_id);

	Collection<? extends Liste> findAll(Sort sort);

	// @Query("select m FROM Mot m JOIN FETCH m.listeMots lm JOIN FETCH m.motPrononciations mp WHERE lm.liste = :liste order by lm.mot.mot")
	// List<Mot> tutu(long liste_id);

}
