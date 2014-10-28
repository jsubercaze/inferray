package fr.ujm.tse.lt2c.satin.inferray.reasoner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import fr.ujm.tse.lt2c.satin.inferray.configuration.DefaultConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.configuration.InferrayConfiguration;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.transitive.FastTransitiveClosure;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.parser.FullFileRIOTParser;
import fr.ujm.tse.lt2c.satin.inferray.rules.AbstractFastRule;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SCO;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SCO_EQC2;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SPO;
import fr.ujm.tse.lt2c.satin.inferray.rules.impl.FCB_SCM_SPO_EQP2;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.RulesProfile;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.RulesProfileFactory;
import fr.ujm.tse.lt2c.satin.inferray.triplestore.SortedCacheObliviousTripleStore;

/**
 * Reasoner :
 * <ul>
 * <li>Very fast reasoning using cache friendly memory patterns</li>
 * <li>Integration with Jena</li>
 * </ul>
 * 
 * The principle of <code>Inferray</code> is the following. At each sequence of
 * rules execution :
 * 
 * <pre>
 *                   newTriples
 *                       |
 *                       |
 *                      \/
 * mainTripleStore---->[Inference]----> outPutTriples
 * 
 * newTriples <-------- outputTriples \ mainTripleStore
 * mainTripleStore <--- newTriples U mainTripleStore
 * </pre>
 * 
 * Then
 * 
 * 
 * 
 * See {@link #process()} for more details.
 * <p>
 * 
 * <ol>
 * <li>All rules are fired</li>
 * <li>newTriples is cleared, while memory allocation is maintained to avoid
 * reallocation</li>
 * <li>outputTriples and mainTripleStore are merged into mainTripleStore. see
 * {@link #updateTripleStores()}</li>
 * <li>newTriples contains only new inferred triples</li>
 * </ol>
 * </p>
 * 
 * @author Julien Subercaze
 * 
 * 
 * 
 */
public class Inferray {
	/**
	 * CUTOFF for fast closure. Not a parameter, too easy to crash. Just don't,
	 * really.
	 */
	private static final int CLOSURE_LIMIT = 50000;
	/**
	 * Logger
	 */
	protected static Logger logger = Logger.getLogger(Inferray.class);
	/**
	 * Main triple store, that will store all the inferred triples
	 */
	protected CacheTripleStore mainTripleStore;
	/**
	 * Auxiliary triple store, contains new triples inferred at the previous
	 * execution of rules.
	 * 
	 * For the first rule execution, {@code mainTripleStore} is used
	 */
	protected CacheTripleStore newTriples;

	/**
	 * Second auxiliary triple store, contains inferred triples
	 */
	protected CacheTripleStore outputTriples;

	/**
	 * Buffer for new Triples with Jena
	 */
	protected CacheTripleStore exportTriples;

	/**
	 * Stores the rules for rhoDF
	 */
	protected final AbstractFastRule[] rules;
	/**
	 * Rules profile
	 */
	protected final RulesProfile rulesprofile;

	/**
	 * Dictionary mapping URI to numerical values
	 */
	protected NodeDictionary dictionary;
	/**
	 * Parser used to parse the input resource
	 */
	protected FullFileRIOTParser parser;
	/**
	 * Global configuration
	 */
	protected InferrayConfiguration config;
	/**
	 * Number of iteration performed
	 */
	protected int iteration = 0;
	/**
	 * Number of inferred triples
	 */
	protected long inferredTriples = 0;
	/**
	 * Infer with stupid rules such as rdfs4a/4b and other stupidities entered
	 * by ****** logicians who never coded anything
	 */
	protected boolean inferStupidRules = false;
	/**
	 * Triples at the beginning
	 */
	private long initialTriples;
	/**
	 * Total triples at the end
	 */
	private long totalTriples;
	/**
	 * Was it possible to execute the fast closure for subclass
	 */
	private boolean fastSubClassSuccess;
	/**
	 * Was it possible to execute the fast closure for subclass
	 */
	private boolean fastSubPropertySuccess;
	/**
	 * Time required for inference in ms
	 */
	private long inferenceTime;

	/**
	 * Empty constructor, for test purpose only
	 */
	public Inferray() {
		this(new DefaultConfiguration());
	}

