package com.servicelibre.corpus.analysis;

import java.util.Comparator;

public class MotInfoDéfautExportateur implements MotInfoExportateur {

	String séparateurDeChamps = "|";
	private boolean doublonsAutorisés = true;
	private Comparator<MotInfo> motInfoComparator = new MotInfoFrCaComparator<MotInfo>();
	private Comparator<String> motComparator = null;// new CanadaFrenchStringComparator();

	@Override
	public String getFormatCourt(MotInfo motInfo) {
		return getFormatLong(motInfo);
	}

	@Override
	public String getFormatLong(MotInfo motInfo) {
		return getFormatStructuré(motInfo, "|");
	}

	@Override
	public String getFormatStructuré(MotInfo motInfo, String séparateur) {
		StringBuilder sb = new StringBuilder();

		sb.append(motInfo.mot).append(séparateur).append(motInfo.lemme).append(séparateur);

		// Si catgram NULL, problème à signaler
		if (motInfo.catgram == null) {
			System.err.println("catgram.id == NULL pour " + motInfo.mot + séparateur + motInfo.lemme + séparateur + motInfo.note);
			sb.append("NULL");
		} else {

			sb.append(motInfo.catgram.id);
		}

		sb.append(séparateur).append(motInfo.freqMot).append(séparateur).append(motInfo.freqLemme).append(séparateur)
				.append(motInfo.isLemme).append(séparateur).append(motInfo.note).append(séparateur).append(motInfo.prononciation)
				.append(séparateur).append(motInfo.genre).append(séparateur).append(motInfo.nombre).append(séparateur)
				.append(motInfo.personne);

		return sb.toString();
	}

	@Override
	public boolean isExportable(MotInfo motInfo) {
		return true;
	}

	@Override
	public boolean isOffensant(MotInfo motInfo) {
		return false;
	}

	@Override
	public boolean isDoublonsAutorisés() {
		return this.doublonsAutorisés;
	}

	@Override
	public void setDoublonsAutorisés(boolean doublonsAutorisés) {
		this.doublonsAutorisés = doublonsAutorisés;

	}

	@Override
	public Comparator<String> getStringComparator() {
		return this.motComparator;
	}

	@Override
	public void setStringComparator(Comparator<String> stringComparator) {
		this.motComparator = stringComparator;

	}

	@Override
	public Comparator<MotInfo> getMotInfoComparator() {
		return this.motInfoComparator;
	}

	@Override
	public void setMotInfoComparator(Comparator<MotInfo> motInfoComparator) {
		this.motInfoComparator = motInfoComparator;

	}

	@Override
	public String getSéparateurDeChamps() {
		return this.séparateurDeChamps;
	}

	@Override
	public void setSéparateurDeChamps(String séparateurDeChamps) {
		this.séparateurDeChamps = séparateurDeChamps;

	}

}
