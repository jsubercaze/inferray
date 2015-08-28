package fr.ujm.tse.lt2c.satin.inferray.parser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

import com.hp.hpl.jena.graph.Triple;

import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.rules.profile.RulesProfile;

/**
 * RIOT(Jena) Based parser for the {@link CacheDictionary} structure.
 * 
 * Currently specified for rho-df, may require changes for the OWL2 RL rules
 * support
 * 
 * @author Julien Subercaze
 * 
 *         Dec. 2013
 */
public class FullFileRIOTParser extends AbstractJenaParser {

	public FullFileRIOTParser(final CacheTripleStore tripleStore,
			final NodeDictionary dictionary, final RulesProfile rules) {
		super(tripleStore, dictionary, rules);
	}

	@Override
	public void parse(final String fileInput) {
		if (logger.isInfoEnabled()) {
			logger.info("Parsing " + fileInput);
		}
		final long startTime = System.currentTimeMillis();
		final PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
		final PipedRDFStream<Triple> tripleStream = new PipedTriplesStream(iter);
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Runnable parser = new Runnable() {
			@Override
			public void run() {
				// Call the parsing process.
				try {
					RDFDataMgr.parse(tripleStream, fileInput);
				} catch (final Exception e) {
					e.printStackTrace();
					logger.error("Error while parsing "+fileInput,e);
					System.exit(-1);
				}
			}
		};
		executor.submit(parser);
		while (iter.hasNext()) {
			final Triple triple = iter.next();
			parseTriple(triple);
		}
		logger.trace("Starting shutdown");
		executor.shutdown();
		try {
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Executor issue for parsin ", e);
			}

		}
		if (logger.isInfoEnabled()) {
			logger.info("Time for parsing "
					+ (System.currentTimeMillis() - startTime) + " ms");
			logger.info("Triplestore size" + tripleStore.size());
		}
		iter.close();
	}

}
