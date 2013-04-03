package com.servicelibre.zk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.MotRepository;

@SuppressWarnings("rawtypes")
public class FicheMotCtrl extends GenericForwardComposer implements VariableResolver {

	private static final long serialVersionUID = -3190337135508498208L;

	private static final Logger logger = LoggerFactory.getLogger(FicheMotCtrl.class);

	private MotRepository motRepo = ServiceLocator.getMotRepo();

	Label titreFicheLabel;

	Textbox motTextbox;
	Textbox lemmeTextbox;
	Textbox autreGraphieTextbox;
	Textbox noteTextbox;

	Listbox catgramListbox;
	Listbox genreListbox;
	Listbox nombreListbox;
	Listbox catgramPrécisionListbox;
	Listbox listePrimaireListbox;

	Checkbox estUnLemmeCheckbox;
	Checkbox roCheckbox;

	Button modifierButton;
	Button enregistrerButton;
	Button supprimerButton;
	Button annulerButton;

	private Mot mot;

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		mot = (Mot) requestScope.get("mot");

		if (mot == null) {
			mot = new Mot();
			titreFicheLabel.setValue("Création d'un nouveau mot");
			interditÉdition(false);
			modifierButton.setVisible(false);
			enregistrerButton.setLabel("Enregistrer le nouveau mot");
		} else {
			initialiseValeurs(mot);
		}

	}

	private void initialiseValeurs(Mot mot) {

		initialiseGenreListbox(mot);
		initialiseCatgramListbox(mot);
		initialiseNombreListbox(mot);
		initialiseCatgramPrécisionListbox(mot);
		initialisePartitionListbox(mot);

		motTextbox.setValue(mot.getMot());
		lemmeTextbox.setValue(mot.getLemme());
		estUnLemmeCheckbox.setChecked(mot.isEstUnLemme());

		roCheckbox.setChecked(mot.isRo());
		autreGraphieTextbox.setValue(mot.getAutreGraphie());
		noteTextbox.setValue(mot.getNote());

		ajusteTitre(mot);
	}

	private void ajusteTitre(Mot mot) {
		titreFicheLabel.setValue("Fiche du mot « " + mot.getMot() + " »");
	}

	private void initialisePartitionListbox(Mot mot) {
		mot.getListePartitionPrimaire();
		// récupérer les listes primaires
		ServiceLocator.getListeRepo();
	}

	private void initialiseCatgramPrécisionListbox(Mot mot) {
		String catgramPrécision = mot.getCatgramPrécision();
		for (int i = 0; i < catgramPrécisionListbox.getItemCount(); i++) {
			if (catgramPrécisionListbox.getItemAtIndex(i).getValue().equals(catgramPrécision)) {
				catgramPrécisionListbox.setSelectedIndex(i);
				break;
			}
		}
	}

	private void initialiseCatgramListbox(Mot mot) {
		String catgram = mot.getCatgram();
		for (int i = 0; i < catgramListbox.getItemCount(); i++) {
			if (catgramListbox.getItemAtIndex(i).getValue().equals(catgram)) {
				catgramListbox.setSelectedIndex(i);
				break;
			}
		}
	}

	private void initialiseNombreListbox(Mot mot) {
		String nombre = mot.getNombre();
		for (int i = 0; i < nombreListbox.getItemCount(); i++) {
			if (nombreListbox.getItemAtIndex(i).getValue().equals(nombre)) {
				nombreListbox.setSelectedIndex(i);
				break;
			}
		}
	}

	private void initialiseGenreListbox(Mot mot) {
		String genre = mot.getGenre();
		for (int i = 0; i < genreListbox.getItemCount(); i++) {
			if (genreListbox.getItemAtIndex(i).getValue().equals(genre)) {
				genreListbox.setSelectedIndex(i);
				break;
			}
		}
	}

	private void interditÉdition(boolean interdit) {

		motTextbox.setReadonly(interdit);
		lemmeTextbox.setReadonly(interdit);
		autreGraphieTextbox.setReadonly(interdit);
		noteTextbox.setReadonly(interdit);

		catgramListbox.setDisabled(interdit);
		catgramPrécisionListbox.setDisabled(interdit);
		genreListbox.setDisabled(interdit);
		nombreListbox.setDisabled(interdit);
		catgramListbox.setDisabled(interdit);

		estUnLemmeCheckbox.setDisabled(interdit);
		roCheckbox.setDisabled(interdit);

		enregistrerButton.setDisabled(interdit);
		modifierButton.setDisabled(!interdit);
	}

	public void onClick$modifierButton() {
		// Tous les champs deviennent modifiables
		interditÉdition(false);
		modifierButton.setDisabled(true);
	}

	public void onClick$annulerButton() {
		initialiseValeurs(mot);
		interditÉdition(true);

	}

	public void onClick$enregistrerButton() {

		interditÉdition(true);

		boolean nouveauMot = mot.getId() == 0;

		mot.setMot(motTextbox.getValue());
		mot.setLemme(lemmeTextbox.getValue());
		mot.setCatgram((String) catgramListbox.getSelectedItem().getValue());
		mot.setGenre((String) genreListbox.getSelectedItem().getValue());
		mot.setCatgramPrécision((String) catgramPrécisionListbox.getSelectedItem().getValue());
		mot.setNombre((String) nombreListbox.getSelectedItem().getValue());
		mot.setEstUnLemme(estUnLemmeCheckbox.isChecked());
		mot.setRo(roCheckbox.isChecked());
		mot.setAutreGraphie(autreGraphieTextbox.getValue());
		mot.setNote(noteTextbox.getValue());

		mot = motRepo.save(mot);

		ajusteTitre(mot);

		// Si ajout d'un nouveau mot
		if (nouveauMot) {
			enregistrerButton.setLabel("Enregistrer les modifications");
			modifierButton.setVisible(true);
		}
	}

	public void onClick$supprimerButton() {

		Messagebox.show("Supprimer le mot " + mot.getMot() + " ?", "Suppression d'un mot", new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL}, Messagebox.QUESTION,
				new EventListener<ClickEvent>() {
					public void onEvent(ClickEvent e) {
						switch (e.getButton()) {
						case OK: // OK is clicked
							logger.debug("Suppression du mot {}", mot);
							motRepo.delete(mot);
							// TODO fermer l'onglet (afficher confirmation?)
							break;
						case CANCEL:
							// Annuler
							logger.debug("Annuler la suppression du mot {}", mot);
							break;
						default:
							// Ignore
							logger.debug("Ignorer la suppression du mot {}", mot);
						}
					}
				});

	}

	@Override
	public Object resolveVariable(String arg0) throws XelException {
		// TODO Auto-generated method stub
		return null;
	}

}
