package com.servicelibre.repositories.corpus;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.Corpus;

public interface CorpusRepository extends CrudRepository<Corpus, Long> {

	Corpus findByNom(String nom);

	Corpus findByParDéfaut(boolean parDéfaut);

}
