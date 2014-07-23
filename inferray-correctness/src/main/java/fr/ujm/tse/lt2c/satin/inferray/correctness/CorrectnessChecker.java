package fr.ujm.tse.lt2c.satin.inferray.correctness;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;

/**
 * Check the correctness of {@link Inferray} against {@link Jena}.
 * <p>
 * The checker uses the following procedures:
 * <ol>
 * <li>Take two models as input, already loaded</li>
 * <li>List every triple from Inferray, check wether triple in Jena or not</li>
 * <li>Outputs result into file</li>
 * </ol>
 * 
 * 
 * @author Julien
 * 
 */
public class CorrectnessChecker {
	/**
	 * Hide constructor
	 */
	private CorrectnessChecker() {

	}

	public static CorrectnessResult checkForCorrectness(
			final Model modelExpected, final Model modelActual) {
		final CorrectnessResult res = new CorrectnessResult();
		// Check for missing statements
		StmtIterator statements = modelExpected.listStatements();
		while (statements.hasNext()) {
			final Statement st = statements.next();
			if (!modelActual.contains(st)) {
				res.addMissingStatement(st);
			}
		}
		statements.close();
		// Conversely check for unexpected statements
		statements = modelActual.listStatements();
		while (statements.hasNext()) {
			final Statement st = statements.next();
			if (!modelExpected.contains(st)) {
				res.addOverheadStatement(st);
			}
		}
		statements.close();
		return res;
	}

}
