package com.servicelibre.corpus.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class Contexte
{
    @Id
    @GeneratedValue
    int Id;

    @ManyToOne(optional = false)
    Mot mot;

}
