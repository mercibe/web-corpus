package com.servicelibre.repositories.ui;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.ui.Utilisateur;
import com.servicelibre.entities.ui.UtilisateurRôle;

public interface UtilisateurRôleRepository extends CrudRepository<UtilisateurRôle, Long> {

	UtilisateurRôle findByUtilisateur(Utilisateur utilisateur);
}
