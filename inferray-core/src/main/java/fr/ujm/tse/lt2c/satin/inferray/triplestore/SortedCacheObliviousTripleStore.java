package fr.ujm.tse.lt2c.satin.inferray.triplestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;
import fr.ujm.tse.lt2c.satin.inferray.interfaces.CacheTripleStore;
import fr.ujm.tse.lt2c.satin.inferray.reasoner.Inferray;

/**
 * Cache Oblivious Triple Store
 * 
 * The triple store is architectured to favor data locality for traversal.
 * {@link LongPairArrayList} are used to back the arrays of triples indexed on
 * <code>p</code>.
 * 
 * The following primitive are used :
 * <ul>
 * <li><code>s</code> long</li>
 * <li><code>p</code> int</li>
 * <li><code>o</code> long</li>
 * </ul>
 * 
 * This does not raise issue when <code>p</code> is either <code>i</code> or
 * <code>o</code> in a triple since int included in long.
 * 
 * The dictionary takes care of the good identification of properties for
 * indexation.
 * 
 * Properties id goes down from the {@code CacheDictionary.SPLIT_INDEX}, while
 * other resources goes up. This reduces the span of ids in order to favor the
 * good execution of the various counting sort based algorithms in
 * {@link LongPairArrayList}.
 * 
 * This class is the core of Inferray and contains the main structure that is
 * intensively used in the reasoner. Most optimizations are either carried here,
 * or in {@link LongPairArrayList} and {@link Inferray}.
 * 
 * @author Julien Subercaze
 * 
 *         2014
 * 
 */
public final class SortedCacheObliviousTripleStore implements CacheTripleStore {

	private static Logger logger = Logger
			.getLogger(SortedCacheObliviousTripleStore.class);
	/**
	 * ID generator.
	 */
	private static int idGenerator = 0;
	/**
	 * Used to identify in debugging process
	 */
	private final int idTripleStore = ++idGenerator;
	/**
	 * The list of properties
	 */
	final LongPairArrayList[] properties; // Access the properties by their
	/**
	 * Internal counter for triples
	 */
	long triples = 0;
	/**
	 * Active properties in the table. The list is pre-allocated with
	 * <code>null</code>. Reduce scan time of the array
	 */
	int maxActiveProperty;
	private final SortingAlgorithm algorithm;

	public SortedCacheObliviousTripleStore(final int props,
			final SortingAlgorithm sortingAlgorithm) {
		properties = new LongPairArrayList[props];
		maxActiveProperty = 0;
		this.algorithm = sortingAlgorithm;
	}

	@Override
	public final void add(final long s, int p, final long o) {
		p = NodeDictionary.SPLIT_INDEX - p;
		// Check wether the p already in the store
		if (properties[p] == null) {
			// Update the max active props if required
			maxActiveProperty = p > maxActiveProperty ? p : maxActiveProperty;
			final LongPairArrayList list = new LongPairArrayList(algorithm);
			list.add(s);
			list.add(o);
			properties[p] = list;
		} else {
			properties[p].add(s);
			properties[p].add(o);
		}
		++triples;
	}

	@Override
	public final LongPairArrayList getAll() {
		final LongPairArrayList allTriples = new LongPairArrayList(
				(int) triples * 3, algorithm);
		for (int i = 0; i < properties.length; i++) {
			final LongPairArrayList property = properties[i];
			if (property == null || property.isEmpty()) {
				continue;
			}
			for (int j = 0; j < property.size(); j++) {
				allTriples.add(property.getQuick(j));// S
				allTriples.add(i);// P
				allTriples.add(property.getQuick(++j));// O
			}
		}
		return allTriples;
	}

	@Override
	public final LongPairArrayList getbySubject(final long s) {
		final LongPairArrayList triplesBySubject = new LongPairArrayList(
				algorithm);
		for (int i = 0; i < properties.length; i++) {
			final LongPairArrayList property = properties[i];
			if (property == null || property.isEmpty()) {
				continue;
			}
			for (int j = 0; j < property.size(); j += 2) {
				final long prop = property.getQuick(j);
				if (prop > s) {
					break;
				}
				if (property.getQuick(j) == s) {
					triplesBySubject.add(i);// P
					triplesBySubject.add(property.getQuick(j + 1));// O
				}

			}

		}
		return triplesBySubject;
	}

