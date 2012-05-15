package com.servicelibre.repositories.corpus;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Corpus;

public interface CatégorieListeRepository extends CrudRepository<CatégorieListe, Long> {

	CatégorieListe findByNom(String nom);

	List<CatégorieListe> findByCorpus(Corpus corpus);

	List<CatégorieListe> findByCorpus(Corpus corpus, Sort sort);

	List<CatégorieListe> findAll();

	List<CatégorieListe> findAll(Sort sort);

}
