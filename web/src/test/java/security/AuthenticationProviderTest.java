package security;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.entities.corpus.Rôle;
import com.servicelibre.entities.corpus.Utilisateur;
import com.servicelibre.entities.corpus.UtilisateurRôle;
import com.servicelibre.repositories.corpus.RôleRepository;
import com.servicelibre.repositories.corpus.UtilisateurRepository;
import com.servicelibre.repositories.corpus.UtilisateurRôleRepository;
import com.servicelibre.sécurité.CorpusJpaAuthentificationProvider;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticationProviderTest {

	@Autowired
	CorpusJpaAuthentificationProvider corpusJpaAuthentificationProvider;

	@Autowired
	UtilisateurRepository utilisateurRepo;

	@Autowired
	UtilisateurRôleRepository utilisateurRôleRepo;

	@Autowired
	RôleRepository rôleRepo;

	// @Rule
	// public ExpectedException exception = ExpectedException.none();

	@Test(expected = BadCredentialsException.class)
	public void authentification() {
		// créer un bean « encoder » , class=StandardPasswordEncoder
		// authentication-manager/authentication-provider/password-encoder-ref="encoder"
		StandardPasswordEncoder spe = new StandardPasswordEncoder();

		// Création d'un utilisateur
		String rawPassword = "qwerty";
		
		System.out.println(rawPassword + " = " + spe.encode(rawPassword));

		Utilisateur u = new Utilisateur("corpus", "", "", "", spe.encode(rawPassword));
		u = utilisateurRepo.save(u);
		Rôle rôle = new Rôle("ROLE_UTILISATEUR");
		rôle = rôleRepo.save(rôle);
		UtilisateurRôle utilisateurRôle = new UtilisateurRôle(u, rôle, "");
		utilisateurRôleRepo.save(utilisateurRôle);

		Utilisateur findByPseudo = utilisateurRepo.findByPseudo("corpus");

		System.out.println(findByPseudo.getId() + " - " + findByPseudo.getPseudo());

		Authentication authentification = corpusJpaAuthentificationProvider.authenticate(new UsernamePasswordAuthenticationToken(findByPseudo.getPseudo(),
				rawPassword));

		assertTrue(authentification.isAuthenticated());

		// exception.expect(BadCredentialsException.class);
		authentification = corpusJpaAuthentificationProvider.authenticate(new UsernamePasswordAuthenticationToken(findByPseudo.getPseudo(), "54321"));

	}

}
