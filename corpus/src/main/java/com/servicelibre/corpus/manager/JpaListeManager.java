package com.servicelibre.corpus.manager;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.Mot;

@Repository
public class JpaListeManager implements ListeManager
{

    private EntityManager entityManager;
    
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    
    @Override
    public Map<Integer, Liste> getListes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Mot> getMots(int listeId)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Liste getListe(int listeId)
    {
        return (Liste) entityManager
        .createQuery("select l from Liste l where l.id = ?")
        .setParameter(1, listeId)
        .getSingleResult();
    }

    @Override
    @Transactional
    public Liste save(Liste liste)
    {
        entityManager.persist(liste);
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMaxMots(int maxMots)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public int getMaxMots()
    {
        // TODO Auto-generated method stub
        return 0;
    }



}
