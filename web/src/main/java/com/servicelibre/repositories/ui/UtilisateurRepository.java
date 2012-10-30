package com.servicelibre.repositories.ui;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.ui.Utilisateur;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

	Utilisateur findByPseudo(String pseudo);
}
