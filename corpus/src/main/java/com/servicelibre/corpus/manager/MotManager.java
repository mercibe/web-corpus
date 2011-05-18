package com.servicelibre.corpus.manager;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.Mot;

@Transactional
public interface MotManager {

	Mot findOne(long motId);

	Mot findByMot(String mot);

	Mot save(Mot mot);

	int deleteFromListe(Liste liste);
}
