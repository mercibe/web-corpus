package com.servicelibre.zk.controller.renderer;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleGroupsModel;

import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.zk.controller.CorpusCtrl;
import com.servicelibre.zk.recherche.Recherche;
import com.servicelibre.zk.recherche.RechercheExécution;

public class HistoriqueRowRenderer implements RowRenderer {

	// private static Logger logger = LoggerFactory.getLogger(HistoriqueRowRenderer.class);

	public CorpusCtrl corpusCtrl;

	public HistoriqueRowRenderer(CorpusCtrl corpusCtrl) {
		super();
		this.corpusCtrl = corpusCtrl;
	}

	@Override
	public void render(Row row, Object model) throws Exception {

		final RechercheExécution rx = (RechercheExécution) model;

		// Sauvegarde de l'objet recherche dans les attribut de la ligne
		row.setAttribute("recherche", rx.recherche);

		final Row currentRow = row;

		// Ajout de la cellule avec la chaîne+précision
		Cell cell = new Cell();
		cell.setParent(row);

		String chaîneEtPrécision = rx.recherche.getChaîneEtPrécision();
		final Label labelDescription = new Label(chaîneEtPrécision);
		labelDescription.setParent(cell);

		// Info filtre
		cell = new Cell();
		cell.setParent(row);

		int nombreConditions = rx.recherche.getNombreConditions();
		if (nombreConditions > 0) {
			final Label filtreDescription = new Label(nombreConditions + " filtre" + (nombreConditions > 1 ? "s" : ""));
			filtreDescription.setParent(cell);
		}

		// Ajout d'un bouton Information
		final Button informationBtn = new Button();
		informationBtn.setParent(row);
		informationBtn.setMold("os");
		informationBtn.setImage("/images/information-16x16.png");
		informationBtn.setTooltiptext("Cliquer pour avoir plus d'information sur la recherche");
		informationBtn.addEventListener(Events.ON_CLICK, new EventListener() {

			@Override
			public void onEvent(Event arg0) throws Exception {

				corpusCtrl.popupHistorique.open(corpusCtrl.popupHistorique);

				corpusCtrl.descriptionRechercheHistorique.setValue("Recherche " + rx.recherche.getDescriptionChaîne()
						+ (rx.recherche.isFiltrée() ? " " + rx.recherche.getDescriptionPortéeFiltre() : ""));

				FiltreRecherche filtres = rx.recherche.getFiltres();
				corpusCtrl.gridFiltreHistorique.setModel(new SimpleGroupsModel(filtres.getFiltreValeurs(), filtres.getFiltreGroupes()));

			}
		});

		FiltreGridRowRenderer fgrr = new FiltreGridRowRenderer(corpusCtrl);
		fgrr.setBoutonSupprimer(false);
		corpusCtrl.gridFiltreHistorique.setRowRenderer(fgrr);

		// Ajout du bouton Exécuter
		final Button exécuterBtn = new Button();
		exécuterBtn.setParent(row);
		exécuterBtn.setMold("os");
		exécuterBtn.setImage("/images/exécuter-16x16.png");
		exécuterBtn.setTooltiptext("exécuter la recherche");
		exécuterBtn.addEventListener(Events.ON_CLICK, new EventListener() {

			@Override
			public void onEvent(Event arg0) throws Exception {

				System.err.println("Exécution de la requête");
				corpusCtrl.chargerRecherche((Recherche) currentRow.getAttribute("recherche"));
				corpusCtrl.chercheEtAffiche(false);

			}
		});

		// long tempsÉcoulé = System.currentTimeMillis() -
		// rx.dateExécution.getTime();
		//
		// String ilya = String.format("%d min, %d sec",
		// TimeUnit.MILLISECONDS.toMinutes(tempsÉcoulé),
		// TimeUnit.MILLISECONDS.toSeconds(tempsÉcoulé) -
		// TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tempsÉcoulé))
		// );

		// final Label labelDate = new
		// Label(df_historique.format(rx.dateExécution));
		// labelDate.setAttribute("recherche", rx.recherche);
		// labelDate.setParent(cell);
		//
		// cell = new Cell();
		// cell.setParent(row);
		// final Label labelNb = new Label(rx.nbRésultats + "");
		// labelNb.setParent(cell);

	}

}
