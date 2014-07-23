package fr.ujm.tse.lt2c.satin.inferray.dictionary;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashBiMap;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;

import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;

/**
 * Similar to the internal dictionary of {@link Inferray}, stores Jena
 * {@link Node} instead of String. Avoid useless reallocation of data. Manages,
 * literal, blank and datatypes
 * 
 * @author Julien
 * 
 */
public class NodeDictionary extends AbstractDictionary {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(NodeDictionary.class);

	/**
	 * Index at which the property counter will start going downward, and the
	 * resources counter will start going upward
	 */
	public static final int SPLIT_INDEX = Integer.MAX_VALUE;

	/**
	 * Counters start at the SPLIT_INDEX
	 */
	protected long cntResources = SPLIT_INDEX;
	/**
	 * Counters start at the SPLIT_INDEX
	 */
	protected int cntProperties = SPLIT_INDEX;
	/**
	 * The main triple store
	 */
	protected final CacheTripleStore ts;
	/**
	 * Stores if there was some remaping in the process
	 */
	protected boolean remapOccured = false;
	/**
	 * Removed values due to remaping
	 */
	protected Set<Long> removed;

	/**
	 * Stores nodes
	 */
	HashBiMap<Node, Long> resourcesNode;
	/**
	 * Store properties
	 */
	HashBiMap<Node, Integer> propertiesNode;

	public NodeDictionary(final CacheTripleStore ts) {
		this.ts = ts;
		resourcesNode = HashBiMap.create();
		propertiesNode = HashBiMap.create();
		initValues();

	}

	@Override
	public long add(final String s) {
		return add(NodeFactory.createURI(s));

	}

	public int addProperty(final String s) {
		return addPropertyNode(NodeFactory.createURI(s));

	}

	public int addPropertyNode(final Node node) {
		// Check if it is already affected as a resource
		if (resourcesNode.containsKey(node)) {
			remapR2P(node);
		}
		if (propertiesNode.containsKey(node)) {
			return propertiesNode.get(node);
		} else {
			propertiesNode.put(node, --cntProperties);
		}
		return cntProperties;

	}

	public long add(final Node node) {
		if (propertiesNode.containsKey(node)) {
			return propertiesNode.get(node);
		}
		if (resourcesNode.containsKey(node)) {
			return resourcesNode.get(node);
		} else {
			resourcesNode.put(node, ++cntResources);
		}
		return cntResources;
	}

	@Override
	public String get(final long index) {
		return getNode(index).toString();
	}