	@Override
	public final LongPairArrayList getbyPredicate(final int p) {
		return properties[NodeDictionary.SPLIT_INDEX - p];
	}

	@Override
	public final LongPairArrayList getbyPredicateRawIndex(final int p) {
		return properties[p];
	}

	@Override
	public final LongPairArrayList getbyObject(final long o) {
		final LongPairArrayList triplesByObject = new LongPairArrayList(
				algorithm);
		for (int i = 0; i < properties.length; i++) {
			final LongPairArrayList property = properties[i];
			if (property == null || property.isEmpty()) {
				continue;
			}
			for (int j = 0; j < property.size(); j += 2) {
				// If sorted could optimize here
				// it is now really necessary ?
				if (property.getQuick(j) == o) {
					triplesByObject.add(property.getQuick(j));// S
					triplesByObject.add(i);// P
				}

			}
		}
		return triplesByObject;
	}

	@Override
	public final long size() {
		return triples;
	}

	@Override
	public final void writeToFile(final String file,
			final NodeDictionary dictionary) {


	}

	@Override
	public final boolean isEmpty() {
		return triples == 0;
	}

	@Override
	public final boolean contains(final long s, int p, final long o) {
		p = NodeDictionary.SPLIT_INDEX - p;
		if (p > properties.length) {
			return false;
		}

		final LongPairArrayList prop = properties[p];
		if (prop == null) {
			return false;
		}
		for (int i = 0; i < prop.size(); i += 2) {
			final long val = prop.getQuick(i);
			if (val == s) {
				if (prop.getQuick(i + 1) == o) {
					return true;
				}
			} else if (val > s) {
				return false;
			}

		}
		return false;
	}

	@Override
	public final void reset() {
		triples = 0;
		for (final LongPairArrayList property : properties) {
			property.clear(); // Colt clear keeps the capacity of the list
		}
	}

	@Override
	public final void sort(final boolean multithread, final int threads) {
		if (multithread) {
			if (logger.isInfoEnabled()) {
				logger.info("Multithread");
			}
			multisort(threads);
		} else {
			triples = 0;
			for (final LongPairArrayList eslal : properties) {
				if (eslal != null) {
					eslal.totalSortingNoDuplicate();
					triples += eslal.size();
				}

			}
			if (logger.isDebugEnabled()) {
				logger.debug("Triples after sorting " + triples);
			}
		}
	}

