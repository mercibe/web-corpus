package com.servicelibre.corpus.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.CatégorieListe;
import com.servicelibre.corpus.entity.Liste;

@Transactional(readOnly = true)
public interface CatégorieListeManager // extends JpaRepository<CatégorieListe,
				       // Long>
{

    CatégorieListe findOne(long catégorieListeId);

    CatégorieListe findByNom(String nom);

    CatégorieListe save(CatégorieListe catégorieListe);
    
    List<CatégorieListe> findAllByCorpusId(long corpusId);

    List<Liste> getListes(CatégorieListe catégorie);
    
    void flush();

}
