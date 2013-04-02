package com.servicelibre.repositories.corpus;

import java.util.Collection;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.Utilisateur;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

	Utilisateur findByPseudo(String pseudo);

	Utilisateur findByPseudo(String pseudo, Sort sort);

	Collection<? extends Utilisateur> findAll(Sort sort);

	Utilisateur findByPseudoAndIdNot(String pseudo, Long id);

}
