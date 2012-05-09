package com.servicelibre.corpus.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.DocMetadata;

@Transactional
@Deprecated
public interface DocMetadataManager
{

	DocMetadata findOne(long docMetadataId);

	DocMetadata findByNom(String nom);

	List<DocMetadata> findByCorpusId(long corpusId);

	DocMetadata save(DocMetadata docMetadata);

}
