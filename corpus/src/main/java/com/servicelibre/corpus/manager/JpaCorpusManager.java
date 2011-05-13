package com.servicelibre.corpus.manager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Corpus;

@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaCorpusManager implements CorpusManager
{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Corpus findOne(long corpusId)
    {
        return (Corpus) entityManager.createQuery("select c from Corpus c where c.id = ?").setParameter(1, corpusId)
                .getSingleResult();
    }

    @Override
    public Corpus save(Corpus corpus)
    {
        entityManager.persist(corpus);
        return corpus;
    }

}
