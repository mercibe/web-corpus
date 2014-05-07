package com.servicelibre.zk.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.ZulEvents;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.metadata.Metadata;
import com.servicelibre.corpus.service.Contexte;
import com.servicelibre.corpus.service.ContexteSet;
import com.servicelibre.corpus.service.ContexteSet.Position;
import com.servicelibre.corpus.service.CorpusPhraseService;
import com.servicelibre.corpus.service.InfoCooccurrent;
import com.servicelibre.corpus.service.PhraseService;
import com.servicelibre.entities.corpus.DocMetadata;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.entities.ui.Paramètre;
import com.servicelibre.repositories.corpus.DocMetadataRepository;
import com.servicelibre.zk.controller.renderer.ContexteRowRenderer;
import com.servicelibre.zk.recherche.Recherche;
import com.servicelibre.zk.recherche.RechercheContexte;
import com.servicelibre.zk.recherche.RechercheExécution;

/**
 * 
 * 
 * @author benoitm
 * 
 */
public class ContexteCtrl extends CorpusCtrl {

	private static Logger logger = LoggerFactory.getLogger(ContexteCtrl.class);

	private static final int MAX_COOCCURRENTS = 100;

	Combobox condition; // autowire car même type/ID que le composant dans la page ZUL

	Combobox voisinage;

	Grid contextesGrid; // autowire car même type/ID que le comgetposant dans la page ZUL

	Window contexteWindow;

	A cooccurrentLien;

	Button remarqueContextesButton;

	PhraseService phraseService = new CorpusPhraseService();

	DocMetadataRepository docMetadataRepo = ServiceLocator.getDocMetadataRepo();

	private static final long serialVersionUID = 779679285074159073L;

	private ContexteSet contexteSetCourant;

	private boolean phraseComplète;

	Combobox actionCombobox;
	Button actionButton;

	private String urlRemarque;

	private String titreRemarque;

	public void onClick$actionButton() {
		Comboitem selectedItem = actionCombobox.getSelectedItem();
		if (selectedItem != null) {
			String action = selectedItem.getValue();
			logger.debug(" exécuter l'action demandée : {}", action);

			if (action.equals("AJOUTER_AU_MOT")) {

				ajouterContextesSélectionnésAuMotDUneListeExistante();
			} else {
				Messagebox.show("Fonctionnalité en cours d'implémentation.", "En cours...", Messagebox.OK, Messagebox.INFORMATION);
			}

		}
	}

	public void onClick$remarqueContextesButton() {

		// Créér la fenêtre Popup
		/*
		 * 
		 * <window id="remarqueContextesWin" width="50%" height="50%" title="Remarque sur les contextes" border="normal"
		 * maximizable="true" visible="false" sizable="true" mode="popup" >
		 * <iframe id="remarqueContextesIframe" src="" height="100%" width="100%"/>
		 * </window>
		 */

		Window remarqueContextesWin = new Window(this.titreRemarque.toUpperCase(Locale.CANADA_FRENCH), "normal", true);
		remarqueContextesWin.setPosition("center,center");
		remarqueContextesWin.setWidth("75%");
		remarqueContextesWin.setHeight("75%");
		remarqueContextesWin.setParent(contexteWindow);

		Iframe remarqueContextesIframe = new Iframe(urlRemarque);
		remarqueContextesIframe.setWidth("100%");
		remarqueContextesIframe.setHeight("100%");

		remarqueContextesWin.appendChild(remarqueContextesIframe);

		remarqueContextesWin.doModal();
	}

	private void ajouterContextesSélectionnésAuMotDUneListeExistante() {
		Messagebox.show("Fonctionnalité en cours d'implémentation.", "En cours...", Messagebox.OK, Messagebox.INFORMATION);

	}

	public void onOK$liste(Event event) {
		chercheEtAffiche(true);
	}

	public void onClick$cooccurrentLien(Event event) {
		afficheCooccurrents();
	}

	public void onOK$condition(Event event) {
		chercheEtAffiche(true);
	}

	public void onSelect$condition(Event event) {
		cherche.setFocus(true);
		cherche.select();
	}