	/**
	 * Minimal constructor.
	 */
	public Inferray(final InferrayConfiguration configuration) {
		parser = null;
		config = configuration;
		mainTripleStore = new SortedCacheObliviousTripleStore(
				config.getMinimalPropertyNumber(),
				this.config.getSortingAlgorithm());
		this.dictionary = new NodeDictionary(mainTripleStore);
		newTriples = new SortedCacheObliviousTripleStore(
				config.getMinimalPropertyNumber(),
				this.config.getSortingAlgorithm());
		outputTriples = new SortedCacheObliviousTripleStore(
				config.getMinimalPropertyNumber(),
				this.config.getSortingAlgorithm());
		rulesprofile = RulesProfileFactory.getProfileInstance(
				config.getRulesProfile(), dictionary, mainTripleStore,
				newTriples, outputTriples, config);
		rules = rulesprofile.getRules();
		if (configuration.exportSupport()) {
			exportTriples = new SortedCacheObliviousTripleStore(
					config.getMinimalPropertyNumber(),
					this.config.getSortingAlgorithm());
		}
	}

	/**
	 * Minimal constructor.
	 */
	public Inferray(final NodeDictionary dictionary,
			final InferrayConfiguration configuration) {
		parser = null;
		this.dictionary = dictionary;

		config = configuration;
		mainTripleStore = new SortedCacheObliviousTripleStore(
				config.getMinimalPropertyNumber(),
				this.config.getSortingAlgorithm());
		newTriples = new SortedCacheObliviousTripleStore(
				config.getMinimalPropertyNumber(),
				this.config.getSortingAlgorithm());
		outputTriples = new SortedCacheObliviousTripleStore(
				config.getMinimalPropertyNumber(),
				this.config.getSortingAlgorithm());
		rulesprofile = RulesProfileFactory.getProfileInstance(
				config.getRulesProfile(), dictionary, mainTripleStore,
				newTriples, outputTriples, config);
		rules = rulesprofile.getRules();
	}

	/**
	 * Minimal constructor.
	 */
	public Inferray(final NodeDictionary dictionary) {
		this(dictionary, new DefaultConfiguration());

	}

	/**
	 * Parse the ontology at the given location
	 * 
	 * @param ontology
	 *            location of the file
	 */
	public void parse(final String ontology) {
		final File f = new File(ontology);

		if (!f.exists()) {
			throw new RuntimeException("File " + f.getAbsolutePath()
					+ " not found");
		}
		if (parser == null) {
			parser = new FullFileRIOTParser(mainTripleStore, dictionary,
					rulesprofile);
		}
		parser.parse(ontology);
		mainTripleStore.recount();
		initialTriples = mainTripleStore.size();
	}

	/**
	 * <p>
	 * Process the ontology specified in the constructor, and starts the
	 * inference process
	 * </p>
	 * <p>
	 * Final result is stored in {@code mainTripleStore}
	 * </p>
	 * 
	 * @throws IOException
	 */
	public void process() {
		final long t1 = System.nanoTime();
		// First inference cycle
		startup();
		if (logger.isTraceEnabled()) {
			outputToFile(iteration);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("1st NewTriples " + newTriples.size());
		}
		// If fast transitity, check if equivalent class must still be run
		// (otherwise will be done in 2nd round)
		// if (newTriples.isEmpty() && rulesprofile.hasSCMEQPSCMEQC()) {
		// for (int i = 0; i < rules.length; i++) {
		// final AbstractFastRule rule = rules[i];
		// if (config.isFastClosure()) {
		// if ((rulesprofile.hasSubClassClosure() && fastSubClassSuccess)
		// || (rulesprofile.hasSubPropertyClosure() && fastSubPropertySuccess))
		// {
		// if (rule instanceof FCB_SCM_SCO_EQC2
		// || rule instanceof FCB_SCM_SPO_EQP2) {
		// rule.fire();
		// }
		// }
		// }
		// }
		// // Merge
		// updateTripleStores();
		// } else {
		// System.out.println("non");
		// }

		// Fail fast
		if (newTriples.isEmpty()) {
			inferenceTime = (System.nanoTime() - t1) / 1_000_000;
			mainTripleStore.recount();
			totalTriples = mainTripleStore.size();
			displayInfo(0);
			return;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Inferred " + newTriples.size());
			}
		}
		// Prepare for steady processing
		prepareSteadyProcessing();

