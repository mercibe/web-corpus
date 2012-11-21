package com.servicelibre.repositories.ui;

import java.util.Collection;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.ui.Utilisateur;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

	Utilisateur findByPseudo(String pseudo);

	Utilisateur findByPseudo(String pseudo, Sort sort);

	Collection<? extends Utilisateur> findAll(Sort sort);

}