	public Node getNode(final long index) {
		Node nd = null;
		if (index > SPLIT_INDEX) {
			nd = resourcesNode.inverse().get(index);
		} else {
			nd = propertiesNode.inverse().get((int) index);
			// } else {
			// nd= propertiesNode.inverse().get(SPLIT_INDEX - index);
			// }
		}
		if (nd == null) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Node null for " + index);
				// throw new IllegalArgumentException("index "+index);
			}
		}
		assert nd != null;
		return nd;
	}

	@Override
	public long get(final String s) {
		if (s == null || s.length() == 0) {
			throw new IllegalArgumentException("s must not be null nor empty");
		}
		final Node n = NodeFactory.createURI(s);
		if (propertiesNode.containsKey(n)) {
			return propertiesNode.get(n);
		} else if (resourcesNode.containsKey(n)) {
			return resourcesNode.get(n);
		} else {
			throw new IllegalArgumentException(
					"URI not found in the dictionary " + s);
		}
	}

	@Override
	public long size() {
		return (SPLIT_INDEX-cntProperties) + (cntResources-SPLIT_INDEX);
	}

	@Override
	public String printConcept(final String c) {
		// Will not be implemented FIXME remove
		return null;
	}

	/**
	 * Remap a resource as a property value
	 * 
	 * @param s
	 *            resource URI
	 */
	private void remapR2P(final Node node) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Remaping " + node + " as property");
		}
		if (!remapOccured) {
			remapOccured = true;
			// Lazy instantiation
			removed = new HashSet<>();
		}
		// Go through all properties - sort and update
		final long old = resourcesNode.get(node);
		final int p = --cntProperties;
		propertiesNode.put(node, p);
		removed.add(resourcesNode.remove(node));
		ts.replaceResourceByProperty(old, p);

	}

	protected void initValues() {

		// ---------------RDFS
		rdfsResource = add("http://www.w3.org/2000/01/rdf-schema#Resource");
		rdfsClass = add("http://www.w3.org/2000/01/rdf-schema#Class");
		rdfsDatatype = add("http://www.w3.org/2000/01/rdf-schema#Datatype");
		rdfsLiteral = add("http://www.w3.org/2000/01/rdf-schema#Literal");
		rdfsContainer = add("http://www.w3.org/2000/01/rdf-schema#Container");

		rdfsdomain = addProperty("http://www.w3.org/2000/01/rdf-schema#domain");
		rdfsrange = addProperty("http://www.w3.org/2000/01/rdf-schema#range");
		rdfssubClassOf = addProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");
		rdfssubPropertyOf = addProperty("http://www.w3.org/2000/01/rdf-schema#subPropertyOf");
		rdfsSeeAlso = addProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
		rdfsisDefinedBy = addProperty("http://www.w3.org/2000/01/rdf-schema#isDefinedBy");
		rdfsComment = addProperty("http://www.w3.org/2000/01/rdf-schema#comment");
		rdfsMember = addProperty("http://www.w3.org/2000/01/rdf-schema#member");
		rdfsContainerMembershipProperty = addProperty("http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty");
		rdfsLabel = addProperty("http://www.w3.org/2000/01/rdf-schema#label");

		// -----------------RDF

		rdfList = add("http://www.w3.org/1999/02/22-rdf-syntax-ns#List");
		rdfAlt = add("http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt");
		rdfBag = add("http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag");
		rdfSeq = add("http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq");
		rdfXMLLiteral = add("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
		rdfStatement = add("http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement");
		rdfnil = add("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");

		rdfProperty = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
		rdftype = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		rdfsubject = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#subject");
		rdfobject = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#object");
		rdfpredicate = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate");
		rdffirst = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
		rdfrest = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest");
		rdfValue = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
		rdf_1 = addProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#_1");

		// ------------------XSD

		xsdnonNegativeInteger = add("http://www.w3.org/2001/XMLSchema#nonNegativeInteger");
		xsdstring = add("http://www.w3.org/2001/XMLSchema#string");

		// ------------------OWL

		owlthing = addProperty("http://www.w3.org/2002/07/owl#Thing");
		owltransitiveProperty = addProperty("http://www.w3.org/2002/07/owl#TransitiveProperty");
		owlequivalentClass = addProperty("http://www.w3.org/2002/07/owl#equivalentClass");
		owlequivalentProperty = addProperty("http://www.w3.org/2002/07/owl#equivalentProperty");
		owlobjectProperty = addProperty("http://www.w3.org/2002/07/owl#ObjectProperty");
		owldataTypeProperty = addProperty("http://www.w3.org/2002/07/owl#DatatypeProperty");
		owlsameAs = addProperty("http://www.w3.org/2002/07/owl#sameAs");

		owlinverseOf = addProperty("http://www.w3.org/2002/07/owl#inverseOf");
		owlpropertyDisjointWith = addProperty("http://www.w3.org/2002/07/owl#propertyDisjointWith");
		owldifferentFrom = addProperty("http://www.w3.org/2002/07/owl#differentFrom");
		owlallDifferent = addProperty("http://www.w3.org/2002/07/owl#AllDifferent");
		owlallDisjointClasses = addProperty("http://www.w3.org/2002/07/owl#AllDisjointClasses");
		owlallValuesFrom = addProperty("http://www.w3.org/2002/07/owl#allValuesFrom");
		owlannotationProperty = addProperty("http://www.w3.org/2002/07/owl#AnnotationProperty");
		owlassertionProperty = addProperty("http://www.w3.org/2002/07/owl#assertionProperty");
		owlclass = add("http://www.w3.org/2002/07/owl#Class");
		owlcomplementOf = addProperty("http://www.w3.org/2002/07/owl#complementOf");
		owldisjoinWith = addProperty("http://www.w3.org/2002/07/owl#disjointWith");
		owldistinctmembers = addProperty("http://www.w3.org/2002/07/owl#distinctMembers");
		owlfunctionalProperty = addProperty("http://www.w3.org/2002/07/owl#FunctionalProperty");
		intersectionOf = addProperty("http://www.w3.org/2002/07/owl#intersectionOf");
		unionOf = addProperty("http://www.w3.org/2002/07/owl#unionOf");
		owlinverseFunctionalProperty = addProperty("http://www.w3.org/2002/07/owl#InverseFunctionalProperty");
		irreflexiveProperty = addProperty("http://www.w3.org/2002/07/owl#IrreflexiveProperty");
		maxCardinality = addProperty("http://www.w3.org/2002/07/owl#maxCardinality");
		members = addProperty("http://www.w3.org/2002/07/owl#members");
		nothing = addProperty("http://www.w3.org/2002/07/owl#Nothing");
		onClass = addProperty("http://www.w3.org/2002/07/owl#onClass");
		onProperty = addProperty("http://www.w3.org/2002/07/owl#onProperty");
		oneOf = addProperty("http://www.w3.org/2002/07/owl#oneOf");
		propertyChainAxiom = addProperty("http://www.w3.org/2002/07/owl#propertyChainAxiom");
		owlsomeValuesFrom = addProperty("http://www.w3.org/2002/07/owl#someValuesFrom ");
		sourceIndividual = addProperty("http://www.w3.org/2002/07/owl#sourceIndividual");
		owlsymetricProperty = addProperty("http://www.w3.org/2002/07/owl#SymmetricProperty");
		owltargetIndividual = addProperty("http://www.w3.org/2002/07/owl#targetIndividual");
		targetValue = addProperty("http://www.w3.org/2002/07/owl#targetValue ");
		maxQualifiedCardinality = addProperty("http://www.w3.org/2002/07/owl#maxQualifiedCardinality");

	}

	public boolean hasRemapOccured() {
		return remapOccured;
	}

	public long getCntResources() {
		return cntResources;
	}

	@Override
	public long countProperties() {
		return SPLIT_INDEX - cntProperties;
	}

	public boolean wasRemoved(final long i) {
		return removed.contains(i);
	}

	@Override
	public String toString() {
		return "NodeDictionary [cntResources=" + cntResources
				+ ", cntProperties=" + cntProperties + ", remapOccured="
				+ remapOccured + ", removed=" + removed + ", resourcesNode="
				+ resourcesNode + ", propertiesNode=" + propertiesNode + "]";
	}

}
