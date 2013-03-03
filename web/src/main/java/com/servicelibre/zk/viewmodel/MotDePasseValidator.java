package com.servicelibre.zk.viewmodel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

public class MotDePasseValidator extends AbstractValidator {

	// (?=.*[@#$%^&+=])
	// Le mot de passe doit contenir au moins 8 caractères dont au moins un nombre, une lettre majuscule, une lettre
	// minuscule et pas d'espace.
	private static final String MOTDEPASSE_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

	private Pattern pattern;
	private Matcher matcher;

	public MotDePasseValidator() {
		super();
		pattern = Pattern.compile(MOTDEPASSE_PATTERN);
	}

	@Override
	public void validate(ValidationContext ctx) {

		String motDePasse = (String) ctx.getProperty().getValue();

		if (motDePasse == null || motDePasse.isEmpty()) {
			addInvalidMessage(ctx, "Veuillez préciser un mot de passe.");
		} else if (motDePasse.length() < 80) {
			// Si un mot de passe a été précisé, s'assurer qu'il est valide
			matcher = pattern.matcher(motDePasse);
			if (!matcher.matches()) {
				addInvalidMessage(ctx,
						"Le mot de passe doit contenir au moins 8 caractères dont au moins un nombre, une lettre majuscule, une lettre minuscule et pas d'espace.");
			}
		}

	}

}
