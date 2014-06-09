package jp.ac.kyoto_u.i.soc.ai.langrid.webapps.opennlppostagger;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.da;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.nl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.pt;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.se;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

public class OpenNLPPosTaggerService extends
		AbstractMorphologicalAnalysisService {
	
	private static final Map<String, PartOfSpeech> POS_TAG_MAP;
	private static Logger logger = Logger.getLogger(OpenNLPPosTaggerService.class.getName());

	
	/**
	 * Static Constructor
	 */
	static
	{
		//Initializing POS tag mapping between OpenNLP library and Language Grid
		//http://www.cis.upenn.edu/~treebank/
		
		POS_TAG_MAP = new HashMap<String, PartOfSpeech>();

		POS_TAG_MAP.put("XOTHER", PartOfSpeech.other); // *%$*
		POS_TAG_MAP.put("-LRB-", PartOfSpeech.other); // (
		POS_TAG_MAP.put("-RRB-", PartOfSpeech.other); // )

		POS_TAG_MAP.put("NNP", PartOfSpeech.noun_proper);
		POS_TAG_MAP.put("NNPS", PartOfSpeech.noun_proper);

		POS_TAG_MAP.put("NN", PartOfSpeech.noun_common);
		POS_TAG_MAP.put("NNS", PartOfSpeech.noun_common);
		

		POS_TAG_MAP.put("PRP", PartOfSpeech.noun_pronoun);
		POS_TAG_MAP.put("PRP$", PartOfSpeech.noun_pronoun);
		
		POS_TAG_MAP.put("NT", PartOfSpeech.noun);
		POS_TAG_MAP.put("NR", PartOfSpeech.noun);
		POS_TAG_MAP.put("VB", PartOfSpeech.verb);
		POS_TAG_MAP.put("VBD", PartOfSpeech.verb);
		POS_TAG_MAP.put("VBG", PartOfSpeech.verb);
		POS_TAG_MAP.put("VBN", PartOfSpeech.verb);
		POS_TAG_MAP.put("VBP", PartOfSpeech.verb);
		POS_TAG_MAP.put("VBZ", PartOfSpeech.verb);
		POS_TAG_MAP.put("VV", PartOfSpeech.verb);

		POS_TAG_MAP.put("JJ", PartOfSpeech.adjective);
		POS_TAG_MAP.put("JJR", PartOfSpeech.adjective);
		POS_TAG_MAP.put("JJS", PartOfSpeech.adjective);

		POS_TAG_MAP.put("RB", PartOfSpeech.adverb);
		POS_TAG_MAP.put("RBR", PartOfSpeech.adverb);
		POS_TAG_MAP.put("RBS", PartOfSpeech.adverb);
		POS_TAG_MAP.put("SYM", PartOfSpeech.unknown);
	}
	
	
	private String modelPath;
	
	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}
	
	/**
	 * Constructor
	 * @throws IOException 
	 */
	public OpenNLPPosTaggerService() throws IOException {
		modelPath = getInitParameterString("modelPath", "/srv/openlangrid/resource/OpenNLP/models");
		setSupportedLanguageCollection(Arrays.asList(en,da,de,nl,pt,se));
	}
	
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		List<Morpheme> results = new LinkedList<Morpheme>(); // a list to store POS tagging result
		String tokens[] = null;
		try {
			tokens = tokinize(text, language);
		} catch(IOException e){
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		}
		String tags[] = null;
		try {
			tags = POSTag(tokens, language);
		} catch(IOException e){
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		}
		
		for(int i=0;i<tokens.length;i++)
		{
        	String word = tokens[i];
        	String lemma = tokens[i];
        	String posText = tags[i];
        	
        	//Mapping to langrid POS tag
        	PartOfSpeech pos = POS_TAG_MAP.containsKey(posText)?POS_TAG_MAP.get(posText):PartOfSpeech.other; 
        	
        	Morpheme morpheme = new Morpheme(word, lemma, pos);
        	results.add(morpheme);
		}
       
        return results;
	}
	
	private String[] tokinize(String sentence, Language language) throws IOException
	{
		InputStream modelIn = null;
		modelIn = new FileInputStream(getTokinizerModelByLang(language));
		TokenizerModel model = new TokenizerModel(modelIn);
		Tokenizer tokenizer = new TokenizerME(model);
		String tokens[] = tokenizer.tokenize(sentence);
		if (modelIn != null)
			modelIn.close();
		return tokens;
	}
	
	private String[] POSTag(String[] tokens, Language language)  throws IOException
	{
		InputStream modelIn = null;
		modelIn = new FileInputStream(getPOSModelByLang(language));
		POSModel model = new POSModel(modelIn);
		POSTaggerME tagger = new POSTaggerME(model);
		String tags[] = tagger.tag(tokens);	  
		if (modelIn != null)
			modelIn.close();	
		return tags;
	}
	
	private String getPOSModelByLang(Language language)
	{
		String modelFileName = "en-pos-maxent.bin";
		
		if(language == en )
			modelFileName = "en-pos-maxent.bin";
		if(language == da )
			modelFileName = "da-pos-perceptron.bin";
		if(language == de )
			modelFileName = "de-pos-maxent.bin";
		if(language == nl )
			modelFileName = "nl-pos-maxent.bin";
		if(language == pt )
			modelFileName = "pt-pos-maxent.bin";
		if(language == se )
			modelFileName = "se-pos-maxent.bin";

		return modelPath + File.separator + modelFileName;
	}
	
	private String getTokinizerModelByLang(Language language)
	{
		String modelFileName = "en-token.bin";
		
		if(language == en )
			modelFileName = "en-token.bin";
		if(language == da )
			modelFileName = "da-token.bin";
		if(language == de )
			modelFileName = "de-token.bin";
		if(language == nl )
			modelFileName = "nl-token.bin";
		if(language == pt )
			modelFileName = "pt-token.bin";
		if(language == se )
			modelFileName = "se-token.bin";

		return modelPath + File.separator + modelFileName;
	}
}
