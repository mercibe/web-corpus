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

import com.servicelibre.entities.ui.Utilisateur;
import com.servicelibre.entities.ui.UtilisateurRôle;
import com.servicelibre.repositories.ui.RôleRepository;
import com.servicelibre.repositories.ui.UtilisateurRepository;
import com.servicelibre.repositories.ui.UtilisateurRôleRepository;

public class UtilisateurVM {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurVM.class);

	private Validator nonVideValidator = new NonVideValidator();
	private Validator pseudoValidator = new PseudoValidator();
	private Validator courrielValidator = new CourrielValidator();
	private Validator motDePasseValidator = new MotDePasseValidator();

	ListModelList<Utilisateur> utilisateurs;

	Utilisateur sélectionné;

	String mode = null;

	UtilisateurRepository utilisateurRepo;
	RôleRepository rôleRepo;
	UtilisateurRôleRepository utilisateurRôleRepo;

	String messageSuppression;

	StandardPasswordEncoder encodeur = new StandardPasswordEncoder();

	public ListModelList<Utilisateur> getUtilisateurs() {
		if (utilisateurs == null) {
			// new Sort("prénom,nom")
			utilisateurs = new ListModelList<Utilisateur>((Collection<? extends Utilisateur>) getUtilisateurRepo().findAll(
					new Sort(new Order(Direction.ASC, "prénom"), new Order(Direction.ASC, "nom"))));
		}
		return utilisateurs;
	}

	public Utilisateur getSélectionné() {
		return sélectionné;
	}

	public void setSélectionné(Utilisateur sélectionné) {
		this.sélectionné = sélectionné;
	}

	public String getMessageSuppression() {
		return messageSuppression;
	}

	public void setMessageSuppression(String messageSuppression) {
		this.messageSuppression = messageSuppression;
	}

	public UtilisateurRepository getUtilisateurRepo() {
		if (utilisateurRepo == null) {
			utilisateurRepo = (UtilisateurRepository) SpringUtil.getBean("utilisateurRepository", UtilisateurRepository.class);
		}
		return utilisateurRepo;
	}

	public RôleRepository getRôleRepo() {
		if (rôleRepo == null) {
			rôleRepo = (RôleRepository) SpringUtil.getBean("rôleRepository", RôleRepository.class);
		}
		return rôleRepo;
	}

	public UtilisateurRôleRepository getUtilisateurRôleRepo() {
		if (utilisateurRôleRepo == null) {
			utilisateurRôleRepo = (UtilisateurRôleRepository) SpringUtil.getBean("utilisateurRôleRepository", UtilisateurRôleRepository.class);
		}
		return utilisateurRôleRepo;
	}

	@NotifyChange({ "sélectionné", "utilisateurs", "mode" })
	@Command
	public void ajouterUtilisateur() {
		Utilisateur utilisateur = new Utilisateur();
		getUtilisateurs().add(utilisateur);
		sélectionné = utilisateur;
		mode = "ajout";
	}

	@NotifyChange("mode")
	@Command
	public void modifierUtilisateur() {
		mode = "modification";
	}

	@NotifyChange({ "mode", "utilisateurs" })
	@Command
	public void annulerModification() {
		utilisateurs = null;
		getUtilisateurs();
		mode = null;
	}

	@NotifyChange({ "sélectionné", "utilisateurs", "mode" })
	@Command
	@Transactional
	public void enregistrerUtilisateur() {

		// Si le mot de passe a une longueur inférieure à 80 caractères, considérer qu'il a été modifié manuellement
		// et qu'il doit donc être encodé. Sinon, ne pas y toucher.
		// TODO trouver mieux et plus fiable?
		String motDePasse = sélectionné.getMotDePasse();
		if (motDePasse.length() < 80) {
			sélectionné.setMotDePasse(encodeur.encode(motDePasse));
			logger.info("Modification du mot de passe de l'utilisateur {}", sélectionné);
		}

		logger.info("Enregistrement de l'utilisateur {}", sélectionné);

		boolean création = sélectionné.getId() == 0 ? true : false;

		try {

			sélectionné = getUtilisateurRepo().save(sélectionné);

			// ne faire que si nouvel utilisateur
			if (création) {
				// ajout du rôle utilisateur
				UtilisateurRôle utilisateurRôle = new UtilisateurRôle(sélectionné, getRôleRepo().findByNom("ROLE_UTILISATEUR"), "");
				utilisateurRôle = getUtilisateurRôleRepo().save(utilisateurRôle);
				sélectionné.getUtilisateurRôles().add(utilisateurRôle);
				// List<UtilisateurRôle> utilisateurRôles = new ArrayList<UtilisateurRôle>();
				// utilisateurRôles.add(utilisateurRôle);
				// sélectionné.setUtilisateurRôles(utilisateurRôles);
			}

			// Forcer le rafraississement correct (fastidieux mais ESSENTIEL)
			// Récupération de l'item sélectionné dans le master
			Utilisateur utilisateurMaster = utilisateurs.getSelection().iterator().next();
			// Recherche de l'index de cet item
			int indexOfSélectionné = utilisateurs.indexOf(utilisateurMaster);
			// Mise à jour de cet item
			utilisateurs.set(indexOfSélectionné, sélectionné);

		} catch (DataIntegrityViolationException e) {
			logger.info("Un utilisateur avec le pseudo {} ou le courriel {} existe déjà.", sélectionné.getPseudo(), sélectionné.getCourriel());
		}

		logger.info("Utilisateur enregistré: {}", sélectionné);

		// Pour fermer la zone d'édition
		mode = null;

	}

	@NotifyChange({ "sélectionné", "utilisateurs", "messageSuppression", "mode" })
	@Command
	@Transactional
	public void supprimerUtilisateur() {

		logger.info("Suppression de l'utilisateur {} - rôles: {}", sélectionné, sélectionné.getUtilisateurRôles());

		if (sélectionné.getId() == 0) {
			annulerModification();
			sélectionné = null;
			messageSuppression = null;
			return;
		}

		try {
			getUtilisateurRepo().delete(sélectionné.getId());
		} catch (Exception e) {
			logger.error("À traiter?", e);
		}

		getUtilisateurs().remove(sélectionné);
		sélectionné = null;
		messageSuppression = null;
		mode = null;

	}

	@NotifyChange("messageSuppression")
	@Command
	public void confirmerSuppression() {
			messageSuppression = "Voulez-vous vraiment supprimer l'utilisateur " + sélectionné.getPseudo() + " ?" + " (" + sélectionné.getId() + ")";
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

	public Validator getPseudoValidator() {
		return pseudoValidator;
	}

	public void setPseudoValidator(Validator pseudoValidator) {
		this.pseudoValidator = pseudoValidator;
	}

	public Validator getCourrielValidator() {
		return courrielValidator;
	}

	public void setCourrielValidator(Validator courrielValidator) {
		this.courrielValidator = courrielValidator;
	}

	public Validator getMotDePasseValidator() {
		return motDePasseValidator;
	}

	public void setMotValidator(Validator motValidator) {
		this.motDePasseValidator = motValidator;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