	/**
	 * Parallel sorting of the properties
	 * 
	 * @param threads
	 *            number of threads
	 */
	private final void multisort(final int threads) {
		final ExecutorService es = Executors.newFixedThreadPool(threads);
		for (final LongPairArrayList eslal : properties) {
			if (eslal != null) {
				es.submit(new SortingThread(eslal));
			}
		}
		es.shutdown();
		try {
			es.awaitTermination(10, TimeUnit.DAYS);
		} catch (final InterruptedException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Error while sorting ", e);
			}
		}
		// Update triples value
		triples = 0;
		for (final LongPairArrayList eslal : properties) {
			if (eslal != null) {
				triples += eslal.size();
			}
		}
	}

	@Override
	public final void replaceResourceByProperty(final long old, final int p) {
		// Create a new Arraylist for the new property
		final LongPairArrayList newTriples = new LongPairArrayList(algorithm);
		properties[NodeDictionary.SPLIT_INDEX - p] = newTriples;
		// All triples will be added there
		for (int i = 0; i < maxActiveProperty; i++) {
			final LongPairArrayList lpal = properties[i];
			if (lpal == null) {
				continue;
			}
			for (int j = 0; j < lpal.size(); j++) {
				final long current = lpal.getQuick(j);
				if (current == old) {
					lpal.setQuick(j, p);// Exists for sure since checked before
				}
			}
		}

	}

	@Override
	public final String toString() {
		return "SortedCacheObliviousTripleStore [properties="
				+ Arrays.toString(properties) + ", triples=" + triples
				+ ", activeProps=" + maxActiveProperty + "]";
	}

	@Override
	public final void clear() {
		for (int i = 0; i < properties.length; i++) {
			final LongPairArrayList property = properties[i];
			if (property == null || property.isEmpty()) {
				continue;
			}
			property.clear();
		}
		triples = 0;
	}

	@Override
	public final void batchInsertion(final int p, final LongPairArrayList values) {
		batchInsertionRawIndex(NodeDictionary.SPLIT_INDEX - p, values);
	}

	@Override
	public final void batchInsertionRawIndex(final int p,
			final LongPairArrayList values) {
		// Using raw index
		if (properties[p] == null) {
			// Update the max active props if required
			maxActiveProperty = p > maxActiveProperty ? p : maxActiveProperty;
			properties[p] = values;
			triples += values.size();
		} else {
			final LongPairArrayList list = properties[p];
			list.addAllOfFromTo(values, 0, values.size() - 1);
			triples += values.size();
		}

	}

	@Override
	public int getMaxActiveProperty() {
		return maxActiveProperty;
	}

	@Override
	public final void setPropertyTriples(final int property,
			final LongPairArrayList values) {
		maxActiveProperty = property > maxActiveProperty ? property
				: maxActiveProperty;
		if (this.properties[property] != null) {
			triples -= this.properties[property].size();
		}
		this.properties[property] = values;
		triples += values.size();
	}

	@Override
	public int hashCode() {
		return idTripleStore;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SortedCacheObliviousTripleStore other = (SortedCacheObliviousTripleStore) obj;
		if (idTripleStore != other.idTripleStore) {
			return false;
		}
		return true;
	}

	@Override
	public int getID() {
		return this.idTripleStore;
	}

	@Override
	public void recount() {
		this.triples = 0;
		for (int i = 0; i <= maxActiveProperty; i++) {
			final LongPairArrayList lp = properties[i];
			if (lp != null) {
				triples += lp.size() / 2;
			}
		}
	}

	@Override
	public void addAllFrom(final CacheTripleStore ts) {
		// Ensure triple stores are different
		assert ts.getID() != this.getID();
		for (int i = 0; i < ts.getMaxActiveProperty(); i++) {
			final LongPairArrayList tstmp = ts.getbyPredicateRawIndex(i);
			if (tstmp == null || tstmp.isEmpty()) {
				continue;
			}
			LongPairArrayList thistmp = this.getbyPredicateRawIndex(i);
			if (thistmp == null || thistmp.isEmpty()) {
				thistmp = ts.getbyPredicateRawIndex(i);
			} else {
				// Add in bulk at the end
				thistmp.addAllOfFromTo(tstmp, 0, tstmp.size());
			}
		}
	}

	@Override
	public Iterator<LongPairArrayList> verticalIterator() {
		final MyPropertyIterator it = new MyPropertyIterator(properties);
		if (it.props.length < 1) {
			return (Collections.<LongPairArrayList> emptyList().iterator());
		} else {
			return it;
		}
	}

	@Override
	public SortingAlgorithm getSortingAlgorithm() {
		return algorithm;
	}

	/**
	 * Iterator that iterates non <code>null</code> and non empty property
	 * tables
	 * 
	 * @author Julien
	 * 
	 */
	public class MyPropertyIterator implements Iterator<LongPairArrayList> {

		final LongPairArrayList[] props;

		int counter;

		public MyPropertyIterator(final LongPairArrayList[] properties) {
			super();
			counter = -1;
			int nulls = 0;
			for (int i = 0; i < properties.length; i++) {
				if (properties[i] == null || properties[i].isEmpty()) {
					nulls++;
				} else {
					properties[i].setProperty(i);
				}
			}
			this.props = new LongPairArrayList[properties.length - nulls];
			if (this.props.length != 0) {
				int i = 0;
				for (final LongPairArrayList lpal : properties) {
					if (lpal != null) {
						props[i] = lpal;
						i++;
					}
				}
			}

		}

		@Override
		public boolean hasNext() {

			return (counter < props.length - 1);

		}

		@Override
		public LongPairArrayList next() {
			// System.out.println(++counter);
			return props[++counter];

		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
