package corpus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.DocMetadata;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.DocMetadataManager;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusManagerTest implements ApplicationContextAware {
	@Autowired
	CorpusManager cm;
	
	@Autowired
	DocMetadataManager dmm;

	private ApplicationContext ctx;

	@Test
	@Transactional
	public void créationCorpus() {
		Corpus corpus = new Corpus("Corpus de test", "description du corpus de test");

		cm.save(corpus);
		
		System.out.println("corpus_id: " + corpus.getId());
		
		DocMetadata docMetadata = new DocMetadata("Catégorie", "La catégorie du document", "categorie", 40, corpus);
		corpus.getMétadonnéesDoc().add(docMetadata);
		
		dmm.save(docMetadata);
		
		List<DocMetadata> métadonnéesDoc = corpus.getMétadonnéesDoc();

		assertNotNull(métadonnéesDoc);
		assertEquals(1, métadonnéesDoc.size());
		
		for(DocMetadata métadonnéeDoc : métadonnéesDoc) {
			System.out.println(métadonnéeDoc);
		}
		

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;

	}

}
