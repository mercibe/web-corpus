package com.servicelibre.controller;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.liste.Mot;

class Row {
		long id;
		List<String> cell;
		
		public Row(Mot mot) {
			this.id = mot.getId();
			 List<String> cell = new ArrayList<String>(5);
			 
			 cell.add(mot.lemme);
			 cell.add(mot.getMot());
			 cell.add(mot.getCatgram());
			 
		}
		
		public Row(long id, List<String> cell) {
			super();
			this.id = id;
			this.cell = cell;
		}
		
		
	}