package com.servicelibre.corpus.service;

public class Phrase {

	public String phrase = "";
	public boolean complète;
	
	public Phrase() {
		super();
	}

	public Phrase(String phrase) {
		super();
		this.phrase = phrase;
	}

	public String nettoyée() {
		
		String phraseNettoyée = phrase.replaceFirst("—", "").replaceFirst("–", "");
		
		boolean guillemetOuvrant = phraseNettoyée.indexOf("«") >=0;
		boolean guillemetFermant = phraseNettoyée.indexOf("»") >=0;
		
		if(guillemetOuvrant && !guillemetFermant){
			phraseNettoyée = phraseNettoyée.replaceFirst("«","");
		}
		else if (guillemetFermant && !guillemetOuvrant){
			phraseNettoyée = phraseNettoyée.replaceFirst("»","");;
		}
		
		return phraseNettoyée.trim();
		
	}
	
	public Phrase nettoie(){
		this.phrase = nettoyée();
		return this;
	}
	
	
}
