package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.entities.corpus.DocMetadata;
import com.servicelibre.entities.corpus.Rôle;
import com.servicelibre.repositories.corpus.DocMetadataRepository;

public class ContexteFiltreManager extends FiltreManager {

	private DocMetadataRepository docMetadataRepo;
	private CorpusService corpusService;

	@Override
	public void init() {

		List<DocMetadata> metadatas = docMetadataRepo.findByCorpus(corpusService.getCorpus());

		// Ajout d'un filtre pour chaque champ d'index
		filtres.clear();
		for (DocMetadata meta : metadatas) {

			if (meta.isFiltre()) {

				String champIndex = meta.getChampIndex();

				// Récupération du rôle éventuel pour lequel se filtre/champ doit être limité (visible exclusivement)
				Rôle rôle = meta.getRôle();
				String nomRôle = "";
				if(rôle != null) {
					nomRôle = rôle.getNom();
				}
				
				filtres.add(new Filtre(champIndex, meta.getNom(), getChampValeurs(champIndex, meta),nomRôle, meta.getRemarqueValeurFiltre()));
			}
		}

	}

	private List<DefaultKeyValue> getChampValeurs(String champIndex, DocMetadata meta) {

		List<DefaultKeyValue> valeursChamp = corpusService.getValeursChampAvecFréquence(champIndex);
		List<DefaultKeyValue> clésValeurs = new ArrayList<DefaultKeyValue>(valeursChamp.size());
		
		Pattern p = Pattern.compile("^(.*)\\|");
		
		boolean admin = isRôleAdmin();

		for (DefaultKeyValue cléValeur : valeursChamp) {

			
			StringBuilder keySb = new StringBuilder(cléValeur.getKey().toString());

			Matcher matcher = p.matcher(keySb);
			if(matcher.find()) {
				String codeIdEtTri = matcher.group(1);
				
				// Toutes les valeurs doivent-elles être affichées (adminSeulement)?
				if(!admin) {
					String valeursCachéesString = meta.getValeursCachées();
					if(valeursCachéesString != null && !valeursCachéesString.isEmpty()) {
						List<String> valeursCachées =  Arrays.asList(valeursCachéesString.split(","));
						// si code fait partie des restriction
						if(valeursCachées.contains(codeIdEtTri)) {
							continue;
						}
					}
				}
				
			}
			
			// Ajout du nombre de valeurs possibles entre parenthèse
			keySb.append(" (").append(cléValeur.getValue()).append(")");
			
			
			// y a-t-il un tri personnalisé à supprimer (tous les caractères avant le premier | ) ?
			// Exemple : 001|fragment HTML
			String valeur = keySb.toString().replaceAll("^(.*\\|)", "");
			
			

			clésValeurs.add(new DefaultKeyValue(cléValeur.getKey(), valeur));
		}

		return clésValeurs;
	}

	public DocMetadataRepository getDocMetadataRepository() {
		return docMetadataRepo;
	}

	public void setDocMetadataRepository(DocMetadataRepository docMetadataRepo) {
		this.docMetadataRepo = docMetadataRepo;
	}

	public CorpusService getCorpusService() {
		return corpusService;
	}

	public void setCorpusService(CorpusService corpusService) {
		this.corpusService = corpusService;
	}

}