	public void onOK$gp(Event event) {
		chercheEtAffiche(true);
	}

	public void onAfficheContexte$contexteWindow(Event event) {

		// System.err.println(event.getName() + ": affiche contexte de " +
		// event.getData());
		String lemme = (String) contexteWindow.getAttribute("lemme");

		// Mettre le lemme dans le champ recherche
		cherche.setValue(lemme);

		// Chercher toutes les formes
		condition.setSelectedIndex(1);

		// TODO devrions-nous réinitialiser filtre ? Option?
		// effacerTousLesFiltres();

		// Sélectionner l'onglet Contexte
		Tab contexteTab = (Tab) webCorpusWindow.getFellow("contexteTab");
		contexteTab.setSelected(true);

		// Lancer la recherche
		chercheEtAffiche(true);

	}

	private String getInfoRésultat(ContexteSet contexteSet) {

		StringBuilder sb = new StringBuilder();

		// TODO rendre plus « intelligent » (cf. listes)
		String terminaison = contexteSet.size() > 1 ? "s" : "";
		sb.append(contexteSet.getTotalContextesCount()).append(" occurrence").append(terminaison)
				.append(contexteSet.isFormesDuLemme() ? " des formes du mot « " : " du mot « ").append(contexteSet.getMotCherché())
				.append(" » trouvée").append(terminaison).append(" dans ").append(contexteSet.getDocumentCount()).append(" document")
				.append(contexteSet.getDocumentCount() > 1 ? "s" : "").append(".");

		return sb.toString();
	}

	// private ContexteSet getContexteSet(int voisinage) {
	//
	// phraseComplète = false;
	//
	// ContexteSet contexteSet = new ContexteSet();
	//
	// String aChercher = cherche.getText();
	//
	// if (aChercher == null || aChercher.trim().isEmpty()) {
	// return contexteSet;
	// }
	//
	// System.out.println(getDescriptionRecherche());
	//
	// // Recherche phrase complète
	// if (voisinage == 0) {
	// phraseComplète = true;
	// }
	//
	// corpusService.setTailleVoisinage(phraseComplète ? 50 : voisinage);
	//
	// FiltreRecherche filtres = getFiltres();
	//
	// // si le mot n'est pas un lemme, rechercher ce mot seulement
	// if (!corpusService.isLemme(aChercher)) {
	// // ajustement du critère de recherche
	// condition.setSelectedIndex(0);
	// }
	//
	// // Chercher toutes les formes en fonction de condition.getValue()
	// if
	// (condition.getItemAtIndex(condition.getSelectedIndex()).getValue().equals("TOUTES_LES_FORMES_DU_MOT"))
	// {
	// contexteSet = corpusService.getContextesLemme(aChercher, filtres);
	// } else {
	// contexteSet = corpusService.getContextesMot(aChercher, filtres);
	// }
	//
	// return contexteSet;
	// }

	@Override
	public Recherche getRecherche() {

		Recherche recherche = new RechercheContexte();

		// Recherche phrase complète
		int voisinage = getVoisinageUtilisateur();
		if (voisinage == 0) {
			recherche.setPrécisionRésultat("50");
			phraseComplète = true;
		} else {
			recherche.setPrécisionRésultat(voisinage + "");
			phraseComplète = false;
		}

		// recherche de contextes
		recherche.setCible(Recherche.Cible.CONTEXTE);

		String motCherché = getMotCherché();

		// chaîne à chercher (entrée par l'utilisateur)
		recherche.setChaîne(motCherché);

		// si le mot n'est pas un lemme, rechercher ce mot seulement
		// FIXME hack - ou qu'il contient un tiret...
		if (!corpusService.isLemme(motCherché) || motCherché.contains("-")) {
			// ajustement du critère de recherche
			condition.setSelectedIndex(0);
		}

		// exactement le mot ou toutes les formes du lemmes?
		recherche.setPrécisionChaîne(getPrécisionChaîne());

		// Filtres
		recherche.setFiltres(getFiltres());
		
		recherche.grilleRésultatsPaging = grilleRésultatsPaging;

		return recherche;
	}

