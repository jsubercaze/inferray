package fr.ujm.tse.lt2c.satin.inferray.correctness;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;

/**
 * Bean that contains the result of the correctness checking performed by
 * {@link CorrectnessChecker#checkForCorrectness(com.hp.hpl.jena.rdf.model.Model, com.hp.hpl.jena.rdf.model.Model)}
 * 
 * @author Julien
 * 
 */
public class CorrectnessResult {
	/**
	 * Number of statements in common
	 */
	int correctStatement;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(CorrectnessResult.class);
	/**
	 * List of the statements missing in {@link Inferray}
	 */
	List<Statement> missingStatements;
	/**
	 * List of the statements that are not in {@link Jena} but in
	 * {@link Inferray}
	 */
	List<Statement> overheadStatements;

	public CorrectnessResult() {
		super();
		this.missingStatements = new ArrayList<>();
		this.overheadStatements = new ArrayList<>();
	}

	/**
	 * 
	 * @param stmt
	 *            statement that should be in the actual model but is missing
	 */
	public void addMissingStatement(final Statement stmt) {
		this.missingStatements.add(stmt);
	}

	/**
	 * 
	 * @param stmt
	 *            statement that should not be in the actual model but is
	 *            present
	 */
	public void addOverheadStatement(final Statement stmt) {
		this.overheadStatements.add(stmt);
	}

	/**
	 * 
	 * @return the missing statements
	 */
	public List<Statement> getMissingStatements() {
		return missingStatements;
	}

	/**
	 * 
	 * @return the statement that should have not been there
	 */
	public List<Statement> getOverheadStatements() {
		return overheadStatements;
	}

	/**
	 * Declare a new statement in common
	 */
	public void addGoodStatement() {
		this.correctStatement++;
	}

	/**
	 * 
	 * @param f
	 *            File where the triples are written in N-TRIPLES
	 */
	public void dumpMissingToFile(final File f) {
		final Model model = ModelFactory.createDefaultModel();
		for (final Statement st : missingStatements) {
			model.add(st);
		}
		dumpModel(model, f);

	}

	/**
	 * 
	 * @param f
	 *            File where the triples are written in N-TRIPLES
	 */
	public void dumpOverheadToFile(final File f) {
		final Model model = ModelFactory.createDefaultModel();
		for (final Statement st : overheadStatements) {
			model.add(st);
		}
		dumpModel(model, f);

	}

	private void dumpModel(final Model model, final File f) {
		try {
			RDFDataMgr.write(new BufferedOutputStream(new FileOutputStream(f)),
					model, Lang.NTRIPLES);
		} catch (final FileNotFoundException e) {
			LOGGER.error("Unable to dump file ", e);
		}

	}

}
