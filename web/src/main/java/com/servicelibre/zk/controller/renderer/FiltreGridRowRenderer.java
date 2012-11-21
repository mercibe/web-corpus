package com.servicelibre.zk.controller.renderer;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Html;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Group;
import org.zkoss.zul.SimpleGroupsModel;

import com.servicelibre.zk.controller.CorpusCtrl;

public class FiltreGridRowRenderer implements RowRenderer {

		protected boolean boutonSupprimer = true;
		
		public CorpusCtrl corpusCtrl;
		
		public FiltreGridRowRenderer(CorpusCtrl corpusCtrl) {
			super();
			this.corpusCtrl = corpusCtrl;
		}

		@Override
		public void render(Row row, Object model, int index) throws Exception {
			DefaultKeyValue cv = (DefaultKeyValue) model;

			final Row currentRow = row;

			Cell cell = new Cell();
			cell.setParent(row);

			//final Label label = new Label(cv.getValue().toString());
			final Html htmlLabel = new Html(cv.getValue().toString());
			htmlLabel.setClass("z-label");
			htmlLabel.setAttribute("key", cv.getKey());
			htmlLabel.setParent(cell);

			// Ajouter bouton « supprimer » si pas un groupe
			if (!(row instanceof Group)) {

				if (boutonSupprimer) {
					final Button supprimeBtn = new Button();
					supprimeBtn.setMold("os");
					supprimeBtn.setParent(row);
					supprimeBtn.setImage("/images/enlever-10x10.png");
					//FIXME en passant par un popup/tooltip?
					//supprimeBtn.setTooltiptext("retirer « " + htmlLabel.getContent() + " » des filtres actifs");					
					supprimeBtn.setTooltiptext("retirer des filtres actifs");
					supprimeBtn.addEventListener(Events.ON_CLICK, new EventListener() {

						@Override
						public void onEvent(Event arg0) throws Exception {

							if (currentRow != null) {

								Cell cell = (Cell) currentRow.getFirstChild();

								Html htmlLabelValeur = (Html) cell.getFirstChild();
								Html htmlLabelGroupe = (Html) currentRow.getGroup().getFirstChild().getFirstChild();

								corpusCtrl.filtreActifModel.removeFiltre(htmlLabelGroupe.getAttribute("key").toString(), htmlLabelValeur.getAttribute("key"));

								corpusCtrl.gridFiltreActif.setModel(new SimpleGroupsModel(corpusCtrl.filtreActifModel.getFiltreValeurs(), corpusCtrl.filtreActifModel.getFiltreGroupes()));

								corpusCtrl.filtreManager.setFiltreActif(corpusCtrl.filtreActifModel);

								corpusCtrl.actualiseValeursFiltreCourant();

								corpusCtrl.chercheEtAffiche(true);
							}
						}
					});
				} else {
					cell.setColspan(2);
				}
			} else {
				// Ligne du groupe (nom de la « catégorie de liste »)
				cell.setColspan(2);
				Html htmlGroup = (Html) cell.getFirstChild();
				htmlGroup.setSclass("groupe z-label");
			}

		}

		public void setBoutonSupprimer(boolean boutonSupprimer) {
			this.boutonSupprimer = boutonSupprimer;
		}

	}