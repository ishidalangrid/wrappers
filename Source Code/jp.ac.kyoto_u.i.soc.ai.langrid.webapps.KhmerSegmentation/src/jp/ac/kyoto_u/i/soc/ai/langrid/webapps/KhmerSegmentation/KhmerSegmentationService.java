package jp.ac.kyoto_u.i.soc.ai.langrid.webapps.KhmerSegmentation;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.km;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.springframework.util.ReflectionUtils;

import KhmerSpellCheckerED_and_KCR.ErrorWordDetector.ErrorWordDetector;
import KhmerSpellCheckerED_and_KCR.ErrorWordDetector.KhDictionary;
import KhmerSpellCheckerED_and_KCR.ErrorWordDetector.SEG_METHOD;
import KhmerSpellCheckerED_and_KCR.ErrorWordDetector.SegWord;
import KhmerSpellCheckerED_and_KCR.ErrorWordDetector.WordBigramModel;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

public class KhmerSegmentationService extends
		AbstractMorphologicalAnalysisService {

	private static ErrorWordDetector khmerworddetector;

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public KhmerSegmentationService() throws Exception {
		setSupportedLanguageCollection(Arrays.asList(km));
		// khmerworddetector = new
		// MMAModel(Integer.valueOf(KhDictionary.MIX_DICTIONARY).intValue());
		// //faster, less accurate
		if (null == khmerworddetector)
			khmerworddetector = new WordBigramModel(); // slower, more accurate
	}

	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		// invoke private analyze method
		Collection<Morpheme> results = new LinkedList<Morpheme>();
		try {
			results = analyze(text);
			return results;

		} catch (Exception e) {
			
			String errorMessage = "";
			errorMessage+=e.getMessage();
			errorMessage+="\r";
			
			StackTraceElement[] stacks = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stacks) {
				errorMessage+= stackTraceElement.toString()+"\r";
			}
			
			java.util.logging.Logger.getGlobal().log(Level.SEVERE, errorMessage);
			
			return null;
		}
	}

	private Collection<Morpheme> analyze(String text) throws Exception {
		List<Morpheme> results = new LinkedList<Morpheme>(); // a list to store
																// POS tagging
																// result
		java.util.Vector segments = khmerworddetector.getBestSegmentation(text,
				KhDictionary.MIX_DICTIONARY, SEG_METHOD.WITH_EDIT_DISTANCE);

		for (int i = 1; i < segments.size(); i++) {
			SegWord segWord = (SegWord) segments.get(i);

			String word = segWord.Word;
			String lemma = segWord.Word;
			PartOfSpeech pos = PartOfSpeech.unknown;

			Morpheme morpheme = new Morpheme(word, lemma, pos);
			results.add(morpheme);
		}

		return results;
	}

	/**
	 * Returns user preference of algorithm selection
	 * @throws ProcessFailedException 
	 */
	private static boolean getAlgorithm(String filename) throws ProcessFailedException {
		String buffer = "";
		String algo = "";
		try {
			if (checkHeader(filename)) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(filename),
								"UTF8"));
				buffer = "";
				while (((buffer = reader.readLine()) != null)
						|| (algo.equals(""))) {
					if (!buffer.equals("")) {
						if (!buffer.substring(0, 1).equals("#")) {
							algo = buffer;
						}
					}
				}
				reader.close();
				if (!algo.equals("0")) {
					return false;
				}
				return true;
			}
			return true;
		} catch (Exception ex) {
			java.util.logging.Logger.getGlobal().log(Level.ALL, ex.getMessage());
			return false;
		}
	}

	private static boolean checkHeader(String fileName) throws Exception {
		String buffer = "";
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			int v = 0;
			do {
				buffer = reader.readLine();
				if ((!buffer.equals(null))
						&& (!buffer.equals(""))
						&& (buffer.trim()
								.equalsIgnoreCase("# Author : PAN Localization Cambodia"))) {
					v = 1;
				}
			} while ((!buffer.equals(null)) && (v == 0));
			if (!buffer.equals(null)) {
				if (!buffer.equals("")) {
					if (buffer.trim().equalsIgnoreCase(
							"# Author : PAN Localization Cambodia")) {
						reader.close();
						return true;
					}
					reader.close();
					return false;
				}
				reader.close();
				return false;
			}
			reader.close();
			return false;
	}
}


