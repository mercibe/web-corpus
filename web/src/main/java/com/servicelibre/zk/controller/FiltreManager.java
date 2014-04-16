package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.zkoss.spring.security.SecurityUtil;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.repositories.corpus.ListeRepository;

public abstract class FiltreManager {

	public static final String VIDE = "     ";

	// Définition valeur vide
	protected DefaultKeyValue keyValueVide = new DefaultKeyValue("-1", VIDE);

	protected List<Filtre> filtres = new ArrayList<Filtre>(3);

	protected CorpusService corpusService;

	protected ListeRepository listeRepo;

	private FiltreRecherche filtreActifModel;

	public FiltreManager() {
		super();
	}

	public List<DefaultKeyValue> getFiltreNoms() {

		List<DefaultKeyValue> noms = new ArrayList<DefaultKeyValue>(filtres.size());
		for (Filtre filtre : filtres) {
			noms.add(new DefaultKeyValue(filtre.nom, filtre.description));
		}

		return noms;
	}
	
	public List<DefaultKeyValue> getFiltreNomsVisibles(List<String> rôles) {
		
		List<DefaultKeyValue> noms = new ArrayList<DefaultKeyValue>(filtres.size());
		
		for (Filtre filtre : filtres) {
			if(filtre.nomRôle == null || filtre.nomRôle.isEmpty() || rôles.contains(filtre.nomRôle)) {
				noms.add(new DefaultKeyValue(filtre.nom, filtre.description));
			}
		}

		return noms;
	}

	
	

	/**
	 * Pour un filtre donné, retourne les valeurs sélectionnables du filtre («
	 * que l'on peut encore ajouter », « qui ne font pas partie du filtre actif
	 * »)
	 * 
	 * @param nom
	 * @param nomsRôles 
	 * @return
	 */
	public List<DefaultKeyValue> getFiltreValeurs(String nom, boolean afficheCompteurValeurFiltre) {
		List<DefaultKeyValue> values = new ArrayList<DefaultKeyValue>();

		Filtre f = getFiltre(nom);

		// Si le filtre est trouvé, récupérer ses valeurs
		if (f != null) {
			values = getValeursActives(nom, f.keyValues);
			
			// Supprimer le nombre entre () à la fin de la valeur du filtre
			if(!afficheCompteurValeurFiltre) {
				for (DefaultKeyValue valeurFiltre : values) {
					valeurFiltre.setValue(valeurFiltre.getValue().toString().replaceAll("\\(.*\\)", ""));
				}
			}
			
//			// y a-t-il un tri personnalisé à supprimer (tous les caractères avant le premier | ) ?
//			// Exemple : 001|fragment HTML
//			for (DefaultKeyValue valeurFiltre : values) {
//				valeurFiltre.setValue(valeurFiltre.getValue().toString().replaceAll("^(.*\\|)", ""));
//			}
			
			
		}

		return values;
	}
	
	public String getRemarqueValeursFiltre(String nomDuFiltre) {
		Filtre f = getFiltre(nomDuFiltre);
		// Si le filtre est trouvé, récupérer sa remarque éventuelle
		if (f != null) {
			if(f.remarqueValeurs != null && !f.remarqueValeurs.isEmpty()) {
				return f.remarqueValeurs;
			}
		}
		return  null;
	}

	public Filtre getFiltre(String nom) {
		// recherche du filtre
		Filtre f = null;
		for (Filtre filtre : filtres) {
			if (filtre.nom.equalsIgnoreCase(nom)) {
				f = filtre;
				break;
			}
		}
		return f;
	}

	/**
	 * Retourne toutes les valeurs possibles du filtre <i>nom</i> qui ne font
	 * pas partie du filtre actif
	 * 
	 * @param nom
	 * @param keyValues
	 * @return
	 */
	private List<DefaultKeyValue> getValeursActives(String nom, List<DefaultKeyValue> keyValues) {
		List<DefaultKeyValue> valeursActives = new ArrayList<DefaultKeyValue>(keyValues.size());

		// Construire la liste des valeurs disponibles (non actives)
		if (filtreActifModel != null) {
			for (DefaultKeyValue keyValue : keyValues) {
				if (!filtreActifModel.isActif(nom, keyValue.getKey())) {
					valeursActives.add(keyValue);
				}
			}
		} else {
			return keyValues;
		}

		return valeursActives;
	}

	public CorpusService getCorpusService() {
		return corpusService;
	}

	public void setCorpusService(CorpusService corpusService) {
		this.corpusService = corpusService;
	}

	public ListeRepository getListeRepository() {
		return listeRepo;
	}

	public void setListeRepository(ListeRepository listeRepo) {
		this.listeRepo = listeRepo;
	}

	abstract public void init();

	public void setFiltreActif(FiltreRecherche filtreActifModel) {
		this.filtreActifModel = filtreActifModel;

	}

	public boolean isRôleAdmin() {
		return SecurityUtil.isAnyGranted("ROLE_ADMINISTRATEUR");
	}

}
