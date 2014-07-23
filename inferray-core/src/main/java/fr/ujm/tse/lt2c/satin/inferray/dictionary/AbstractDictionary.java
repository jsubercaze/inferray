package fr.ujm.tse.lt2c.satin.inferray.dictionary;

import fr.ujm.tse.lt2c.satin.inferray.interfaces.Dictionary;

/**
 * List the different supported and unsupported axioms from rdfs and OWL
 * 
 * @author Jules Chevalier, Julien Subercaze
 * 
 */
public abstract class AbstractDictionary implements Dictionary {
	/**
	 * For values not taken into account
	 */
	public static long wedontcare = -1;

	/**
	 * owl:allDifferent
	 */
	public static long owlallDifferent = 0;
	/**
	 * owl:allDisjointClasses
	 */
	public static long owlallDisjointClasses = 0;
	/**
	 * owl:allValuesFrom
	 */
	public static long owlallValuesFrom = 0;
	/**
	 * owl:annotationProperty
	 */
	public static long owlannotationProperty = 0;
	/**
	 * owl:assertionProperty
	 */
	public static long owlassertionProperty = 0;
	/**
	 * owl:asymetricProperty
	 */
	public static long asymetricProperty = 0;
	/**
	 * owl:Class
	 */
	public static long owlclass = 0;
	/**
	 * owl:complementOf
	 */
	public static long owlcomplementOf = 0;
	/**
	 * owl:differentFrom
	 */
	public static long owldifferentFrom = 0;
	/**
	 * owl:disjoinWith
	 */
	public static long owldisjoinWith = 0;
	/**
	 * owl:distinctmembers
	 */
	public static long owldistinctmembers = 0;
	/**
	 * owl:equivalentClass
	 */
	public static long owlequivalentClass = 0;
	/**
	 * owl:equivalentProperty
	 */
	public static long owlequivalentProperty = 0;
	/**
	 * owl:functionalProperty
	 */
	public static long owlfunctionalProperty = 0;
	/**
	 * owl:hasKey
	 */
	public static long hasKey = 0;
	/**
	 * owl:hasValue
	 */
	public static long hasValue = 0;
	/**
	 * owl:intersectionOf
	 */
	public static long intersectionOf = 0;
	/**
	 * owl:inverseFunctionalProperty
	 */
	public static long owlinverseFunctionalProperty = 0;
	/**
	 * owl:inverseOf
	 */
	public static long owlinverseOf = 0;
	/**
	 * owl:irreflexiveProperty
	 */
	public static long irreflexiveProperty = 0;
	/**
	 * owl:maxCardinality
	 */
	public static long maxCardinality = 0;
	/**
	 * owl:maxQualifiedCardinality
	 */
	public static long maxQualifiedCardinality = 0;
	/**
	 * owl:members
	 */
	public static long members = 0;
	/**
	 * owl:nothing
	 */
	public static long nothing = 0;
	/**
	 * owl:onClass
	 */
	public static long onClass = 0;
	/**
	 * owl:oneOf
	 */
	public static long oneOf = 0;
	/**
	 * owl:onProperty
	 */
	public static long onProperty = 0;
	/**
	 * owl:propertyChainAxiom
	 */
	public static long propertyChainAxiom = 0;
	/**
	 * owl:propertyDisjointWith
	 */
	public static long owlpropertyDisjointWith = 0;
	/**
	 * owl:sameAs
	 */
	public static long owlsameAs = 0;
	/**
	 * owl:someValuesFrom
	 */

	public static long owlsomeValuesFrom = 0;
	/**
	 * owl:sourceIndividual
	 */
	public static long sourceIndividual = 0;
	/**
	 * owl:symetricProperty
	 */
	public static long owlsymetricProperty = 0;
	/**
	 * owl:targetIndividual
	 */
	public static long owltargetIndividual = 0;
	/**
	 * owl:targetValue
	 */
	public static long targetValue = 0;
	/**
	 * owl:Thing
	 */
	public static long owlthing = 0;
	/**
	 * owl:transitiveProperty
	 */
	public static long owltransitiveProperty = 0;

	/**
	 * owl:unionOf
	 */
	public static long unionOf = 0;
	/**
	 * owl:hasValue
	 */
	public static long rdfsdomain = 0;
	/**
	 * owl:hasValue
	 */
	public static long rdfsrange = 0;
	/**
	 * owl:hasValue
	 */
	public static long rdfssubClassOf = 0;
	/**
	 * owl:hasValue
	 */
	public static long rdfssubPropertyOf = 0;

	public static long owlobjectProperty = 0;

	public static long owldataTypeProperty = 0;

	// --------------RDF/S Stuff here --------------------//
	/**
	 * rdf:type
	 */
	public static long rdftype = 0;

	/**
	 * rdf:label
	 */
	public static long rdflabel = 0;
	/**
	 * rdf:rest
	 */
	public static long rdfrest = 0;
	/**
	 * rdf:first
	 */
	public static long rdffirst = 0;
	/**
	 * rdf:first
	 */
	public static long rdfsubject = 0;
	/**
	 * rdf:first
	 */
	public static long rdfobject = 0;
	/**
	 * rdfs:Resource
	 */
	public static long rdfsResource = 0;
	/**
	 * rdfs:Class
	 */
	public static long rdfsClass = 0;
	/**
	 * rdf:Property
	 */
	public static long rdfProperty = 0;
	/**
	 * rdfs:ContainerMembershipProperty
	 */
	public static long rdfsContainerMembershipProperty;
	/**
	 * rdfs:Datatype
	 */
	public static long rdfsDatatype;

	/**
	 * rdfs:member
	 */
	public static long rdfsMember;
	/**
	 * rdfs:Literal
	 */
	public static long rdfsLiteral;

	public static long rdfsComment;

	public static long rdfsContainert;

	public static long rdfsLabel;

	public static long rdfsSeeAlso;

	public static long rdfStatement;

	public static long rdfsisDefinedBy;

	public static long rdfList;

	public static long rdfAlt;

	public static long rdfBag;

	public static long rdfpredicate;

	public static long rdfnil;

	public static long rdfValue;

	public static long rdfSeq;

	public static long rdfsContainer;

	public static long rdf_1;

	public static long rdfXMLLiteral;

	public static long xsdnonNegativeInteger;

	public static long xsdstring;

	@Override
	public String printConcept(final String c) {
		return "";
	}

	public abstract long countProperties();

}
