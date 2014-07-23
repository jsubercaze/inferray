package fr.ujm.tse.lt2c.satin.inferray.bindings.jena;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

public class InferrayReasonerFactory implements ReasonerFactory {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(InferrayReasonerFactory.class);
	/**
	 * Supported rules profiles mapped to {@link OntModelSpec}
	 */
	public static OntModelSpec INFERRAY_RDFSPLUS;
	public static OntModelSpec INFERRAY_RDFSDEFAULT;
	public static OntModelSpec INFERRAY_RDFS;
	public static OntModelSpec INFERRAY_RHODF;
	/**
	 * Dirty but efficient
	 */
	private static InferrayReasonerFactory instanceRDFSPLUS;
	private static InferrayReasonerFactory instanceRDFSDEFAULT;
	private static InferrayReasonerFactory instanceRDFS;
	private static InferrayReasonerFactory instanceRHODF;

	static {
		// RDFSPlus
		instanceRDFSPLUS = new InferrayReasonerFactory(
				SupportedProfile.RDFSPLUS);
		INFERRAY_RDFSPLUS = new OntModelSpec(OntModelSpec.OWL_MEM);
		INFERRAY_RDFSPLUS.setReasonerFactory(instanceRDFSPLUS);
		ReasonerRegistry.theRegistry().register(instanceRDFSPLUS);
		// RDFSDefault
		instanceRDFSDEFAULT = new InferrayReasonerFactory(
				SupportedProfile.RDFSDEFAULT);
		INFERRAY_RDFSDEFAULT = new OntModelSpec(OntModelSpec.OWL_MEM);
		INFERRAY_RDFSDEFAULT.setReasonerFactory(instanceRDFSDEFAULT);
		ReasonerRegistry.theRegistry().register(instanceRDFSDEFAULT);
		// RDFS
		instanceRDFS = new InferrayReasonerFactory(SupportedProfile.RDFS);
		INFERRAY_RDFS = new OntModelSpec(OntModelSpec.OWL_MEM);
		INFERRAY_RDFS.setReasonerFactory(instanceRDFS);
		ReasonerRegistry.theRegistry().register(instanceRDFS);
		// RHODF
		instanceRHODF = new InferrayReasonerFactory(SupportedProfile.RHODF);
		INFERRAY_RHODF = new OntModelSpec(OntModelSpec.OWL_MEM);
		INFERRAY_RHODF.setReasonerFactory(instanceRHODF);
		ReasonerRegistry.theRegistry().register(instanceRHODF);
	}

	private static final String URI = "http://www.inferray.org";

	/**
	 * Description of the capabilities of Inferray
	 */
	private Model reasonerCapabilities;

	private final SupportedProfile profile;

	public InferrayReasonerFactory(final SupportedProfile profile) {
		super();
		this.profile = profile;
	}

	@Override
	public Reasoner create(final Resource configuration) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Called create");
		}
		return new InferrayReasoner(getCapabilities(), profile);
	}

	@Override
	public Model getCapabilities() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Called getCapabilites");
		}
		if (reasonerCapabilities == null) {
			reasonerCapabilities = ModelFactory.createDefaultModel();
			final Resource base = reasonerCapabilities.createResource(URI);
			base.addProperty(ReasonerVocabulary.nameP, "Inferray Reasoner")
			.addProperty(ReasonerVocabulary.descriptionP,
					"Reasoner backed by the Inferray Reasoner")
					.addProperty(ReasonerVocabulary.supportsP, RDFS.subClassOf)
					.addProperty(ReasonerVocabulary.supportsP,
							RDFS.subPropertyOf)
							.addProperty(ReasonerVocabulary.supportsP, RDFS.range)
							.addProperty(ReasonerVocabulary.supportsP, RDFS.domain)
							.addProperty(ReasonerVocabulary.supportsP,
									ReasonerVocabulary.directSubClassOf)
									.addProperty(ReasonerVocabulary.supportsP,
											ReasonerVocabulary.directSubPropertyOf)
											.addProperty(ReasonerVocabulary.supportsP,
													ReasonerVocabulary.directRDFType);
			if (profile.equals(SupportedProfile.RDFSPLUS)) {
				base.addProperty(ReasonerVocabulary.supportsP,
						OWL.InverseFunctionalProperty)
						.addProperty(ReasonerVocabulary.supportsP,
								OWL.equivalentProperty)
								.addProperty(ReasonerVocabulary.supportsP,
										OWL.FunctionalProperty)
										.addProperty(ReasonerVocabulary.supportsP,
												OWL.inverseOf)
												.addProperty(ReasonerVocabulary.supportsP,
														OWL.SymmetricProperty)
														.addProperty(ReasonerVocabulary.supportsP,
																OWL.TransitiveProperty)
																.addProperty(ReasonerVocabulary.supportsP, OWL.Class)
																.addProperty(ReasonerVocabulary.supportsP, OWL.sameAs);
			}
		}

		return reasonerCapabilities;
	}

	@Override
	public String getURI() {
		return URI;
	}

}
