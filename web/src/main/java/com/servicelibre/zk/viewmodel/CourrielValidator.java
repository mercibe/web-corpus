package com.servicelibre.zk.viewmodel;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

public class CourrielValidator extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {

		String courriel = (String) ctx.getProperty().getValue();

		// Si une adresse de courriel a été précisée...
		if (courriel != null && !courriel.isEmpty()) {

			// .. s'assurer qu'elle est valide
			try {
				new InternetAddress(courriel).validate();
			} catch (AddressException e) {
				addInvalidMessage(ctx, "Adresse de courriel non valide.");
			}

		}

	}

}
