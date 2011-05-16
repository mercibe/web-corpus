package com.servicelibre.corpus.manager;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Corpus;

@Transactional
public interface CorpusManager// extends JpaRepository<Corpus, Long>
{
    
    Corpus findOne(long corpusId);
    Corpus findByNom(String nom);
    Corpus save(Corpus corpus);
    void flush();
}
