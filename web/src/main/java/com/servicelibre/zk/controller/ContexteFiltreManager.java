package com.servicelibre.zk.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
		
		List<DocMetadata> metadatas = docMetadataManager.findByCorpusId(corpusService.getCorpus().getId());

		// Ajout d'un filtre pour chaque champ d'index
		filtres.clear();
		for(DocMetadata meta : metadatas) {

			String champIndex = meta.getChampIndex();

			filtres.add(new Filtre(champIndex, meta.getNom(), getChampValeurs(champIndex)));
		}
	
	}

	private Set<DefaultKeyValue> getChampValeurs(String champIndex) {
		
		
		List<DefaultKeyValue> valeursChamp = corpusService.getValeursChamp(champIndex);
		Set<DefaultKeyValue> clésValeurs = new TreeSet<DefaultKeyValue>(new Comparator<DefaultKeyValue>() {

			@Override
			public int compare(DefaultKeyValue arg0, DefaultKeyValue arg1) {
				return arg0.getKey().toString().compareTo(arg1.getKey().toString());
			}
		});
		
		for(DefaultKeyValue cléValeur : valeursChamp) {
			StringBuilder sb = new StringBuilder(cléValeur.getKey().toString());
			
			sb.append(" (").append(cléValeur.getValue()).append(")");
			
			clésValeurs.add(new DefaultKeyValue(cléValeur.getKey(), sb.toString()));
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
