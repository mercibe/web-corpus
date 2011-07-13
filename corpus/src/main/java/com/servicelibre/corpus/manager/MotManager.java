package com.servicelibre.corpus.manager;

import java.util.List;

import javax.persistence.EntityManager;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;

public interface MotManager {

    enum Condition {
	ENTIER, COMMENCE_PAR, FINIT_PAR, CONTIENT
    };

    List<Mot> findAll();

    Mot findOne(long motId);

    List<Mot> findByMot(String mot);

    List<Mot> findByGraphie(String graphie, Condition condition);

    Mot save(Mot mot);

    int deleteFromListe(Liste liste);

    List<Mot> findByGraphie(String value, Condition valueOf, FiltreMot filtres);

	EntityManager getEntityManager();

	void setEntityManager(EntityManager entityManager);

	int ajoutePrononciation(String forme, String phonétique);
}
