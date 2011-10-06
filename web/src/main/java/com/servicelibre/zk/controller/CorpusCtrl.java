package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Group;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleGroupsModel;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.service.CorpusService;

public abstract class CorpusCtrl extends GenericForwardComposer implements VariableResolver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5225701427150774798L;

	Textbox cherche; // autowire car même type/ID que le composant dans la page
	// ZUL

	Button boutonRecherche;

	Button effacerRecherche;

	Listbox nomFiltre; // autowire car même type/ID que le composant dans la
	// page ZUL
	Listbox valeurFiltre;// autowire car même type/ID que le composant dans la
	// page ZUL

	Button boutonAjoutFiltre;// autowire car même type/ID que le composant dans
	// la page ZUL

	Button boutonEffacerFiltre;

	Grid gridFiltreActif;// autowire car même type/ID que le composant dans la
	// page ZUL

	Label infoRésultats;

	protected Window webCorpusWindow;

	Div caractèresSpéciaux;
	
	Image exportationCsv;
	Image exportationXls;

	// Enregistrement des événements onOK (la touche ENTER) sur tous les
	// composants de la recherche
	public void onOK$cherche(Event event) {
		chercheEtAffiche();
	}

	public void onClick$boutonRecherche(Event event) {

		chercheEtAffiche();

		cherche.setFocus(true);

	}

	public void onClick$effacerRecherche(Event event) {
		effacerRecherche();
	}

	public void onClick$exportationCsv(Event event) {
		exporterRésultatsCsv();
	}

	public void onClick$exportationXls(Event event) {
		exporterRésultatsXls();
	}

	/**
	 * Permet de remplir les choix de filtres/valeurs possibles
	 */
	FiltreManager filtreManager;

	/**
	 * Filtre qui sera passé au MotManager pour filtrer les mots à retourner. Ce
	 * filtre sert également comme Model (GroupModel) pour le Grid qui affiche
	 * le filtre en construction par l'utilisateur.
	 */
	FiltreMot filtreActifModel = new FiltreMot();

	CorpusService corpusService = ServiceLocator.getCorpusService();

	protected Tabs corpusTabs;

	protected Tabpanels corpusTabpanels;

	/**
	 * Ajout d'une nouvelle valeur (condition) au filtre courant
	 * @param event
	 */
	public void onClick$boutonAjoutFiltre(Event event) {
		
		// Y a-t-il encore des valeurs disponibles pour le filtre courant ?
		if (valeurFiltre.getItemCount() > 0 && valeurFiltre.getSelectedCount() > 0) {

			// Récupérer le nom du filtre auquel est associée cette valeur 
			Listitem filtreNomActuel = nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex());
			String nom = filtreNomActuel.getValue().toString();
			String description = filtreNomActuel.getLabel();
			
			Filtre filtre = new Filtre(nom, description, new ArrayList<DefaultKeyValue>(1)); //filtreActifModel.getFiltre(nom);
			
			// Quelles sont les valeurs à ajouter?
			for (@SuppressWarnings("unchecked")
			Iterator<Listitem> it = (Iterator<Listitem>)valeurFiltre.getSelectedItems().iterator(); it.hasNext();) {
				ajouteValeurAuFiltre( it.next(), filtre);
			}
			
			filtreActifModel.addFiltre(filtre);
			
			gridFiltreActif.setModel(new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(), filtreActifModel.getFiltreGroupes()));

			// Mettre à jour (modèle) les valeurs du filtre courant (sans la valeur qui vient d'être ajoutée au filtre)
			filtreManager.setFiltreActif(filtreActifModel);
			
			// Actualiser l'affichage (vue) des valeurs du filtre courant
			actualiseValeursFiltreCourant();

			// Relancer la recherche
			chercheEtAffiche();
		}

	}

	public void ajouteValeurAuFiltre(Listitem filtreValeurActuel, Filtre filtre) {
		
		String valeurString = filtreValeurActuel.getValue().toString();
		// Si la valeur == -1 cela signifie, par convention, qu'il s'agit d'une valeur « vide » à ignorer
		if (valeurString.equals("-1")) {
			return;
		}
		
		// Ajout du filtre au modèle ( => « de l'interface vers le modèle »)
		List<DefaultKeyValue> valeurs = filtre.getKeyValues();

		// Il faut comparer des pommes avec des pommes...  s'asurer que de la String HTML on passe bien vers le bon format Java
		//booléen?
		if (valeurString.equalsIgnoreCase("true") || valeurString.equalsIgnoreCase("false")) {
			valeurs.add(new DefaultKeyValue(new Boolean(valeurString), filtreValeurActuel.getLabel()));
		} 
		// Tous les nombres sont convertis en Long.  La couche JPA Criterai fera automatiquement les cast nécessaires.
		// nombre ?
		else if (valeurString.matches("\\d+")) {
			valeurs.add(new DefaultKeyValue(Long.parseLong(valeurString), filtreValeurActuel.getLabel()));
		}
		// String par défaut
		else {
			valeurs.add(new DefaultKeyValue(valeurString, filtreValeurActuel.getLabel()));
		}

	}

	public void onClick$boutonEffacerFiltre(Event event) {
		effacerTousLesFiltres();
	}

	protected void effacerTousLesFiltres() {
		if (filtreActifModel.getFiltres().size() > 0) {
			filtreActifModel.removeAll();
			gridFiltreActif.setModel(new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(), filtreActifModel.getFiltreGroupes()));
			actualiseValeursFiltreCourant();
			chercheEtAffiche();
		}
	}

	public void onSelect$nomFiltre(Event event) {
		actualiseValeursFiltreCourant();
	}

	// public void onSelect$valeurFiltre(Event event) {
	// //Désactive bouton ajouter si aucune valeur sélectionnée
	// Listitem currentItem =
	// valeurFiltre.getItemAtIndex(valeurFiltre.getSelectedIndex());
	//
	// if (currentItem != null && currentItem.getValue() != null &&
	// !currentItem.getValue().toString().equals("-1")) {
	// // activer le bouton ajouter
	// boutonAjoutFiltre.setDisabled(false);
	// System.err.println("activer le bouton ajouter");
	// }
	// else {
	// //désactiver le bouton ajouter
	// boutonAjoutFiltre.setDisabled(true);
	// System.err.println("désactiver le bouton ajouter");
	// }
	// }

	public void onAfterRender$nomFiltre() {
		actualiseValeursFiltreCourant();
	}

	private void actualiseValeursFiltreCourant() {
		
		Listitem currentItem = nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex());

		if (currentItem != null && currentItem.getValue() != null) {
			List<DefaultKeyValue> filtreValeurs = filtreManager.getFiltreValeurs(currentItem.getValue().toString());
			valeurFiltre.setModel(new SimpleListModel(filtreValeurs.toArray()));
//			if (filtreValeurs.size() > 0) {
//				valeurFiltre.setSelectedIndex(0);
//			}
		}
	}

	/**
	 * Permet d'associer dynamiquement le contenu d'un composant via variable
	 * resolver et EL expression (read-only seulement)
	 */
	@Override
	public Object resolveVariable(String variableName) throws XelException {
		// if (variableName.equals("listes"))
		// {
		// List<Liste> listes = new ArrayList<Liste>(1);
		// listes.add(new Liste("Toutes les listes", "Toutes les listes",
		// null));
		// listes.addAll(listeManager.findByCorpusId(CORPUS_ID_PAR_DÉFAUT));
		// return listes;
		// }
		return null;
	}

	/**
	 * Retourne le filtre à appliquer pour la recherche des mots
	 * 
	 * @return
	 */
	protected FiltreMot getFiltres() {

		FiltreMot filtres = new FiltreMot();

		// Ajout des autres filtres
		for (Filtre filtre : filtreActifModel.getFiltres()) {
			filtres.addFiltre(filtre);
		}

		return filtres;
	}

	protected void initialiseFiltre() {

		// Initialisation des filtres (noms)
		nomFiltre.setModel(new SimpleListModel(filtreManager.getFiltreNoms()));

		nomFiltre.setItemRenderer(new ListitemRenderer() {

			@Override
			public void render(Listitem item, Object keyValue) throws Exception {
				DefaultKeyValue kv = (DefaultKeyValue) keyValue;
				item.setValue(kv.getKey());
				item.setLabel(kv.getValue().toString());

			}
		});

		valeurFiltre.setItemRenderer(new ListitemRenderer() {

			@Override
			public void render(Listitem item, Object keyValue) throws Exception {
				DefaultKeyValue kv = (DefaultKeyValue) keyValue;
				item.setValue(kv.getKey());
				item.setLabel(kv.getValue().toString());
			}
		});

		if (nomFiltre.getItemCount() > 0) {
			nomFiltre.setSelectedIndex(0);
		}

		// DefaultKeyValue
		Object[][] data = new Object[][] {};
		Object[] heads = new Object[] {};

		gridFiltreActif.setModel(new SimpleGroupsModel(data, heads));

		gridFiltreActif.setRowRenderer(new RowRenderer() {

			@Override
			public void render(Row row, Object model) throws Exception {

				DefaultKeyValue cv = (DefaultKeyValue) model;

				final Row currentRow = row;

				Cell cell = new Cell();
				cell.setParent(row);

				final Label label = new Label(cv.getValue().toString());
				label.setAttribute("key", cv.getKey());
				label.setParent(cell);

				// Ajouter bouton « supprimer » si pas un groupe
				if (!(row instanceof Group)) {

					final Button supprimeBtn = new Button();
					supprimeBtn.setMold("os");
					supprimeBtn.setParent(row);
					supprimeBtn.setImage("/images/enlever-10x10.png");
					supprimeBtn.setTooltiptext("retirer « " + label.getValue() + " » des filtres actifs");
					supprimeBtn.addEventListener(Events.ON_CLICK, new EventListener() {

						@Override
						public void onEvent(Event arg0) throws Exception {

							if (currentRow != null) {

								Cell cell = (Cell) currentRow.getFirstChild();
								Label labelValeur = (Label) cell.getFirstChild();
								Label labelGroupe = (Label) currentRow.getGroup().getFirstChild().getFirstChild();

								filtreActifModel.removeFiltre(labelGroupe.getAttribute("key").toString(), labelValeur.getAttribute("key"));
								gridFiltreActif.setModel(new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(), filtreActifModel.getFiltreGroupes()));

								filtreManager.setFiltreActif(filtreActifModel);
								actualiseValeursFiltreCourant();

								chercheEtAffiche();
							}
						}
					});
				} else {
					cell.setColspan(2);
					((Label) cell.getFirstChild()).setSclass("groupe");
				}

			}
		});

	}

	abstract public void chercheEtAffiche();

	abstract protected void initialiseRecherche();
	
	abstract protected void exporterRésultatsCsv();
	
	abstract protected void exporterRésultatsXls();

	protected void effacerRecherche() {
		initialiseRecherche();
		cherche.setFocus(true);
	}

	/**
	 * Contient généralement qqchose du genre<br />
	 * <br />
	 * <code>this.filtreManager = ServiceLocator.getContexteFiltreManager();</code>
	 * 
	 * @param filtreManager
	 */
	abstract public void initFiltreManager();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		initFiltreManager();

		initialiseRecherche();

		initialiseFiltre();

		Tabbox tabbox = (Tabbox) webCorpusWindow.getFellow("corpusTabbox");
		corpusTabs = tabbox.getTabs();
		corpusTabpanels = tabbox.getTabpanels();

		initialiseClavierCaractèresSpéciaux();

		caractèresSpéciaux.setVisible(true);

	}

	protected void initialiseClavierCaractèresSpéciaux() {
		String[][] caractères = { { "à", "à" }, { "â", "â" }, { "é", "é" }, { "è", "è" }, { "ê", "ê" }, { "ë", "ë" }, { "ï", "ï" }, { "î", "î" }, { "ô", "ô" },
				{ "ù", "ù" }, { "û", "û" }, { "ç", "ç" }, { "œ", "œ" }, { "æ", "æ" } };

		for (String[] caractère : caractères) {

			Label label = new Label(caractère[0]);
			label.setTooltiptext(caractère[1]);
			label.setParent(caractèresSpéciaux);
			label.setSclass("caractèreSpécial");
			label.addEventListener(Events.ON_CLICK, new EventListener() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					Label l = (Label) arg0.getTarget();
					cherche.setText(getMotCherché() + l.getValue());
					cherche.setFocus(true);
				}
			});

		}

	}

	protected String getMotCherché() {
		return cherche.getText().trim();
	}

	protected String getEntêteCsv(Grid grid, String séparateur) {
		
		StringBuilder entete = new StringBuilder();
		
		String sép = "";
		
		// Récupération des entêtes
		for(Object column : grid.getColumns().getChildren()) {
			String label = ((Column) column).getLabel();
			label = ajouteGuillemetsCsv(label);
			entete.append(sép).append(label);
			sép = séparateur;
		}
		entete.append("\n");
		
		return entete.toString();
	}

	
	// Entourer de guillemets et doubler les éventuels guillemets contenus dans le nom de l'entête
	protected String ajouteGuillemetsCsv(String label) {
		String guillemetsOK = "\"" + label.replaceAll("\"", "\"\"") + "\"";
		return guillemetsOK.replaceAll("\n", "\u2028");
	}
	
}
