package com.servicelibre.zk.controller.renderer;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Span;

import com.servicelibre.corpus.service.Contexte;
import com.servicelibre.zk.controller.ContexteCtrl;

public class ContexteRowRenderer implements RowRenderer {

	// private static Logger logger = LoggerFactory.getLogger(ContexteRowRenderer.class);

	protected ContexteCtrl contexteCtrl;

	public ContexteRowRenderer(ContexteCtrl contexteCtrl) {
		super();
		this.contexteCtrl = contexteCtrl;
	}

	@Override
	public void render(Row row, Object model , int index) throws Exception {

		final Contexte contexte = contexteCtrl.getContexteInitial((Contexte) model);

		Span ctxSpan = new Span();
		ctxSpan.appendChild(new Label(contexte.texteAvant));

		Label mot = new Label(contexte.mot);
		mot.setTooltiptext(contexte.getId());
		mot.setSclass("mot");

		mot.addEventListener(Events.ON_CLICK, new EventListener() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				// Label l = (Label) arg0.getTarget();
				contexteCtrl.créeEtAfficheOngletInfoContexte(contexte);
			}

		});

		// mot.setHeight("20px");
		ctxSpan.appendChild(mot);

		ctxSpan.appendChild(new Label(contexte.texteAprès));

		row.appendChild(ctxSpan);

		// row.appendChild(new Label(contexte.texteAvant + contexte.mot
		// + contexte.texteAprès));

	}

}
