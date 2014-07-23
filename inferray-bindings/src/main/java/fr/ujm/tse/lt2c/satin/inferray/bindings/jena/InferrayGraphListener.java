package fr.ujm.tse.lt2c.satin.inferray.bindings.jena;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphListener;
import com.hp.hpl.jena.graph.Triple;

import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;

/**
 * Graph Listener that binds the two internal datastructures of Inferray and
 * Jena.
 *
 * @author Julien
 *
 */
public class InferrayGraphListener implements GraphListener {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(InferrayGraphListener.class);
	/**
	 * The graph we listen to
	 */
	private final Graph jenaGraph;
	/**
	 * The inferray reasoner bound to this graph
	 */
	private final Inferray inferray;
	/**
	 * Dictionary Binder between inferray and Jena
	 */
	private final JenaBinder binder;

	/**
	 * Deletion implies full reinference
	 */
	private boolean deletion;

	public InferrayGraphListener(final Graph g, final Inferray inferray,
			final JenaBinder binder) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Building graph listener");
		}
		this.jenaGraph = g;
		this.inferray = inferray;
		this.binder = binder;
		g.getEventManager().register(this);
	}

	@Override
	public void notifyAddTriple(final Graph g, final Triple t) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Triple added " + t);
		}
		binder.addTriple(t);
	}

	@Override
	public void notifyAddArray(final Graph g, final Triple[] triples) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAddList(final Graph g, final List<Triple> triples) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Triple list added " + triples);
		}

	}

	@Override
	public void notifyAddIterator(final Graph g, final Iterator<Triple> it) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Triple iterator");
		}

	}

	@Override
	public void notifyAddGraph(final Graph g, final Graph added) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Graph added");
		}

	}

	@Override
	public void notifyDeleteTriple(final Graph g, final Triple t) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Triple deleted");
		}
		deletion = true;

	}

	@Override
	public void notifyDeleteList(final Graph g, final List<Triple> L) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteArray(final Graph g, final Triple[] triples) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteIterator(final Graph g, final Iterator<Triple> it) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDeleteGraph(final Graph g, final Graph removed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyEvent(final Graph source, final Object value) {
		// TODO Auto-generated method stub

	}

}
