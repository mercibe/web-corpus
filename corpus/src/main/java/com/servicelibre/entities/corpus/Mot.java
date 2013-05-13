package com.servicelibre.entities.corpus;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "lemme", "mot", "catgram", "genre" }))
/**
 * Cette classe représente un conteneur d'information pour un mot donné de la langue française.
 * 
 * @author mercibe
 *
 */
public class Mot implements Comparable<Mot> {

	static Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	// TODO devrait être propre à un corpus donné, une List<CorpusMotListePrimaire>
	// Liste primaire (normalement une partition)
	@ManyToOne(optional = true)
	private Liste listePartitionPrimaire;

	@OneToMany(mappedBy = "mot", cascade = { CascadeType.REMOVE }, orphanRemoval = true)
	private List<ListeMot> listeMots = new ArrayList<ListeMot>();

	// Utilisation d'un Set pour permettre le chargement EAGER et éviter
	// javax.persistence.PersistenceException: org.hibernate.HibernateException:
	// cannot simultaneously fetch multiple
	// bags cf.
	// http://blog.eyallupu.com/2010/06/hibernate-exception-simultaneously.html
	@OneToMany(mappedBy = "mot", cascade = { CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<MotPrononciation> motPrononciations = new HashSet<MotPrononciation>();

	@Column(nullable = false)
	String mot;

	/**
	 * Graphie alternative. Si la graphie du mot est rectifiée (ro = true), ce
	 * champ contient la graphie traditionnelle. Si la graphie du mot est
	 * traditionnelle, ce champ contient la graphie rectifiée.
	 */
	@Column
	String autreGraphie;

	@Column
	public String lemme;

	@Column
	Boolean ro;

	@Column
	public Boolean estUnLemme;

	@Column(nullable = false)
	String catgramAffichage;
	
	@Column(nullable = false)
	String catgram;

	/**
	 * Genre du mot: m., f., épicène
	 */
	@Column
	String genre;

	/**
	 * Nombre du mot: sing., pl., inv.
	 */
	@Column
	String nombre;

	/**
	 * Information additionnelles sur la classe du mot (et donc du lemme
	 * également) Transitivité du verbe (tr. dir., tr. indir. ou intr.) Mais
	 * aussi: pron., impers., etc.
	 */
	@Column
	String catgramPrécision;

	@Column
	String lemmeNote;
	
	@Column
	String motNote;
	
	@Column
	String note;

	@ManyToOne
	Utilisateur utilisateur;

	@Transient
	public boolean sélectionné;

	public Mot() {
		super();
	}

	public Mot(String mot, String lemme, Boolean isLemme, String catgram, String genre, String nombre, String catgramPrécision, boolean ro, String note) {
		super();
		this.mot = mot;
		this.lemme = lemme;
		this.estUnLemme = isLemme;
		this.catgram = catgram;
		this.genre = genre;
		this.nombre = nombre;
		this.catgramPrécision = catgramPrécision;
		this.ro = ro;
		this.note = note;
	}

	@Override
	public String toString() {
		return "Mot [id=" + id + ", mot=" + mot + ", lemme=" + lemme + ", estUnLemme=" + estUnLemme + ", prononciations=" + motPrononciations + ", catgram="
				+ catgram + ", genre=" + genre + ", nombre=" + nombre + ", catgramPrésicion=" + catgramPrécision + ", ro=" + ro + ", note=" + note + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMot() {
		return mot;
	}

	public void setMot(String mot) {
		this.mot = mot;
	}

	public String getCatgram() {
		return catgram;
	}

	public void setCatgram(String catgram) {
		this.catgram = catgram;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCatgramPrécision() {
		return catgramPrécision;
	}

	public void setCatgramPrécision(String catgramPrécision) {
		this.catgramPrécision = catgramPrécision;
	}

	public Boolean isRo() {
		return ro==null?false:ro;
	}

	public void setRo(Boolean ro) {
		this.ro = ro;
	}

	public void setAutreGraphie(String autreGraphie) {
		this.autreGraphie = autreGraphie;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPrononciationsString() {
		StringBuilder prononcs = new StringBuilder();
		String sep = "";
		for (MotPrononciation mp : motPrononciations) {
			prononcs.append(sep).append("[").append(mp.getPrononciation().prononciation).append("]");
			sep = ", ";
		}
		return prononcs.toString();
	}

	public String getLemme() {
		return lemme;
	}

	public void setLemme(String lemme) {
		this.lemme = lemme;
	}

	public String getAutreGraphie() {
		return autreGraphie;
	}

	public Boolean getEstUnLemme() {
		return this.estUnLemme;
	}

	public void setEstUnLemme(Boolean isLemme) {
		this.estUnLemme = isLemme;
	}

	@Override
	public int compareTo(Mot autreMot) {
		return collator.compare(this.lemme, autreMot.lemme);
	}

	public List<ListeMot> getListeMots() {
		return listeMots;
	}

	public void setListeMots(List<ListeMot> listeMots) {
		this.listeMots = listeMots;
	}

	public Set<MotPrononciation> getMotPrononciations() {
		return motPrononciations;
	}

	public void setMotPrononciations(Set<MotPrononciation> motPrononciations) {
		this.motPrononciations = motPrononciations;
	}

	public Liste getListePartitionPrimaire() {
		return listePartitionPrimaire;
	}

	public void setListePartitionPrimaire(Liste listePartitionPrimaire) {
		this.listePartitionPrimaire = listePartitionPrimaire;
	}

	public boolean isSélectionné() {
		return sélectionné;
	}

	public void setSélectionné(boolean sélectionné) {
		this.sélectionné = sélectionné;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	public String getLemmeNote() {
		return lemmeNote;
	}

	public void setLemmeNote(String lemmeNote) {
		this.lemmeNote = lemmeNote;
	}

	public String getMotNote() {
		return motNote;
	}

	public void setMotNote(String motNote) {
		this.motNote = motNote;
	}

	public String getCatgramAffichage() {
		return catgramAffichage;
	}

	public void setCatgramAffichage(String catgramAffichage) {
		this.catgramAffichage = catgramAffichage;
	}

	

}
