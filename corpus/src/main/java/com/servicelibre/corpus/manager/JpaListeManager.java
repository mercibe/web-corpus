package com.servicelibre.corpus.manager;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Liste;

@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaListeManager implements ListeManager
{
    private static Logger logger = LoggerFactory.getLogger(JpaListeManager.class);

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

    @Override
    public Liste findByNom(String nom)
    {
        Liste liste = null;

        try
        {
            liste = (Liste) entityManager.createQuery("select l from Liste l where l.nom = ?").setParameter(1, nom)
                    .getSingleResult();
        }
        catch (NoResultException e)
        {
            logger.warn("La liste du nom [{}] est introuvable dans la table des listes. ({})", nom, e.getMessage());
        }
        return liste;
    }

}
