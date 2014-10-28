package fr.ujm.tse.lt2c.satin.inferray.parser;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.Parser;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.RulesProfile;

/**
 * Common method for Jena RIOT based parsers
 *
 * @author Julien Subercaze
 *
 */
public abstract class AbstractJenaParser implements Parser {
	/**
	 * usual logger
	 */
	static Logger logger = Logger.getLogger(FullFileRIOTParser.class);
	/**
	 * Properties indexing
	 */
	protected int startProperties = 0;

	/**
	 * The triple this parser will be adding triples to
	 */
	protected CacheTripleStore tripleStore;
	/**
	 * Dictionary to match
	 */
	protected NodeDictionary dictionary;
	/**
	 * Set containing rules having properties as subject
	 */
	protected Set<String> havePropertiesInSubject;
	/**
	 * Set containing rules having properties as object
	 */
	protected Set<String> havePropertiesInObject;
	/**
	 * The rules supported. Useful for inferring at parsing
	 */
	@SuppressWarnings("unused")
	private final RulesProfile rules;
	/**
	 * The trick is to declare this final, so that JIT can optimize branching
	 */
	final boolean hasEQCEQP;

	public AbstractJenaParser(final CacheTripleStore tripleStore,
			final NodeDictionary dictionary, final RulesProfile rules) {
		super();
		this.tripleStore = tripleStore;
		this.dictionary = dictionary;
		this.rules = rules;
		this.hasEQCEQP = rules.hasSCMEQPSCMEQC();
		initSets();
	}

	/**
	 * Parse a triple and add it to the triple store
	 *
	 * @param triple
	 */
	public void parseTriple(final Triple triple) {
		final long[] t = encodeTriple(triple);
		/**
		 * SCM-EQC & SCM-EQP
		 */
		if (hasEQCEQP) {
			if (t[1] == AbstractDictionary.owlequivalentClass) {
				tripleStore.add(t[0], (int) AbstractDictionary.rdfssubClassOf,
						t[2]);
				tripleStore.add(t[2], (int) AbstractDictionary.rdfssubClassOf,
						t[0]);
			} else if (t[1] == AbstractDictionary.owlequivalentProperty) {
				tripleStore.add(t[0],
						(int) AbstractDictionary.rdfssubPropertyOf, t[2]);
				tripleStore.add(t[2],
						(int) AbstractDictionary.rdfssubPropertyOf, t[0]);
			}
		}
		tripleStore.add(t[0], (int) t[1], t[2]);
	}

	/**
	 * Encode a triple into a long[]
	 *
	 * @param triple
	 * @return s,p,o
	 */
	public long[] encodeTriple(final Triple triple) {
		//if(triple.getPredicate().getURI().startsWith("http://www.w3.org/2000/01/")){
		//		System.out.println(triple);
		//		System.out.println(triple.getPredicate().getURI());
		//		try {
		//			System.in.read();
		//		} catch (final IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//}


		// Add the triple into the triple
		final int hasProp = hasPropertyInObjectOrSubject(triple);
		long s = -1, o = -1;
		int p = -1;
		String ss, ps, os;
		ss = triple.getSubject().toString();
		os = triple.getObject().toString();
		ps = triple.getPredicate().toString();

		// Property will always be property
		p = dictionary.addProperty(ps);
		if (hasProp != -1) {
			switch (hasProp) {
			case 1:
				s = dictionary.addProperty(ss);
				o = dictionary.add(os);
				break;
			case 3:
				s = dictionary.addProperty(ss);
				o = dictionary.addProperty(os);
				// Object is also property, as well as subject
				break;
			default:
				break;

			}
		} else {
			// Add a regular triple
			s = dictionary.add(ss);
			o = dictionary.add(os);
		}
		final long[] result = new long[3];
		result[0] = s;
		result[1] = p;
		result[2] = o;
		return result;
	}

	/**
	 * Check wether the triple has a property in object or subject
	 *
	 * @param triple
	 * @return
	 */
	protected int hasPropertyInObjectOrSubject(final Triple triple) {
		final String ps = triple.getPredicate().toString();
		// Check if type ... property
		if (ps.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {

			if (triple.getObject().toString().toLowerCase()
					.endsWith("property")) {

				if (triple.getObject().toString()
						.startsWith("http://www.w3.org/2002/07/owl#")) {

					return 3;
				}
			}

		}
		if (havePropertiesInObject.contains(ps)) {
			return 3;
		} else if (havePropertiesInSubject.contains(ps)) {
			return 1;
		}
		return -1;
	}

	public void setTripleStore(final CacheTripleStore tripleStore) {
		this.tripleStore = tripleStore;
	}

	/**
	 * Initialize the different sets of the parser
	 */
	protected void initSets() {
		havePropertiesInSubject = new HashSet<>();
		havePropertiesInObject = new HashSet<>();
		// For subjects
		havePropertiesInSubject.add(dictionary
				.get(AbstractDictionary.rdfsdomain));
		havePropertiesInSubject.add(dictionary
				.get(AbstractDictionary.rdfsrange));
		havePropertiesInSubject.add(dictionary
				.get(AbstractDictionary.owlequivalentProperty));
		havePropertiesInSubject.add(dictionary
				.get(AbstractDictionary.owlinverseOf));
		havePropertiesInSubject.add(dictionary
				.get(AbstractDictionary.rdfssubPropertyOf));
		havePropertiesInSubject.add(dictionary
				.get(AbstractDictionary.owlsymetricProperty));
		// Objects
		havePropertiesInObject.add(dictionary
				.get(AbstractDictionary.rdfssubPropertyOf));
		havePropertiesInObject.add(dictionary
				.get(AbstractDictionary.owlinverseOf));
		havePropertiesInObject.add(dictionary
				.get(AbstractDictionary.owlequivalentProperty));
		havePropertiesInObject.add(dictionary
				.get(AbstractDictionary.owlsymetricProperty));

	}
}
