package fr.ujm.tse.lt2c.satin.inferray.bindings.jena;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerException;

import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

/**
 * Reasoner for binding with Jena
 *
 * @author Julien
 *
 */
public class InferrayReasoner implements Reasoner {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(InferrayReasoner.class);
	/**
	 * What we can do
	 */
	Model capabilities;
	/**
	 * Jena's graph
	 */
	private Graph schema;
	/**
	 * RDFS/RDFSDefault/RhoDF/RDFSPlus
	 */
	SupportedProfile profile = SupportedProfile.RDFSPLUS;

	public InferrayReasoner(final Model capabilities) {
		this.capabilities = capabilities;
	}

	public InferrayReasoner(final Graph tbox, final Model capabilities) {
		this.schema = tbox;
		this.capabilities = capabilities;
	}

	public InferrayReasoner(final Model capabilities,
			final SupportedProfile profile) {
		this.capabilities = capabilities;
		this.profile = profile;
	}

	@Override
	public Reasoner bindSchema(final Graph tbox) throws ReasonerException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Binding schema graph");
		}
		return new InferrayReasoner(tbox, capabilities);

	}

	@Override
	public Reasoner bindSchema(final Model tbox) throws ReasonerException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Binding schema model");
		}
		return bindSchema(tbox.getGraph());
	}

	@Override
	public InferrayInfGraph bind(final Graph data) throws ReasonerException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Binding graph with profile " + profile);
		}
		return new InferrayInfGraph(data, this, profile);
	}

	@Override
	public void setDerivationLogging(final boolean logOn) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setParameter(final Property parameterUri, final Object value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Model getReasonerCapabilities() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Called reasoner capabilities");
		}
		return this.capabilities;
	}

	@Override
	public void addDescription(final Model configSpec, final Resource base) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supportsProperty(final Property property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Capabilities getGraphCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

}
