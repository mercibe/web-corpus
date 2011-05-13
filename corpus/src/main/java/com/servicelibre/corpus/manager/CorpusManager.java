package com.servicelibre.corpus.manager;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Corpus;

@Transactional
public interface CorpusManager// extends JpaRepository<Corpus, Long>
{
    
    Corpus findOne(long corpusId);
    Corpus save(Corpus corpus);
}
