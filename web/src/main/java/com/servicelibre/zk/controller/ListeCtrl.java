package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Group;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleGroupsModel;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.corpus.manager.PrononciationManager;

/**
 * Démontre MVC: Autowire UI objects to data members
 * 
 * @author benoitm
 * 
 */
public class ListeCtrl extends GenericForwardComposer implements VariableResolver {

	private static Logger logger = LoggerFactory.getLogger(ListeCtrl.class);

	private static final int CORPUS_ID_PAR_DÉFAUT = 1;

	Combobox gp; // autowire car même type/ID que le composant dans la page ZUL
	Combobox condition; // autowire car même type/ID que le composant dans la
	// page ZUL

	Textbox cherche; // autowire car même type/ID que le composant dans la page
	// ZUL
	Grid motsGrid; // autowire car même type/ID que le composant dans la page
	// ZUL
	Column mot;

	Listbox nomFiltre; // autowire car même type/ID que le composant dans la
	// page ZUL
	Listbox valeurFiltre;// autowire car même type/ID que le composant dans la
	// page ZUL

	Button boutonAjoutFiltre;// autowire car même type/ID que le composant dans
	// la page ZUL
	Grid gridFiltreActif;// autowire car même type/ID que le composant dans la
	// page ZUL

	Button boutonRecherche;

	Label infoRésultats;
	
	Div api;

	/**
	 * Permet de remplir les choix de filtres/valeurs possibles
	 */
	FiltreManager filtreManager = ServiceLocator.getListeFiltreManager();

	/**
	 * Filtre qui sera passé au MotManager pour filtrer les mots à retourner. Ce
	 * filtre sert également comme Model (GroupModel) pour le Grid qui affiche
	 * le filtre en construction par l'utilisateur.
	 */
	FiltreMot filtreActifModel = new FiltreMot();

	ListeManager listeManager = ServiceLocator.getListeManager();

	MotManager motManager = ServiceLocator.getMotManager();
	PrononciationManager prononciationManager = ServiceLocator.getPrononciationManager();

	private static final long serialVersionUID = 779679285074159073L;

	private Column motColumn;

	private Window webCorpusWindow;

	// Enregistrement des événements onOK (la touche ENTER) sur tous les
	// composants de la recherche
	public void onOK$cherche(Event event) {
		chercheEtAfficheMot();
	}

	public void onClick$boutonRecherche(Event event) {

		chercheEtAfficheMot();

	}

	public void onOK$liste(Event event) {
		chercheEtAfficheMot();
	}

	public void onOK$condition(Event event) {
		chercheEtAfficheMot();
	}

	public void onOK$gp(Event event) {
		chercheEtAfficheMot();
	}

	public void onClick$boutonAjoutFiltre(Event event) {

		if (valeurFiltre.getItemCount() > 0) {

			Listitem filtreNomActuel = nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex());
			String nom = filtreNomActuel.getValue().toString();
			String description = filtreNomActuel.getLabel();

			Listitem filtreValeurActuel = valeurFiltre.getItemAtIndex(valeurFiltre.getSelectedIndex());
			List<DefaultKeyValue> valeurs = new ArrayList<DefaultKeyValue>(1);
			valeurs.add(new DefaultKeyValue(filtreValeurActuel.getValue().toString(), filtreValeurActuel.getLabel()));

			filtreActifModel.addFiltre(new Filtre(nom, description, valeurs));
			SimpleGroupsModel model = new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(), filtreActifModel.getFiltreGroupes());
			gridFiltreActif.setModel(model);

			// suppression de la valeur active de la liste de choix
			filtreManager.setFiltreActif(filtreActifModel);
			rafraichiValeurFiltreCourant();