	private String getPrécisionChaîne() {
		return condition.getItemAtIndex(condition.getSelectedIndex()).getValue().toString();
	}

	public ContexteSet exécuterRecherche(Recherche recherche, boolean ajouterHistorique, boolean retournerTousLesRésultats) {

		ContexteSet contexteSet = new ContexteSet();

		// Ne rien faire si la chaîne est vide
		if (recherche.chaîne == null || recherche.chaîne.trim().isEmpty()) {
			return contexteSet;
		}

		int taillePage = recherche.grilleRésultatsPaging.getPageSize();
		int pageActive = recherche.grilleRésultatsPaging.getActivePage();
		int deIndex = Math.max(0, pageActive * taillePage);
		
		if(retournerTousLesRésultats) {
			deIndex = -1;
			taillePage = -1;
		}

		logger.info(recherche.getDescriptionChaîne());
		logger.debug("Recherche page {} (à partir de {}), {} éléments par page", new int[] {pageActive, deIndex, taillePage});

		corpusService.setTailleVoisinage(Integer.parseInt(recherche.précisionRésultat));

		switch (recherche.cible) {
		case CONTEXTE:
			switch (RechercheContexte.PrécisionChaîne.valueOf(recherche.précisionChaîne)) {
			case EXACTEMENT_LE_MOT:
				contexteSet = corpusService.getContextesMot(prépareChaîneMot(recherche.chaîne), recherche.filtres, deIndex, taillePage);
				break;
			case TOUTES_LES_FORMES_DU_MOT:
				contexteSet = corpusService.getContextesLemme(prépareChaîneLemme(recherche.chaîne), recherche.filtres, deIndex, taillePage);
				break;
			default:
				logger.error("PrécisionChaîne invalide pour une recherche de contextes: " + recherche.précisionChaîne);
				break;
			}
			break;
		default:
			logger.error("Cible invalide pour une recherche de contextes: " + recherche.cible);
			break;
		}

		if (ajouterHistorique) {
			ajouterRechercheHistorique(recherche, contexteSet.size());
		}

		logger.debug("contexteSet.getTotalContextesCount() = {}",contexteSet.getTotalContextesCount());
		recherche.grilleRésultatsPaging.setTotalSize(contexteSet.getTotalContextesCount());
		
		return contexteSet;
	}

	/**
	 * Hack en attendant couche Lucene OK (alignement analyser/query paser, etc.)
	 * 
	 * @param recherche
	 * @return
	 */
	private String prépareChaîneLemme(String chaîne) {
		return chaîne.replaceAll("-", " ");
	}

	/**
	 * Hack en attendant couche Lucene OK (alignement analyser/query paser, etc.)
	 * 
	 * @param recherche
	 * @return
	 */
	private String prépareChaîneMot(String rechechaînerche) {
		return "\"" + prépareChaîneLemme(rechechaînerche) + "\"";
	}

	private int getVoisinageUtilisateur() {
		return Integer.parseInt(voisinage.getSelectedItem().getValue().toString());
	}

	/**
	 * Retourne une phrase qui décrit les critères de recherche courant
	 * 
	 * @return
	 */
	public String getDescriptionRecherche() {
		StringBuilder desc = new StringBuilder();

		String aChercher = cherche.getValue().trim();

		if (!aChercher.isEmpty()) {
			desc.append("Rechercher les contextes pour ").append(condition.getValue().toLowerCase());

			desc.append(" « ").append(aChercher).append(" »");
		}

		return desc.toString();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		// Afficher ou non le bouton de remarque
		Paramètre urlRemarque = paramètreRepo.findByNom("URLRemarqueContexte");
		if (urlRemarque != null) {
			this.urlRemarque = urlRemarque.getValeur();
			remarqueContextesButton.setVisible(true);
			// A-t-il un libellé spécifique?
			Paramètre texteBouton = paramètreRepo.findByNom("texteBoutonRemarqueContextes");
			if (texteBouton != null) {
				this.titreRemarque = texteBouton.getValeur();
				remarqueContextesButton.setLabel(titreRemarque);
			} else {
				this.titreRemarque = "Remarque sur les contextes";
			}
		}

		initialiseChamps();
		
		créeEventListenerRésultatsPaging();

		initialiseContexteGrid();

	}

