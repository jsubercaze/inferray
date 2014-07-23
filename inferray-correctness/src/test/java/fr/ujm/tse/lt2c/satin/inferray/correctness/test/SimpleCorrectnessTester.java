package fr.ujm.tse.lt2c.satin.inferray.correctness.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import fr.ujm.tse.lt2c.satin.inferray.correctness.CorrectnessChecker;
import fr.ujm.tse.lt2c.satin.inferray.correctness.CorrectnessResult;

/**
 * Test the correctness of the correctness checker on very simple examples.
 * 
 * 
 * @author Julien
 * 
 */
public class SimpleCorrectnessTester {
	/**
	 * Expected model
	 */
	Model expected;
	/**
	 * Actual model
	 */
	Model actual;

	@Before
	public void setUp() throws Exception {
		expected = ModelFactory.createDefaultModel();
		actual = ModelFactory.createDefaultModel();
	}

	@Test
	public void testMissing() {
		final Resource s1 = expected.createResource("http://www.gros.com/11");
		final Property p1 = expected.createProperty("http://www.gros.com/21");
		final Resource o1 = expected.createResource("http://www.gros.com/31");
		final Statement st = expected.createStatement(s1, p1, o1);
		expected.add(st);
		final CorrectnessResult correctness = CorrectnessChecker
				.checkForCorrectness(expected, actual);
		assertTrue(correctness.getMissingStatements().contains(st));

	}

	@Test
	public void testOverhead() {
		final Resource s1 = actual.createResource("http://www.gros.com/12");
		final Property p1 = actual.createProperty("http://www.gros.com/22");
		final Resource o1 = actual.createResource("http://www.gros.com/32");
		final Statement st = actual.createStatement(s1, p1, o1);
		actual.add(st);
		final CorrectnessResult correctness = CorrectnessChecker
				.checkForCorrectness(expected, actual);
		assertTrue(correctness.getOverheadStatements().contains(st));
	}

	@Test
	public void testBoth() {
		final Resource s1 = actual.createResource("http://www.gros.com/13");
		final Property p1 = actual.createProperty("http://www.gros.com/23");
		final Resource o1 = actual.createResource("http://www.gros.com/33");
		final Resource s2 = expected.createResource("http://www.gros.com/14");
		final Property p2 = expected.createProperty("http://www.gros.com/24");
		final Resource o2 = expected.createResource("http://www.gros.com/34");
		final Statement st = actual.createStatement(s1, p1, o1);
		final Statement st2 = expected.createStatement(s2, p2, o2);
		actual.add(st);
		expected.add(st2);
		final CorrectnessResult correctness = CorrectnessChecker
				.checkForCorrectness(expected, actual);
		assertTrue(correctness.getOverheadStatements().contains(st));
		assertTrue(correctness.getMissingStatements().contains(st2));
	}

	@Test
	public void testBothAndDump() {
		final Resource s1 = actual.createResource("http://www.gros.com/131");
		final Property p1 = actual.createProperty("http://www.gros.com/231");
		final Resource o1 = actual.createResource("http://www.gros.com/331");
		final Resource s2 = expected.createResource("http://www.gros.com/141");
		final Property p2 = expected.createProperty("http://www.gros.com/241");
		final Resource o2 = expected.createResource("http://www.gros.com/341");
		final Statement st = actual.createStatement(s1, p1, o1);
		final Statement st2 = expected.createStatement(s2, p2, o2);
		actual.add(st);
		expected.add(st2);
		final CorrectnessResult correctness = CorrectnessChecker
				.checkForCorrectness(expected, actual);
		assertTrue(correctness.getOverheadStatements().contains(st));
		assertTrue(correctness.getMissingStatements().contains(st2));
		final File missing = new File("missing.nt");
		final File overhead = new File("overhead.nt");
		correctness.dumpMissingToFile(missing);
		correctness.dumpOverheadToFile(overhead);
		assertTrue(missing.exists());
		assertTrue(overhead.exists());
		assertTrue(missing.length() != 0);
		assertTrue(overhead.length() != 0);
		overhead.delete();
		missing.delete();
	}

}
