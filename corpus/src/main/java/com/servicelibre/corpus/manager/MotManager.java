package com.servicelibre.corpus.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.liste.Liste;


@Transactional
public interface MotManager {


	enum Condition {MOT_ENTIER, MOT_COMMENCE_PAR, MOT_FINIT_PAR, MOT_CONTIENT};

    List<Mot> findAll();
	
	Mot findOne(long motId);

	Mot findByMot(String mot);
	
	List<Mot> findByGraphie(String graphie, Condition condition);

	Mot save(Mot mot);

	int deleteFromListe(Liste liste);

    List<Mot> findByGraphie(String value, Condition valueOf, FiltreMot filtres);
}
