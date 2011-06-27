package com.servicelibre.corpus.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Liste;

@Transactional
public interface ListeManager // extends JpaRepository<Liste, Long>
{

	Liste findOne(long listeId);

	Liste findByNom(String nom);
	
	List<Liste> findByCorpusId(long corpusId);
	
	Liste save(Liste liste);

}
