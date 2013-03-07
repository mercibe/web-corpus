package com.servicelibre.zk.viewmodel;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.ListModelList;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.ListeRepository;

public class CatégoriesListesVM {

	private static final Logger logger = LoggerFactory.getLogger(CatégoriesListesVM.class);

	private Validator nonVideValidator = new NonVideValidator();

	Corpus corpus = ServiceLocator.getCorpusService().getCorpus();

	ListModelList<CatégorieListe> catégoriesListes;

	CatégorieListe catégorieListeSélectionné;

	String mode = null;

	ListeRepository listeRepo;
	CatégorieListeRepository catégorieListeRepo;

	String messageSuppression;

	StandardPasswordEncoder encodeur = new StandardPasswordEncoder();

	public ListModelList<CatégorieListe> getCatégoriesListes() {
		if (catégoriesListes == null) {
			catégoriesListes = new ListModelList<CatégorieListe>((Collection<? extends CatégorieListe>) getCatégorieListeRepo().findAll(
					new Sort(new Order(Direction.ASC, "ordre"),	new Order(Direction.ASC, "nom"))));
		}
		return catégoriesListes;
	}

	private CatégorieListeRepository getCatégorieListeRepo() {
		if (catégorieListeRepo == null) {
			catégorieListeRepo = (CatégorieListeRepository) SpringUtil.getBean("catégorieListeRepository", CatégorieListeRepository.class);
		}
		return catégorieListeRepo;
	}

	public CatégorieListe getCatégorieListeSélectionné() {
		return catégorieListeSélectionné;
	}

	public void setSélectionné(CatégorieListe catégorieListeSélectionné) {
		this.catégorieListeSélectionné = catégorieListeSélectionné;
	}

	public String getMessageSuppression() {
		return messageSuppression;
	}

	public void setMessageSuppression(String messageSuppression) {
		this.messageSuppression = messageSuppression;
	}

	@NotifyChange({ "catégorieListeSélectionné", "catégoriesListes", "mode" })
	@Command
	public void ajouterCatégorieListe() {
		CatégorieListe catégorieListe = new CatégorieListe();
		catégorieListe.setCorpus(corpus);
		getCatégoriesListes().add(catégorieListe);
		catégorieListeSélectionné = catégorieListe;
		mode = "ajout";
	}

	@NotifyChange({ "mode" })
	@Command
	public void modifierCatégorieListe() {
		mode = "modification";
	}

	@NotifyChange({ "mode", "listes" })
	@Command
	public void annulerModification() {
		catégoriesListes = null;
		getCatégoriesListes();
		mode = null;
	}

	@NotifyChange({ "listeSélectionné", "listes", "mode" })
	@Command
	@Transactional
	public void enregistrerCatégorieListe() {

		logger.info("Enregistrement de la catégorie de liste {}", catégorieListeSélectionné);

		boolean création = catégorieListeSélectionné.getId() == 0 ? true : false;

		try {

			catégorieListeSélectionné = getCatégorieListeRepo().save(catégorieListeSélectionné);

			// ne faire que si nouvelle liste
			if (création) {

			}

			// Forcer le rafraississement correct (fastidieux mais ESSENTIEL)
			// Récupération de l'item listeSélectionné dans le master
			CatégorieListe listeMaster = catégoriesListes.getSelection().iterator().next();
			// Recherche de l'index de cet item
			int indexOfListeSélectionné = catégoriesListes.indexOf(listeMaster);
			// Mise à jour de cet item
			catégoriesListes.set(indexOfListeSélectionné, catégorieListeSélectionné);

		} catch (DataIntegrityViolationException e) {
			logger.info("Une catégorie de liste avec le même ????? existe déjà.");
		}

		logger.info("Catégorie de liste enregistrée: {}", catégorieListeSélectionné);

		// Pour fermer la zone d'édition
		mode = null;

	}

	@NotifyChange({ "catégorieListeSélectionné", "catégoriesListes", "messageSuppression", "mode" })
	@Command
	@Transactional
	public void supprimerCatégorieListe() {

		logger.info("Suppression de la catégorie de liste {} :", catégorieListeSélectionné);

		if (catégorieListeSélectionné.getId() == 0) {
			annulerModification();
			catégorieListeSélectionné = null;
			messageSuppression = null;
			return;
		}

		try {
			getCatégorieListeRepo().delete(catégorieListeSélectionné.getId());
		} catch (Exception e) {
			logger.error("À traiter?", e);
		}

		getCatégoriesListes().remove(catégorieListeSélectionné);
		catégorieListeSélectionné = null;
		messageSuppression = null;
		mode = null;

	}

	@NotifyChange("messageSuppression")
	@Command
	public void confirmerSuppression() {
		messageSuppression = "Voulez-vous vraiment supprimer la catégorie de liste " + catégorieListeSélectionné.getNom() + " ?" + " ("
				+ catégorieListeSélectionné.getId() + ")";
	}

	@NotifyChange("messageSuppression")
	@Command
	public void annulerSuppression() {
		messageSuppression = null;
	}

	public Validator getNonVideValidator() {
		return nonVideValidator;
	}

	public void setNonVideValidator(Validator nonVideValidator) {
		this.nonVideValidator = nonVideValidator;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setCatégoriesListes(ListModelList<CatégorieListe> catégoriesListes) {
		this.catégoriesListes = catégoriesListes;
	}

	public void setListeSélectionné(CatégorieListe catégorieListeSélectionné) {
		this.catégorieListeSélectionné = catégorieListeSélectionné;
	}

	public void setListeRepo(ListeRepository listeRepo) {
		this.listeRepo = listeRepo;
	}

	public void setCatégorieListeSélectionné(CatégorieListe catégorieListeSélectionné) {
		this.catégorieListeSélectionné = catégorieListeSélectionné;
	}
	
	

}
