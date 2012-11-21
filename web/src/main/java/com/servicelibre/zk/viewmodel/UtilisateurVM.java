package com.servicelibre.zk.viewmodel;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.ListModelList;

import com.servicelibre.entities.ui.Utilisateur;
import com.servicelibre.repositories.ui.UtilisateurRepository;

public class UtilisateurVM {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurVM.class); 
	
	ListModelList<Utilisateur> utilisateurs;
	
	Utilisateur sélectionné;
	
	UtilisateurRepository utilisateurRepo;
	
	String messageSuppression;
	
	StandardPasswordEncoder encodeur = new StandardPasswordEncoder();

	public ListModelList<Utilisateur> getUtilisateurs() {
		if(utilisateurs == null) {
			//new Sort("prénom,nom")
			utilisateurs = new ListModelList<Utilisateur>((Collection<? extends Utilisateur>) getUtilisateurRepo().findAll(new Sort(new Order(Direction.ASC, "prénom"),new Order(Direction.ASC, "nom"))));
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
		if(utilisateurRepo == null) {
			utilisateurRepo = (UtilisateurRepository) SpringUtil.getBean("utilisateurRepository",UtilisateurRepository.class);
		}
		return utilisateurRepo;
	}

	@NotifyChange({"sélectionné","utilisateurs"})
	@Command
	public void ajouterUtilisateur(){
		Utilisateur utilisateur = new Utilisateur();
		getUtilisateurs().add(utilisateur);
		sélectionné = utilisateur;
	}
		
	@NotifyChange({"sélectionné","utilisateurs"})
	@Command
	public void enregistrerUtilisateur(){
		
		// Si le mot de passe a une longueur inférieure à 80 caractères, considérer qu'il a été modifié manuellement
		// et qu'il doit donc être encodé.  Sinon, ne pas y toucher.
		// TODO trouver mieux et plus fiable?
		String motDePasse = sélectionné.getMotDePasse();
		if(motDePasse.length() < 80) {
			sélectionné.setMotDePasse(encodeur.encode(motDePasse));
			logger.info("Modification du mot de passe de l'utilisateur {}", sélectionné);
		}
		
		logger.info("Enregistrement de l'utilisateur {}", sélectionné);
		getUtilisateurRepo().save(sélectionné);
		
		// TODO valider ou gérer DataIntegrityViolationException (pseudo identique)
		
		
	}
	
	@NotifyChange({"sélectionné","utilisateurs","messageSuppression"})
	@Command
	public void supprimerUtilisateur(){
		logger.info("Suppression de l'utilisateur {}", sélectionné);
		getUtilisateurRepo().delete(sélectionné);
		getUtilisateurs().remove(sélectionné);
		sélectionné = null;
		messageSuppression = null;
		
	}
	
	@NotifyChange("messageSuppression")
	@Command
	public void confirmerSuppression(){
		messageSuppression = "Voulez-vous vraiment supprimer l'utilisateur " + sélectionné.getPseudo()+" ?";
	}
	
	@NotifyChange("messageSuppression")
	@Command
	public void annulerSuppression(){
		messageSuppression = null;
	}
	
}
