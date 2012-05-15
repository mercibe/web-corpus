package com.servicelibre.entities.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Aurait dû s'appeler Client, mais impossible avec Spring Data JPA dans ce contexte (entitées dans sous-paquetages différents)
 * @author benoitm
 *
 */
@Entity
public class Consommateur {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column
	String prénom;
	
	@Column
	String nom;
	
	@Column
	Boolean particulier;
	
	@Column
	String organisation;
	
	@Column
	String rue;
	
	@Column
	String ville;
	
	@Column
	String cp;
	
	@Column
	String pays;
	
}
