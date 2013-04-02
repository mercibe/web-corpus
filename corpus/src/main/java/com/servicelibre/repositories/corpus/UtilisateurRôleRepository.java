package com.servicelibre.repositories.corpus;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.Utilisateur;
import com.servicelibre.entities.corpus.UtilisateurRôle;

public interface UtilisateurRôleRepository extends CrudRepository<UtilisateurRôle, Long> {

	UtilisateurRôle findByUtilisateur(Utilisateur utilisateur);
}
