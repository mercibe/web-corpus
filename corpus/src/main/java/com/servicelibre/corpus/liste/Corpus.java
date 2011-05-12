package com.servicelibre.corpus.liste;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames="nom"))
public class Corpus
{

    @Id
    int id;
    
    @Column
    String nom;
    
    @Column
    String description;

    @Override
    public String toString()
    {
        return "Corpus [id=" + id + ", nom=" + nom + ", description=" + description + "]";
    }
    
    
    
}
