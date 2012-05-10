package com.servicelibre.corpus.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.servicelibre.corpus.entity.CatégorieListe;
import com.servicelibre.corpus.entity.Corpus;

public interface CatégorieListeRepository extends CrudRepository<CatégorieListe, Long> {

	CatégorieListe findByNom(String nom);
	
	List<CatégorieListe> findByCorpus(Corpus corpus);
	
	List<CatégorieListe> findByCorpus(Corpus corpus, Sort sort);

}
