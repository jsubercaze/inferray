package fr.ujm.tse.lt2c.satin.inferray.correctness;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import fr.ujm.tse.lt2c.satin.inferray.configuration.DefaultConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.configuration.MyConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;
import fr.ujm.tse.lt2c.satin.inferray.utils.ExportUtils;

/**
 * Various methods to check the correctness of {@link Inferray} taking
 * {@link Jena} as the ground truth.
 * 
 * When triples are missing/overhead, these latter are dumped on the file system
 * in the files: "missing.nt", "overhead.nt"
 * 
 * 
 * @author Julien
 * 
 */
public class InferrayCorrectness {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(InferrayCorrectness.class);

	/**
	 * Hide constructor
	 */
	private InferrayCorrectness() {

	}

	/**
	 * Check the correctness for RDFS FULL, i.e. follow the RDFS specification
	 * exactly, even inferring syntactic sugar.
	 * <p>
	 * Ontology must be in NT format
	 * 
	 * @param ontologyLocation
	 *            where the ontology is on disk
	 */
	public static void checkInferrayCorrectnessRDFSAndDumpToFiles(
			final String ontologyLocation) {
		// Compute the model processed by Inferray
		final MyConfiguration config = new DefaultConfiguration();
		config.setRulesProfile(SupportedProfile.RDFS);
		final Inferray infere = new Inferray(config);
		infere.parse(ontologyLocation);
		infere.process();
		final Model actualModel = ExportUtils.exportToJenaModel(infere);
		// Compute the Jena Model
		final Model intemerdiateModel = ModelFactory.createDefaultModel();
		try {
			final InputStream is = new BufferedInputStream(new FileInputStream(
					ontologyLocation));
			intemerdiateModel.read(is, null, "N-TRIPLE");//
		} catch (final FileNotFoundException e) {
			LOGGER.error("Error reading file", e);
			return;
		}
		final Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
		reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel,
				ReasonerVocabulary.RDFS_FULL);
		final InfModel expectedModel = ModelFactory.createInfModel(reasoner,
				intemerdiateModel);
		// Compute correctness checking
		final CorrectnessResult result = CorrectnessChecker
				.checkForCorrectness(expectedModel, actualModel);
		result.dumpMissingToFile(new File("missing.nt"));
		result.dumpOverheadToFile(new File("overhead.nt"));
	}
}