			chercheEtAfficheMot();
		}

	}
	
	public void onSelect$gp(Event event) {
		// 
		if(getGpActif().equals("p")){
			api.setVisible(true);
		}
		else {
			api.setVisible(false);
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

		if (currentItem.getValue() != null) {
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
		if (variableName.equals("listes")) {
			List<Liste> listes = new ArrayList<Liste>(1);
			listes.add(new Liste("Toutes les listes", "Toutes les listes", null));
			listes.addAll(listeManager.findByCorpusId(CORPUS_ID_PAR_DÉFAUT));
			return listes;
		}
		return null;
	}

	public void chercheEtAfficheMot() {
		ListModelList modelList = new ListModelList(getMotsRecherchés());
		motsGrid.setModel(modelList);
		motsGrid.getPaginal().setActivePage(0);
		// les mots sont toujours retournés par ordre alphabétique => refléter
		// dans la colonne (réinitialisation du marqueur de tri)

		motColumn.sort(true);

		infoRésultats.setValue(getInfoRésultat(modelList));

	}

	private String getInfoRésultat(ListModelList modelList) {

		StringBuilder sb = new StringBuilder();

		String conditionActive = condition.getItemAtIndex(condition.getSelectedIndex()).getLabel();

		String prononc = getGpActif().equals("p") ? " la prononciation" : "";

		String motCherché = getMotCherché();

		if (motCherché != null && motCherché.isEmpty()) {
			String terminaison = modelList.size() > 1 ? "s" : "";
			sb.append(modelList.size()).append(" mot").append(terminaison).append(" trouvé").append(terminaison).append(".");
		} else {
			if (modelList.size() == 0) {
				sb.append(" Aucun mot ne ").append(conditionActive.replace("ent ", "e ")).append(prononc).append(" « ").append(motCherché).append(" ».");
			} else if (modelList.size() == 1) {
				sb.append(" Un seul mot ").append(conditionActive.replace("nnent ", "nt ").replace("dent ", "d ").replace("ent ", "e ")).append(prononc)
						.append(" « ").append(motCherché).append(" ».");
			} else {
				sb.append(modelList.size()).append(" mots").append(" ").append(conditionActive).append(prononc).append(" « ").append(motCherché).append(" ».");
			}
		}

		return sb.toString();
	}

	private List<Mot> getMotsRecherchés() {
		List<Mot> mots = new ArrayList<Mot>();

		System.out.println(getDescriptionRecherche());

		String conditionActive = getConditionActive();

		// TODO sort sur colonnes
		// TODO historique des recherches

		String gpActif = getGpActif();

		FiltreMot filtres = getFiltres();

		if (gpActif.equals("g")) {
			mots = motManager.findByGraphie(getMotCherché(), MotManager.Condition.valueOf(conditionActive), filtres);
		} else {
			mots = motManager.findByPrononciation(getMotCherché(), MotManager.Condition.valueOf(conditionActive), filtres);

			// try
			// {
			// Messagebox.show("La recherche par phonème n'est pas encore implémentée.",
			// "Corpus", Messagebox.OK,
			// Messagebox.INFORMATION);
			// }
			// catch (InterruptedException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

		return mots;
	}

	private String getGpActif() {
		return (String) gp.getItemAtIndex(gp.getSelectedIndex()).getValue();
	}

	private String getConditionActive() {
		return (String) condition.getItemAtIndex(condition.getSelectedIndex()).getValue();
	}

	private String getMotCherché() {
		return cherche.getText().trim();
	}

	/**
	 * Retourne le filtre à appliquer pour la recherche des mots
	 * 
	 * @return
	 */
	private FiltreMot getFiltres() {
		FiltreMot filtres = new FiltreMot();

		// Ajout des filtres
		for (Filtre filtre : filtreActifModel.getFiltres()) {
			System.err.println("Ajout du filtre utilisateur " + filtre);
			filtres.addFiltre(filtre);
		}

		return filtres;
	}

	/**
	 * Retourne une phrase qui décrit les critères de recherche courant
	 * 
	 * @return
	 */
	public String getDescriptionRecherche() {
		StringBuilder desc = new StringBuilder();

		desc.append("Rechercher tous les ").append(gp.getValue());

		String aChercher = getMotCherché();

		if (!aChercher.isEmpty()) {
			desc.append(" qui ").append(condition.getValue()).append(" « ").append(aChercher).append(" »");
		}

		return desc.toString();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		initialiseChamps();

		initialiseRecherche();

		initialiseMotsGrid();

		initialiseFiltre();
		
		initialiseClavierPhonétique();

	}

	private void initialiseClavierPhonétique() {
		
		
		String[][] apiLettres = {
				{"i","épi, île, lys, outil"},
				{"i:","jean, tweed"},
				{"y","hutte, bulle, fût"},
				{"u","ours, toundra, pouls"},
				{"u:","slow food, pool"},
				{"e","(e fermé) érable, pécher, chez, hockey"},
				{"ø","(eu fermé)jeu, heureux, bleuet"},
				{"o","(o fermé) auto, côté, beau, sirop"},
				{"ɛ","(e ouvert) aimer, épinette, accès"},
				{"ɛ:","blême, caisse, gène, mètre, paraître, presse"},
				{"œ","(eu ouvert) neuf, oeuf, bonheur, golden, joker"},
				{"ɔ","(o ouvert) obéir, autochtone, port"},
				{"ə","(e caduc, ou muet)mener, crénelage"},
				{"ə̠","fenouil, cafetière, justement"},
				{"a","(a antérieur) à, clavardage, patte"},
				{"ɑ","(a postérieur) là-bas, pâte, cipaille, pyjama"},
				{"ɛ̃","brin, impair, indien, certain, frein"},
				{"œ̃","un, lundi, brun, parfum"},
				{"ɔ̃","montagnais, omble, pont"},
				{"ɑ̃","an, en, jambon, sang, temps"},
				
				{"p","paix, sapin, cégep"},
				{"t","toit, thé, patin, attaché, fourchette"},
				{"k","coq, chrome, bec, disque, kayak"},
				{"b","boréal, tablée, snob"},
				{"d","danse, cheddar, balade, bled"},
				{"g","gag, algue, guide"},
				{"f","fleuve, alphabet, effort, boeuf"},
				{"s","sourcil, cinq, force, mocassin, glaçon"},
				{"ʃ","chalet, schéma, échelle, brunch"},
				{"v","ville, cavité, drave"},
				{"z","maison, zénith, dixième, brise"},
				{"ʒ","jeudi, giboulée, neige"},
				{"l","laine, alcool, pelle"},
				{"ʀ","rang, courriel, finir"},
				{"m","mitaine, femme, aluminium"},
				{"n","nordet, antenne, cabane"},
				{"ɲ","beigne, campagne"},
				{"ŋ","big bang, camping, flamenco, bingo, ping pong"},
				{"'","les haches, un huit, la ouananiche, les unes (sans élision ni liaison)"},
				
				{"j","rien, payer, écureuil, fille, yogourt"},
				{"ɥ","lui, huissier, tuile"},
				{"w","louer, ouate, watt, bois"},
		};
		
		for(String[] apiLettreInfo : apiLettres) {
			
			Label label = new Label(apiLettreInfo[0]);
			label.setTooltiptext(apiLettreInfo[1]);
			label.setParent(api);
			label.setClass("apiLettre");
			label.addEventListener(Events.ON_CLICK, new EventListener() {
				
				@Override
				public void onEvent(Event arg0) throws Exception {
					Label l = (Label) arg0.getTarget();
					cherche.setText(getMotCherché() + l.getValue());
					
				}
			});
			
		}
		
	}

	// @Override
	// public void onEvent(Event evt) throws Exception
	// {
	// super.onEvent(evt);
	// System.err.println("ListeCtrl: " + evt.getName());
	// }

	private void initialiseChamps() {
		Page webCorpusPage = desktop.getPage("webCorpusPage");
		webCorpusWindow = (Window) webCorpusPage.getFellow("webCorpusWindow");

	}

	private void initialiseFiltre() {
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

							Label labelValeur = (Label) currentRow.getFirstChild();
							Label labelGroupe = (Label) currentRow.getGroup().getFirstChild();

							filtreActifModel.removeFiltre(labelGroupe.getAttribute("key").toString(), labelValeur.getAttribute("key").toString());
							SimpleGroupsModel model = new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(), filtreActifModel.getFiltreGroupes());
							gridFiltreActif.setModel(model);

							filtreManager.setFiltreActif(filtreActifModel);
							rafraichiValeurFiltreCourant();

							chercheEtAfficheMot();

						}
					});
				}

			}
		});

	}

	private void initialiseMotsGrid() {
		motsGrid.setModel(new ListModelList(getMotsRecherchés()));

		motsGrid.setRowRenderer(new RowRenderer() {

			@Override
			public void render(Row row, Object model) throws Exception {
				Mot mot = (Mot) model;

				Label motLabel = new Label(mot.getMot());
				motLabel.setStyle("cursor:hand;cursor:pointer");

				motLabel.addEventListener(Events.ON_CLICK, new EventListener() {

					@Override
					public void onEvent(Event event) throws Exception {
						Label label = (Label) event.getTarget();
						afficheContexte(label.getValue());
					}
				});

				row.appendChild(motLabel);
				row.appendChild(new Label(mot.getCatgram()));
				row.appendChild(new Label(mot.getGenre()));
				row.appendChild(new Label(mot.getNombre()));
				row.appendChild(new Label(mot.getCatgramPrésicion()));
				row.appendChild(new Label(mot.getListe().getNom()));

			}
		});

		// Enregistrement événement pour lien vers contextes
		motColumn = (Column) motsGrid.getColumns().getFellow("mot");

	}

	private void initialiseRecherche() {
		gp.setSelectedIndex(0);
		condition.setSelectedIndex(0);

	}

	private void afficheContexte(String lemme) {

		logger.debug("Afficher les contextes de « {} »", lemme);

		Include contexteInclude = (Include) webCorpusWindow.getFellow("contexteInclude");
		Window contexteWindow = (Window) contexteInclude.getFellow("contexteWindow");

		contexteWindow.setAttribute("lemme", lemme);
		Events.sendEvent("onAfficheContexte", contexteWindow, lemme);

	}

	@SuppressWarnings("unused")
	private void displayChildren(Component comp, String sep) {
		@SuppressWarnings("unchecked")
		List<Component> children = comp.getChildren();

		System.out.println("Children of " + comp.getUuid() + "(" + comp.getId() + ")");
		for (Component component : children) {
			System.out.println(component.getUuid() + "(" + component.getId() + ")");
			displayChildren(component, sep + "\t");
		}

	}

}
