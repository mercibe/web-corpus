package com.servicelibre.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;
import org.zkoss.zul.Column;
import org.zkoss.zul.Filedownload;

public class XlsRecomp {

	File source;
	File dest;

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

		// initialisation des index des colonnes
		int tamponRowIdx = 1;
		int motGraphieIdx = 0;
		int noteIdx = 1;
		int destSupIdx = 2;
		int destAjoIdx = 3;
		int destVérIdx = 4;
		int motGraphie2Idx = 5;
		int catgramIdx = 7;
		int genreIdx = 8;

		// Récupération de la première ligne significative du fichier source
		Row tamponRow = sourceMotSheet.getRow(tamponRowIdx);

		// Récupération de la première colonne de la première ligne
		// significative

		int cptRow = 1;
		Row curRow = null;
		boolean bris = false;
		int ligneTampon = cptRow;
		String tampon = null;
		Cell tamponCell = null;
		while ((curRow = sourceMotSheet.getRow(cptRow)) != null) {
			Cell curMotCell = curRow.getCell(motGraphie2Idx);
			if (curMotCell == null) {
				break;
			}
			String mot = curMotCell.getStringCellValue().trim();

			tamponRow = sourceMotSheet.getRow(ligneTampon);
			tamponCell = tamponRow.getCell(motGraphieIdx);
			if (tamponCell != null) {
				tampon = tamponCell.getStringCellValue().trim();
			}

			// Créer la ligne dans le fichier de destination
			Row destRow = destMotsSheet.createRow(cptRow);

			System.out.println("Traiter le mot " + mot + " - " + tampon + " - " + ligneTampon + "/" + cptRow);
			// tampon = ref => recopier toutes les cellules
			if (tampon != null && tamponCell != null && tampon.equals(mot)) {

				CellStyle cellStyle = tamponCell.getCellStyle();
				short fontIndex = cellStyle.getFontIndex();
				Font fontAt = tamponCell.getSheet().getWorkbook().getFontAt(fontIndex);

				// ajouter note + mot
				Cell destCell = destRow.createCell(motGraphieIdx);
				destCell.setCellValue(mot);
				destCell = destRow.createCell(noteIdx);
				Cell cell = tamponRow.getCell(1);
				if (cell != null) {
					destCell.setCellValue(cell.getStringCellValue());
				}

				for (int i = 2; i < 15; i++) {
					destCell = destRow.createCell(i);
					Cell srcCell = curRow.getCell(i);

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
				ligneTampon++;

			} else {

				// Conserver les colonnes de 5 à 15 de la ligne courante
				for (int i = 5; i < 15; i++) {
					Cell destCell = destRow.createCell(i);
					Cell srcCell = curRow.getCell(i);
					if (srcCell != null) {
						destCell.setCellValue(srcCell.getStringCellValue());
					}
				}

				// ajouter note + mot
				Cell destCell = destRow.createCell(motGraphieIdx);
				destCell.setCellValue(mot);
				destCell = destRow.createCell(noteIdx);
				destCell.setCellValue("À VÉRIFIER");

			}

			cptRow++;

			// if (cptRow == 515)
			// break;
		}

		// Sauvegarder le document
		sauverXlsRecomposé(destWb);

	}

	private void insèreLigneEntête(Sheet destMotsSheet, CreationHelper createHelper, CellStyle entêteCellStyle) {
		String[] entêtes = { "Mots", "Notes", "supprimer", "ajouter", "Vérification", "Mot", "Phonétique", "OR",
				"Classe de mot", "Genre", "Nombre", "Précision", "Année scolaire" };
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
		 "/home/benoitm/Dropbox/ServiceLibre/contrats/2015-02-MEESR-EnvRech/travail/AutresMots-B-recomposé-tmp.xls",
		 "/home/benoitm/Dropbox/ServiceLibre/contrats/2015-02-MEESR-EnvRech/travail/AutresMots-B-recomposé.xls");

//		XlsRecomp recomp = new XlsRecomp(
//				"/home/benoitm/Dropbox/ServiceLibre/contrats/2015-02-MEESR-EnvRech/travail/coucou-recomposé-tmp.xls",
//				"/home/benoitm/Dropbox/ServiceLibre/contrats/2015-02-MEESR-EnvRech/travail/coucou-recomposé.xls");

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
