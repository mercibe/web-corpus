package com.servicelibre.zk.viewmodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.corpus.manager.FiltreRecherche.CléFiltre;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.ListeMotRepository;
import com.servicelibre.repositories.corpus.ListeRepository;
import com.servicelibre.repositories.corpus.MotRepository;
import com.servicelibre.repositories.corpus.MotRepositoryCustom;
import com.servicelibre.zk.controller.IndexCtrl;
import com.servicelibre.zk.controller.IndexCtrl.Mode;

public class ListesEtMotsVM {

	private static final Logger logger = LoggerFactory.getLogger(ListesEtMotsVM.class);

	private Validator nonVideValidator = new NonVideValidator();

	Corpus corpus = ServiceLocator.getCorpusService().getCorpus();

	ListModelList<Liste> listes;
	ListModelList<Mot> mots;

	Liste listeSélectionné;
	Mot motSélectionné;

	ListModelList<CatégorieListe> catégories;

	String mode = null;

	ListeRepository listeRepo;
	CatégorieListeRepository catégorieListeRepo;
	ListeMotRepository listeMotRepo;
	MotRepository motRepo;

	String messageSuppression;
	String messageSuppressionMots;
	String messageRapportImportation;

	StandardPasswordEncoder encodeur = new StandardPasswordEncoder();

	public ListModelList<Liste> getListes() {
		if (listes == null) {
			listes = new ListModelList<Liste>((Collection<? extends Liste>) getListeRepo().findAll(
					new Sort(new Order(Direction.ASC, "Catégorie.ordre"), new Order(Direction.ASC, "ordre"),
							new Order(Direction.ASC, "nom"))));
		}
		return listes;
	}

	public ListModelList<CatégorieListe> getCatégories() {
		if (catégories == null) {
			catégories = new ListModelList<CatégorieListe>((Collection<? extends CatégorieListe>) getCatégorieListeRepo().findAll(
					new Sort(new Order(Direction.ASC, "ordre"), new Order(Direction.ASC, "nom"))));
		}
		return catégories;
	}

	private CatégorieListeRepository getCatégorieListeRepo() {
		if (catégorieListeRepo == null) {
			catégorieListeRepo = (CatégorieListeRepository) SpringUtil.getBean("catégorieListeRepository", CatégorieListeRepository.class);
		}
		return catégorieListeRepo;
	}

	private ListeMotRepository getListeMotRepo() {
		if (listeMotRepo == null) {
			listeMotRepo = (ListeMotRepository) SpringUtil.getBean("listeMotRepository", ListeMotRepository.class);
		}
		return listeMotRepo;
	}

	private MotRepository getMotRepo() {
		if (motRepo == null) {
			motRepo = (MotRepository) SpringUtil.getBean("motRepository", MotRepository.class);
		}
		return motRepo;
	}

	public Liste getListeSélectionné() {
		return listeSélectionné;
	}

	public void setSélectionné(Liste listeSélectionné) {
		this.listeSélectionné = listeSélectionné;
	}

	public String getMessageSuppression() {
		return messageSuppression;
	}

	public void setMessageSuppression(String messageSuppression) {
		this.messageSuppression = messageSuppression;
	}

	public ListeRepository getListeRepo() {
		if (listeRepo == null) {
			listeRepo = (ListeRepository) SpringUtil.getBean("listeRepository", ListeRepository.class);
		}
		return listeRepo;
	}

	@NotifyChange({ "listeSélectionné", "listes", "mode", "catégorieCourante", "mots" })
	@Command
	public void ajouterListe() {
		Liste liste = new Liste();
		getListes().add(liste);
		listeSélectionné = liste;
		mode = "ajout";
	}

	@NotifyChange({ "mode" })
	@Command
	public void modifierListe() {
		mode = "modification";
		logger.debug("Catégorie courante: {}", listeSélectionné.getCatégorie());
	}

	@NotifyChange({ "mode", "listes" })
	@Command
	public void annulerModification() {
		listes = null;
		getListes();
		mode = null;
	}

	@NotifyChange({ "listeSélectionné", "listes", "mode" })
	@Command
	@Transactional
	public void enregistrerListe() {

		logger.info("Enregistrement de la liste {}", listeSélectionné);

		boolean création = listeSélectionné.getId() == 0 ? true : false;

		try {

			listeSélectionné = getListeRepo().save(listeSélectionné);

			// ne faire que si nouvelle liste
			if (création) {

			}

			// Forcer le rafraississement correct (fastidieux mais ESSENTIEL)
			// Récupération de l'item listeSélectionné dans le master
			Liste listeMaster = listes.getSelection().iterator().next();
			// Recherche de l'index de cet item
			int indexOfListeSélectionné = listes.indexOf(listeMaster);
			// Mise à jour de cet item
			listes.set(indexOfListeSélectionné, listeSélectionné);

		} catch (DataIntegrityViolationException e) {
			logger.info("Une liste avec le même ????? existe déjà.");
		}

		logger.info("Liste enregistrée: {}", listeSélectionné);

		// Pour fermer la zone d'édition
		mode = null;

	}