		// mainTripleStore.recount();
		// logger.info("Size " + mainTripleStore.size());
		// Start fix point iteration
		fixPointInference();
		// That's all folks
		inferenceTime = (System.nanoTime() - t1) / 1_000_000;
		mainTripleStore.recount();
		totalTriples = mainTripleStore.size();
		// Dump log

		displayInfo(iteration);
		// Dump for trace
		if (logger.isTraceEnabled()) {
			outputToFile(iteration);
		}
		if (config.isDumpFileOnExit()) {
			outputToFile(0);
		}

	}

	/**
	 * Iterate until fix point is reached
	 */
	private void fixPointInference() {
		do {
			iteration++;
			steadyInferenceCycle();
			mainTripleStore.recount();
			if (exportTriples != null) {
				logger.info("Fixpoint : Size " + iteration + " _ "
						+ exportTriples.size());
			}
		} while (!newTriples.isEmpty());
		// Finalization may be required, depending on rule profiles
		finalization();

	}

	private void startup() {
		mainTripleStore
		.sort(config.isMultithread(), config.getThreadpoolSize());
		// For model binding, may call startup more than once
		newTriples.clear();
		//
		if (logger.isDebugEnabled()) {
			logger.debug("Size after parsing");
			logger.debug("Main " + mainTripleStore.size());
			logger.debug("New " + newTriples.size());
			logger.debug("Output " + outputTriples.size());

		}
		if (logger.isTraceEnabled()) {
			logger.trace(mainTripleStore);
			outputToFile(iteration);
		}

		if (config.isFastClosure()) {
			logger.info("Configurated for fast closure");
			if (rulesprofile.hasSubClassClosure()) {
				logger.info("Fast closure is activated in the profile");
				if (logger.isDebugEnabled()) {
					logger.debug(mainTripleStore
							.getbyPredicate((int) AbstractDictionary.rdfssubClassOf) == null);
				}
				if (mainTripleStore
						.getbyPredicate((int) AbstractDictionary.rdfssubClassOf) != null
						&& mainTripleStore.getbyPredicate(
								(int) AbstractDictionary.rdfssubClassOf).size() < CLOSURE_LIMIT) {
					try {
						logger.info("Fast closure starting");
						@SuppressWarnings("static-access")
						final FastTransitiveClosure fc = new FastTransitiveClosure(
								mainTripleStore,
								(int) dictionary.rdfssubClassOf,
								config.exportSupport(), exportTriples);
						fc.computeTransitiveClosure(this.config.getSortingAlgorithm());
						fastSubClassSuccess = true;
					} catch (final Exception e) {
						logger.error("Exception in closure ", e);
						fastSubClassSuccess = false;
					}
				} else {
					fastSubClassSuccess = true;
				}
			}
			if (rulesprofile.hasSubPropertyClosure()) {
				if (mainTripleStore
						.getbyPredicate((int) AbstractDictionary.rdfssubPropertyOf) != null
						&& mainTripleStore.getbyPredicate(
								(int) AbstractDictionary.rdfssubPropertyOf)
								.size() < CLOSURE_LIMIT) {
					try {
						@SuppressWarnings("static-access")
						final FastTransitiveClosure fc = new FastTransitiveClosure(
								mainTripleStore,
								(int) dictionary.rdfssubPropertyOf,
								config.exportSupport(), exportTriples);
						fc.computeTransitiveClosure(this.config.getSortingAlgorithm());
						fastSubPropertySuccess = true;
					} catch (final Exception e) {
						logger.error("Exception in closure ", e);
						fastSubPropertySuccess = false;
					}
				} else {
					fastSubPropertySuccess = true;
				}

			}
		}
		// Initialize rules and start first round
		mainTripleStore.recount();
		logger.info("Size after recounting " + mainTripleStore.size());
		// Start inference
		firstInferenceCycle();
		// Update number of iteration
		iteration = 1;
		// Update the rules with the newtriplestore as secondary input

	}

	/**
	 * Inference process that support exports to external Triple Store, SAIL, or
	 * whatsoever
	 * 
	 * @return the new triples inferred
	 */
	public CacheTripleStore exportInference() {
		// Clear what has been previously inferred
		exportTriples.clear();
		// Infer new triples
		if (iteration == 0) {
			startup();
		}
		fixPointInference();
		return exportTriples;
	}

	/**
	 * Set Inferray to perform full inference at the next call to
	 * {@link #exportInference()}
	 */
	public void reset() {
		iteration = 0;
		this.mainTripleStore.clear();
		this.exportTriples.clear();
	}

	/**
	 * Some rules requires finalization
	 */
	private void finalization() {
		rulesprofile.finalization();
	}

	/**
	 * Start a full iteration of inference
	 */
	public void executeInferenceIteration() {
		if (iteration == 0) {
			firstInferenceCycle();
		} else {
			steadyInferenceCycle();
		}
	}

	/**
	 * Steady state inference cycle.
	 */
	protected void steadyInferenceCycle() {
		if (logger.isDebugEnabled()) {
			logger.debug("Inferred " + newTriples.size());
		}
		for (int i = 0; i < rules.length; i++) {
			inferredTriples += rules[i].fire();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("NewTriples " + newTriples);
		}
		// Update the stores
		if (logger.isTraceEnabled()) {
			outputToFile(-iteration);
		}

		updateTripleStores();

		++iteration;
		if (logger.isTraceEnabled()) {
			outputToFile(iteration);
			logger.trace("NewTriples before recount " + newTriples.size());
		}
		newTriples.recount();
		if (logger.isTraceEnabled()) {
			logger.trace("NewTriples after recount " + newTriples.size());
			logger.trace(newTriples);
		}
	}

	/**
	 * For the first inference, main and new are set to mainTripleStore, for the
	 * fix point iteration, new is set to newTriples
	 */
	protected void prepareSteadyProcessing() {
		for (int i = 0; i < rules.length; i++) {
			final AbstractFastRule rule = rules[i];
			rule.setNewTripleStore(newTriples);
		}
	}

	/**
	 * Add into Jena Model and use Jena to dump on file system
	 */
	protected void outputToFile(final int iteration) {
		// Create an empty model.
		final Model model = ModelFactory.createDefaultModel();
		// Add all the triples to the model
		final int maxP = (int) this.dictionary.countProperties();
		// Iterate over properties
		for (int i = 1; i < maxP; i++) {
			final LongPairArrayList lp = mainTripleStore
					.getbyPredicateRawIndex(i);
			if (lp == null || lp.size() == 0) {
				continue;
			}
			final Property p = model.getProperty(this.dictionary
					.get(NodeDictionary.SPLIT_INDEX - i));
			// Add each triple
			for (int j = 0; j < lp.size(); j++) {
				final long s = lp.getQuick(j);
				final long o = lp.getQuick(++j);
				try {
					final Resource rs = model.getResource(this.dictionary
							.get(s));
					final Resource ro = model.getResource(this.dictionary
							.get(o));
					model.add(rs, p, ro);
				} catch (final Exception e) {
					logger.error("Error dumping to fs", e);
				}
			}
		}
		// Write on FS
		try {
			final OutputStream os = new FileOutputStream("logs/inferray_"
					+ iteration + ".nt");
			model.write(os, "N-TRIPLE");
			os.close();
		} catch (final Exception e) {
			logger.error("Exception while dumping file", e);
		}

	}

	/**
	 * Display basic stats about the reasoning process
	 * 
	 * @param iteration
	 *            number of iterations
	 * @param inferred
	 *            number of inferred triples
	 * @param t1
	 *            starting time
	 * @param startingSize
	 *            number of triples after parsing
	 */
	protected void displayInfo(final long iteration) {
		if (logger.isInfoEnabled()) {
			logger.info(iteration + " iterations - Inference time "
					+ inferenceTime + " ms");
			logger.info("Inferred " + (totalTriples - initialTriples) + " in "
					+ iteration + " iterations");
		}

	}

	/**
	 * Execute the first round of inference
	 */
	protected final void firstInferenceCycle() {
		int first = 0;
		for (int i = 0; i < rules.length; i++) {
			final AbstractFastRule rule = rules[i];
			if (config.isFastClosure()) {
				if (rulesprofile.hasSubClassClosure() && fastSubClassSuccess) {
					if (rule instanceof FCB_SCM_SCO_EQC2
							|| rule instanceof FCB_SCM_SCO) {
						logger.info("Skipped subclassof closure");
						continue;
					}
				}
				if (rulesprofile.hasSubPropertyClosure()
						&& fastSubPropertySuccess) {
					if (rule instanceof FCB_SCM_SPO_EQP2
							|| rule instanceof FCB_SCM_SPO) {
						logger.info("Skipped subproperty closure");
						continue;
					}
				}
			}
			logger.debug("Firing rule " + rule.getRuleName());
			first += rule.fire();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("First round " + first + " triples inferred");
		}
		// Update and merge the three triples store

		updateTripleStores();

		++iteration;
		inferredTriples += first;
		// Update the rules to change the usable triple store
		// prepareSteadyProcessing();
	}

	/**
	 * Merges the different triples stores after the execution of a sequence of
	 * rules.
	 * <ol>
	 * <li>Clears {@code newTriples}</li>
	 * <li>Removes duplicates and sort {@code outpuTriples}</li>
	 * <li>Add all the triples from {@code outputTriples} to
	 * {@code mainTripleStore}</li>
	 * <li>Duplicates from {@code outputTriples and mainTriples} are removed
	 * from {@code outputTriples} which is then transferred to
	 * {@code newTriples}</li>
	 * </ol>
	 * Memory allocation from previous state of {@code newTriples} and
	 * {@code outpuTriples} is maintained to avoid unnecessary reallocation.
	 */
	public void updateTripleStores() {
		newTriples.clear();
		final int threadPoolSize = config.isMultithread() ? config
				.getThreadpoolSize() : 1;
				final ExecutorService es = Executors.newFixedThreadPool(threadPoolSize);
				if (logger.isInfoEnabled()) {
					logger.info("Starting update with " + threadPoolSize + " threads");
				}
				// Sort the triples stores
				// Process property per property to save some cache misses
				if (logger.isTraceEnabled()) {
					logger.trace("");
					logger.trace("-------------- Updating Triple Stores after iteration- Multithreaded -------- ");
					logger.trace("");
				}
				final int maxOutputProperty = outputTriples.getMaxActiveProperty();
				for (int i = 0; i <= maxOutputProperty; i++) {
					es.submit(new UpdatingThread(i, mainTripleStore, newTriples,
							outputTriples, exportTriples, config.exportSupport(),
							dictionary));
				}

				es.shutdown();
				try {
					es.awaitTermination(1, TimeUnit.HOURS);
				} catch (final InterruptedException e) {
					if (logger.isDebugEnabled()) {
						logger.error("Exception updating triple store", e);
					}
				}
				if (logger.isTraceEnabled()) {
					logger.trace("");
					logger.trace("-------------- End of Update --- ");
					logger.trace("");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Size of new triples after update "
							+ newTriples.size());
				}
	}

	/**
	 * 
	 * @return the main triple store
	 */
	public CacheTripleStore getMainTripleStore() {
		return mainTripleStore;
	}

	/**
	 * 
	 * @param mainTripleStore
	 *            the main triple store
	 */
	public void setMainTripleStore(final CacheTripleStore mainTripleStore) {
		this.mainTripleStore = mainTripleStore;
	}

	/**
	 * 
	 * @return the triple store containing inferred triples
	 */
	public CacheTripleStore getNewTriples() {
		return newTriples;
	}

	public void setNewTriples(final CacheTripleStore newTriples) {
		this.newTriples = newTriples;
	}

	public CacheTripleStore getOutputTriples() {
		return outputTriples;
	}

	public void setOutputTriples(final CacheTripleStore outputTriples) {
		this.outputTriples = outputTriples;
	}

	/**
	 * 
	 * @return the dictionary that maps {@link Node} to {@link Long}
	 */
	public NodeDictionary getDictionary() {
		return dictionary;
	}

	/**
	 * 
	 * @return number of triples inferred
	 */
	public long getInferredTriples() {
		return Math.max(totalTriples - initialTriples, 0);
	}

	/**
	 * 
	 * @return number of triples after parsing (last file)
	 */
	public long getInitialTriples() {
		return initialTriples;
	}

	/**
	 * 
	 * @return number of triples in the main triple store
	 */
	public long getTotalTriples() {
		return totalTriples;
	}

	/**
	 * 
	 * @return time (ns) required to perform inference
	 */
	public long getInferenceTime() {
		return inferenceTime;
	}

	/**
	 * 
	 * @return the ruleset that is used to perform inference
	 */
	public RulesProfile getRulesprofile() {
		return rulesprofile;
	}

	public void setDictionary(final NodeDictionary dictionary) {
		this.dictionary = dictionary;
	}

}
