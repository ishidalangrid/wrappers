package jp.ac.kyoto_u.i.soc.ai.langrid.webapps.zemberekpostager.test;

import static org.junit.Assert.*;
import jp.ac.kyoto_u.i.soc.ai.langrid.webapps.eclipse.EclipseServiceContext;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;

import org.junit.Test;

public class ZemberekPosTaggerServiceTest {

	@Test
	public void test_Analyse_Sentence() throws Exception {

		ServiceLoader serviceLoader = new ServiceLoader(new EclipseServiceContext());
		MorphologicalAnalysisService service = serviceLoader.load(
				"ZemberekPosTaggerService",MorphologicalAnalysisService.class
				);
		
		String sentence  = "Kırmızı kalemi al.";
		Morpheme[] result = service.analyze("tr", sentence);

		assertEquals(result.length, 4);
		assertEquals(result[0].getLemma(), "kırmızı");
		assertEquals(result[1].getLemma(), "kalem");
		assertEquals(result[2].getLemma(), "almak");
		assertEquals(result[3].getLemma(), ".");
		
		assertEquals(result[0].getPartOfSpeech(), "adjective");
	}
}
