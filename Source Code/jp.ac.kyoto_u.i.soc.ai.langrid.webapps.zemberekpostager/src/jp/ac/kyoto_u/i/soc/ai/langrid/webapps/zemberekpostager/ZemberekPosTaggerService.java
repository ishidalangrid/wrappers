package jp.ac.kyoto_u.i.soc.ai.langrid.webapps.zemberekpostager;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.tr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;
import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.apps.TurkishSentenceParser;
import zemberek.morphology.parser.SentenceMorphParse;

public class ZemberekPosTaggerService extends
		AbstractMorphologicalAnalysisService {
	
	private static TurkishSentenceParser sentenceParser;
	private static TurkishMorphParser morphParser;
	private static Z3MarkovModelDisambiguator disambiguator;
	private static Boolean enableDisambiguate = true; // Switch of using Disambiguation.
	static final Map<String, PartOfSpeech> POS_TAG_MAP;
	
	/**
	 * Static Constructor
	 */
	static
	{
		//Initializing POS tag mapping between zemberek library and Language Grid
		//see detail: PrimaryPos.java	\zemberek-nlp-master\zemberek-nlp-master\core\src\main\java\zemberek\core\turkish
		
		POS_TAG_MAP = new HashMap<String, PartOfSpeech>();
		POS_TAG_MAP.put("Noun", PartOfSpeech.verb);
		POS_TAG_MAP.put("Adj", PartOfSpeech.adjective);
		POS_TAG_MAP.put("Adv", PartOfSpeech.adverb);
		POS_TAG_MAP.put("Pron", PartOfSpeech.noun_pronoun);
		
		POS_TAG_MAP.put("Num", PartOfSpeech.other);
		POS_TAG_MAP.put("Det", PartOfSpeech.other);
		POS_TAG_MAP.put("Postp", PartOfSpeech.other);
		POS_TAG_MAP.put("Ques", PartOfSpeech.other);
		POS_TAG_MAP.put("Dup", PartOfSpeech.other);
		POS_TAG_MAP.put("Punc", PartOfSpeech.other);
		
		POS_TAG_MAP.put("Unk", PartOfSpeech.unknown);
	}
	
	
	/**
	 * Constructor
	 * @throws IOException 
	 */
	public ZemberekPosTaggerService() throws IOException {
		setSupportedLanguageCollection(Arrays.asList(tr));
		morphParser = morphParser == null? TurkishMorphParser.createWithDefaults():morphParser;
        disambiguator = disambiguator==null? new Z3MarkovModelDisambiguator():disambiguator;
        sentenceParser = sentenceParser==null? new TurkishSentenceParser(morphParser,disambiguator):sentenceParser;
	}
	
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		//invoke private analyze method
		Collection<Morpheme> results =  analyze(text);
		//analyzing complete; returning result
		return results;
	}
	
	private Collection<Morpheme> analyze(String text)
	{
		List<Morpheme> results = new LinkedList<Morpheme>(); // a list to store POS tagging result
		SentenceMorphParse sentenceParse = sentenceParser.parse(text);
		sentenceParser.parse(text);
		if(enableDisambiguate)
			sentenceParser.disambiguate(sentenceParse);
		
        for (SentenceMorphParse.Entry entry : sentenceParse) {
            //Obtaining POS information of each word (token)
        	String word = entry.input;
        	String lemma = entry.parses.get(0).getLemma();
        	String posText = entry.parses.get(0).getPos().shortForm;
        	//Map to langrid POS tag
        	PartOfSpeech pos = POS_TAG_MAP.containsKey(posText)?POS_TAG_MAP.get(posText):PartOfSpeech.unknown; 
        	
        	Morpheme morpheme = new Morpheme(word, lemma, pos);
        	results.add(morpheme);
        }
        
        return results;
	}
}
