package fr.ujm.tse.lt2c.satin.inferray.utils;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;

public class ExportUtils {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger.getLogger(ExportUtils.class);

	public static Model exportToJenaModel(final Inferray inferray) {
		final CacheTripleStore mainTs = inferray.getMainTripleStore();
		final Iterator<LongPairArrayList> verticalIterator = mainTs
				.verticalIterator();
		Node s, o;
		final Model model = ModelFactory.createDefaultModel();
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
				if (s == null || o == null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Null subject on value " + i);
						LOGGER.debug("longPairArrayList");
					}
					continue;
				}
				final Triple triple = new Triple(s, p, o);
				if (model.getGraph().contains(triple)) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Contains " + triple);
					}
				} else {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Added " + triple);
					}
					model.getGraph().add(triple);
				}

			}
		}
		return model;
	}
}
