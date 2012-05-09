package com.servicelibre.corpus.manager;

import java.util.List;

import javax.persistence.EntityManager;

import com.servicelibre.corpus.entity.Mot;

@Deprecated
public interface MotManager {

    enum Condition {
    	ENTIER, COMMENCE_PAR, FINIT_PAR, CONTIENT
    };

    List<Mot> findAll();

    Mot findOne(long motId);
    Mot findByMot(String lemme, String mot, String catgram, String genre);

    List<Mot> findByMot(String mot);

    List<Mot> findByGraphie(String graphie, Condition condition);

    Mot save(Mot mot);

    //int removeAllFrom(Liste liste);

    List<Mot> findByGraphie(String value, Condition valueOf, FiltreRecherche filtres);

    EntityManager getEntityManager();

    void setEntityManager(EntityManager entityManager);

    int ajoutePrononciation(String forme, String phonétique);

    List<Mot> findByPrononciation(String prononciation, Condition condition, FiltreRecherche filtres);

}
