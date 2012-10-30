package com.servicelibre.sécurité;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import com.servicelibre.entities.ui.Utilisateur;
import com.servicelibre.entities.ui.UtilisateurRôle;
import com.servicelibre.repositories.ui.UtilisateurRepository;

public class CorpusJpaAuthentificationProvider implements AuthenticationProvider {

	@Autowired
	UtilisateurRepository utilisateurRepo;

	StandardPasswordEncoder spe = new StandardPasswordEncoder();

	@Override
	public Authentication authenticate(Authentication authentification) throws AuthenticationException {

		// Rechercher l'utilisateur dans la DB
		Utilisateur findByPseudo = utilisateurRepo.findByPseudo(authentification.getName());

		String rawPassword = authentification.getCredentials().toString();

		boolean authentifié = spe.matches(rawPassword, findByPseudo.getMotDePasse());

		System.out.println(rawPassword + " ?= " + findByPseudo.getMotDePasse() + " => " + authentifié);

		// convertir findByPseudo.getUtilisateurRôles()
		List<GrantedAuthority> rôles = new ArrayList<GrantedAuthority>();
		for (UtilisateurRôle r : findByPseudo.getUtilisateurRôles()) {
			String nomRôle = r.getRôle().getNom();
			System.out.println("ajout du rôle: " + nomRôle);
			rôles.add(new SimpleGrantedAuthority(nomRôle));
		}

		
		
		if(!authentifié) {
			throw new BadCredentialsException("Mot de passe invalide.");
		}
		
		return new UsernamePasswordAuthenticationToken(authentification.getPrincipal(), authentification.getCredentials(), rôles);

	}

	@Override
	public boolean supports(Class<?> authentification) {

		return authentification.equals(UsernamePasswordAuthenticationToken.class);
	}

}
