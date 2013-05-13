package com.servicelibre.entities.corpus;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "pseudo"))
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column
    String pseudo;

    @Column
    String prénom;

    @Column
    String nom;

    @Column
    String courriel;

    @Column
    String motDePasse;

    @OneToMany(mappedBy = "utilisateur", cascade = { CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.EAGER)
    Set<UtilisateurRôle> utilisateurRôles = new HashSet<UtilisateurRôle>();

    @OneToMany(mappedBy = "utilisateur", cascade = { CascadeType.REMOVE }, orphanRemoval = true)
    Set<CatégorieListe> catégorieListes = new HashSet<CatégorieListe>();

    @OneToMany(mappedBy = "utilisateur", cascade = { CascadeType.REMOVE }, orphanRemoval = true)
    Set<Liste> listes = new HashSet<Liste>();

    @OneToMany(mappedBy = "utilisateur", cascade = { CascadeType.REMOVE }, orphanRemoval = true)
    Set<Mot> mots = new HashSet<Mot>();

    @OneToMany(mappedBy = "utilisateur", cascade = { CascadeType.REMOVE }, orphanRemoval = true)
    Set<Contexte> contextes = new HashSet<Contexte>();

    public Utilisateur() {
	super();
    }

    public Utilisateur(String pseudo, String prénom, String nom, String courriel, String motDePasse) {
	super();
	this.pseudo = pseudo;
	this.prénom = prénom;
	this.nom = nom;
	this.courriel = courriel;
	this.motDePasse = motDePasse;
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

    public String getPseudo() {
	return pseudo;
    }

    public void setPseudo(String pseudo) {
	this.pseudo = pseudo;
    }

    public String getPrénom() {
	return prénom;
    }

    public void setPrénom(String prénom) {
	this.prénom = prénom;
    }

    public String getCourriel() {
	return courriel;
    }

    public void setCourriel(String courriel) {
	this.courriel = courriel;
    }

    public String getMotDePasse() {
	return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
	this.motDePasse = motDePasse;
    }

    public Set<UtilisateurRôle> getUtilisateurRôles() {
	return utilisateurRôles;
    }

    public void setUtilisateurRôles(Set<UtilisateurRôle> utilisateurRôles) {
	this.utilisateurRôles = utilisateurRôles;
    }

    public Set<CatégorieListe> getCatégorieListes() {
	return catégorieListes;
    }

    public void setCatégorieListes(Set<CatégorieListe> catégorieListes) {
	this.catégorieListes = catégorieListes;
    }

    public Set<Liste> getListes() {
	return listes;
    }

    public void setListes(Set<Liste> listes) {
	this.listes = listes;
    }

    public Set<Mot> getMots() {
	return mots;
    }

    public void setMots(Set<Mot> mots) {
	this.mots = mots;
    }

    public Set<Contexte> getContextes() {
	return contextes;
    }

    public void setContextes(Set<Contexte> contextes) {
	this.contextes = contextes;
    }

    @Override
    public String toString() {
	return "Utilisateur [id=" + id + ", pseudo=" + pseudo + ", prénom=" + prénom + ", nom=" + nom + ", courriel=" + courriel + "]";
    }

}
