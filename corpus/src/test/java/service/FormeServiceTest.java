package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.analysis.Catgram;
import com.servicelibre.corpus.analysis.MotInfo;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.corpus.service.FormeService;
import com.servicelibre.corpus.service.PrononciationService;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FormeServiceTest {
	@Autowired
	FormeService formeService;

	@Autowired
	PrononciationService prononciationService;

	@Test
	public void formeServiceTest() {

		List<MotInfo> lemmesMotInfo = formeService.getMotInfo("suis");
		assertNotNull("lemmes ne peut être null", lemmesMotInfo);
		System.out.println(lemmesMotInfo);
		assertEquals("Le candidat « suis » doit retourner 2 lemmes, suivre et être", 2, lemmesMotInfo.size());

		List<String> lemmes = formeService.getLemmes("suis");
		assertNotNull("lemmes ne peut être null", lemmes);
		System.out.println(lemmes);
		assertEquals("Le candidat « suis » doit retourner 2 lemmes, suivre et être", 2, lemmes.size());

		List<MotInfo> formesMotInfo = formeService.getFormesMotInfo("pomme");
		assertNotNull("formes ne peut être null", formesMotInfo);
		System.out.println(formesMotInfo);
		assertEquals("Le candidat pomme doit retourner 2 formes, pomme et pommes", 2, lemmesMotInfo.size());

		List<String> formes = formeService.getFormes("pomme");
		assertNotNull("formes ne peut être null", formes);
		System.out.println(formes);
		assertEquals("Le candidat pomme doit retourner 2 formes, pomme et pommes", 2, formes.size());

		System.out.println(formeService.getFormes("manger"));

	}

	/**
	 * À partir d'une liste de lemmes, construit un fichier XML complet avec catgrams et prononciations
	 * prêt à être importé via l'interface Web.
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void TestAjoutGenreNombre() throws IOException {

		CorpusService cs = new CorpusService(null);
		cs.créeFichierImportationDeLemmes("autresLemmesDuCorpus.txt", null, null, "Autres mots du corpus", new File("/tmp/autres-mots-du-corpus.xml"));

	}

}
