package fr.ujm.tse.lt2c.satin.inferray.interfaces;

import java.util.Iterator;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.utils.SortingAlgorithm;
import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;
import fr.ujm.tse.lt2c.satin.inferray.dictionary.NodeDictionary;


/**
 * Interface for the cache oblivious triple store
 *
 * @author Julien
 *
 *         Dec 2013
 */
public interface CacheTripleStore {

	public void add(long s, int p, long o);

	/**
	 * Returns a list of triples in the form (s,p,o) containing all the triples
	 * in the triple store
	 *
	 * @return all the triples from the triples store
	 */
	public LongPairArrayList getAll();

	/**
	 * Slow method, should only be used when required
	 *
	 * @param s
	 *            the subject
	 * @return the pairs (p,o) for the given subject {@code s}
	 */
	public LongPairArrayList getbySubject(long s);

	/**
	 * Fastest method for the triple store, should almost exclusively be used in
	 * contrast with {@link #getbyObject(long)} and {@link #getbySubject(long)}
	 *
	 * @param p
	 *            predicate
	 * @return the list of pairs (s,o) for the given predicate {@code null} is
	 *         no such triples
	 */
	public LongPairArrayList getbyPredicate(int p);

	/**
	 * Similar to {@link #getbySubject(long)}, however no index conversion is
	 * performed on the predicate
	 *
	 * @param p
	 *            predicate index, starting from zero
	 * @return the list of pairs (s,o) for the given predicate {@code null} is
	 *         no such triple
	 */
	public LongPairArrayList getbyPredicateRawIndex(int p);

	/**
	 * The list of pairs (s,p) for the object {@code o} Should be used as few as
	 * possible
	 *
	 * @param o
	 *            the object
	 * @return list of pairs
	 */
	public LongPairArrayList getbyObject(long o);

	/**
	 *
	 * @return the number of triples in the triple store
	 */
	public long size();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object obj);

	/**
	 * Dumps the triple store on disk
	 *
	 * @param file
	 *            path of the file on disk
	 * @param dictionary
	 *            {@link CacheDictionary} used for this triple store
	 */
	public void writeToFile(String file, NodeDictionary dictionary);

	/**
	 *
	 * @return true if the current triple store is empty
	 */
	public boolean isEmpty();

	/**
	 *
	 * @param s
	 *            subject
	 * @param p
	 *            predicate
	 * @param o
	 *            object
	 * @return true if the triple contains this triple
	 */
	public boolean contains(long s, int p, long o);

	/**
	 *
	 * @return the index of the highest non null property in the triple store
	 */
	public int getMaxActiveProperty();

	/**
	 * Empty the triple store. Triple store is not emptied but the array indices
	 * are resetted. The goal is to minimize the memory allocation on the next
	 * steps.
	 *
	 */
	public void reset();

	/**
	 * Sort s o, based on s in the different {@link LongPairArrayList} Optional
	 *
	 * @param multithread
	 * @param threads
	 *            when multithreaded, number of threads
	 */
	public void sort(boolean multithread, int threads);

	/**
	 * Remapping may be required on some rare occasions
	 *
	 * @param old
	 * @param p
	 */
	public void replaceResourceByProperty(long old, int p);

	/**
	 * Clears all the properties, maintains the memory allocation.
	 *
	 */
	public void clear();

	/**
	 * Add a batch of triples to the current triple store
	 *
	 * @param property
	 *            the property to which the triples should be added
	 * @param values
	 *            the triples in forms of {s,o}
	 */
	public void batchInsertion(int property, LongPairArrayList values);

	/**
	 * Add a batch of triples to the current triple store
	 *
	 * @param property
	 *            the property to which the triples should be added
	 * @param values
	 *            the triples in forms of {s,o}
	 */
	public void batchInsertionRawIndex(int property, LongPairArrayList values);

	/**
	 * Replace the list of triples for a given property with the given one
	 *
	 * @param property
	 *            the property for which values will be replaced, or added if
	 *            previously {@code null}. Using raw index, from zero to
	 *            maxactive
	 * @param values
	 *            the new values
	 */
	public void setPropertyTriples(int property, LongPairArrayList values);

	/**
	 *
	 * @return the unique identifier of the triple store
	 */
	public int getID();

	/**
	 * Recount the number of triples FIXME should be removed one day
	 */
	public void recount();

	/**
	 * Import all the triples from the triple store. It is assumed that both
	 * triple stores have been encoded using the same dictionary.
	 *
	 * @param ts
	 *            triple store
	 */
	public void addAllFrom(CacheTripleStore ts);

	/**
	 *
	 * @return an iterator of all property tables that are not <code>null</code>
	 *         nor empty
	 */
	public Iterator<LongPairArrayList> verticalIterator();
	/**
	 * 
	 * @return the algorithm used to sort the property tables
	 */
	public SortingAlgorithm getSortingAlgorithm();

}
