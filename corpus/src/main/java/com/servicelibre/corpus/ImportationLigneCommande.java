package com.servicelibre.corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.servicelibre.corpus.Importation.Mode;

public class ImportationLigneCommande {

	@Parameter
	public List<String> parameters = new ArrayList<String>();

	@Parameter(names = "-c", description = "nom du corpus à importer")
	public String nomDuCorpus;

	@Parameter(names = "-ci", description = "nom du fichier qui contient le corpus à importer", converter = FileConverter.class)
	File corpusFichier;

	@Parameter(names = "-mi", description = "nom du fichier qui contient les mots à importer", converter = FileConverter.class)
	File motsFichier;

	@Parameter(names = "-mode", description = "mode d'importation: MAJ ou REMPLACE_TOUT")
	Mode modeImportation;

	@Parameter(names = "-pi", description = "nom du fichier qui contient les prononciations à importer", converter = FileConverter.class)
	File prononciationsFichier;

}