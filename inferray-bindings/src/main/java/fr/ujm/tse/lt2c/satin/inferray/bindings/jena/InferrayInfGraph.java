package fr.ujm.tse.lt2c.satin.inferray.bindings.jena;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Factory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.reasoner.BaseInfGraph;
import com.hp.hpl.jena.reasoner.FGraph;
import com.hp.hpl.jena.reasoner.Finder;
import com.hp.hpl.jena.reasoner.FinderUtil;
import com.hp.hpl.jena.reasoner.InfGraph;
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.reasoner.rulesys.FBRuleInfGraph;
import com.hp.hpl.jena.reasoner.rulesys.Functor;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
import fr.ujm.tse.lt2c.satin.inferray.configuration.ConfigurationBuilder;
import fr.ujm.tse.lt2c.satin.inferray.configuration.PropertyConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.SupportedProfile;

/**
 * <p>
 * Inference Graph for {@link Inferray}. Incremental reasoning is reasonably
 * efficient when triples are added. Deletion requires full inference.
 * 
 * </p>
 * <p>
 * Largely inspired by {@link FBRuleInfGraph}, uses a {@link FGraph} to store
 * deduced triples. Triples inferred by {@link Inferray} are synchronized with
 * this graph. Reuses existing methods to implement
 * {@link #findWithContinuation(TriplePattern, Finder)}.
 * </p>
 * 
 * @author Julien
 * 
 *         2014
 */
public class InferrayInfGraph extends BaseInfGraph implements InfGraph {

	/**
	 * The set of deduced triples, this is in addition to base triples in the
	 * fdata graph
	 */
	protected FGraph fdeductions;

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(InferrayInfGraph.class);

	/**
	 * Were some changes introduced to the graph
	 */
	boolean hasChanges;
	/**
	 * Changes contain deletion of nodes, require full recomputation
	 */
	boolean changesRequireFlush;
	/**
	 * Listen to graph changes
	 */
	InferrayGraphListener graphListener;
	/**
	 * Reasoner bound to this graph
	 */
	Inferray inferray;
	/**
	 * Binding jena to the datamodel
	 */
	private final JenaBinder binder;
	/**
	 * Counter to avoid useless rebinding
	 */
	private int previousVersion;
	/**
	 * Supported fragment
	 */
	private final SupportedProfile profile;
	/**
	 * These properties will require sorting
	 */
	Set<Integer> modified;

	public InferrayInfGraph(final Graph data, final InferrayReasoner reasoner,
			final SupportedProfile profile) {
		super(data, reasoner);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Building Infgraph");
		}
		fdata = new FGraph(data);
		this.profile = profile;
		final ConfigurationBuilder builder = new ConfigurationBuilder();
		final PropertyConfiguration config = builder.setDumpFileOnExit(false)
				.setForceQuickSort(false).setMultithread(true)
				.setThreadpoolSize(8).setAxiomaticTriplesDirectory("/tmp/")
				.setFastClosure(true).setExportTriples(true)
				.setSortingAlgorithm(SortingAlgorithm.HYBRID_IMD)
				.setRulesProfile(profile).setDumpFileOnExit(false).build();
		inferray = new Inferray(config);
		this.binder = new JenaBinder(inferray, inferray.getRulesprofile(), this);
		this.modified = new HashSet<>();
		graphListener = new InferrayGraphListener(data, inferray, binder);
		previousVersion = version;
		this.fdeductions = new FGraph(Factory.createDefaultGraph());
	}

	@Override
	public void rebind() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Rebind " + version + " previous " + previousVersion);
			LOGGER.trace("Full inference due to delete :" + changesRequireFlush);
		}
		if (previousVersion != version) {
			if (changesRequireFlush) {
				// Clear the fdeductions graph
				fdeductions.getGraph().clear();
				binder.resync();
			} else {
				// sort the required property in the triple stores, prior to any
				// inference
				for (final Integer integer : modified) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Resorting property table " + integer
								+ " " + inferray.getDictionary().get(integer));
					}
					inferray.getMainTripleStore().getbyPredicate(integer)
					.totalSortingNoDuplicate();
					inferray.getNewTriples().getbyPredicate(integer)
					.totalSortingNoDuplicate();
				}
				//
			}
			final CacheTripleStore inferredTriples = inferray.exportInference();
			inferredTriples.recount();
			addTriplesToDeductionGraph(inferredTriples);
			previousVersion = version;
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Performed inference for version " + version);
				LOGGER.trace("Inferred " + inferredTriples.size() + " triples");
			}
		}
		changesRequireFlush = false;
	}

	/**
	 * Transfer the triples inferred by {@link Inferray} into the deduced
	 * {@link FGraph}
	 * 
	 * @param inferredTriples
	 */
	private void addTriplesToDeductionGraph(
			final CacheTripleStore inferredTriples) {
		final Graph fgraph = fdeductions.getGraph();
		final Iterator<LongPairArrayList> verticalIterator = inferredTriples
				.verticalIterator();
		Node s, o;
		int counter = 0;
		final NodeDictionary dictionary = inferray.getDictionary();
		// Iterate over properties table
		while (verticalIterator.hasNext()) {
			final LongPairArrayList longPairArrayList = verticalIterator.next();
			final Node p = dictionary.getNode(NodeDictionary.SPLIT_INDEX
					- longPairArrayList.getProperty());
			// Iterate over s,o for a given p
			for (int i = 0; i < longPairArrayList.size();) {
				s = dictionary.getNode(longPairArrayList.getQuick(i++));
				o = dictionary.getNode(longPairArrayList.getQuick(i++));
				final Triple triple = new Triple(s, p, o);
				if (fgraph.contains(triple)
						|| fdata.getGraph().contains(triple)) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Contains " + triple);
					}
				} else {
					counter++;
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Added " + triple);
					}
					fgraph.add(triple);
				}

			}
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Deducted triples " + fgraph.size());
			LOGGER.trace("Already present " + fdata.getGraph().size());
			LOGGER.trace("Counter value " + counter);
		}

	}

	@Override
	public void prepare() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Prepare");
		}
		super.prepare();
	}

	@Override
	public synchronized void performAdd(final Triple t) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("add");
		}
		fdata.getGraph().add(t);
		binder.addTriple(t);
		version++;
	}

	@Override
	public void performDelete(final Triple t) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Perform delete");
		}
		changesRequireFlush = true;
		fdata.getGraph().delete(t);
		version++;
	}

	@Override
	public Graph getSchemaGraph() {
		return null;
	}

	@Override
	public ExtendedIterator<Triple> findWithContinuation(
			final TriplePattern pattern, final Finder continuation) {

		ExtendedIterator<Triple> result = null;
		if (fdata == null) {
			result = fdeductions.findWithContinuation(pattern, continuation);
		} else {
			if (continuation == null) {
				result = fdata.findWithContinuation(pattern, fdeductions);
			} else {
				result = fdata.findWithContinuation(pattern,
						FinderUtil.cascade(fdeductions, continuation));
			}
		}

		return result.filterDrop(Functor.acceptFilter);

	}

	/**
	 * 
	 * @return the fragment supported by the {@link InfGraph} : one of
	 *         {@link SupportedProfile} values
	 */
	public SupportedProfile getProfile() {
		return profile;
	}

	public void addModified(final int p) {
		this.modified.add(p);

	}

	ExtendedIterator<Triple> getAllConcreteStatements() {
		return fdata.getGraph().find(new Triple(Node.ANY, Node.ANY, Node.ANY));
	}

}
