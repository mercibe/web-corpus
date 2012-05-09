package com.servicelibre.corpus.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Liste;


@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
@Deprecated
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

    @SuppressWarnings("unchecked")
    @Override
    /**
     * Retourne toutes les listes de mots associées au corpus dont l'id est passé en paramètre
     */
    public List<Liste> findAllByCorpusId(long corpusId)
    {
        List<Liste> listes = new ArrayList<Liste>();
        
        try
        {
            listes = (List<Liste>) entityManager.createQuery("select l from Liste l where l.corpus.id = ? order by l.ordre, l.nom").setParameter(1, corpusId).getResultList();
        }
        catch (NoResultException e)
        {
            logger.warn("Aucune liste pour le corpus [{}]. ({})", corpusId, e.getMessage());
        }        
        
        return listes;
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public List<Liste> findPrimaireByCorpusId(long corpusId) {
		  List<Liste> listes = new ArrayList<Liste>();
	        
	        try
	        {
	        	Query nativeQuery = entityManager.createNativeQuery("select DISTINCT l.*  from liste l join  mot m on (l.id = m.liste_id)  order by l.ordre, l.nom", Liste.class);
	        	listes = nativeQuery.getResultList();
	            //listes = (List<Liste>) entityManager.createQuery("SELECT DISTINCT m.liste, m.liste.ordre FROM Mot m JOIN m.liste l WHERE l.corpus.id = ? ORDER BY l.ordre, l.nom").setParameter(1, corpusId).getResultList();
	        }
	        catch (NoResultException e)
	        {
	            logger.warn("Aucune liste primaire pour le corpus [{}]. ({})", corpusId, e.getMessage());
	        }        
	        
	        return listes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Liste> findParticularitésByCorpusId(long corpusId) {
		  List<Liste> listes = new ArrayList<Liste>();
	        
	        try
	        {
	        	
	        	Query nativeQuery = entityManager.createNativeQuery("select distinct l.*  from liste l left outer join  mot m on (l.id = m.liste_id)  where m.liste_id is null AND l.nom NOT LIKE 'Thème:%' order by l.ordre, l.nom", Liste.class);
	        	listes = nativeQuery.getResultList();
	            //listes = (List<Liste>) entityManager.createQuery("SELECT DISTINCT m.liste FROM  Mot m OUTER JOIN m.liste WHERE l.corpus.id = ? and m.liste IS NULL ORDER BY l.ordre, l.nom").setParameter(1, corpusId).getResultList();
	        }
	        catch (NoResultException e)
	        {
	            logger.warn("Aucune liste pour le corpus [{}]. ({})", corpusId, e.getMessage());
	        }        
	        
	        return listes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Liste> findThématiquesByCorpusId(long corpusId) {
		  List<Liste> listes = new ArrayList<Liste>();
	        
	        try
	        {
	        	
	        	Query nativeQuery = entityManager.createNativeQuery("select distinct l.*  from liste l left outer join  mot m on (l.id = m.liste_id)  where m.liste_id is null AND l.nom LIKE 'Thème:%' order by l.ordre, l.nom", Liste.class);
	        	listes = nativeQuery.getResultList();
	            //listes = (List<Liste>) entityManager.createQuery("SELECT DISTINCT m.liste FROM  Mot m OUTER JOIN m.liste WHERE l.corpus.id = ? and m.liste IS NULL ORDER BY l.ordre, l.nom").setParameter(1, corpusId).getResultList();
	        }
	        catch (NoResultException e)
	        {
	            logger.warn("Aucune liste pour le corpus [{}]. ({})", corpusId, e.getMessage());
	        }        
	        
	        return listes;
	}
	
	
	@Override
	public int findMaxOrdre() {
		Number n = (Number)entityManager.createQuery("select MAX(l.ordre) from Liste l").getSingleResult();
		if(n == null){
			return 0;
		}
		return n.intValue();
	}

}
