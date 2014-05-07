package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.security.SecurityUtil;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.event.ListDataEvent;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.manager.Ordre;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.MotRepository;
import com.servicelibre.repositories.corpus.MotRepositoryCustom;
import com.servicelibre.repositories.corpus.MotRepositoryCustom.Condition;
import com.servicelibre.repositories.corpus.MotRepositoryCustom.MotRésultat;
import com.servicelibre.zk.recherche.Recherche;

public class MotsModelList<E> extends ListModelList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(MotsModelList.class);

	boolean rôleAdmin = SecurityUtil.isAnyGranted("ROLE_ADMINISTRATEUR");

	MotRepository motRepository = ServiceLocator.getMotRepo();

	private Recherche recherche;

	
	public MotsModelList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MotsModelList(E[] array) {
		super(array);
		// TODO Auto-generated constructor stub
	}

	public MotsModelList(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	public MotsModelList(Collection<? extends E> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public MotsModelList(List<E> list, boolean live) {
		super(list, live);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sort(Comparator<E> cmpr, boolean ascending) {

		if (cmpr instanceof FieldComparator) {
			// Créer la liste de Ordre
			List<Ordre> ordres = new ArrayList<Ordre>();
			FieldComparator fc = (FieldComparator) cmpr;
			// Split sur « , », puis sur le blanc
			// listePartitionPrimaire ASC,lemme ASC
			String[] ordresString = fc.getOrderBy().split(",");
			for (String ordreString : ordresString) {
				String[] ordreChampDirection = ordreString.trim().split(" ");
				ordres.add(new Ordre(ordreChampDirection[0].trim(), ordreChampDirection[1].trim().equals("ASC") ? true : false));
			}

			// Exécuter la mise à jour du modèle (exécution de la recherche avec tri BD)
			rafraîchi(ordres);

			fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
		} else {
			Collections.sort(_list, cmpr);
			fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
		}
	}

	public MotRésultat exécuterRecherche(Recherche recherche, int deIndex, int taillePage) {

		logger.info(recherche.getDescriptionChaîne());
		Condition conditionChaîne = MotRepositoryCustom.Condition.valueOf(recherche.précisionChaîne);
		logger.debug("MotRepositoryCustom.Condition.valueOf(recherche.précisionChaîne) = " + conditionChaîne);
		logger.debug("filtres: " + recherche.filtres);

		MotRésultat motRésultat = new MotRésultat();
		
		String chaîne = recherche.getChaîne();
		switch (recherche.cible) {
		case GRAPHIE:
			motRésultat = motRepository.findByGraphie(chaîne, conditionChaîne, recherche.filtres, rôleAdmin, recherche.ordres, deIndex, taillePage);
			break;
		case PRONONCIATION:
			motRésultat = motRepository.findByPrononciation(chaîne, conditionChaîne, recherche.filtres, rôleAdmin, recherche.ordres, deIndex, taillePage);

			// Est-ce que la chaîne recherchée contient au moins un des caractères suivant non suivi du « combining
			// tilde » ou du « : » ?
			Pattern pattern = Pattern.compile("ɛ(?![\u0303:])|ɔ(?!\u0303)|ɑ(?!\u0303)");
			Matcher matcher = pattern.matcher(chaîne);

			// Filtrer les résultats si un des caractères problématiques non suivi d'un caractère combiné est détecté
			// dans la chaîne
			if (matcher.find()) {
				motRésultat.mots = filtrePrononciations(chaîne, conditionChaîne, motRésultat.mots);
			}

			break;
		default:
			logger.error("Cible invalide pour une recherche de mots: " + recherche.cible);
			break;
		}
		
		recherche.grilleRésultatsPaging.setTotalSize((int) motRésultat.nbTotalMots);

		return motRésultat;
	}

	private List<Mot> filtrePrononciations(String chaîne, Condition conditionChaîne, List<Mot> mots) {

		List<Mot> motsFiltrés = new ArrayList<Mot>(mots.size());

		// COMBINING TILDE (̃) http://www.fileformat.info/info/unicode/char/303/index.htm

		// La chaîne devient une expression régulière
		StringBuffer regexp = new StringBuffer(chaîne.replaceAll("(ɛ(?![\u0303:]))", "(ɛ(?![\u0303:]))")
				.replaceAll("ɔ(?!\u0303)", "ɔ(?!\u0303)").replaceAll("ɑ(?!\u0303)", "ɑ(?!\u0303)"));

		// Convertir la chaîne en un pattern
		switch (conditionChaîne) {
		case COMMENCE_PAR:
			regexp.insert(0, "^\\[").append(".*");
			break;
		case CONTIENT:
			regexp.insert(0, ".*").append(".*");
			break;
		case ENTIER:
			regexp.insert(0, "^\\[").append("\\]$");
			break;
		case FINIT_PAR:
			regexp.insert(0, ".*").append("\\]$");
			break;
		default:
			break;

		}

		Pattern pattern = Pattern.compile(regexp.toString());
		Matcher matcher;

		logger.debug("Ne conserver que les prononciations qui matchent {}", regexp.toString());

		for (Mot mot : mots) {
			// FIXME: KO si prononciations multiples
			matcher = pattern.matcher(mot.getPrononciationsString());
			if (matcher.matches()) {
				motsFiltrés.add(mot);
			}
		}
		return motsFiltrés;
	}

	private int rafraîchi(List<Ordre> ordres) {
		this.recherche.ordres = ordres;
		if (this.recherche.grilleRésultatsPaging != null) {
			this.recherche.grilleRésultatsPaging.setActivePage(0);
			
			// récupération du controller
			CorpusCtrl corpusCtrl = (CorpusCtrl) this.recherche.grilleRésultatsPaging.getAttribute("controller");
			// Désélection de tous les items éventuellement sélectionnés
			corpusCtrl.résultatsSélectionnés.clear();
			
		}
		
		return rafraîchi(this.recherche);

	}

	@SuppressWarnings("unchecked")
	public int rafraîchi(Recherche recherche) {

		this.recherche = recherche;
		
		int taillePage = recherche.grilleRésultatsPaging.getPageSize();
		int pageActive = recherche.grilleRésultatsPaging.getActivePage();
		int deIndex = Math.max(0, pageActive * taillePage);
		
		MotRésultat motRésultat = exécuterRecherche(recherche, deIndex, taillePage);

		// Ne conserver en mémoire que les lignes à afficher
		//_list = (List<E>) mots.subList(deIndex, àIndex);
		
		_list = (List<E>) motRésultat.mots;
		
		return (int) motRésultat.nbTotalMots;
	}

}
