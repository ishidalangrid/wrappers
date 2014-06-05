package jp.ac.kyoto_u.i.soc.ai.langrid.webapps.opennlppostagger.test;

import static org.junit.Assert.*;

import java.io.Console;

import jp.ac.kyoto_u.i.soc.ai.langrid.webapps.eclipse.EclipseServiceContext;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;

import org.junit.Test;

public class OpenNLPPosTaggerServiceTest {

	@Test
	public void test_Analyse_Sentence() throws Exception {

		ServiceLoader serviceLoader = new ServiceLoader(new EclipseServiceContext());
		MorphologicalAnalysisService service = serviceLoader.load(
				"OpenNLPPosTaggerService",MorphologicalAnalysisService.class
				);
		
		String sentence  = "Most large cities in the US had morning and afternoon newspapers.";
		Morpheme[] result = service.analyze("en", sentence);

		for (Morpheme morpheme : result) {
			
			System.out.println(morpheme.getWord()+":"+morpheme.getPartOfSpeech());
		}
	}
}
