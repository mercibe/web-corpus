package com.servicelibre.zk.controller;

import java.util.ArrayList;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Group;
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

	Listbox nomFiltre; // autowire car même type/ID que le composant dans la
	// page ZUL
	Listbox valeurFiltre;// autowire car même type/ID que le composant dans la
	// page ZUL

	Button boutonAjoutFiltre;// autowire car même type/ID que le composant dans
	// la page ZUL
	Grid gridFiltreActif;// autowire car même type/ID que le composant dans la
	// page ZUL

	Label infoRésultats;

	protected Window webCorpusWindow;
	
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

	public void onClick$boutonAjoutFiltre(Event event) {

		if (valeurFiltre.getItemCount() > 0) {

			Listitem filtreNomActuel = nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex());
			String nom = filtreNomActuel.getValue().toString();
			String description = filtreNomActuel.getLabel();

			Listitem filtreValeurActuel = valeurFiltre.getItemAtIndex(valeurFiltre.getSelectedIndex());
			List<DefaultKeyValue> valeurs = new ArrayList<DefaultKeyValue>(1);
			valeurs.add(new DefaultKeyValue(filtreValeurActuel.getValue().toString(), filtreValeurActuel.getLabel()));

			filtreActifModel.addFiltre(new Filtre(nom, description, valeurs));
			gridFiltreActif.setModel(new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(), filtreActifModel.getFiltreGroupes()));

			// suppression de la valeur active de la liste de choix
			filtreManager.setFiltreActif(filtreActifModel);
			rafraichiValeurFiltreCourant();

			chercheEtAffiche();
		}

	}

	public void onSelect$nomFiltre(Event event) {
		rafraichiValeurFiltreCourant();

	}

	public void onAfterRender$nomFiltre() {
		rafraichiValeurFiltreCourant();
	}

	private void rafraichiValeurFiltreCourant() {
		Listitem currentItem = nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex());

		if (currentItem != null && currentItem.getValue() != null) {
			List<DefaultKeyValue> filtreValeurs = filtreManager.getFiltreValeurs(currentItem.getValue().toString());
			valeurFiltre.setModel(new SimpleListModel(filtreValeurs.toArray()));
			if (filtreValeurs.size() > 0) {
				valeurFiltre.setSelectedIndex(0);
			}
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
			System.err.println("Ajout du filtre utilisateur " + filtre);
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

				Label label = new Label(cv.getValue().toString());
				label.setAttribute("key", cv.getKey());
				label.setParent(row);

				// Ajouter bouton « supprimer » si pas un groupe
				if (!(row instanceof Group)) {

					final Button supprimeBtn = new Button("Supprimer");
					supprimeBtn.setMold("os");
					supprimeBtn.setParent(row);
					supprimeBtn.addEventListener(Events.ON_CLICK, new EventListener() {

						@Override
						public void onEvent(Event arg0) throws Exception {

							if (currentRow != null) {

								Label labelValeur = (Label) currentRow.getFirstChild();
								Label labelGroupe = (Label) currentRow.getGroup().getFirstChild();
								filtreActifModel.removeFiltre(labelGroupe.getAttribute("key").toString(), labelValeur.getAttribute("key").toString());
								gridFiltreActif.setModel(new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(), filtreActifModel.getFiltreGroupes()));

								filtreManager.setFiltreActif(filtreActifModel);
								rafraichiValeurFiltreCourant();

								chercheEtAffiche();
							}
						}
					});
				}

			}
		});

	}

	abstract public void chercheEtAffiche();

	abstract protected void initialiseRecherche();

	
	/**
	 * Contient généralement qqchose du genre<br /><br />
	 * <code>this.filtreManager = ServiceLocator.getContexteFiltreManager();</code>
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
	} 

	
	
}
