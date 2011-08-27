package com.servicelibre.corpus.service;

import java.util.List;

public interface PhraseService {

	Phrase getPhraseComplète(Contexte c);
	Contexte getContextePhraseComplète(Contexte c);
	List<Phrase> getPhrasesComplètes(Contexte c);
	List<Phrase> getPhrasesComplètes(String texte);
}