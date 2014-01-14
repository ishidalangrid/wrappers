package jp.ac.kyoto_u.i.soc.ai.langrid.clientapps.zemberekpostager.unittest;
import static org.junit.Assert.*;

import org.junit.Test;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.tr;

import java.net.URL;

import jp.go.nict.langrid.client.ws_1_2.ClientFactory;
import jp.go.nict.langrid.client.ws_1_2.MorphologicalAnalysisClient;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;

import org.junit.Test;

public class ZemberekPosTaggerServiceSoapTest {

	@Test
	public void test_Analyse_Sentence() throws Exception {
		MorphologicalAnalysisClient service = ClientFactory
				.createMorphologicalAnalysisClient(new URL(
						"http://localhost:8080/jp.go.nict.langrid.webapps.blank/services/ZemberekPosTaggerService"));

		String sentence = "Kırmızı kalemi al.";
		Morpheme[] result = service.analyze(tr, sentence);

		assertEquals(result.length, 4);
		assertEquals(result[0].getLemma(), "kırmızı");
		assertEquals(result[1].getLemma(), "kalem");
		assertEquals(result[2].getLemma(), "almak");
		assertEquals(result[3].getLemma(), ".");

		assertEquals(result[0].getPartOfSpeech().name(), "adjective");

	}
}
