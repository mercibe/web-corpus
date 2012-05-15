package mots;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.Importation;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.repositories.corpus.ListeMotRepository;

@ContextConfiguration("MotManagerTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ListeMotTest {
	
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	ListeMotRepository listeMotRepo;

	@Before
	@Rollback(value=false)
	public void InitDB() {
		Importation importation = new Importation(applicationContext);
		
		importation.importeMots(new File("/tmp/mots.xml"));
		importation.importeCorpus("CORPUS-TEST", new File("/tmp/CORPUS-TEST.xml"));
	}
	
	@Test
	public void ListeMotTest() {
		
		List<ListeMot> findAll = listeMotRepo.findAll();

		System.err.println("listemots count = " + findAll.size());
	}


}
