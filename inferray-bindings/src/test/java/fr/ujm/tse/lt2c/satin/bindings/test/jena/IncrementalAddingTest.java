package fr.ujm.tse.lt2c.satin.bindings.test.jena;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

import fr.ujm.tse.lt2c.satin.inferray.bindings.jena.InferrayReasonerFactory;

public class IncrementalAddingTest {
	String NS = "urn:x-js-inferray:eg/";

	@Test
	public void test() {
		Statement s1, s2, s3, s4, s5, s6, s7, s8;
		Set<Statement> allStatements;
		// create an empty ontology model using Inferray spec - do no use RDFS,
		// axiomatic triples are such a mess
		final OntModel model = ModelFactory
				.createOntologyModel(InferrayReasonerFactory.INFERRAY_RDFSPLUS);
		// Create two resources
		final Property p = model.createProperty(NS, "p");
		final Property q = model.createProperty(NS, "q");
		final Property r = model.createProperty(NS, "r");
		final Property s = model.createProperty(NS, "s");
		final Property t = model.createProperty(NS, "t");
		final Property u = model.createProperty(NS, "u");
		/**
		 * First chain
		 */
		s1 = model.createStatement(p, RDFS.subPropertyOf, q);
		model.add(s1);
		assertEquals(1, model.size());
		s2 = model.createStatement(q, RDFS.subPropertyOf, r);
		model.add(s2);
		assertEquals(2, model.size());
		// Start inference, infer that p subpropertyOf r
		model.rebind();
		allStatements = model.listStatements().toSet();
		assertEquals(3, allStatements.size());
		// Assert it is the right statement that has been inferred
		s3 = model.createStatement(p, RDFS.subPropertyOf, r);
		assertEquals (true,model.contains(s3));

		/**
		 * Second chain
		 */
		s4 = model.createStatement(s, RDFS.subPropertyOf, t);
		model.add(s4);
		allStatements = model.listStatements().toSet();
		assertEquals(4, allStatements.size());
		s5 = model.createStatement(t, RDFS.subPropertyOf, u);
		model.add(s5);
		allStatements = model.listStatements().toSet();
		assertEquals(5, allStatements.size());
		// Start inference, infer that p subpropertyOf r
		model.rebind();
		allStatements = model.listStatements().toSet();
		assertEquals(6, allStatements.size());
		// Assert it is the right statement that has been inferred
		s6 = model.createStatement(s, RDFS.subPropertyOf, u);
		assertEquals(true,model.contains(s6));
		/**
		 * Make one chain, add r sp s
		 */
		s7 = model.createStatement(r, RDFS.subPropertyOf, s);
		model.add(s7);
		model.rebind();
		// Should have added 8 triples, total to 6+1+8 =15 check it out :
		allStatements = model.listStatements().toSet();
		assertEquals(15, allStatements.size());
		// Check one out of the 8 :
		s8 = model.createStatement(p, RDFS.subPropertyOf, u);
		assertEquals(true,model.contains(s8));
		/**
		 * Destroy the chain
		 */
		model.remove(s7);
		model.rebind();
		// Verify that the sp chain has been broken
		assertEquals(false,model
				.contains(model.createStatement(p, RDFS.subPropertyOf, s)));
		assertEquals(false,model
				.contains(model.createStatement(q, RDFS.subPropertyOf, s)));
		assertEquals(false,model
				.contains(model.createStatement(r, RDFS.subPropertyOf, s)));
		assertEquals(false,model
				.contains(model.createStatement(p, RDFS.subPropertyOf, t)));
		assertEquals(false,model
				.contains(model.createStatement(q, RDFS.subPropertyOf, t)));
		assertEquals(false,model
				.contains(model.createStatement(r, RDFS.subPropertyOf, t)));
		assertEquals(false,model
				.contains(model.createStatement(p, RDFS.subPropertyOf, u)));
		assertEquals(false,model
				.contains(model.createStatement(q, RDFS.subPropertyOf, u)));
		assertEquals(false,model
				.contains(model.createStatement(r, RDFS.subPropertyOf, u)));

	}
}
