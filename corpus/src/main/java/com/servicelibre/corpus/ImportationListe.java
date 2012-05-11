/**
 * Librairie de code pour la création d'index textuels
 *
 * Copyright (C) 2009 Benoit Mercier <benoit.mercier@servicelibre.com> — Tous droits réservés.
 *
 * Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le
 * modifier suivant les termes de la “GNU General Public License” telle que
 * publiée par la Free Software Foundation : soit la version 3 de cette
 * licence, soit (à votre gré) toute version ultérieure.
 *
 * Ce programme est distribué dans l’espoir qu’il vous sera utile, mais SANS
 * AUCUNE GARANTIE : sans même la garantie implicite de COMMERCIALISABILITÉ
 * ni d’ADÉQUATION À UN OBJECTIF PARTICULIER. Consultez la Licence Générale
 * Publique GNU pour plus de détails.
 *
 * Vous devriez avoir reçu une copie de la Licence Générale Publique GNU avec
 * ce programme ; si ce n’est pas le cas, consultez :
 * <http://www.gnu.org/licenses/>.
 */

package com.servicelibre.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.liste.LigneSplitter;
import com.servicelibre.corpus.repository.CorpusRepository;
import com.servicelibre.corpus.repository.ListeRepository;

/**
 * 
 * @author mercibe
 * 
 */
// TODO argument ligne de commande ou fichier de config (properties ou Spring)
public class ImportationListe {

	static Logger logger = LoggerFactory.getLogger(ImportationListe.class);

	private String nomListe;
	private String descriptionListe;
	private String fichierSource;
	private String nomCorpus;

	private static String configLocation;

	private CorpusRepository corpusRepo;

	private ApplicationContext ctx;

	private ListeRepository listeRepo;

	private String ligneSplitterFQN;

	public ImportationListe() {
		super();
	}

	public String getNomListe() {
		return nomListe;
	}

	public void setNomListe(String nom) {
		this.nomListe = nom;
	}

	public String getDescriptionListe() {
		return descriptionListe;
	}

	public void setDescriptionListe(String description) {
		this.descriptionListe = description;
	}

	public String getFichierSource() {
		return fichierSource;
	}

	public void setFichierSource(String fichierSource) {
		this.fichierSource = fichierSource;
	}

	public String getNomCorpus() {
		return nomCorpus;
	}

	public void setNomCorpus(String corpus) {
		this.nomCorpus = corpus;
	}

	private String run() {

		String message = "";

		ctx = new ClassPathXmlApplicationContext(configLocation);

		// Vérifier si le nomCorpus existe
		Corpus corpus = getCorpus(nomCorpus);
		if (corpus == null) {
			message = "Corpus [" + nomCorpus + "] introuvable dans la base de données.";
			System.err.println(message);
			return message;
		}

		// Vérifier si la liste existe déjà
		Liste liste = getListe(nomListe, descriptionListe, corpus);
		if (liste == null) {
			message = "La liste [" + liste + "] est introuvable dans la base de données et n'a pu être créée.";
			System.err.println(message);
			return message;
		}

		// Instanciation du lineSplitter
		LigneSplitter splitter = getLigneSplitter(ligneSplitterFQN);

		// Chargement du fichier
		chargementListe(splitter, liste);

		// // récupération des champs transient éventuels
		// dbListe.setFichierSource(new File(fichierSource));
		// dbListe.setLigneSplitter(currentListe.getLigneSplitter());

		return message;

	}

	private void chargementListe(LigneSplitter splitter, Liste liste) {

		File fichierSource = new File(getFichierSource());
		// Chargement du fichier en liste
		if (fichierSource != null && fichierSource.exists()) {

			try {
				List<String> lignes = FileUtils.readLines(fichierSource);

				int cptMot = 0;
				for (String ligne : lignes) {
					List<Mot> mots = splitter.splitLigne(ligne);
					for (Mot mot : mots) {
						// TODO utiliser JdbcTemplate pour performance et facilité...
						//
						// // Vérifier si le mot existe déjà. Si oui, mettre à jour sa liste/partition et sauver. Sinon,
						// ajouter.
						// Mot findByMot = mm.findByMot(mot.lemme, mot.getMot(), mot.getCatgram(), mot.getGenre());
						// if(findByMot != null) {
						// findByMot.setListe(liste);
						// }
						//
						// mm.save(findByMot);
						// cptMot++;
					}
				}

				logger.info("{} mots ont été ajoutés à la partition {}.", cptMot, liste);

			} catch (IOException e) {
				logger.error("Erreur lors de la lecture de la liste des mots {}", fichierSource, e);
			}

		} else {
			logger.error("Fichier null ou inexistant: {}", fichierSource);
		}

	}