	private void créeEventListenerRésultatsPaging() {
		
		grilleRésultatsPaging.addEventListener(ZulEvents.ON_PAGING, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				PagingEvent e = (PagingEvent) event;

				Recherche recherche = (Recherche) contextesGrid.getAttribute("recherche", Component.COMPONENT_SCOPE);
				if (recherche != null) {
					// Les requêtes de pagination ne doivent pas être mise dans l'historique!
					recherche.grilleRésultatsPaging = (Paging) e.getPageable();
					remplisGrilleContextes(exécuterRecherche(recherche, false, false), recherche.grilleRésultatsPaging.getActivePage(), grilleRésultatsTaillePage);
				} else {
					System.err.println("L'objet recherche associé au Grid est introuvable");
				}

			}
		});
		
	}

	private void initialiseChamps() {
		Page webCorpusPage = desktop.getPage("webCorpusPage");
		webCorpusWindow = (Window) webCorpusPage.getFellow("webCorpusWindow");

	}

	private void initialiseContexteGrid() {
		
		Recherche recherche = getRecherche();
		remplisGrilleContextes(new ContexteSet(), PREMIÈRE_PAGE, grilleRésultatsTaillePage);

		// Stockage de la recherche comme attribut du grid pour réutilisation par le composant Paging
		contextesGrid.setAttribute("recherche", recherche, Component.COMPONENT_SCOPE);
		
		contextesGrid.setRowRenderer(new ContexteRowRenderer(this));

	}
	
	private int remplisGrilleContextes(ContexteSet contextes, int pageActive, int taillePage) {

		int nbContextes = contextes.getTotalContextesCount();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ListModelList modelList = new ListModelList(contextes.getContextes());//.subList(deIndex, àIndex));

		grilleRésultatsPaging.setTotalSize(nbContextes);
		grilleRésultatsPaging.setActivePage(pageActive);

		contextesGrid.setModel(modelList);

		return nbContextes;
	}

	public Contexte getContexteInitial(Contexte contexte) {

		if (phraseComplète) {
			contexte = phraseService.getContextePhraseComplète(contexte);
		}
		return contexte;
	}

	private void afficheCooccurrents() {

		// TODO prévenir/possibilité annuler si voisinage trop grand et beaucoup
		// de contexte: 5 minutes au moins!
		// conseil: réduire le voisinage à moins de 5 mots... (phrase complète)

		// ContexteSet contexteSetCooccurrent;
		// if (contexteSetCourant.getContextesSize() > 200
		// || contexteSetCourant.getTailleVoisinage() > 10) {
		// System.err
		// .println("Cela pourrait être long... Réduction des contextes");
		// // Relancer la recherche avec voisinage = 3
		// contexteSetCooccurrent = getContexteSet(3);
		// } else {
		// contexteSetCooccurrent = contexteSetCourant;
		// }

		// Relancer la recherche avec voisinage = 3 par défaut
		Recherche recherche = getRecherche();
		recherche.setPrécisionRésultat("3");
		ContexteSet contexteSetCooccurrent = exécuterRecherche(recherche, false, false);

		if (contexteSetCooccurrent == null) {
			return;
		}

		String id = contexteSetCooccurrent.getMotCherché() + "_" + contexteSetCooccurrent.getTailleVoisinage();

		// TODO pour améliorer vue des cooccurrents
		/*
		 * - lemmatiser les tokens + comptage sur lemme et plus sur mot -
		 * afficher catgram du lemme + filtre sur catgram - cliquer sur un
		 * cooccurrent recherche les contextes du terme et du cooccurrent
		 * (SpanNear query custom)
		 */

		Tab infoCooccurrentTab = getTabDéjàOuvert(id);

		if (infoCooccurrentTab == null) {
			infoCooccurrentTab = new Tab(id);
			infoCooccurrentTab.setId(id);
			infoCooccurrentTab.setClosable(true);
			infoCooccurrentTab.setTooltiptext(id);
			infoCooccurrentTab.setParent(corpusTabs);

			// Ajouter panel
			Tabpanel tabpanel = new Tabpanel();

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("terme", contexteSetCooccurrent.getMotCherché());
			contexteSetCooccurrent.setMaxCooccurrent(MAX_COOCCURRENTS);
			Map<Position, List<InfoCooccurrent>> infoCooccurrents = contexteSetCooccurrent.getInfoCooccurrents();
			args.put("infoG", infoCooccurrents.get(Position.AVANT));
			args.put("infoM", infoCooccurrents.get(Position.AVANT_APRÈS));
			args.put("infoD", infoCooccurrents.get(Position.APRÈS));

			Executions.createComponents("/infoCooccurrents.zul", tabpanel, args);

			tabpanel.setParent(corpusTabpanels);
		} else {
			System.out.println("Onglet info cooccurrent déjà ouvert: " + id);
		}

		infoCooccurrentTab.setSelected(true);

	}

	public void créeEtAfficheOngletInfoContexte(Contexte contexte) {

		// Vérifier si contexte déjà ouvert
		Tab infoContexteTab = getTabDéjàOuvert(contexte.getId());

		if (infoContexteTab == null) {
			infoContexteTab = new Tab(contexte.getId());
			infoContexteTab.setId(contexte.getId());
			infoContexteTab.setClosable(true);
			infoContexteTab.setTooltiptext(contexte.getId());
			infoContexteTab.setParent(corpusTabs);
			final Tab tabPrécédent = corpusTabs.getTabbox().getSelectedTab();
			infoContexteTab.addEventListener(Events.ON_CLOSE, new EventListener() {

				@Override
				public void onEvent(Event event) throws Exception {
					// Redonne le focus au Tab qui était actif lors de la demande d'affichage de l'onglet d'information
					// sur les contextes SI le tab actuel est sélectionné
					Tab tabActuel = (Tab) event.getTarget();
					if (tabActuel.isSelected() && tabPrécédent != null) {
						tabPrécédent.setSelected(true);
					}

					// vérifier si bouton fermer tous les onglets doit être masqué
					afficheOuMasqueBoutonFermerTousLesOnglets(-1);

				}

			});

			// vérifier si bouton fermer tous les onglets doit être affiché
			afficheOuMasqueBoutonFermerTousLesOnglets(0);

			// Ajouter panel
			Tabpanel tabpanel = new Tabpanel();

			// TODO pour améliorer vue des contextes
			/*
			 * - s'asurer que tous les contextes aient des métadonnées
			 * 
			 * - naviguer au contexte suivant/précédent (cf. grid de l'onglet
			 * contexte. Via Model?) => changer id du tab aussi!
			 * 
			 * - afficher numéro de ligne/contexte - mapping DB entre nom champ
			 * index Lucene et nom logique
			 * 
			 * - phrases du voisinage non nettoyées (conserver retours à la
			 * ligne, etc.) - afficher document binaire source (téléchargement)
			 * si rôle admin
			 */

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("mot", contexte.mot);

			// TODO appeler un service traducteurmetadata

			args.put("métadonnées", contexte.getDocMétadonnéesPrimaires());

			Contexte contexteSource = contexte.getContexteSource();
			if (contexteSource != null) {
				// La phrase complète a déjà été extraite. Il s'agit du contexte
				// lui-même
				args.put("phrase_g", contexte.texteAvant);
				args.put("phrase_m", contexte.mot);
				args.put("phrase_d", contexte.texteAprès);

				// Le voisinage plus complet = contexteSource
				args.put("voisinage_g", contexteSource.texteAvant);
				args.put("voisinage_m", contexteSource.mot);
				args.put("voisinage_d", contexteSource.texteAprès);
			} else {

				Contexte contextePhraseComplète = phraseService.getContextePhraseComplète(contexte);
				args.put("phrase_g", contextePhraseComplète.texteAvant);
				args.put("phrase_m", contextePhraseComplète.mot);
				args.put("phrase_d", contextePhraseComplète.texteAprès);

				args.put("voisinage_g", contexte.texteAvant);
				args.put("voisinage_m", contexte.mot);
				args.put("voisinage_d", contexte.texteAprès);
			}

			Executions.createComponents("/infoContexte.zul", tabpanel, args);

			tabpanel.setParent(corpusTabpanels);
		} else {
			System.out.println("Onglet info contexte déjà ouvert: " + contexte.getId());
			// Donner le focus
		}

		infoContexteTab.setSelected(true);

	}

	private void afficheOuMasqueBoutonFermerTousLesOnglets(int incrément) {

		// incrément: un onglet en cours de fermeture (traitement du ON_CLOSE) existe encore => il faut le décompter

		Component boutonFermerTousLesOnglets = corpusTabs.getFellow("boutonFermerTousLesOnglets");

		// Y a-t-il au moins un onglet fermable visible ouvert?
		@SuppressWarnings("unchecked")
		List<Component> children = corpusTabs.getChildren();

		int cptVisibleFermable = 0;

		for (Component comp : children) {
			Tab tab = (Tab) comp;
			if (tab.isClosable() && tab.isVisible()) {
				cptVisibleFermable++;
			}
		}

		// afficher ou masquer le bouton
		boutonFermerTousLesOnglets.setVisible(cptVisibleFermable + incrément > 0 ? true : false);

	}

	// private List<Metadata> traduitMétadonnées(List<Metadata> docMétadonnées)
	// {
	//
	//
	// // Chargement des définitions des métadonnées du corpus si ce n'est déjà
	// fait
	// if(métadonnéesTraductions.size() == 0)
	// {
	// List<DocMetadata> métadonnéesCorpus =
	// corpusMétadonnéesManager.findByCorpusId(corpusService.getCorpus().getId());
	// for(DocMetadata md : métadonnéesCorpus) {
	// métadonnéesTraductions.put(md.getChampIndex(), md);
	// }
	// }
	//
	//
	// // Traduction nom champ index => libellé écran
	// for(Metadata md : docMétadonnées) {
	// DocMetadata indexMetadata = métadonnéesTraductions.get(md.getName());
	// //if(indexMetadata != null)
	//
	// }
	//
	//
	//
	// // TODO Auto-generated method stub
	// return null;
	// }

	private Tab getTabDéjàOuvert(String id) {
		@SuppressWarnings("unchecked")
		List<Component> children = corpusTabs.getChildren();
		for (Component comp : children) {
			Tab tab = (Tab) comp;
			if (tab.getId().equals(id)) {
				return tab;
			}
		}
		return null;
	}

	@Override
	public void chercheEtAffiche(boolean ajouterHistorique) {

		Recherche recherche = getRecherche();
		
		grilleRésultatsPaging.setActivePage(PREMIÈRE_PAGE);
		
		contexteSetCourant = exécuterRecherche(recherche, ajouterHistorique, false);
		
		remplisGrilleContextes(contexteSetCourant, PREMIÈRE_PAGE, grilleRésultatsTaillePage);

		// Stockage de la recherche comme attribut du grid pour réutilisation par le composant Paging
		contextesGrid.setAttribute("recherche", recherche, Component.COMPONENT_SCOPE);

		infoRésultats.setValue(getInfoRésultat(contexteSetCourant));
		
		// réinitialiser la sélection
		résultatsSélectionnés.clear();

		if (cooccurrentLien != null) {
			if (contexteSetCourant.getContextes().size() > 0) {
				cooccurrentLien.setVisible(true);
			} else {
				cooccurrentLien.setVisible(false);
			}
		}

	}

	@Override
	public void initialiseRecherche() {
		condition.setSelectedIndex(0);
		voisinage.setSelectedIndex(0);
		cherche.setText("");

		infoRésultats.setValue("Aucun contexte trouvé");
		initialiseContexteGrid();
	}

	@Override
	public void initFiltreManager() {
		this.filtreManager = ServiceLocator.getContexteFiltreManager();

	}

	@Override
	protected void exporterRésultatsCsv() {
		StringBuilder csv = new StringBuilder();

		String séparateur = ";";

		csv.append("\"Contexte complet\";\"Texte avant mot\";\"Mot\";\"Texte après\"\n");

		// Récupération des données
		@SuppressWarnings("unchecked")
		List<Contexte> contextes = (List<Contexte>) contextesGrid.getModel();
		boolean exportationPartielle = false;
		if(résultatsSélectionnés.size() > 0) {
			exportationPartielle = true;
		}
		
		for (Contexte contexte : contextes) {
			if (!exportationPartielle || contexte.sélectionné) {
				Contexte contextePhraseComplète = getContexteInitial(contexte);
				csv.append(ajouteGuillemetsCsv(contextePhraseComplète.getPhrase().phrase)).append(séparateur);
				csv.append(ajouteGuillemetsCsv(contextePhraseComplète.texteAvant)).append(séparateur);
				csv.append(ajouteGuillemetsCsv(contextePhraseComplète.mot)).append(séparateur);
				csv.append(ajouteGuillemetsCsv(contextePhraseComplète.texteAprès)).append("\n");
			}

		}
		Filedownload.save(csv.toString().getBytes(), "text/csv, charset=UTF-8; encoding=UTF-8", getNomFichier() + ".csv");
	}

	@Override
	protected void exporterRésultatsXls() {
		Workbook wb = new HSSFWorkbook();

		// Création des styles, font, etc. pour les cellules de la feuille de
		// calcul
		Font entêteFont = wb.createFont();
		entêteFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle entêteCellStyle = wb.createCellStyle();
		entêteCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		entêteCellStyle.setFont(entêteFont);

		CellStyle ligneCellStyle = wb.createCellStyle();
		ligneCellStyle.setWrapText(true);

		// Récupération de la description de la dernière recherche
		RechercheExécution rechercheExécution = null;
		if (this.historiqueRecherche.size() > 0) {
			rechercheExécution = this.historiqueRecherche.get(0);
		} else {
			rechercheExécution = new RechercheExécution();
		}

		CreationHelper createHelper = wb.getCreationHelper();
		// Création d'une nouvelle feuille de calcul
		Sheet contextesSheet = wb.createSheet("contextes");

		// Récupération des données
		@SuppressWarnings("unchecked")
		List<Contexte> contextes = (List<Contexte>) contextesGrid.getModel();
		
		Recherche recherche = (Recherche) contextesGrid.getAttribute("recherche", Component.COMPONENT_SCOPE);
		if (recherche != null) {
			ContexteSet contexteSet = exécuterRecherche(recherche, false, true);
			contextes = contexteSet.getContextes();
		}	

		// Est-ce une exportation partielle
		boolean exportationPartielle = false;
		if(résultatsSélectionnés.size() > 0) {
			exportationPartielle = true;
		}

		insérerTitreRecherche(wb, createHelper, rechercheExécution, contextesSheet, exportationPartielle);

		// Création de la ligne d'entête
		int colCpt = 0;
		int rowCpt = 3;

		org.apache.poi.ss.usermodel.Row row = contextesSheet.createRow(rowCpt++);
		org.apache.poi.ss.usermodel.Cell entêteCell = row.createCell(colCpt++);
		entêteCell.setCellStyle(entêteCellStyle);
		entêteCell.setCellValue(createHelper.createRichTextString("Contexte"));

		// Créer les colonnes pour les métadonnées primaires
		List<DocMetadata> metadatas = docMetadataRepo.findByCorpusAndPrimaire(corpusService.getCorpus(), true, new Sort("ordre"));
		for (DocMetadata docMetadata : metadatas) {
			entêteCell = row.createCell(colCpt++);
			entêteCell.setCellStyle(entêteCellStyle);
			entêteCell.setCellValue(createHelper.createRichTextString(docMetadata.getNom()));
		}

		//
		// entêteCell = row.createCell(colCpt++);
		// entêteCell.setCellStyle(entêteCellStyle);
		// entêteCell.setCellValue(createHelper.createRichTextString("Mot"));
		//
		// entêteCell = row.createCell(colCpt++);
		// entêteCell.setCellStyle(entêteCellStyle);
		// entêteCell.setCellValue(createHelper.createRichTextString("Texte après"));

		Integer contexteCpt = 0;
		for (Contexte contexte : contextes) {

			int dataColCpt = 0;

			if (!exportationPartielle || résultatsSélectionnés.contains(contexteCpt)) {
				Contexte contextePhraseComplète = getContexteInitial(contexte);

				row = contextesSheet.createRow(rowCpt++);

				// FIXME ajuster hauteur de la ligne en fonction du nombre de ligne de texte?
				// cf. http://apache-poi.1045710.n5.nabble.com/Autosize-row-for-HSSF-library-td2308264.html

				org.apache.poi.ss.usermodel.Cell cell = row.createCell(dataColCpt++);
				cell.setCellStyle(ligneCellStyle);
				cell.setCellValue(createHelper.createRichTextString(contextePhraseComplète.getPhrase().phrase));

				// afficher les métadonnées primaires
				for (Metadata metadata : contexte.getDocMétadonnéesPrimaires()) {
					cell = row.createCell(dataColCpt++);
					cell.setCellStyle(ligneCellStyle);
					cell.setCellValue(createHelper.createRichTextString(metadata.getSimpleString()));
				}
				//
				// cell = row.createCell(2);
				// cell.setCellStyle(ligneCellStyle);
				// cell.setCellValue(createHelper.createRichTextString(contextePhraseComplète.mot));
				//
				// cell = row.createCell(3);
				// cell.setCellStyle(ligneCellStyle);
				// cell.setCellValue(createHelper.createRichTextString(contextePhraseComplète.texteAprès));
			}
			contexteCpt++;
		}

		for (int i = 0; i <= colCpt; i++) {
			contextesSheet.autoSizeColumn(i);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			Filedownload.save(baos.toByteArray(), "application/vnd.ms-excel", getNomFichier() + ".xls");
			baos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void insérerTitreRecherche(Workbook wb, CreationHelper createHelper, RechercheExécution rechercheExécution, Sheet motsSheet,
			boolean exportationPartielle) {
		// Titre de la recherche
		Font titreFont = wb.createFont();
		titreFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle titreCellStyle = wb.createCellStyle();
		// titreCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		titreCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		titreCellStyle.setFont(titreFont);
		titreCellStyle.setWrapText(true);

		Row titreRow = motsSheet.createRow(0);
		Cell titreCell = titreRow.createCell(0);

		titreRow.setHeightInPoints(motsSheet.getDefaultRowHeightInPoints() * 15);

		titreCell.setCellStyle(titreCellStyle);

		String descriptionTextuelle = "";

		String exportationPartielleTexte = "";
		if (exportationPartielle) {
			exportationPartielleTexte = "Une sélection de contextes parmi ";
		}

		if (rechercheExécution.recherche == null) {
			descriptionTextuelle = exportationPartielleTexte + "tous les contextes";
		} else {
			descriptionTextuelle = exportationPartielleTexte + rechercheExécution.recherche.getDescriptionTextuelle(!exportationPartielle);
		}

		titreCell.setCellValue(createHelper.createRichTextString(descriptionTextuelle));
		motsSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
	}

	// TODO générer un nom de fichier qui représente mieux la recherche
	private String getNomFichier() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		return sdf.format(new Date()) + "-contextes";
	}

	@Override
	protected Grid getHistoriqueRecherchesGrid() {
		return (Grid) Path
				.getComponent("//webCorpusPage/webCorpusWindow/contexteInclude/contexteWindow/historiqueRechercheInclude/historiqueRecherchesGrid");
	}

	@Override
	public void chargerRecherche(Recherche recherche) {
		RechercheContexte r = (RechercheContexte) recherche;

		// Cible : rien à faire - toujours CONTEXTE

		// Précision chaîne
		RechercheContexte.PrécisionChaîne précision = RechercheContexte.PrécisionChaîne.valueOf(r.précisionChaîne);

		switch (précision) {
		case EXACTEMENT_LE_MOT:
			condition.setSelectedIndex(0);
			break;
		case TOUTES_LES_FORMES_DU_MOT:
			condition.setSelectedIndex(1);
			break;
		}

		// Chaîne
		cherche.setText(r.chaîne);

		// Filtres
		remplacerFiltres(r.getFiltres());

	}

}
