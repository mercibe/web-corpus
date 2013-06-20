package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.service.PrononciationService;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class PrononciationServiceTest {
	@Autowired
	PrononciationService prononciationService;

	@Test
	public void prononciationServiceTest() {

		List<String> prononciations = prononciationService.getGraphiePrononciations("pommé");
		assertNotNull("prononciations ne peut être null", prononciations);
		System.out.println("Prononciation:" + prononciations);
		assertEquals("Le candidat pommé doit retourner 1 prononciation", 1, prononciations.size());
		
		prononciations = prononciationService.getGraphiePrononciations("persil");
		assertNotNull("prononciations ne peut être null", prononciations);
		System.out.println("Prononciation:" + prononciations);
		assertEquals("Le candidat persil doit retourner 2 prononciations", 2, prononciations.size());
		

	}

}
