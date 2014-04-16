package com.servicelibre.corpus.analysis;

import java.util.Comparator;

/**
 * Défini si un MotInfo doit être exporter ou non et sous quelle forme
 * 
 * @author benoitm
 * 
 */
public interface MotInfoExportateur {
	
	String getFormatCourt(MotInfo motInfo);

	String getFormatLong(MotInfo motInfo);

	String getFormatStructuré(MotInfo motInfo, String séparateur);

	boolean isExportable(MotInfo motInfo);
	
	boolean isOffensant(MotInfo motInfo);

	boolean isDoublonsAutorisés();
	
	void setDoublonsAutorisés(boolean doublonsAutorisés);

	Comparator<String> getStringComparator();

	void setStringComparator(Comparator<String> stringComparator);

	Comparator<MotInfo> getMotInfoComparator();
	
	void setMotInfoComparator(Comparator<MotInfo> motInfoComparator);

	String getSéparateurDeChamps();
	void setSéparateurDeChamps(String séparateurDeChamps);
}
