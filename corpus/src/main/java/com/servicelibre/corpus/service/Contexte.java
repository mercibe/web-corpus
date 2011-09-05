package com.servicelibre.corpus.service;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.metadata.Metadata;

public class Contexte {

	public String texteAvant;
	public String mot;
	public String texteAprès;
	private List<Metadata> docMétadonnées = new ArrayList<Metadata>();
	public String id;
	private Contexte contexteSource;

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

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setContexteSource(Contexte contexteSource) {
		this.contexteSource = contexteSource;
	}

	public Contexte getContexteSource() {
		return contexteSource;
	}

}
