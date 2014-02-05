package jp.ac.kyoto_u.i.soc.ai.langrid.clientapps.KhmerSegmentation.unittest;
import static org.junit.Assert.*;

import org.junit.Test;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.km;

import java.net.URL;

import jp.go.nict.langrid.client.ws_1_2.ClientFactory;
import jp.go.nict.langrid.client.ws_1_2.MorphologicalAnalysisClient;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;

import org.junit.Test;

public class KhmerSegmentationServiceSoapTest {

	@Test
	public void test_Analyse_Sentence() throws Exception {
		MorphologicalAnalysisClient service = ClientFactory
				.createMorphologicalAnalysisClient(new URL(
						"http://localhost:8080/jp.go.nict.langrid.webapps.blank/services/KhmerSegmentationService"));

		String sentence = "មនុស្សប្រើប្រាស់ពេជ្រចាប់តាំងពីរាប់ពាន់ឆ្នាំមកហើយ។ ពេជ្រនៅតែតំណាងឲ្យអំណាចនិងកេរ្តិ៍ឈ្មោះ។ ជនល្បីៗនៅទូទាំងពិភពលោកសុទ្ធតែពាក់ពេជ្រ។";
		Morpheme[] result = service.analyze(km, sentence);
	}
}
