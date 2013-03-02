package com.servicelibre.zk.viewmodel;

import java.util.Map;

import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.spring.SpringUtil;

import com.servicelibre.entities.ui.Utilisateur;
import com.servicelibre.repositories.ui.UtilisateurRepository;

public class PseudoValidator extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		String pseudo = (String) ctx.getProperty().getValue();
		if(pseudo == null || pseudo.isEmpty()) {
			addInvalidMessage(ctx, "Veuillez préciser un pseudo.");
			return;
		}
		
//		Map<String, Property[]> properties = ctx.getProperties();
//		System.err.println(properties.size() + " propriétés****************************");
//		for(String key : properties.keySet()) {
//			System.err.println(key + ": " + properties.get(key));
//		}
//		
		Long utilisateurId =  Long.parseLong((String) ctx.getProperties("id")[0].getValue());
		
		UtilisateurRepository ur = (UtilisateurRepository) SpringUtil.getBean("utilisateurRepository",UtilisateurRepository.class);
		Utilisateur utilisateur = null;
		
		if(utilisateurId == 0) {
			utilisateur = ur.findByPseudo(pseudo);
		}
		else {
			utilisateur = ur.findByPseudoAndIdNot(pseudo, utilisateurId);
		}
		
		if(utilisateur != null) {
			addInvalidMessage(ctx, "Ce pseudo n'est plus disponible. Veuillez en choisir un autre.");
		}

	}

}
