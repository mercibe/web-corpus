package com.servicelibre.corpus.manager;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Liste;


@Transactional
public interface ListeManager //extends JpaRepository<Liste, Long>
{
    
    Liste findOne(long listeId);
    Liste findByNom(String nom);
    Liste save(Liste liste);
    
}
