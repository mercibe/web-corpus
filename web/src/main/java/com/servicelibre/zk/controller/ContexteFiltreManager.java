package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.entity.DocMetadata;
import com.servicelibre.corpus.manager.DocMetadataManager;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.service.CorpusService;

public class ContexteFiltreManager extends FiltreManager {

	private DocMetadataManager docMetadataManager;
	private CorpusService corpusService;

	@Override
	public void init() {

		List<DocMetadata> metadatas = docMetadataManager
				.findByCorpusId(corpusService.getCorpus().getId());

		// Ajout d'un filtre pour chaque champ d'index
		filtres.clear();
		for (DocMetadata meta : metadatas) {

			if (meta.isFiltre()) {

				String champIndex = meta.getChampIndex();

				filtres.add(new Filtre(champIndex, meta.getNom(),
						getChampValeurs(champIndex)));
			}
		}

	}

	private List<DefaultKeyValue> getChampValeurs(String champIndex) {

		List<DefaultKeyValue> valeursChamp = corpusService.getValeursChamp(champIndex);
		List<DefaultKeyValue> clésValeurs = new ArrayList<DefaultKeyValue>(
				valeursChamp.size());

		clésValeurs.add(keyValueVide);

		for (DefaultKeyValue cléValeur : valeursChamp) {
			StringBuilder sb = new StringBuilder(cléValeur.getKey().toString());

			sb.append(" (").append(cléValeur.getValue()).append(")");

			clésValeurs.add(new DefaultKeyValue(cléValeur.getKey(), sb
					.toString()));
		}

		return clésValeurs;
	}

	public DocMetadataManager getDocMetadataManager() {
		return docMetadataManager;
	}

	public void setDocMetadataManager(DocMetadataManager docMetadataManager) {
		this.docMetadataManager = docMetadataManager;
	}

	public CorpusService getCorpusService() {
		return corpusService;
	}

	public void setCorpusService(CorpusService corpusService) {
		this.corpusService = corpusService;
	}

}
