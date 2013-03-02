package com.servicelibre.zk.viewmodel;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

public class NonVideValidator extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		String valeurTextuelle = (String) ctx.getProperty().getValue();
		if(valeurTextuelle == null || valeurTextuelle.isEmpty()) {
			String nomChamp = (String) ctx.getValidatorArg("nomChamp");
			addInvalidMessage(ctx, "Veuillez préciser " + nomChamp + ".");
		}

	}

}
