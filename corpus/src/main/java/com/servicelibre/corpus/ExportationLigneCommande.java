package com.servicelibre.corpus;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class ExportationLigneCommande {

	@Parameter
	public List<String> parameters = new ArrayList<String>();

	@Parameter(names = { "-corpus", "-c" }, description = "nom du corpus à exporter")
	public String nomDuCorpus;
}