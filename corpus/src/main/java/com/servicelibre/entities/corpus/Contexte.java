package com.servicelibre.entities.corpus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class Contexte
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int Id;

    @ManyToOne(optional = false)
    ListeMot listeMot;

    @ManyToOne(optional = false)
    ListeMot typeContexte;
    
    @Column
    String documentId;
    
    @Column
    String note;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public ListeMot getListeMot() {
		return listeMot;
	}

	public void setListeMot(ListeMot listeMot) {
		this.listeMot = listeMot;
	}

	public ListeMot getTypeContexte() {
		return typeContexte;
	}

	public void setTypeContexte(ListeMot typeContexte) {
		this.typeContexte = typeContexte;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

    
    
}
