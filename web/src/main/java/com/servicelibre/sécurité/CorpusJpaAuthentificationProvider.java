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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

		if (findByPseudo == null) {
			throw new BadCredentialsException("Nom d'utilisateur ou mot de passe invalide.");
		}

		boolean authentifié = spe.matches(authentification.getCredentials().toString(), findByPseudo.getMotDePasse());

		if (!authentifié) {
			throw new BadCredentialsException("Nom d'utilisateur ou mot de passe invalide.");
		}

		// convertir findByPseudo.getUtilisateurRôles()
		List<GrantedAuthority> rôles = new ArrayList<GrantedAuthority>();
		for (UtilisateurRôle r : findByPseudo.getUtilisateurRôles()) {
			String nomRôle = r.getRôle().getNom();
			rôles.add(new SimpleGrantedAuthority(nomRôle));
		}

		// Construction d'un objet contenant de l'infrmation sur l'utilisateur authentifié
		UserDetails userDetails = new User(authentification.getName(), authentification.getCredentials().toString(), rôles);

		// L'appel de se constructeur avec la liste de GrantedAuthority (rôle) fait en sorte que l'utilisateur
		// est considéré comme authentifié.
		return new UsernamePasswordAuthenticationToken(userDetails, authentification.getCredentials(), rôles);

	}

	@Override
	public boolean supports(Class<?> authentification) {

		return authentification.equals(UsernamePasswordAuthenticationToken.class);
	}

}
