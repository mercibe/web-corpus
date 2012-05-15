package contextes;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.service.ContexteSet;
import com.servicelibre.corpus.service.ContexteSet.Position;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.corpus.service.FormeService;
import com.servicelibre.corpus.service.InfoCooccurrent;
import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.repositories.corpus.CorpusRepository;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ContexteSetTest {

	@Autowired
	CorpusRepository corpusRepo;

	@Autowired
	FormeService formeService;

	@Test
	public void cooccurrentTest() {
		Corpus corpus = new Corpus("Corpus de test", "Un nouveau corpus de test");
		corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");

		CorpusService cs = new CorpusService(corpusRepo, corpus);
		cs.setFormeService(formeService);

		cs.setTailleVoisinage(2);

		ContexteSet contextesSet = cs.getContextesMot("pommes");
		contextesSet.setMaxCooccurrent(15);
		Map<Position, List<InfoCooccurrent>> infoCooccurrents = contextesSet.getInfoCooccurrents();

		afficheCooccurrents(infoCooccurrents);

		contextesSet = cs.getContextesLemme("pomme");
		contextesSet.setMaxCooccurrent(15);
		infoCooccurrents = contextesSet.getInfoCooccurrents();

		afficheCooccurrents(infoCooccurrents);

		// for (Contexte contexte : contextesSet.getContextes()) {
		//
		// System.out.println("\n" + contexte + "\n");
		// }

	}

	private void afficheCooccurrents(Map<Position, List<InfoCooccurrent>> infoCooccurrents) {

		int cptLigne = 1;
		for (InfoCooccurrent infoCooccurrent : infoCooccurrents.get(Position.AVANT_APRÈS)) {
			System.out.println(cptLigne++ + ") " + infoCooccurrent.cooccurrent + " => " + infoCooccurrent);
		}

	}

}
