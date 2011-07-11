package com.servicelibre.corpus.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;


@Transactional
public interface MotManager {


	enum Condition {ENTIER, COMMENCE_PAR, FINIT_PAR, CONTIENT};

    List<Mot> findAll();
	
	Mot findOne(long motId);

	List<Mot> findByMot(String mot);
	
	List<Mot> findByGraphie(String graphie, Condition condition);

	Mot save(Mot mot);

    List<Mot> findByGraphie(String value, Condition valueOf, FiltreMot filtres);
}
