package com.servicelibre.corpus.repository;

import java.util.List;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.manager.FiltreRecherche;

public interface MotRepositoryCustom {

	public enum Condition {
		ENTIER, COMMENCE_PAR, FINIT_PAR, CONTIENT
	};


	public int ajoutePrononciation(String forme, String phonétique);

	List<Mot> g(String graphie, Condition condition);

	List<Mot> g(String graphie, Condition condition, FiltreRecherche filtres);

	List<Mot> p(String prononciation, Condition condition, FiltreRecherche filtres);
	

}
