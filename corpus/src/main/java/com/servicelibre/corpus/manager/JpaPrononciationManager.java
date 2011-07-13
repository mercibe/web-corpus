package com.servicelibre.corpus.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.Prononciation;

@Repository
@Transactional
public class JpaPrononciationManager implements PrononciationManager
{
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<Prononciation> findAll()
    {
        return (List<Prononciation>) entityManager.createQuery("from Prononciation").getResultList();
    }

    @Override
    public Prononciation findOne(long prononciationId)
    {
        return (Prononciation) entityManager.createQuery("select p from Prononciation p where p.id = ?").setParameter(1, prononciationId).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prononciation> findByMot(Mot mot)
    {
        return (List<Prononciation>) entityManager.createQuery("SELECT p FROM Prononciation p WHERE ? MEMBER OF p.mots").setParameter(1, mot).getResultList();
    }

    @Override
    public Prononciation save(Prononciation prononciation)
    {
        entityManager.persist(prononciation);
        return prononciation;
    }
    
    @Transactional
    @Override
    public Prononciation findByPrononciation(String prononciation)
    {
	Prononciation prononc = null;
        try {
	    prononc = (Prononciation) entityManager.createQuery("SELECT p FROM Prononciation p WHERE p.prononciation = ?").setParameter(1, prononciation)
	            .getSingleResult();
	} catch (javax.persistence.NoResultException e) {
	    //Ignorer
	}
	return prononc;
    }

    @Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

    @Override
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
    

}
