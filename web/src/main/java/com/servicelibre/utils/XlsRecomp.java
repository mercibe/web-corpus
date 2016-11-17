package com.servicelibre.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class XlsRecomp {

	File source;
	File dest;

	int tamponRowIdx = 1;
	int motGraphieIdx = 0;
	int noteIdx = 1;
	int note2Idx = 2;
	int destSupIdx = 3;
	int destAjoIdx = 4;
	int destVérIdx = 5;
	int motGraphie2Idx = 6;

	public XlsRecomp(String source, String dest) {
		this(new File(source), new File(dest));
	}

	public XlsRecomp(File source, File dest) {
		super();
		this.source = source;
		this.dest = dest;
	}

	/**
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void exécute() throws FileNotFoundException, IOException {

		// Préparer la destination
		Workbook destWb = getDestWorkbook();
		CreationHelper createHelper = destWb.getCreationHelper();
		// Création des styles, font, etc. pour les cellules de la feuille de
		// calcul
		Font entêteFont = destWb.createFont();
		entêteFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle entêteCellStyle = destWb.createCellStyle();
		entêteCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		entêteCellStyle.setFont(entêteFont);

		Sheet destMotsSheet = destWb.createSheet("mots");
		insèreLigneEntête(destMotsSheet, createHelper, entêteCellStyle);

		// Ouvrir la source et parcourir ligne par ligne
		Workbook sourceWb = new HSSFWorkbook(new FileInputStream(source));

		// Le première feuille doit s'appeler « mots »
		Sheet sourceMotSheet = sourceWb.getSheet("mots");

		if (sourceMotSheet == null) {
			System.err.println("La feuille « mots » est introuvable - arrêt.");
			return;
		}

		// Récupération de la première ligne significative du fichier source
		Row tamponRow = sourceMotSheet.getRow(tamponRowIdx);

		// Récupération de la première colonne de la première ligne
		// significative

		int cptRow = 1;
		int startIdx = 1;
		Row curRow = null;
		while ((curRow = sourceMotSheet.getRow(cptRow)) != null) {
			// Pour chaque ligne du MELS, rechercher la correspondance dans
			// colonnes suivantes
			Cell curMotCell = curRow.getCell(motGraphieIdx);
			if (curMotCell == null) {
				break;
			}
			String mot = curMotCell.getStringCellValue().trim();

			// Créer la ligne dans le fichier de destination
			Row destRow = destMotsSheet.createRow(cptRow);

			System.out.println("Traiter le mot " + mot + " " + cptRow);
			int motRecompRowIdx = getMotRecompRowIdx(mot, sourceMotSheet, startIdx);

			// recopier 3 premières colonnes
			Cell destCell = destRow.createCell(motGraphieIdx);
			destCell.setCellValue(mot);
			destCell = destRow.createCell(noteIdx);
			Cell noteCell = curRow.getCell(noteIdx);
			if (noteCell != null) {
				destCell.setCellValue(noteCell.getStringCellValue());
			}

			destCell = destRow.createCell(note2Idx);
			Cell note2Cell = curRow.getCell(note2Idx);
			if (note2Cell != null) {
				destCell.setCellValue(note2Cell.getStringCellValue());
			}

			if (motRecompRowIdx < 0) {
				// nouveau mot
				System.out.println("Créer le mot " + mot);

				destCell = destRow.createCell(destAjoIdx);
				destCell.setCellValue("x");
			} else {

				CellStyle cellStyle = curMotCell.getCellStyle();
				short fontIndex = cellStyle.getFontIndex();
				Font fontAt = curMotCell.getSheet().getWorkbook().getFontAt(fontIndex);

				Row rowRecomp = sourceMotSheet.getRow(motRecompRowIdx);
				System.out.println("Recopier les infos de la ligne " + motRecompRowIdx + " : "
						+ rowRecomp.getCell(motGraphie2Idx).getStringCellValue());

				for (int i = 3; i < 17; i++) {
					destCell = destRow.createCell(i);
					Cell srcCell = rowRecomp.getCell(i);

					if (i == destSupIdx) {
						// si police est barrée => mettre un x dans colonne
						// supprimer
						destCell.setCellValue(fontAt.getStrikeout() ? "x" : "");
					} else if (i == destVérIdx) {
						// Fonction à recopier?
					} else if (srcCell != null) {
						destCell.setCellValue(srcCell.getStringCellValue());
					}
				}

				startIdx = motRecompRowIdx + 1;
			}

			cptRow++;

		}

		// Sauvegarder le document
		sauverXlsRecomposé(destWb);

	}

	private int getMotRecompRowIdx(String mot, Sheet sourceMotSheet, int startIdx) {
		int cptRow = startIdx;
		Row curRow;
		while ((curRow = sourceMotSheet.getRow(cptRow)) != null) {
			Cell curMotCell = curRow.getCell(motGraphie2Idx);
			if (curMotCell == null) {
				return -1;
			}
			String mot2 = curMotCell.getStringCellValue().trim();
			if (mot2.equalsIgnoreCase(mot)) {
				return cptRow;
			}
			cptRow++;
		}
		return -1;
	}

	private boolean isNouveauMot(String tampon, Sheet sourceMotSheet, int cptRow, int ligneTampon) {

		Row curRow;

		while ((curRow = sourceMotSheet.getRow(cptRow)) != null) {
			Cell curMotCell = curRow.getCell(motGraphie2Idx);
			if (curMotCell == null) {
				return true;
			}
			String mot = curMotCell.getStringCellValue().trim();
			if (mot.equalsIgnoreCase(tampon)) {
				return false;
			}
			cptRow++;
		}
		return true;
	}

	private void insèreLigneEntête(Sheet destMotsSheet, CreationHelper createHelper, CellStyle entêteCellStyle) {
		String[] entêtes = { "Mots", "Notes", "Notes2", "supprimer", "ajouter", "Vérification", "Mot", "Lemme",
				"Phonétique", "OR", "Classe de mot", "Genre", "Nombre", "Précision", "Année scolaire" };
		int colCpt = 0;

		org.apache.poi.ss.usermodel.Row row = destMotsSheet.createRow(0);
		for (String entête : entêtes) {
			org.apache.poi.ss.usermodel.Cell cell = row.createCell(colCpt++);
			cell.setCellStyle(entêteCellStyle);
			cell.setCellValue(createHelper.createRichTextString(entête));
		}

	}

	private Workbook getDestWorkbook() throws FileNotFoundException, IOException {

		Workbook destWorkbook = new HSSFWorkbook();

		return destWorkbook;
	}

	private void sauverXlsRecomposé(Workbook destWb) {
		// Détruire si existe
		if (dest.exists()) {
			dest.delete();
		}

		try {
			FileOutputStream fos = new FileOutputStream(dest);
			destWb.write(fos);
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		XlsRecomp recomp = new XlsRecomp(
				"/home/benoitm/Dropbox/ServiceLibre/contrats/2015-02-MEESR-EnvRech/travail/DEV/V-Final-recomposé-tmp.xls",
				"/home/benoitm/Dropbox/ServiceLibre/contrats/2015-02-MEESR-EnvRech/travail/DEV/V-Final-recomposé.xls");

		// XlsRecomp recomp = new XlsRecomp(
		// "/home/benoitm/Dropbox/ServiceLibre/contrats/2015-02-MEESR-EnvRech/travail/coucou-recomposé-tmp.xls",
		// "/home/benoitm/Dropbox/ServiceLibre/contrats/2015-02-MEESR-EnvRech/travail/coucou-recomposé.xls");

		recomp.exécute();

	}

	public File getSource() {
		return source;
	}

	public void setSource(File source) {
		this.source = source;
	}

	public File getDest() {
		return dest;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

}
