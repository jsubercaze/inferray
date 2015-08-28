package fr.ujm.tse.lt2c.satin.inferray.datastructure.transitive;

import org.apache.log4j.Logger;

import stixar.graph.BasicDigraph;
import stixar.graph.BasicNode;
import stixar.graph.attr.NodeMatrix;
import stixar.graph.conn.Transitivity;
import stixar.graph.gen.BasicDGFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;

/**
 * Compute fast transitive closure using an adapted version of the Stixar
 * implementation of Nuutila's algorithm. Performance ensues.
 * 
 * @author Julien
 * 
 */
public class FastTransitiveClosure2 {


	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(FastTransitiveClosure.class);

	/**
	 * Main triple store from Inferray
	 */
	private final CacheTripleStore ts;
	/**
	 * Index of the property in the ts.
	 */
	private final int propertyIndex;
	/**
	 * Mapping
	 */
	BiMap<Long, Integer> longToInt;

	int counter = 0;
	/**
	 * Nodes indexed
	 */
	BasicNode[] nodes;
	/**
	 * Node factory
	 */
	BasicDGFactory factory;
	/**
	 * Nodes attributes
	 */
	long[] attributes;
	/**
	 * Should add the triples for Jena
	 */
	private final boolean export;
	/**
	 * TripleStore to export to Jena/Other bindings
	 */
	private final CacheTripleStore exportTS;

	public FastTransitiveClosure2(final CacheTripleStore ts,
			final int propertyIndex, final boolean export,
			final CacheTripleStore exportTS) {
		this.ts = ts;
		this.export = export;
		this.exportTS = exportTS;
		this.propertyIndex = propertyIndex;
		if (ts.getbyPredicate(propertyIndex) != null
				&& !ts.getbyPredicate(propertyIndex).isEmpty()) {
			longToInt = HashBiMap.create(ts.getbyPredicate(propertyIndex)
					.size());
			final int taille = (ts.getbyPredicate(propertyIndex).size() / 1);
			nodes = new BasicNode[taille];
			attributes = new long[ts.getbyPredicate(propertyIndex).size()];
			factory = new BasicDGFactory();
		}
	}

	public void computeTransitiveClosure(final SortingAlgorithm algorithm) {
		if (longToInt == null) {
			return;
		}

		LOGGER.info("Computing closure for  " + this.propertyIndex);

		// Get the list of triples
		final LongPairArrayList triples = ts.getbyPredicate(propertyIndex);
		LongPairArrayList exportTriples = null;
		// In case of export
		if (export) {
			exportTriples = new LongPairArrayList(triples.size() * 2, algorithm);
			exportTS.setPropertyTriples(NodeDictionary.SPLIT_INDEX
					- propertyIndex, exportTriples);
		}

		LOGGER.info("Triples " + (triples.size() / 2));
		// Larger than real - trade one pass against larger memory footprint

		for (int i = 0; i < triples.size();) {
			final long subject = triples.getQuick(i++);
			final long object = triples.getQuick(i++);
			// Check if a number exists for this node
			final BasicNode subjNode = getNode(subject);
			final BasicNode objNode = getNode(object);
			factory.edge(subjNode, objNode);
		}

		final BasicDigraph dg = factory.digraph();

		final NodeMatrix<Boolean> matrix = Transitivity.compactClosure(dg);
		int newT = 0;
		for (int i = 0; i < counter; i++) {
			for (int j = i + 1; j < counter; j++) {
				if (matrix.get(nodes[i], nodes[j])) {

					triples.add(nodes[i].getLong(attributes));
					triples.add(nodes[j].getLong(attributes));
				}
			}
		}

		// Break on through to the other side
		for (int i = 0; i < counter; i++) {
			for (int j = i + 1; j < counter; j++) {
				if (matrix.get(nodes[j], nodes[i])) {
					newT++;
					triples.add(nodes[j].getLong(attributes));
					triples.add(nodes[i].getLong(attributes));
					if (export) {
						exportTriples.add(nodes[j].getLong(attributes));
						exportTriples.add(nodes[i].getLong(attributes));
					}
				}
			}
		}
		LOGGER.info("New triples " + newT);
	}

	/**
	 * If {@link Long} value presents, returns the node. Otherwise create node
	 * 
	 * @param subject
	 * @return
	 */
	private BasicNode getNode(final long longValue) {
		if (longToInt.containsKey(longValue)) {
			return nodes[longToInt.get(longValue)];
		} else {

			nodes[counter] = factory.node();
			nodes[counter].setLong(attributes, longValue);
			longToInt.put(longValue, counter);
			counter++;
			return nodes[counter - 1];
		}

	}
}
