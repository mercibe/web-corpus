package com.servicelibre.corpus.service;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.metadata.Metadata;

public class Contexte {

	// TODO ajouter metadonnées du document source (pour affichage/traitement
	// ultérieur éventuel)
	// Toutes les métas ou seulement une seulement une liste finie passée en
	// argument lors de la construction? (Map<String, String>)

	public String texteAvant;
	public String mot;
	public String texteAprès;
	private List<Metadata> docMétadonnées = new ArrayList<Metadata>();

	public Contexte(String texteAvant, String mot, String texteAprès, List<Metadata> docMétadonnées) {
		super();
		this.texteAvant = texteAvant;
		this.mot = mot;
		this.texteAprès = texteAprès;
		this.docMétadonnées = docMétadonnées;
	}

	public Contexte(String texteAvant, String mot, String texteAprès) {
		super();
		this.texteAvant = texteAvant;
		this.mot = mot;
		this.texteAprès = texteAprès;
	}

	@Override
	public String toString() {
		return texteAvant + "===>" + mot + "<===" + texteAprès;
	}

	public Phrase getPhrase() {
		return new Phrase(new StringBuilder(texteAvant).append(mot)
				.append(texteAprès).toString().trim());
	}

	public void setDocMétadonnées(List<Metadata> docMétadonnées) {
		this.docMétadonnées = docMétadonnées;

	}

	public List<Metadata> getDocMétadonnées() {
		return docMétadonnées;
	}

}
