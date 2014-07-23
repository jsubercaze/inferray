package fr.ujm.tse.lt2c.satin.bindings.test.jena;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.ujm.tse.lt2c.satin.inferray.bindings.jena.InferrayReasonerFactory;



public class SampleInferenceTest {
	// private static final String input = "c:/libs/inferray/geopolitical.rdf";

	private  final String input =  getClass().getResource("/ontologies/kyoto.rdf").getPath();

	// private static final String input = "c:/libs/inferray/lubm/lubm1.nt";

	@Test
	public void test() {

		// create an empty ontology model using Pellet spec
		final OntModel model = ModelFactory
				.createOntologyModel(InferrayReasonerFactory.INFERRAY_RDFSPLUS);
		//		final OntModel model = ModelFactory
		//				.createOntologyModel(OntModelSpec.RDFS_MEM_TRANS_INF);
		// read the file

		try {
			final long t1 = System.currentTimeMillis();
			final InputStream is = new FileInputStream(input);
			model.read(is, null);// , "N-TRIPLES"
			// List the countries
			final StmtIterator it = model.listStatements();
			final Set<com.hp.hpl.jena.rdf.model.Statement> set = it.toSet();
			System.out.println((System.currentTimeMillis() - t1) + "ms");
			System.out.println(set.size());

		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

	}
}
