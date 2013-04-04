package com.servicelibre.zk.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.ListeMotRepository;
import com.servicelibre.repositories.corpus.ListeRepository;
import com.servicelibre.repositories.corpus.MotRepository;

@SuppressWarnings("rawtypes")
public class FicheMotCtrl extends GenericForwardComposer implements VariableResolver {

    private static final long serialVersionUID = -3190337135508498208L;

    private static final Logger logger = LoggerFactory.getLogger(FicheMotCtrl.class);

    private MotRepository motRepo = ServiceLocator.getMotRepo();

    Label titreFicheLabel;

    Grid formulaireGrid;
    Div bouttonDiv;

    Textbox motTextbox;
    Textbox lemmeTextbox;
    Textbox autreGraphieTextbox;
    Textbox noteTextbox;

    Listbox catgramListbox;
    Listbox genreListbox;
    Listbox nombreListbox;
    Listbox catgramPrécisionListbox;
    Listbox partitionListbox;
    Listbox listesListbox;

    Checkbox estUnLemmeCheckbox;
    Checkbox roCheckbox;

    Button modifierButton;
    Button enregistrerButton;
    Button supprimerButton;
    Button annulerButton;

    private Mot mot;

    private ListeRepository listeRepo = ServiceLocator.getListeRepo();
    ListeMotRepository listeMotRepo = ServiceLocator.getListeMotRepo();

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

	formulaireGrid.setVisible(true);

    }

    private void initialiseValeurs(Mot mot) {

	initialiseGenreListbox(mot);
	initialiseCatgramListbox(mot);
	initialiseNombreListbox(mot);
	initialiseCatgramPrécisionListbox(mot);
	initialisePartitionListbox(mot);
	initialiseListesListbox(mot);

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

    private void initialiseListesListbox(Mot mot) {

	// On vide les partitions actuelles
	listesListbox.getItems().clear();

	List<Liste> listes = listeRepo.findByMot(mot);

	for (Liste liste : listes) {
	    Listitem item = new Listitem();
	    item.setValue(liste);
	    item.setLabel(liste.getCatégorie().getNom() + " - " + liste.getNom());
	    listesListbox.appendChild(item);
	}
    }

    private void initialisePartitionListbox(Mot mot) {

	// On vide les partitions actuelles
	partitionListbox.getItems().clear();

	Long motPartitionId = mot.getListePartitionPrimaire().getId();
	List<Liste> partitions = listeRepo.getPartitions();

	// ajout d'un item vide pour ne pas forcer la saisie d'une partition
	partitionListbox.appendChild(new Listitem("", null));

	for (Liste partition : partitions) {
	    Listitem item = new Listitem();
	    item.setValue(partition);
	    item.setLabel(partition.getNom());
	    if (motPartitionId == partition.getId()) {
		item.setSelected(true);
	    }
	    partitionListbox.appendChild(item);
	}
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
	partitionListbox.setDisabled(interdit);

	estUnLemmeCheckbox.setDisabled(interdit);
	roCheckbox.setDisabled(interdit);

	enregistrerButton.setDisabled(interdit);
	annulerButton.setDisabled(interdit);
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

	Listitem catgramItem = catgramListbox.getSelectedItem();
	if (catgramItem != null) {
	    mot.setCatgram((String) catgramItem.getValue());
	}

	Listitem genreItem = genreListbox.getSelectedItem();
	if (genreItem != null) {
	    mot.setGenre((String) genreItem.getValue());
	}

	Listitem catgramPrécisionItem = catgramPrécisionListbox.getSelectedItem();
	if (catgramPrécisionItem != null) {
	    mot.setCatgramPrécision((String) catgramPrécisionItem.getValue());
	}

	Listitem nombreItem = nombreListbox.getSelectedItem();
	if (nombreItem != null) {
	    mot.setNombre((String) nombreItem.getValue());
	}

	Listitem partitionItem = partitionListbox.getSelectedItem();
	if (partitionItem != null) {
	    // Récupérer l'ancienne partition
	    Liste ancienneListePrimaire = mot.getListePartitionPrimaire();
	    Liste nouvelleListePrimaire = (Liste) partitionItem.getValue();

	    // Si différente de l'actuelle (nouvelle), ajuster ListeMot
	    if (ancienneListePrimaire.getId() != nouvelleListePrimaire.getId()) {
		mot.setListePartitionPrimaire(nouvelleListePrimaire);

		// Retire le mot de l'ancienne liste et ajoute dans la nouvelle
		// C'est un choix: nous aurions pu l'ajouter si pas déjà
		// existante et conserver l'appartenance actuelle.
		// Cela paraît logique pour des partitions mais ce comportement
		// pourrait être configurable.
		ListeMot listeMot = listeMotRepo.findByListeAndMot(ancienneListePrimaire, mot);
		if (listeMot == null) {
		    // l'association actuelle existe-t-elle déjà? (incohérence
		    // BD) Si oui, ne rien faire
		    listeMot = listeMotRepo.findByListeAndMot(nouvelleListePrimaire, mot);
		    if (listeMot == null) {
			listeMot = new ListeMot(mot, nouvelleListePrimaire);
		    }
		} else {
		    listeMot.setListe(nouvelleListePrimaire);
		}
		
		listeMot = listeMotRepo.save(listeMot);

		initialiseListesListbox(mot);
	    }
	}

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

	final String motÀSupprimer = mot.getMot();
	Messagebox.show("Supprimer le mot " + motÀSupprimer + " ?", "Suppression d'un mot", new Messagebox.Button[] { Messagebox.Button.OK,
		Messagebox.Button.CANCEL }, Messagebox.QUESTION, new EventListener<ClickEvent>() {
	    public void onEvent(ClickEvent e) {
		switch (e.getButton()) {
		case OK: // OK is clicked
		    logger.debug("Suppression du mot {}", mot);
		    motRepo.delete(mot);
		    // TODO fermer l'onglet (afficher confirmation?)
		    formulaireGrid.setVisible(false);
		    titreFicheLabel.setValue("Le mot « " + motÀSupprimer + " » a été supprimé.");
		    bouttonDiv.setVisible(false);
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