	private LigneSplitter getLigneSplitter(String ligneSplitterFQN) {
		LigneSplitter ligneSplitter = null;
		try {
			Class<?> ligneSplitterClass = Class.forName(ligneSplitterFQN);
			ligneSplitter = (LigneSplitter) ligneSplitterClass.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return ligneSplitter;
	}

	private Liste getListe(String nom, String description, Corpus corpus) {
		listeRepo = (ListeRepository) ctx.getBean("listeRepository");
		Liste dbListe = listeRepo.findByNom(nom);

		if (dbListe == null) {
			logger.info("Création de la liste {} dans la base de données.", nom);
			Liste liste = new Liste(nom, description);
			listeRepo.save(liste);
			return liste;
		} else {
			logger.info("La liste {} a été trouvé dans la base de données.", dbListe);
			return dbListe;
		}
	}

	private Corpus getCorpus(String nomCorpus) {

		corpusRepo = (CorpusRepository) ctx.getBean("corpusRepository");

		Corpus dbCorpus = corpusRepo.findByNom(nomCorpus);

		if (dbCorpus != null) {
			logger.info("Le nomCorpus {} a été trouvé dans la base de données.", dbCorpus);
		}
		return dbCorpus;
	}

	public static String getConfigLocation() {
		return configLocation;
	}

	public static void setConfigLocation(String configLocation) {
		ImportationListe.configLocation = configLocation;
	}

	public static void main(String[] args) {

		configLocation = System.getProperty("configLocation", "/system-context.xml");

		ImportationListe importationListe = new ImportationListe();
		ImportationListe.setConfigLocation(configLocation);

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Entrer le nom de la liste à importer ou mettre à jour: ");
			importationListe.setNomListe(in.readLine());

			System.out.println("(optionnel) Entrer la description de la liste []: ");
			importationListe.setDescriptionListe(in.readLine());

			System.out.println("Entrer le chemin complet du fichier qui contient la liste des mots de cette liste: ");
			importationListe.setFichierSource(in.readLine());

			System.out.println("(optionnel) Entrer le FQN de la classe du splitter de ligne [com.servicelibre.corpus.liste.LigneSimpleSplitter]: ");
			String splitterFQN = in.readLine();
			if (splitterFQN != null && !splitterFQN.isEmpty()) {
				importationListe.setLigneSplitterFQN(splitterFQN);
			} else {
				importationListe.setLigneSplitterFQN("com.servicelibre.corpus.liste.LigneSimpleSplitter");
			}

			System.out.println("Entrer le nom du Corpus auxquel il faut ajouter cette liste (sensible à la casse): ");
			importationListe.setNomCorpus(in.readLine());

			String confirmation = MessageFormat.format("Confirmer: importer la liste de mots {0}({1}) depuis [{2}] et l''associer au Corpus [{3}] [O/N]?",
					new Object[] { importationListe.getNomListe(), importationListe.getDescriptionListe(), importationListe.getFichierSource(),
							importationListe.getNomCorpus() });
			System.out.println(confirmation);

			String exécuter = in.readLine();

			if (exécuter != null && exécuter.trim().equalsIgnoreCase("O")) {
				importationListe.run();
			} else {
				System.out.println("Annulation de l'importation de liste.");
			}

			System.exit(0);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getLigneSplitterFQN() {
		return ligneSplitterFQN;
	}

	public void setLigneSplitterFQN(String ligneSplitterFQN) {
		this.ligneSplitterFQN = ligneSplitterFQN;
	}

}
