/**
 * Code library for textual corpus management
 *
 * Copyright (C) 2011 Benoit Mercier <benoit.mercier@servicelibre.com> — Tous droits réservés.
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

package com.servicelibre.corpus.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class MotInfo {
	public enum FreqPrecision {
		EXACTE, CALCULÉE, SANS_OBJET
	};

	public enum Genre {
		MASCULIN, FÉMININ, MASCULIN_OU_FÉMININ, ÉPICÈNE, INVARIABLE;
		public static Genre asGenre(String genre) {
			if (genre == null) {
				return null;
			}

			String genreÀConvertir = genre.trim().toLowerCase().replaceAll("\\.", "");

			if (genreÀConvertir.equals("m")) {
				return MASCULIN;
			} else if (genreÀConvertir.equals("f")) {
				return FÉMININ;
			} else if (genreÀConvertir.equals("m_ou_f")) {
				return MASCULIN_OU_FÉMININ;
			} else if (genreÀConvertir.equals("épicène")) {
				return ÉPICÈNE;
			} else if (genreÀConvertir.equals("inv")) {
				return INVARIABLE;
			}

			return null;
		}

		@Override
		public String toString() {
			switch (this) {
			case FÉMININ:
				return "f.";
			case INVARIABLE:
				return "inv.";
			case MASCULIN:
				return "m.";
			case MASCULIN_OU_FÉMININ:
				return "m. ou f.";
			case ÉPICÈNE:
				return "épicène";
			}
			return "";
		}

	}

	public enum Nombre {
		SINGULIER, PLURIEL;
		public static Nombre asNombre(String nombre) {
			if (nombre == null) {
				return null;
			}

			String nombreÀConvertir = nombre.trim().toLowerCase().replaceAll("\\.", "");

			if (nombreÀConvertir.equals("s") || nombreÀConvertir.equals("sing")) {
				return SINGULIER;
			} else if (nombreÀConvertir.equals("p") || nombreÀConvertir.equals("pl") || nombreÀConvertir.equals("plur")) {
				return PLURIEL;
			}
			return null;
		}

		@Override
		public String toString() {
			switch (this) {
			case PLURIEL:
				return "pl.";
			case SINGULIER:
				return "s.";
			}
			return "";
		}

	}

	public String mot;
	public String lemme;
	Catgram catgram;
	public String prononciation;

	double freqMot;
	double freqLemme;
	boolean isLemme;
	public String note;
	FreqPrecision freqMotprecision;
	FreqPrecision freqLemmePrecision;

	Genre genre;
	Nombre nombre;
	String personne;

	// Ajouter un tableau de genre/nombre + mode/temps/personne

	public MotInfo(String mot) {
		this(mot, null, null, null);
	}

	public MotInfo(String mot, String lemme, Catgram catgram, String note) {
		this(mot, lemme, catgram, note, 0, 0, false);
	}

	public MotInfo(String mot, String lemme, Catgram catgram, String note, double freqMot, double freqLemme, boolean isLemme) {
		this.mot = mot;
		this.lemme = lemme;
		this.catgram = catgram;
		this.note = note;
		this.freqMot = freqMot;
		this.freqLemme = freqLemme;
		this.isLemme = isLemme;
	}

	public MotInfo(String mot, String lemme, Catgram catgram, String note, double freqMot, double freqLemme, boolean isLemme, String genre,
			String nombre, String personne) {
		this(mot, lemme, catgram, note, freqMot, freqLemme, isLemme);
		this.genre = Genre.asGenre(genre);
		this.nombre = Nombre.asNombre(nombre);
		this.personne = personne.trim();
	}

	public MotInfo(String mot, String lemme, Catgram catgram, String note, double freqMot, double freqLemme, boolean isLemme, Genre genre,
			Nombre nombre, String personne) {
		this(mot, lemme, catgram, note, freqMot, freqLemme, isLemme);
		this.genre = genre;
		this.nombre = nombre;
		this.personne = personne.trim();
	}

	public MotInfo() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(mot).append("|").append(lemme).append("|");

		// Si catgram NULL, problème à signaler
		if (catgram == null) {
			System.err.println("catgram.id == NULL pour " + mot + "|" + lemme + "|" + note);
			sb.append("NULL");
		} else {

			sb.append(catgram.id);
		}

		sb.append("|").append(freqMot).append("|").append(freqLemme).append("|").append(isLemme).append("|").append(note).append("|")
				.append(prononciation).append("|").append(genre).append("|").append(nombre).append("|").append(personne);

		return sb.toString();

	}

	public String getMot() {
		return mot;
	}

	public MotInfo setMot(String mot) {
		this.mot = mot;
		return this;
	}

	public String getLemme() {
		return lemme;
	}

	public MotInfo setLemme(String lemme) {
		this.lemme = lemme;
		return this;
	}

	public Catgram getCatgram() {
		return catgram;
	}

	public MotInfo setCatgram(Catgram catgram) {
		this.catgram = catgram;
		return this;
	}

	public FreqPrecision getFreqMotPrecision() {
		return freqMotprecision;
	}

	public MotInfo setFreqMotPrecision(FreqPrecision freqMotPrecision) {
		this.freqMotprecision = freqMotPrecision;
		return this;
	}

	public FreqPrecision getFreqLemmePrecision() {
		return freqLemmePrecision;
	}

	public void setFreqLemmePrecision(FreqPrecision freqLemmePrecision) {
		this.freqLemmePrecision = freqLemmePrecision;
	}

	public double getFreqMot() {
		return freqMot;
	}

	public MotInfo setFreqMot(double freqMot) {
		this.freqMot = freqMot;
		return this;
	}

	public double getFreqLemme() {
		return freqLemme;
	}

	public MotInfo setFreqLemme(double freqLemme) {
		this.freqLemme = freqLemme;
		return this;
	}

	public boolean isLemme() {
		return isLemme;
	}

	public MotInfo setLemme(boolean isLemme) {
		this.isLemme = isLemme;
		return this;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPrononciation() {
		return prononciation;
	}

	public void setPrononciation(String prononciation) {
		this.prononciation = prononciation;
	}

	public FreqPrecision getFreqMotprecision() {
		return freqMotprecision;
	}

	public void setFreqMotprecision(FreqPrecision freqMotprecision) {
		this.freqMotprecision = freqMotprecision;
	}

	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	public Nombre getNombre() {
		return nombre;
	}

	public void setNombre(Nombre nombre) {
		this.nombre = nombre;
	}

	public String getPersonne() {
		return personne;
	}

	public void setPersonne(String personne) {
		this.personne = personne;
	}

	public static void dumpMotInfos(List<MotInfo> motInfoList, String dumpFilename) {
		File dumpFile = new File(dumpFilename);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
			for (MotInfo motInfo : motInfoList) {
				// System.out.println(motInfo);
				writer.append(motInfo.toString());
				writer.newLine();
			}
			writer.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void dumpMotInfosList(List<List<MotInfo>> motInfoList, String dumpFilename) {
		File dumpFile = new File(dumpFilename);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
			for (List<MotInfo> motInfo : motInfoList) {
				for (MotInfo info : motInfo) {
					writer.append(info.toString());
					writer.newLine();
				}
			}
			writer.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catgram == null) ? 0 : catgram.hashCode());
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((lemme == null) ? 0 : lemme.hashCode());
		result = prime * result + ((mot == null) ? 0 : mot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MotInfo other = (MotInfo) obj;
		if (catgram == null) {
			if (other.catgram != null)
				return false;
		} else if (!catgram.equals(other.catgram))
			return false;
		if (genre != other.genre)
			return false;
		if (lemme == null) {
			if (other.lemme != null)
				return false;
		} else if (!lemme.equals(other.lemme))
			return false;
		if (mot == null) {
			if (other.mot != null)
				return false;
		} else if (!mot.equals(other.mot))
			return false;
		return true;
	}

	public boolean equalsOld(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		MotInfo other = (MotInfo) obj;

		if (catgram == null) {
			if (other.catgram != null)
				return false;
		} else if (!catgram.id.equals(other.catgram.id))
			return false;

		if (lemme == null) {
			if (other.lemme != null)
				return false;
		} else if (!lemme.equals(other.lemme))
			return false;

		if (mot == null) {
			if (other.mot != null)
				return false;
		} else if (!mot.equals(other.mot))
			return false;

		return true;
	}

}
