package com.servicelibre.corpus.manager;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.TypeDocumentSource;

@Transactional(readOnly = true)
@Deprecated
public interface TypeDocumentSourceManager // extends JpaRepository<TypeDocumentSourceManager,
				       // Long>
{

    TypeDocumentSource findOne(long typeDocumentSourceId);

    TypeDocumentSource findByNom(String nom);

    TypeDocumentSource save(TypeDocumentSource typeDocumentSource);

    void flush();
}
