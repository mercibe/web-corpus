package com.servicelibre.entities.ui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.servicelibre.entities.corpus.Rôle;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "idComposant"))
public class Onglet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column
	String idComposant;

	@Column
	String nom;

	@Column
	String description;

	/**
	 * URL de la ressource à afficher
	 */
	@Column
	String src;

	/**
	 * Inclure la ressource (ZUL) ou insérer dans un iframe (« n'importe quoi »)
	 */
	@Column
	Boolean iframe;

	@Column
	int ordre;

	@Column
	Boolean visible;

	/**
	 * Rôle éventuel à qui on doit donner exclusivement l'accès à cet onglet
	 */
	@ManyToOne(optional = true)
	Rôle rôle;

	public Onglet() {
		super();
	}

	public Onglet(String nom, String description) {
		super();
		this.nom = nom;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public Boolean isIframe() {
		return iframe;
	}

	public void setIframe(Boolean iframe) {
		this.iframe = iframe;
	}

	public int getOrdre() {
		return ordre;
	}

	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public String getIdComposant() {
		return idComposant;
	}

	public void setIdComposant(String idComposant) {
		this.idComposant = idComposant;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Rôle getRôle() {
		return rôle;
	}

	public void setRôle(Rôle rôle) {
		this.rôle = rôle;
	}

}
