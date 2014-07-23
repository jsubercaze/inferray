package fr.ujm.tse.lt2c.satin.inferray.bindings.jena;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.parser.FullFileRIOTParser;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.RulesProfile;

/**
 * Reuse some methods from {@link FullFileRIOTParser}
 *
 * @author Julien
 *
 */
public class JenaBinder {

	/**
	 * Logger
	 */
	private final Logger LOGGER = Logger.getLogger(JenaBinder.class);
	/**
	 * Mapping dictionary
	 */
	NodeDictionary dictionary;
	/**
	 * Profile : RDFS, RDFSPlus, ...
	 */
	RulesProfile profile;
	/**
	 * Main TS containing all triples
	 */
	CacheTripleStore mainTripleStore;
	/**
	 * TS containing newly added triples
	 */
	CacheTripleStore newTripleStore;
	/**
	 * The trick is to declare this final, so that JIT can optimize branching
	 */
	final boolean hasEQCEQP;

	/**
	 * Set containing rules having properties as subject
	 */
	protected Set<String> havePropertiesInSubject;
	/**
	 * Set containing rules having properties as object
	 */
	protected Set<String> havePropertiesInObject;

	/**
	 * Coupling the instances
	 */
	private final InferrayInfGraph infGraph;
	/**
	 * Inferray
	 */
	private final Inferray inferray;

	/**
	 * The rules supported. Useful for inferring at parsing
	 */

	public JenaBinder(final Inferray inferray, final RulesProfile profile,
			final InferrayInfGraph infgraph) {
		this.profile = profile;
		this.mainTripleStore = inferray.getMainTripleStore();
		this.newTripleStore = inferray.getNewTriples();
		this.hasEQCEQP = profile.hasSCMEQPSCMEQC();
		this.dictionary = new NodeDictionary(mainTripleStore);
		this.infGraph = infgraph;
		inferray.setDictionary(dictionary);
		this.inferray = inferray;
		initSets();
	}

	/**
	 * Parse a triple and add it to the triple store
	 *
	 * @param triple
	 */
	public void addTriple(final Triple triple) {
		final long[] t = encodeTriple(triple);
		/**
		 * SCM-EQC & SCM-EQP
		 */
		if (hasEQCEQP) {
			if (t[1] == AbstractDictionary.owlequivalentClass) {
				add(t[0], (int) AbstractDictionary.rdfssubClassOf, t[2]);
				add(t[2], (int) AbstractDictionary.rdfssubClassOf, t[0]);
			} else if (t[1] == AbstractDictionary.owlequivalentProperty) {
				add(t[0], (int) AbstractDictionary.rdfssubPropertyOf, t[2]);
				add(t[2], (int) AbstractDictionary.rdfssubPropertyOf, t[0]);
			}
		}
		add(t[0], (int) t[1], t[2]);
	}

	private void add(final long s, final int p, final long o) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Adding in main and new");
		}
		// Set that this property table has been modified
		infGraph.addModified(p);
		// Add to the main and to the new
		mainTripleStore.add(s, p, o);
		newTripleStore.add(s, p, o);
	}

	/**
	 * Encode a triple into a long[]
	 *
	 * @param triple
	 * @return s,p,o
	 */
	public long[] encodeTriple(final Triple triple) {
		// Add the triple into the triple
		final int hasProp = hasPropertyInObjectOrSubject(triple);
		long s = -1, o = -1;
		int p = -1;
		Node ss, ps, os;
		ss = triple.getSubject();
		os = triple.getObject();
		ps = triple.getPredicate();
		// Property will always be property
		p = dictionary.addPropertyNode(ps);
		if (hasProp != -1) {
			switch (hasProp) {
			case 1:
				s = dictionary.addPropertyNode(ss);
				o = dictionary.add(os);
				break;
			case 3:
				s = dictionary.addPropertyNode(ss);
				o = dictionary.addPropertyNode(os);
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

	/**
	 * Resync the jena graph and inferray
	 */
	public void resync() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Resyncing");
		}
		inferray.reset();
		// Add all the triples
		final ExtendedIterator<Triple> it = infGraph.getAllConcreteStatements();
		while (it.hasNext()) {
			final Triple triple = it.next();
			addTriple(triple);
		}
	}

}