	@NotifyChange({ "listeSélectionné", "listes", "messageSuppression", "mode" })
	@Command
	@Transactional
	public void supprimerListe() {

		logger.info("Suppression de la liste {} :", listeSélectionné);

		if (listeSélectionné.getId() == 0) {
			annulerModification();
			listeSélectionné = null;
			messageSuppression = null;
			return;
		}

		try {
			getListeRepo().delete(listeSélectionné.getId());
		} catch (Exception e) {
			logger.error("À traiter?", e);
		}

		getListes().remove(listeSélectionné);
		listeSélectionné = null;
		messageSuppression = null;
		mode = null;

	}

	@NotifyChange("messageSuppression")
	@Command
	public void confirmerSuppression() {
		messageSuppression = "Voulez-vous vraiment supprimer la liste " + listeSélectionné.getNom() + " ?" + " ("
				+ listeSélectionné.getId() + ")";
	}

	@NotifyChange("messageSuppression")
	@Command
	public void annulerSuppression() {
		messageSuppression = null;
	}

	public Validator getNonVideValidator() {
		return nonVideValidator;
	}

	public void setNonVideValidator(Validator nonVideValidator) {
		this.nonVideValidator = nonVideValidator;
	}

	@NotifyChange("mots")
	@Command
	public ListModelList<Mot> getMots() {
		logger.debug("MISE à JOUR DES MOTS pour la liste {}", listeSélectionné);
		if (listeSélectionné != null && listeSélectionné.getId() != 0) {

			FiltreRecherche f = new FiltreRecherche();

			f.addFiltre(new Filtre(CléFiltre.liste.name() + "_" + listeSélectionné.getCatégorie().getNom(), listeSélectionné.getCatégorie()
					.getNom(), new Long[] { listeSélectionné.getId() }));
			mots = new ListModelList<Mot>((Collection<? extends Mot>) getMotRepo().findByGraphie("",
					MotRepositoryCustom.Condition.COMMENCE_PAR, f));

		} else {
			mots = new ListModelList<Mot>();
		}

		return mots;
	}

	@NotifyChange({ "mots", "messageSuppressionMots" })
	@Command
	@Transactional
	public void retirerMotsSélectionnésDeLaListe() {
		logger.debug("Retirer les mots sélectionnés de la liste...");

		listeSélectionné = listeRepo.findOne(listeSélectionné.getId());

		for (Mot mot : mots) {
			if (mot.sélectionné) {
				logger.debug("retirer {}...", mot);
				ListeMot àSupprimer = getListeMotRepo().findByListeAndMot(listeSélectionné, mot);
				listeMotRepo.delete(àSupprimer.getId());
			}
		}

		messageSuppressionMots = null;

	}

	@NotifyChange("messageSuppressionMots")
	@Command
	public void confirmerSuppressionMots() {
		messageSuppressionMots = "Voulez-vous vraiment supprimer ces mots de la liste ?";
	}

	@NotifyChange("messageSuppressionMots")
	@Command
	public void annulerSuppressionMots() {
		messageSuppressionMots = null;
	}

	@Command
	public void ajouterMot() {
		getIndexCtrl().ouvreOngletMot(Mode.CRÉATION, null);
	}

	@NotifyChange("messageRapportImportation")
	@Command
	public void fermerRapportImportation() {
		messageRapportImportation = null;
	}

	enum TypeRapport {
		FAIT, ERREUR, MOT_INCONNU
	};

