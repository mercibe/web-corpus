package service;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import com.servicelibre.corpus.service.LigatureService;

public class LigatureServiceTest {

    @Test
    public void LigatureMotTest() {
	LigatureService service = new LigatureService();

	// Mot sans lettre ligaturée
	assertEquals("patate",service.getMotAvecLigature("patate"));
	assertEquals(" Patate ",service.getMotAvecLigature(" Patate "));
	
	// Mots qui doivent être ligaturés
	assertEquals("cœur",service.getMotAvecLigature("coeur"));
	assertEquals("cœur",service.getMotAvecLigature("cœur"));
	assertEquals(" CœuR ",service.getMotAvecLigature(" CoeuR "));
	assertEquals("ex æquo",service.getMotAvecLigature("ex aequo"));
	
	// Mots qui ne doivent pas être ligaturés
	assertEquals("moelleux",service.getMotAvecLigature("moelleux"));
	assertEquals("sundae",service.getMotAvecLigature("sundae"));
	
    }
    
    @Test
    public void LigaturePhraseTest() {
	LigatureService service = new LigatureService();

	// Phrase sans lettre ligaturée
	assertEquals("une patate douce n'est pas une patate de terre",service.getPhraseAvecLigature("une patate douce n'est pas une patate de terre"));
	assertEquals(" Une patate n'est pas une pomme de terre. ",service.getPhraseAvecLigature(" Une patate n'est pas une pomme de terre. "));
	
	// Mots qui doivent être ligaturés
	assertEquals("Un cœur d'or avec cette fille.",service.getPhraseAvecLigature("Un coeur d'or avec cette fille."));
	assertEquals("Un cœur malade était sa seule excuse.",service.getPhraseAvecLigature("Un cœur malade était sa seule excuse."));
	assertEquals(" Cœur perdu n'amasse pas mousse ",service.getPhraseAvecLigature(" Coeur perdu n'amasse pas mousse "));
	assertEquals("Ils ont terminé la course ex æquo en y mettant tout leur cœur!",service.getPhraseAvecLigature("Ils ont terminé la course ex aequo en y mettant tout leur coeur!"));
	
	// Mots qui ne doivent pas être ligaturés
	assertEquals("Qu'il est moelleux ce sundae fait avec cœur par ma sœur...",service.getPhraseAvecLigature("Qu'il est moelleux ce sundae fait avec cœur par ma soeur..."));
	
    }

}
