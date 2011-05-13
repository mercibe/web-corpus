package com.servicelibre.corpus.manager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Liste;

@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaListeManager implements ListeManager
{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Liste findOne(long listeId)
    {
        return (Liste) entityManager.createQuery("select l from Liste l where l.id = ?").setParameter(1, listeId)
                .getSingleResult();
    }

    @Override
    public Liste save(Liste liste)
    {
        entityManager.persist(liste);
        return liste;
    }

}
