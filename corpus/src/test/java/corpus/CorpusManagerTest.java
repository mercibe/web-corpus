package corpus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.DocMetadata;
import com.servicelibre.repositories.corpus.CorpusRepository;
import com.servicelibre.repositories.corpus.DocMetadataRepository;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusManagerTest implements ApplicationContextAware {

	@Autowired
	DocMetadataRepository docMetadataRepo;

	@Autowired
	CorpusRepository corpusRepo;

	@SuppressWarnings("unused")
	private ApplicationContext ctx;

	@Test
	@Transactional
	public void créationCorpus() {
		Corpus corpus = new Corpus("Corpus de test", "description du corpus de test");
		// cm.save(corpus);
		corpus = corpusRepo.save(corpus);

		assertEquals(1, corpusRepo.count());
		assertEquals("Corpus de test", corpusRepo.findOne(1L).getNom());
		assertTrue(corpusRepo.exists(corpus.getId()));

		System.out.println("corpus_id: " + corpus.getId());

		DocMetadata docMetadataCatégorie = new DocMetadata("Catégorie", "La catégorie du document", "categorie", 30, corpus);
		DocMetadata docMetadataAuteur = new DocMetadata("Auteur", "L'auteur du document", "auteur", 10, corpus);
		DocMetadata docMetadataDate = new DocMetadata("Date", "Date du document", "date", 20, corpus);
		
		corpus.getMétadonnéesDoc().add(docMetadataCatégorie);
		corpus.getMétadonnéesDoc().add(docMetadataAuteur);
		corpus.getMétadonnéesDoc().add(docMetadataDate);
		
		//corpus = corpusRepo.save(corpus);
		//docMetadataCatégorie = docMetadataRepo.save(docMetadataCatégorie);

		List<DocMetadata> métadonnéesDoc = corpus.getMétadonnéesDoc();

		assertNotNull(métadonnéesDoc);
		assertEquals(3, métadonnéesDoc.size());

		for (DocMetadata métadonnéeDoc : métadonnéesDoc) {
			System.out.println("PAS TRIÉ: " + métadonnéeDoc);
		}

		// Vérifie que le tri se fait bien
		métadonnéesDoc = docMetadataRepo.findByCorpus(corpus, new Sort("ordre"));
		assertNotNull(métadonnéesDoc);
		assertEquals(3, métadonnéesDoc.size());
		int ordre = 10;
		for (DocMetadata métadonnéeDoc : métadonnéesDoc) {
			assertEquals(ordre, métadonnéeDoc.getOrdre());
			ordre +=10;
			System.out.println("TRIÉ: " + métadonnéeDoc);
		}
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;

	}

}
