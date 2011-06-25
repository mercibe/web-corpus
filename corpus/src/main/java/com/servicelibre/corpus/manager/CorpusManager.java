package com.servicelibre.corpus.manager;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Corpus;


@Transactional(readOnly = true)
public interface CorpusManager // extends JpaRepository<Corpus, Long>
{

	Corpus findOne(long corpusId);

	Corpus findByNom(String nom);

	Corpus save(Corpus corpus);

	void flush();
}
