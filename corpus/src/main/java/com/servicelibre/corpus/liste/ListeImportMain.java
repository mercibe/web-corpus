package com.servicelibre.corpus.liste;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.servicelibre.entities.corpus.Liste;

public class ListeImportMain {
	
	private static ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("listeImport-config.xml", "system-context.xml");

	public static void main(String[] args) {

		ListeImport li = (ListeImport) ctx.getBean("listeImport");

		li.setApplicationContext(ctx);
		
		for (Liste liste : li.getListes()) {
			li.execute(liste);
		}
	}
}