	@Command
	@NotifyChange({ "mots", "messageRapportImportation" })
	@Transactional
	public void importerMots(@BindingParam("ev") UploadEvent v) {
		logger.debug("Événement {} ", v);
		logger.debug("Média {}", v.getMedia());

		Map<TypeRapport, List<String>> rapports = new HashMap<ListesEtMotsVM.TypeRapport, List<String>>(3);
		List<String> rapportFait = new ArrayList<String>();
		List<String> rapportErreur = new ArrayList<String>();
		List<String> rapportMotInconnu = new ArrayList<String>();

		Media fichierXls = v.getMedia();

		// TODO valider qu'il s'agit bien d'un fichier XLS
		// Lecture du fichier

		// Supprimer tous les mots de la liste actuelle
		getListeMotRepo().deleteByListe(listeSélectionné);

		try {

			Workbook wb = new HSSFWorkbook(new ByteArrayInputStream(fichierXls.getByteData()));

			Sheet sheet = wb.getSheet("mots");

			int rowIdx = 1;
			int motGraphieIdx = 0;
			int catgramIdx = 3;
			int genreIdx = 4;

			Row row = sheet.getRow(rowIdx);
			Cell cell = row.getCell(motGraphieIdx);
			String motÀImporter = (cell == null ? "" : cell.getStringCellValue().trim());

			while (!motÀImporter.isEmpty()) {

				String catgram = row.getCell(catgramIdx).getStringCellValue().trim();
				String genre = row.getCell(genreIdx).getStringCellValue().trim();

				// Retrouver le mot
				// FIXME tenir compte également du genre au besoin
				//Est-ce un nom?
				List<Mot> mots = null;
				if(catgram.equals("n.")) {
					mots = motRepo.findByMotAndCatgramAndGenre(motÀImporter, catgram, genre);
				}
				else {
					mots = motRepo.findByMotAndCatgram(motÀImporter, catgram);
				}
				
				
				if (mots.size() == 1) {
					// TODO Lier ce mot à la liste courante
					logger.debug("Le mot {} va être ajouté à la liste {}", motÀImporter, listeSélectionné.getId());
					ListeMot lm = new ListeMot(mots.get(0), listeSélectionné);
					listeMotRepo.save(lm);
					rapportFait.add(motÀImporter);
				} else if (mots.size() > 1) {
					// erreur / ambiguité. Quel mot associer à la liste?
					// TODO conserver le ou les mots problématiques et afficher un rapport à la fin de l'importation
					logger.error("Le mot {} correspond à plusieurs mot dans la BD", motÀImporter, mots);
					rapportErreur.add(motÀImporter + "\t correspond à plusieurs mot dans la BD");
				} else {
					// TODO créer le mot???
					logger.warn("Le mot {} est introuvable dans la BD", motÀImporter);
					rapportMotInconnu.add(motÀImporter + "\t" + "introuvable dans la B");
				}

				// Associer à la liste courante

				rowIdx++;
				row = sheet.getRow(rowIdx);
				if (row == null) {
					break;
				}
				cell = row.getCell(motGraphieIdx);
				motÀImporter = (cell == null ? "" : cell.getStringCellValue().trim());
			}

			rapports.put(TypeRapport.FAIT, rapportFait);
			rapports.put(TypeRapport.MOT_INCONNU, rapportMotInconnu);
			rapports.put(TypeRapport.ERREUR, rapportErreur);

			messageRapportImportation = getRapportImportationString(rapports);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getRapportImportationString(Map<TypeRapport, List<String>> rapports) {
		StringBuilder sb = new StringBuilder();

		sb.append("Mots importés: ").append(rapports.get(TypeRapport.FAIT).size()).append("\n");

		List<String> motsInconnus = rapports.get(TypeRapport.MOT_INCONNU);
		if (motsInconnus.size() > 0) {
			sb.append("Mots inconnus: ").append(motsInconnus.size()).append("\n");
			for(String motInconnu : motsInconnus) {
				sb.append("\t- ").append(motInconnu).append("\n");
			}
		}

		List<String> erreurs = rapports.get(TypeRapport.ERREUR);
		if(erreurs.size() > 0) {
			sb.append("Erreurs: ").append(erreurs.size()).append("\n");
			for(String erreur:erreurs) {
				sb.append("\t- ").append(erreur).append("\n");
			}
		}

		return sb.toString();
	}

	@Command
	public void afficherFicheMot(@BindingParam("mot") Mot mot) {
		getIndexCtrl().ouvreOngletMot(Mode.AFFICHAGE, mot);
	}

	private IndexCtrl getIndexCtrl() {
		// Pas catholique d'avoir une référence UI dans MVVM mais tellement
		// pratique ;-)
		// autre solution
		// http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/MVVM/Advance/Wire_Components
		Window webCorpusWindow = (Window) Path.getComponent("//webCorpusPage/webCorpusWindow");
		// Map<String, Object> attributes = webCorpusWindow.getAttributes();
		// for(String key: attributes.keySet()) {
		// logger.debug("{}={}", key, attributes.get(key));
		// }
		// Le composer est stocké par ZK dans un attribut du composant
		// http://books.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Controller/Composer
		IndexCtrl indexCtrl = (IndexCtrl) webCorpusWindow.getAttribute("$composer");
		return indexCtrl;
	}

	// @NotifyChange({ "mots" })
	// @Command
	// public void sélectionnerTout() {
	//
	// for (Mot mot : mots) {
	// mot.setSélectionné(true);
	// }
	// }

	public void setMots(ListModelList<Mot> mots) {
		this.mots = mots;
	}

	public Mot getMotSélectionné() {
		return motSélectionné;
	}

	public void setMotSélectionné(Mot motSélectionné) {
		this.motSélectionné = motSélectionné;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setListes(ListModelList<Liste> listes) {
		this.listes = listes;
	}

	public void setListeSélectionné(Liste listeSélectionné) {
		this.listeSélectionné = listeSélectionné;
	}

	public void setListeRepo(ListeRepository listeRepo) {
		this.listeRepo = listeRepo;
	}

	public String getMessageSuppressionMots() {
		return messageSuppressionMots;
	}

	public void setMessageSuppressionMots(String messageSuppressionMots) {
		this.messageSuppressionMots = messageSuppressionMots;
	}

	public String getMessageRapportImportation() {
		return messageRapportImportation;
	}

	public void setMessageRapportImportation(String messageRapportImportation) {
		this.messageRapportImportation = messageRapportImportation;
	}

}
