package com.servicelibre.repositories.corpus;

import java.util.List;

import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.entities.corpus.Mot;

public interface MotRepositoryCustom {

	public enum Condition {
		ENTIER, COMMENCE_PAR, FINIT_PAR, CONTIENT
	};


	public int ajoutePrononciation(String forme, String phonétique);

	List<Mot> findByGraphie(String graphie, Condition condition);

	List<Mot> findByGraphie(String graphie, Condition condition, FiltreRecherche filtres);

	List<Mot> findByPrononciation(String prononciation, Condition condition, FiltreRecherche filtres);
	

}
