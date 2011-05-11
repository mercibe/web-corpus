package com.servicelibre.corpus.liste;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
//@Table(uniqueConstraints=@UniqueConstraint(columnNames="liste,mot"))
public class Mot
{
    @ManyToOne
    @Column(name="liste_id")
    private Liste liste;
    
    @Column(nullable=false)
    String mot;
    
    @Column
    String lemme;
    
    @Column
    boolean isLemme;
    
    @Column
    String catgram;
    
    @Column
    String note;

    public Mot(String mot, String lemme, boolean isLemme, String catgram, String note)
    {
        super();
        this.mot = mot;
        this.lemme = lemme;
        this.isLemme = isLemme;
        this.catgram = catgram;
        this.note = note;
    }
    
    
    
    

}
