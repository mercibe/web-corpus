package com.servicelibre.corpus.prononciation;

import java.io.File;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PrononciationImportMain {

    private static ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("prononciationImport-config.xml", "system-context.xml");

    public static void main(String[] args) {

	PrononciationImport pi = (PrononciationImport) ctx.getBean("prononciationImport");

	File fichierSource = (File) ctx.getBean("prononciationFichierSource");

	pi.execute(fichierSource);

    }
}
