package com.servicelibre.zk.controller.renderer;

import org.zkoss.spring.security.SecurityUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Span;

import com.servicelibre.corpus.service.Contexte;
import com.servicelibre.zk.controller.ContexteCtrl;

public class ContexteRowRenderer implements RowRenderer<Object> {

	// private static Logger logger =
	// LoggerFactory.getLogger(ContexteRowRenderer.class);

	protected final ContexteCtrl contexteCtrl;
	protected final Paging grilleRésultatsPaging;

	private boolean rôleAdmin;

	public ContexteRowRenderer(ContexteCtrl contexteCtrl) {
		super();
		this.contexteCtrl = contexteCtrl;
		this.rôleAdmin = SecurityUtil.isAnyGranted("ROLE_ADMINISTRATEUR");
		this.grilleRésultatsPaging = contexteCtrl.getGrilleRésultatsPaging();
	}

	@Override
	public void render(Row row, Object model, final int index) throws Exception {

		final Contexte contexte = (Contexte) model;
		final Contexte contexteInitial = contexteCtrl.getContexteInitial(contexte);

		final Checkbox cb = new Checkbox();
		cb.setValue(contexteInitial);

		Integer sélectionIdx = (grilleRésultatsPaging.getActivePage() * grilleRésultatsPaging.getPageSize()) + index;
		cb.setChecked(contexteCtrl.getRésultatsSélectionnés().contains(sélectionIdx));

		cb.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				Integer sélectionIdx = (grilleRésultatsPaging.getActivePage() * grilleRésultatsPaging.getPageSize()) + index;
				if (cb.isChecked()) {
					contexteCtrl.getRésultatsSélectionnés().add(sélectionIdx);
				} else {
					contexteCtrl.getRésultatsSélectionnés().remove(sélectionIdx);
				}
			}
		});

		row.appendChild(cb);

		Span ctxSpan = new Span();
		ctxSpan.appendChild(new Label(contexteInitial.texteAvant));

		Label mot = new Label(contexteInitial.mot);
		mot.setTooltiptext(contexteInitial.getId());
		mot.setSclass("mot");

		mot.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				// Label l = (Label) arg0.getTarget();
				contexteCtrl.créeEtAfficheOngletInfoContexte(contexteInitial);
			}

		});

		// mot.setHeight("20px");
		ctxSpan.appendChild(mot);

		ctxSpan.appendChild(new Label(contexteInitial.texteAprès));

		row.appendChild(ctxSpan);

		// row.appendChild(new Label(contexte.texteAvant + contexte.mot
		// + contexte.texteAprès));

	}

}
