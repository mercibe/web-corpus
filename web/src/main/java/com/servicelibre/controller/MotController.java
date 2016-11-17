package com.servicelibre.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.MotRepository;


@Controller
public class MotController {

	@Autowired
	MotRepository motRepo;

	@RequestMapping(value = "/mots", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	List<String> getMots() {

		List<String> motsString = new ArrayList<String>(25000);
		
		List<Mot> mots = motRepo.findAll();

		System.err.println("Trouvé " + mots.size() + " mots.");

		for (Mot mot : mots) {
			motsString.add(mot.lemme);
		}
		
		return motsString;
	}
	
	@RequestMapping(value = "/mots/{lemme}/{catgram}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	Map<String, String> getMots(@PathVariable("lemme") String lemme, @PathVariable("catgram") String catgram, @RequestParam(value="genre",required=false, defaultValue="") String genre) throws RessourceIntrouvableException {

		
		
		System.out.println("Recherche " + lemme + " | " + catgram + " | " + genre);
		
		Mot mot = motRepo.findByLemmeAndMotAndCatgramAndGenre(lemme.toLowerCase().trim(), 
				lemme.toLowerCase().trim(), 
				catgram.toLowerCase().trim(), genre); //motRepo.findAll();

		if(mot == null) {
			throw new RessourceIntrouvableException();
		}
		
		Map<String, String> motInfos = new HashMap<String, String>();
		
		motInfos.put("lemme", mot.getLemme());
		motInfos.put("mot", mot.getMot());
		motInfos.put("catgram", mot.getCatgram());
		motInfos.put("genre", mot.getGenre());
		motInfos.put("nombre", mot.getNombre());
		motInfos.put("listePrimaire", mot.getListePartitionPrimaire().getNom());
		
		
		return motInfos;
	}
	
	@RequestMapping(value = "/entrées/{entrée}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	List<Map<String, String>> getMots(@PathVariable("entrée") String entrée) throws RessourceIntrouvableException {

		System.out.println("Recherche " + entrée );
		
		List<Mot> mots = motRepo.findByMot(entrée.toLowerCase().trim());

		if(mots.size() == 0) {
			throw new RessourceIntrouvableException();
		}
		
		List<Map<String, String>> résultats = new ArrayList<Map<String,String>>();
		
		for (Mot mot : mots) {
			
			 Map<String, String> motInfos = new HashMap<String, String>();
			
			 motInfos.put("lemme", mot.getLemme());
			 motInfos.put("mot", mot.getMot());
			 motInfos.put("catgram", mot.getCatgram());
			 motInfos.put("genre", mot.getGenre());
			 motInfos.put("nombre", mot.getNombre());
			 motInfos.put("listePrimaire", mot.getListePartitionPrimaire().getNom());
			 
			 résultats.add(motInfos);
			 
		}
		
		return résultats;
	}
	
	
}
