package com.servicelibre.zk.viewmodel;

import java.util.Collection;
import java.util.List;

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
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.ListeMotRepository;
import com.servicelibre.repositories.corpus.ListeRepository;
import com.servicelibre.repositories.corpus.MotRepository;

public class ListesEtMotsVM {

	private static final Logger logger = LoggerFactory.getLogger(ListesEtMotsVM.class);

	private Validator nonVideValidator = new NonVideValidator();

	ListModelList<Liste> listes;
	ListModelList<Mot> mots;

	Liste listeSélectionné;
	Mot motSélectionné;

	ListModelList<CatégorieListe> catégories;


	String mode = null;

	ListeRepository listeRepo;
	CatégorieListeRepository catégorieListeRepo;
	ListeMotRepository listeMotRepo;


	String messageSuppression;

	StandardPasswordEncoder encodeur = new StandardPasswordEncoder();

	public ListModelList<Liste> getListes() {
		if (listes == null) {
			listes = new ListModelList<Liste>((Collection<? extends Liste>) getListeRepo().findAll(
					new Sort(new Order(Direction.ASC, "Catégorie.ordre"), new Order(Direction.ASC, "ordre"), new Order(Direction.ASC, "nom"))));
		}
		return listes;
	}

	public ListModelList<CatégorieListe> getCatégories() {
		if (catégories == null) {
			catégories = new ListModelList<CatégorieListe>((Collection<? extends CatégorieListe>) getCatégorieListeRepo().findAll(
					new Sort(new Order(Direction.ASC, "ordre"), new Order(Direction.ASC, "nom"))));
		}
		return catégories;
	}

	private CatégorieListeRepository getCatégorieListeRepo() {
		if (catégorieListeRepo == null) {
			catégorieListeRepo = (CatégorieListeRepository) SpringUtil.getBean("catégorieListeRepository", CatégorieListeRepository.class);
		}
		return catégorieListeRepo;
	}
	
	private ListeMotRepository getListeMotRepo() {
		if (listeMotRepo == null) {
			listeMotRepo = (ListeMotRepository) SpringUtil.getBean("listeMotRepository", ListeMotRepository.class);
		}
		return listeMotRepo;
	}

	public Liste getListeSélectionné() {
		return listeSélectionné;
	}

	public void setSélectionné(Liste listeSélectionné) {
		this.listeSélectionné = listeSélectionné;
	}

	public String getMessageSuppression() {
		return messageSuppression;
	}

	public void setMessageSuppression(String messageSuppression) {
		this.messageSuppression = messageSuppression;
	}

	public ListeRepository getListeRepo() {
		if (listeRepo == null) {
			listeRepo = (ListeRepository) SpringUtil.getBean("listeRepository", ListeRepository.class);
		}
		return listeRepo;
	}

	@NotifyChange({ "listeSélectionné", "listes", "mode", "catégorieCourante" })
	@Command
	public void ajouterListe() {
		Liste liste = new Liste();
		getListes().add(liste);
		listeSélectionné = liste;
		mode = "ajout";
	}

	@NotifyChange({"mode"})
	@Command
	public void modifierListe() {
		mode = "modification";
		logger.debug("Catégorie courante: {}", listeSélectionné.getCatégorie() );
	}

	@NotifyChange({ "mode", "listes" })
	@Command
	public void annulerModification() {
		listes = null;
		getListes();
		mode = null;
	}

	@NotifyChange({ "listeSélectionné", "listes", "mode" })
	@Command
	@Transactional
	public void enregistrerListe() {

		logger.info("Enregistrement de la liste {}", listeSélectionné);

		boolean création = listeSélectionné.getId() == 0 ? true : false;

		try {

			listeSélectionné = getListeRepo().save(listeSélectionné);

			// ne faire que si nouvelle liste
			if (création) {

			}

			// Forcer le rafraississement correct (fastidieux mais ESSENTIEL)
			// Récupération de l'item listeSélectionné dans le master
			Liste listeMaster = listes.getSelection().iterator().next();
			// Recherche de l'index de cet item
			int indexOfListeSélectionné = listes.indexOf(listeMaster);
			// Mise à jour de cet item
			listes.set(indexOfListeSélectionné, listeSélectionné);

		} catch (DataIntegrityViolationException e) {
			logger.info("Une liste avec le même ????? existe déjà.");
		}

		logger.info("Liste enregistrée: {}", listeSélectionné);

		// Pour fermer la zone d'édition
		mode = null;

	}

	@NotifyChange({ "listeSélectionné", "listes", "messageSuppression", "mode" })
	@Command
	@Transactional
	public void supprimerListe() {

		logger.info("Suppression de la liste {} :", listeSélectionné);

		if (listeSélectionné.getId() == 0) {
			annulerModification();
			listeSélectionné = null;
			messageSuppression = null;
			return;
		}

		try {
			getListeRepo().delete(listeSélectionné.getId());
		} catch (Exception e) {
			logger.error("À traiter?", e);
		}

		getListes().remove(listeSélectionné);
		listeSélectionné = null;
		messageSuppression = null;
		mode = null;

	}

	@NotifyChange("messageSuppression")
	@Command
	public void confirmerSuppression() {
		messageSuppression = "Voulez-vous vraiment supprimer la liste " + listeSélectionné.getNom() + " ?" + " (" + listeSélectionné.getId() + ")";
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
	
	
	public ListModelList<Mot> getMots() {
		if (mots == null) {
			mots = new ListModelList<Mot>((Collection<? extends Mot>) getListeRepo().getMotsByListe(listeSélectionné));
		}
		return mots;
	}

	public void setMots(ListModelList<Mot> mots) {
		this.mots = mots;
	}

	public Mot getMotSélectionné() {
		return motSélectionné;
	}

	public void setMotSélectionné(Mot motSélectionné) {
		this.motSélectionné = motSélectionné;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setListes(ListModelList<Liste> listes) {
		this.listes = listes;
	}

	public void setListeSélectionné(Liste listeSélectionné) {
		this.listeSélectionné = listeSélectionné;
	}

	public void setListeRepo(ListeRepository listeRepo) {
		this.listeRepo = listeRepo;
	}

}
