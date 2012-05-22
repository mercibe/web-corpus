package com.servicelibre.entities.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Client {

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
