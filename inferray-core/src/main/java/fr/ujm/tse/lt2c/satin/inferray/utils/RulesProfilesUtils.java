package fr.ujm.tse.lt2c.satin.inferray.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.rules.profile.RulesProfile;
import fr.ujm.tse.lt2c.satin.inferray.rules.utils.InferrayTriple;

/**
 * Some utilities for the rules profiles:
 * <ul>
 * <li>Read axiomatic triples</li>
 * <li></li>
 * </ul>
 * 
 * @author Julien
 * 
 */
public class RulesProfilesUtils {
	protected static Logger logger = Logger.getLogger(RulesProfilesUtils.class);

	/**
	 * Add the axiomatic triples contained in the file to the profile
	 * 
	 * @param profile
	 *            rules profile
	 * @param fileName
	 *            file containing the axiomatic triples
	 */
	public static void addAxiomaticTriples(final RulesProfile profile,
			final String fileName) {
		try {
			// Start with NS
			boolean namespaces = false;
			boolean triples = false;
			// Dico for NS
			final Map<String, String> ns = new HashMap<>();
			// Pattern for triples
			final Pattern p = Pattern.compile("<(.*?)>");
			// Usually very small file, no need to high performing I/O
			final BufferedReader br = new BufferedReader(new FileReader(
					new File(fileName)));
			String line;
			while ((line = br.readLine()) != null) {
				// process the line.
				if (line.equals("#Namespaces")) {
					namespaces = true;
					triples = false;
				} else if (line.equals("#Triples")) {
					namespaces = false;
					triples = true;
				} else if (namespaces) {
					final String[] _ns = line.replaceAll("\\s+", "").split(":",
							2);
					ns.put(_ns[0].trim(), _ns[1].trim());
				} else if (triples) {
					// Quick to write, unefficient in practice. Do not care,
					// small files
					final Matcher m = p.matcher(line);
					m.find();
					final String[] _subject = m.group(1).split(":", 2);
					final String subject = ns.get(_subject[0]) + _subject[1];
					m.find();
					final String[] _property = m.group(1).split(":", 2);
					final String property = ns.get(_property[0]) + _property[1];
					m.find();
					final String[] _object = m.group(1).split(":", 2);
					final String object = ns.get(_object[0]) + _object[1];
					profile.addAxiomaticTriple(new InferrayTriple(subject,
							property, object));
				}
			}
			br.close();

		} catch (final IOException e) {
			logger.error("Error parsing axiomatic triples ", e);
		}
	}
}
