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
import com.servicelibre.corpus.entity.TypeDocumentSource;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.DocMetadataManager;
import com.servicelibre.corpus.manager.TypeDocumentSourceManager;

@ContextConfiguration("CorpusManagerTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TypeDocumentSourceManagerTest implements ApplicationContextAware {
	
	@Autowired
	TypeDocumentSourceManager tdsm;

	private ApplicationContext ctx;

	@Test
	@Transactional
	public void typeDocumentSourceCRUDTest() {
		
	    TypeDocumentSource tds = new TypeDocumentSource("Microsoft Word","Document Microsoft Word quelconque.  Les métadonnées sont extraites des prorpiétés du document.","");
	    
	    tdsm.save(tds);
	    
	    TypeDocumentSource tdsDB = tdsm.findOne(tds.getId());
	    
	    assertNotNull(tdsDB);
	    assertNotNull(tdsDB.getId());
	    assertNotNull(tdsDB.getNom());
	    assertEquals("Microsoft Word",tdsDB.getNom());
	    
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;

	}

}
