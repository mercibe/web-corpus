package com.servicelibre.corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class ExportationLigneCommande {

	@Parameter
	public List<String> parameters = new ArrayList<String>();

	@Parameter(names = "-c", description = "nom du corpus à exporter")
	public String nomDuCorpus;

	@Parameter(names = "-co", description = "nom du fichier dans lequel sera exporté le corpus", converter = FileConverter.class)
	File corpusFichier;

	@Parameter(names = "-m", description = "Exportation des mots")
	public boolean exportationDesMots;

	@Parameter(names = "-mo", description = "nom du fichier dans lequel seront exportés les mots", converter = FileConverter.class)
	File motsFichier;

	@Parameter(names = "-p", description = "Exportation des prononciations")
	public boolean exportationDesPrononciations;

	@Parameter(names = "-po", description = "nom du fichier dans lequel seront exportées les prononciations", converter = FileConverter.class)
	File prononciationsFichier;

}