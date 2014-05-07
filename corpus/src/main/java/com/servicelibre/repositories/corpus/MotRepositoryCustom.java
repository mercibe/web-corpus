package com.servicelibre.repositories.corpus;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.corpus.manager.Ordre;
import com.servicelibre.entities.corpus.Mot;

public interface MotRepositoryCustom {

	public enum Condition {
		ENTIER, COMMENCE_PAR, FINIT_PAR, CONTIENT
	};

	public int ajoutePrononciation(String forme, String phonétique);

	void ajoutePrononciation(Mot mot, String phonétique);

	MotRésultat findByGraphie(String graphie, Condition condition, boolean rôleAdmin);

	MotRésultat findByGraphie(String graphie, Condition condition, FiltreRecherche filtres, boolean rôleAdmin);

	MotRésultat findByGraphie(String graphie, Condition condition, FiltreRecherche filtres, boolean rôleAdmin, List<Ordre> ordres);

	MotRésultat findByGraphie(String graphie, Condition condition, FiltreRecherche filtres, boolean rôleAdmin, List<Ordre> ordres,
			int deIndex, int taillePage);

	MotRésultat findByPrononciation(String prononciation, Condition condition, FiltreRecherche filtres, boolean rôleAdmin);

	MotRésultat findByPrononciation(String prononciation, Condition condition, FiltreRecherche filtres, boolean rôleAdmin, List<Ordre> ordres);

	MotRésultat findByPrononciation(String prononciation, Condition condition, FiltreRecherche filtres, boolean rôleAdmin,
			List<Ordre> ordres, int deIndex, int taillePage);

	public class MotRésultat {
		public List<Mot> mots = new ArrayList<Mot>();
		public int deIndex = 0;
		public int taillePage = 0;
		public long nbTotalMots = 0;
		
	}
	
}
