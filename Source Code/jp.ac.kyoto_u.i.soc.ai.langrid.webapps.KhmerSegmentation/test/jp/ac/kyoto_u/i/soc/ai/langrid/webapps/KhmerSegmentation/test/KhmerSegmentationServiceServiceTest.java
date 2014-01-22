package jp.ac.kyoto_u.i.soc.ai.langrid.webapps.KhmerSegmentation.test;

import static org.junit.Assert.*;
import jp.ac.kyoto_u.i.soc.ai.langrid.webapps.eclipse.EclipseServiceContext;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;

import org.junit.Test;

public class KhmerSegmentationServiceServiceTest {

	@Test
	public void test_Analyse_Sentence() throws Exception {

		ServiceLoader serviceLoader = new ServiceLoader(new EclipseServiceContext());
		MorphologicalAnalysisService service = serviceLoader.load(
				"KhmerSegmentationService",MorphologicalAnalysisService.class
				);
		
		String sentence = "មនុស្សប្រើប្រាស់ពេជ្រចាប់តាំងពីរាប់ពាន់ឆ្នាំមកហើយ។ ពេជ្រនៅតែតំណាងឲ្យអំណាចនិងកេរ្តិ៍ឈ្មោះ។ ជនល្បីៗនៅទូទាំងពិភពលោកសុទ្ធតែពាក់ពេជ្រ។";
		Morpheme[] result = service.analyze("tr", sentence);

		assertEquals(result.length, 4);
	
		assertEquals(result[0].getPartOfSpeech(), "Unknown");
	}
}
