package com.servicelibre.corpus.repository;

import java.util.List;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.manager.FiltreRecherche;

public interface MotRepositoryCustom {

	public enum Condition {
		ENTIER, COMMENCE_PAR, FINIT_PAR, CONTIENT
	};


	public int ajoutePrononciation(String forme, String phonétique);

	List<Mot> findByGraphie(String graphie, Condition condition);

	List<Mot> findByGraphie(String graphie, Condition condition, FiltreRecherche filtres);

	List<Mot> findByPrononciation(String prononciation, Condition condition, FiltreRecherche filtres);
	

}
