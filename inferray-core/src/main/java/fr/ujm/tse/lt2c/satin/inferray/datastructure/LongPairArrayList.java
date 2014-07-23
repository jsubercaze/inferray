package fr.ujm.tse.lt2c.satin.inferray.datastructure;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import cern.colt.list.LongArrayList;

/**
 * Extension of the {@link LongArrayList} from colt, with an adapted version of
 * the sorting algorithms for the triple structure. Both countingsort and
 * quicksott have been adapted to match these requirements. A insertion sort has
 * been added to the class. All three algorithms are wrapped by
 * {@link #subjectSort()} and {@link #objectSort()}. Conditions :
 * <ol>
 * <li>Less than 20 elements (10 pairs) : Insertion Sort</li>
 * <li>Range by values greater than 10 : Quicksort</li>
 * <li>Otherwise : Counting sort</li>
 * </ol>
 *
 * @author Julien Subercaze
 *
 */
public class LongPairArrayList extends LongArrayList {
	/**
	 * Sorts by counting
	 */
	public static AtomicInteger counting = new AtomicInteger();
	/**
	 * Sorts by quicksort
	 */
	public static AtomicInteger quicksort = new AtomicInteger();
	/**
	 * Sorts by insertion
	 */
	public static AtomicInteger insertions = new AtomicInteger();
	/**
	 * Sorted object copy cache hits
	 */
	public static AtomicInteger objectCacheHit = new AtomicInteger();
	/**
	 * Sorted object copy cache miss
	 */
	public static AtomicInteger objectCacheMiss = new AtomicInteger();

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -6667713430446395455L;
	/**
	 * Logger
	 */
	private static final Logger LOGGER = Logger
			.getLogger(LongPairArrayList.class);
	/**
	 * Insertion sort condition.
	 */
	private static final int CUTOFF = 20;

	/**
	 * Store a sorted copy that is sorted by object in cache. Cleared by call to
	 * {@link #totalSortingNoDuplicate(boolean, boolean)}.
	 */
	private SoftReference<LongPairArrayList> softObjectSortedCopy;

	/**
	 * Update this value only on a O(1) basis, i.e. after sorting
	 */
	private long maxSubject = -1L;
	/**
	 * Update this value only on a O(1) basis, i.e. after sorting
	 */
	private long maxObject = -1L;

	/**
	 * Optional, used to keep the value of the property for triple export
	 */
	private int property;

	/**
	 * Empty constructor
	 */
	public LongPairArrayList() {
		super();

	}

	/**
	 * With preallocated array
	 *
	 * @param i
	 *            number of elements
	 */
	public LongPairArrayList(final int i) {
		super(i);
	}

	/**
	 * Calls constructor from {@link LongArrayList}
	 *
	 * @param elements
	 */
	public LongPairArrayList(final long[] elements) {
		super(elements);
	}

	/**
	 * Retrieve an object sorted copy of the current list. This latter may have
	 * been internally cached using {@link SoftReference}. This cached value is
	 * cleared at each iteration of inferring through
	 * {@link #totalSortingNoDuplicate(boolean, boolean)}.
	 *
	 * @return an object sorted copy of the current list
	 */
	public LongPairArrayList objectSortedCopy() {
		LongPairArrayList objectSortedCopy = null;
		if (softObjectSortedCopy == null || softObjectSortedCopy.get() == null) {
			objectCacheMiss.incrementAndGet();
			objectSortedCopy = this.copy();
			objectSortedCopy.objectSort();
			// Update the soft reference
			softObjectSortedCopy = new SoftReference<LongPairArrayList>(
					objectSortedCopy);
		} else {
			objectCacheHit.incrementAndGet();
			// Get the value from the SoftReference
			objectSortedCopy = softObjectSortedCopy.get();

		}

		this.maxObject = this.elements[size - 1];
		objectSortedCopy.maxObject = objectSortedCopy.elements[size - 1];
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Update the maxobject to "
					+ objectSortedCopy.maxObject);
		}
		return objectSortedCopy;
	}

	/**
	 * <h1>
	 * Method used to duplicate the last insertion while replacing the object
	 * <h1>
	 * <p>
	 * This method is notably used in rules when two consecutive triples in the
	 * array have the object, leading to an insertion of same triples that
	 * previously, only with a different object.
	 * </p>
	 *
	 * @param o
	 *            subject
	 * @param size
	 *            size of the last insertion
	 */
	public void duplicateInsertionWithNewObject(final long o, final int size) {
		// No need to use arraycopy since we will iterate to replace objects
		final int oldsize = this.size();
		for (int i = oldsize - (2 * size); i < oldsize; i += 2) {
			this.add(this.getQuick(i));
			this.add(o);
		}
	}

	/**
	 * <h1>
	 * Method used to duplicate the last insertion while replacing the subject
	 * <h1>
	 * <p>
	 * This method is notably used in rules when two consecutive triples in the
	 * array have the subject, leading to an insertion of same triples that
	 * previously, only with a different subject.
	 * </p>
	 *
	 * @param s
	 *            subject
	 * @param size
	 *            size of the last insertion
	 */
	public void duplicateInsertionWithNewSubject(final long s, final int size) {
		// No need to use arraycopy since we will iterate to replace subjects
		final int oldsize = this.size();
		for (int i = oldsize - (2 * size) + 1; i < oldsize; i += 2) {
			this.add(s);
			this.add(this.getQuick(i));
		}
	}

	/**
	 * Add the previous insertion and replace the subject with the new subject.
	 * Checks if there is an equality with the previous inferred one. Useful to
	 * generate <code>owl:equivalent*</code>
	 *
	 * @param s
	 *            the subject
	 * @param size
	 *            size of the previous insertion
	 * @return <code>true</code> if the subject was present in the previous
	 *         insertion
	 */
	public boolean duplicateInsertionNewSubjectWithEqualityCheck(final long s,
			final int size) {
		// No need to use arraycopy since we will iterate to replace subjects
		boolean matches = false;

		final int oldsize = this.size();
		for (int i = oldsize - (2 * size) + 1; i < oldsize; i += 2) {
			final long o = this.getQuick(i);
			matches = (s == o);
			// Don't add X sub*** X
			if (!matches) {
				this.add(s);
				this.add(o);
			}
		}

		return matches;
	}

	/**
	 * Sort by object first, using counting sort
	 *
	 * @param from
	 *            start index
	 * @param to
	 *            end index (inclusive)
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 */
	protected void objectCountSortFromNoCache(final int from, final int to,
			final long min, final long max) {

		if (size == 0) {
			return;
		}
		// checkRangeFromTo(from, to, size); Useless check removed

		int[] counts = new int[(int) (max - min + 1)];
		final long[] theElements = elements;

		for (int i = from; i <= to; i += 2) {
			counts[(int) (theElements[i] - min)]++;

		}
		int[] copycounts = new int[counts.length];
		System.arraycopy(counts, 0, copycounts, 0, counts.length);
		// Add a scan for the next values - count values over one to be added on
		// the triple

		// Alternative version compute the starting indices in the adjacent
		// compact table
		int sum = 0;
		int[] startingPosition = new int[counts.length];
		for (int i = 0; i < counts.length; i++) {
			startingPosition[i] = sum;
			sum += counts[i];
		}

		// Create the adjacent tab

		final long[] adjacents = new long[this.size / 2];

		// Here we add the next values

		for (int i = from; i <= to; i += 2) {
			// Position in counts
			final int position = startingPosition[(int) (theElements[i] - min)];
			// number of this value remaining
			final long remaining = copycounts[(int) (theElements[i] - min)];

			copycounts[(int) (theElements[i] - min)]--;

			adjacents[(int) (position + remaining) - 1] = theElements[i - 1];
		}
		copycounts = null;
		// Sort the adjacents for total sorting, useful to maintain linearity on
		// rule processing
		for (int i = 0; i < startingPosition.length - 1; i++) {
			// java.util.Arrays.sort(adjacents, startingPosition[i],
			// startingPosition[i + 1]);
			if (startingPosition[i + 1] - startingPosition[i] > Integer.MAX_VALUE) {
				partialCountingSort(adjacents, startingPosition[i],
						startingPosition[i + 1]);
			} else {
				java.util.Arrays.sort(adjacents, startingPosition[i],
						startingPosition[i + 1]);
			}
		}
		// Sort the last array part
		if (adjacents.length - startingPosition[startingPosition.length - 1] > Integer.MAX_VALUE) {
			partialCountingSort(adjacents,
					startingPosition[startingPosition.length - 1],
					adjacents.length);
		} else {
			java.util.Arrays.sort(adjacents,
					startingPosition[startingPosition.length - 1],
					adjacents.length);
		}
		// java.util.Arrays
		// .sort(adjacents, startingPosition[startingPosition.length - 1],
		// adjacents.length);
		startingPosition = null;
		int j = 0;
		int l = 0;
		for (int i = 0; i < counts.length; i++) {
			final int val = counts[i];
			for (int k = 0; k < val; k++) {
				elements[j] = adjacents[l++];
				elements[j + 1] = min + i;
				j += 2;
			}
		}
		counts = null;

	}

	/**
	 * Sort the array by object first. Let the class choose which sorting
	 * algorithm will be used
	 */
	public void objectSort() {
		if (size() - 1 == 0) {
			return;
		}
		if (this.size < CUTOFF) {
			counting.incrementAndGet();
			this.pairInsertionObjectSort();
			return;
		}
		objectsortFromTo(1, size() - 1);
	}

	/**
	 * Sort the pairs in the list along to their objects. Count Sort Algorithm
	 *
	 * @param from
	 * @param to
	 */
	public void objectsortFromTo(final int from, final int to) {

		if (size == 2) {
			return;
		}
		checkRangeFromTo(from, to, size);

		// determine minimum and maximum.
		long min = elements[from];
		long max = elements[from];

		final long[] theElements = elements;
		for (int i = from; i <= to; i += 2) {
			final long elem = theElements[i];
			max = elem > max ? elem : max;
			min = elem < min ? elem : min;
		}
		// try to figure out which option is fastest.
		if ((max - min + 1) / size > 10) {
			quicksort.incrementAndGet();
			this.quicksortObject();
		} else {

			counting.incrementAndGet();
			objectCountSortFromNoCache(from, to, min, max);

		}
	}

	/**
	 * Partition for the quicksort, using a s,o comparator
	 *
	 * @param p
	 * @param start
	 * @param end
	 * @return
	 */
	private int totalpartitionSubject(final int p, final int start,
			final int end) {

		int l = start;
		int h = end - 2;
		final long piv = elements[p];
		final long pivnext = elements[p + 1];

		swap(p, end - 2);
		swap(p + 1, end - 1);

		while (l < h) {

			if (compareArrayElementsValueSubject(l, piv, pivnext) == -1) {
				l += 2;
			} else if (compareArrayElementsValueSubject(h, piv, pivnext) > -1) {
				h -= 2;
			} else {
				swap(l, h);
				swap(l + 1, h + 1);
			}
		}

		int idx = h;
		if (compareArrayElementsValueSubject(h, piv, pivnext) == -1) {
			idx += 2;
		}

		swap(end - 2, idx);
		swap(end - 1, idx + 1);

		return idx;
	}

	/**
	 * Partition for the quicksort, using a o,s comparator
	 *
	 * @param p
	 * @param start
	 * @param end
	 * @return
	 */
	private int totalpartitionObject(final int p, final int start, final int end) {

		int l = start;
		int h = end - 2;
		final long piv = elements[p];
		final long pivnext = elements[p + 1];

		swap(p, end - 2);
		swap(p + 1, end - 1);

		while (l < h) {

			if (compareArrayElementsValueObject(l, piv, pivnext) == -1) {
				l += 2;
			} else if (compareArrayElementsValueObject(h, piv, pivnext) > -1) {
				h -= 2;
			} else {
				swap(l, h);
				swap(l + 1, h + 1);
			}
		}

		int idx = h;
		if (compareArrayElementsValueObject(h, piv, pivnext) == -1) {
			idx += 2;
		}
		swap(end - 2, idx);
		swap(end - 1, idx + 1);

		return idx;
	}

	/**
	 * To be used with the QS implementation that was developed for subject
	 * sorting.
	 *
	 *
	 * @param aindex
	 * @param value
	 * @param next
	 * @return
	 */
	private int compareArrayElementsValueObject(final int aindex,
			final long value, final long next) {
		if (elements[aindex + 1] < next) {
			return -1;
		}
		if (elements[aindex + 1] > next) {
			return 1;
		}
		if (elements[aindex] < value) {
			return -1;
		}
		if (elements[aindex] > value) {
			return 1;
		}
		return 0;
	}

	private int compareArrayElementsValueSubject(final int aindex,
			final long value, final long next) {
		if (elements[aindex] < value) {
			return -1;
		}
		if (elements[aindex] > value) {
			return 1;
		}
		if (elements[aindex + 1] < next) {
			return -1;
		}
		if (elements[aindex + 1] > next) {
			return 1;
		}
		return 0;
	}

	/**
	 * Sort and remove duplicates along to s,o order using Quicksort algorithm
	 *
	 * Usually slower than the Counting Sort alternatives. Should be use with
	 * care only when memory size is a concern.
	 *
	 * @param duplicates
	 * @return
	 */
	public LongPairArrayList quickSortFullNoDuplicates(final boolean duplicates) {
		// Shuffle elements to guard from worst case
		shuffleFull();
		LongPairArrayList result = null;
		if (duplicates) {
			result = new LongPairArrayList(this.size / 10);
		}
		recursiveQsort(0, this.size);
		// Remove the duplicates in place
		long previouss = -1;
		long previouso = -1;
		int shifting = 0;

		for (int i = 0; i < this.size; i++) {
			final long s = elements[i];
			final long o = elements[++i];

			if (s == previouss && o == previouso) {
				shifting++;
				if (duplicates) {
					result.add(s);
					result.add(o);
				}
				continue;
			}
			if (shifting > 0) {
				elements[(i - 1) - (shifting * 2)] = s;
				elements[i - (shifting * 2)] = o;
			}
			previouss = s;
			previouso = o;
		}
		this.size -= shifting * 2;

		return result;
	}

	/**
	 * Recursive quicksort for subject sorting
	 *
	 * @param start
	 *            start index
	 * @param end
	 *            end index
	 */
	private void recursiveQsort(final int start, final int end) {

		if (end - start < 3) {
			// stop clause
			return;
		}
		int p = start + ((end - start) / 2);
		if (p % 2 != 0) {
			p++;
		}

		p = totalpartitionSubject(p, start, end);
		recursiveQsort(start, p);
		recursiveQsort(p + 2, end);
	}

	private void recursiveQsortObject(final int start, final int end) {

		if (end - start < 3) {
			return; // stop clause
		}
		int p = start + ((end - start) / 2);
		if (p % 2 != 0) {
			p++;
		}

		p = totalpartitionObject(p, start, end);
		recursiveQsortObject(start, p);
		recursiveQsortObject(p + 2, end);
	}

	/**
	 * Shuffle the whole array, used for quicksort
	 */
	private void shuffleFull() {

		int N = this.size;
		N = N >> 1;
		final Random r = new Random();
		for (int i = 0; i < N; i++) {
			final int pos = r.nextInt(N) << 1;
			swap((i << 1), pos);
			swap((i << 1) + 1, pos + 1);
		}
	}

	@Override
	/**
	 * Method is normally never used
	 */
	public void sortFromTo(final int from, final int to) {
		/*
		 * Computes min and max and decides on this basis. In practice the
		 * additional overhead is very small compared to the potential gains.
		 */
		final int widthThreshold = 100000; // never consider options resulting
		// in
		// outrageous memory allocations.

		if (size == 0) {
			return;
		}
		checkRangeFromTo(from, to, size);

		// determine minimum and maximum.
		long min = elements[from];
		long max = elements[from];

		final long[] theElements = elements;
		for (int i = from + 2; i <= to;) {

			final long elem = theElements[i];
			if (elem > max) {
				max = elem;
			} else if (elem < min) {
				min = elem;
			}
			i += 2;// check only one every to
		}

		// try to figure out which option is fastest.
		final double N = (double) to - (double) from + 1.0;
		final double quickSortEstimate = N * Math.log(N) / 0.6931471805599453; // O(N*log(N,base=2))
		// ;
		// ln(2)=0.6931471805599453

		final double width = (double) max - (double) min + 1.0;
		final double countSortEstimate = Math.max(width, N); // O(Max(width,N))

		if (width < widthThreshold && countSortEstimate < quickSortEstimate) {
			subjectCountSortFromTo(from, to, min, max);
		} else {
			// quickSortFull();// Full array sorting
			subjectCountSortFromTo(from, to, min, max);
		}
	}

	/**
	 * Counting Sort by subject first
	 *
	 * @param from
	 *            start index
	 * @param to
	 *            end index (inclusive)
	 * @param min
	 *            min value
	 * @param max
	 *            max value
	 */
	protected void subjectCountSortFromTo(final int from, final int to,
			final long min, final long max) {

		if (size == 0) {
			return;
		}
		// checkRangeFromTo(from, to, size); Useless check removed

		final int width = (int) (max - min + 1);

		final int[] counts = new int[width];
		final long[] theElements = elements;

		for (int i = from; i <= to;) {

			counts[(int) (theElements[i] - min)]++;
			i += 2;
		}
		final int[] copycounts = new int[counts.length];
		System.arraycopy(counts, 0, copycounts, 0, counts.length);
		// Add a scan for the next values - count values over one to be added on
		// the triple

		/*
		 * int overOne =0; for(int i=0;i<counts.length;i++){ int over =
		 * counts[i]-1; if(over>0) overOne+=over; } //Instantiate the adjacents
		 * array long[] adjacents = new long[width+overOne];
		 */

		// Alternative version compute the starting indices in the adjacent
		// compact table
		int sum = 0;
		final int[] startingPosition = new int[counts.length];
		for (int i = 0; i < counts.length; i++) {
			startingPosition[i] = sum;
			sum += counts[i];
		}

		final long[] adjacents = new long[this.size / 2];
		// Here we add the next values

		for (int i = from; i <= to;) {

			final int val = (int) (theElements[i] - min);

			final int position = startingPosition[val];// Position
			// in
			// counts
			final long remaining = copycounts[val]; // number
			// of
			// this
			// value
			// remaining

			copycounts[(int) (theElements[i] - min)]--;
			adjacents[(int) (position + remaining) - 1] = theElements[i + 1];

			i += 2;
		}
		int j = 0;
		int l = 0;
		for (int i = 0; i < counts.length; i++) {
			final int val = counts[i];
			for (int k = 0; k < val; k++) {
				elements[j] = min + i;
				elements[j + 1] = adjacents[l++];
				j += 2;
			}
		}
		// Notable performance loss here since we cannot use System.arraycopy

	}

	/**
	 * Sort the array by subject using counting sort
	 */
	public void subjectSort() {
		final int from = 0;
		final int to = size() - 1;
		long min = elements[from];
		long max = elements[from];

		final long[] theElements = elements;
		for (int i = from + 2; i <= to; i += 2) {

			final long elem = theElements[i];
			if (elem > max) {
				max = elem;
			} else if (elem < min) {
				min = elem;
			}// check only one every to
		}
		subjectCountSortFromTo(from, to, min, max);
		// sortFromTo(0, size()-1);
	}

	/**
	 * Swap two values at the given indices
	 *
	 * @param posa
	 * @param posb
	 */
	private void swap(final int posa, final int posb) {
		if (posa == posb) {
			return;
		}
		final long tmp = elements[posa];
		elements[posa] = elements[posb];
		elements[posb] = tmp;
	}

	/**
	 * <p>
	 * <b> Sorts the pairs in the array in total order and removes
	 * duplicates</b>
	 * </p>
	 * <p>
	 * When the final array is reconstructed, duplicates are not introduced.
	 * Since memory is already allocated and the triple store is normally set to
	 * grow up, the overhead due to the duplicates removal is not trimmed unless
	 * sufficiently large. Check the sources for implementation details.
	 * </p>
	 * <p>
	 * Experiments shows that this method is as efficient as its counterpart
	 * with duplicate regardless of the trim parameter value.
	 * </p>
	 *
	 */
	public void totalSortingNoDuplicate() {
		if (softObjectSortedCopy != null) {
			softObjectSortedCopy.clear();
			softObjectSortedCopy = null;
		}
		if (size > 2) {
			if (this.size < CUTOFF) {
				insertions.incrementAndGet();
				this.pairInsertionSortNoDuplicate();
			} else {
				totalSortingNoDuplicateNoCache();
			}
		}
		// Update maxsubject
		this.maxSubject = this.elements[size - 2];

	}

	public LongPairArrayList totalSortingNoDuplicateNoCache() {
		// Since this method is called during update of the value, clear the
		// softref

		// Counting sort
		final int from = 0;
		final int to = size() - 1;
		long min = elements[from];
		long max = elements[from];

		final long[] theElements = elements;
		for (int i = from + 2; i <= to; i += 2) {

			final long elem = theElements[i];
			max = elem > max ? elem : max;
			min = elem < min ? elem : min;
			// check only one every two
		}

		if (size == 0) {
			return null;
		}

		final int width = (int) (max - min + 1);

		// Fallback to quicksort using rule of thumb
		if (width / size > 10) {
			quicksort.incrementAndGet();
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Quicksort");
			}
			return this.quickSortFullNoDuplicates(false);
		}
		counting.incrementAndGet();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Counting sort");
		}

		int[] counts = new int[width];
		for (int i = from; i <= to; i += 2) {
			counts[(int) (theElements[i] - min)]++;
		}
		int[] copycounts = new int[width];
		System.arraycopy(counts, 0, copycounts, 0, width);
		// Alternative version compute the starting indices in the adjacent
		// compact table
		int sum = 0;
		int[] startingPosition = new int[width];
		for (int i = 0; i < width; i++) {
			startingPosition[i] = sum;
			sum += counts[i];
		}
		long[] adjacents = new long[this.size >> 1];

		// logger.debug("Startin position " +
		// Arrays.toString(startingPosition));
		// Here we add the next values
		for (int i = from; i <= to; i += 2) {
			// Position in histogram
			final int position = startingPosition[(int) (theElements[i] - min)];
			// number of this value remaining
			final long remaining = copycounts[(int) (theElements[i] - min)];
			copycounts[(int) (theElements[i] - min)]--;
			adjacents[(int) (position + remaining) - 1] = theElements[i + 1];

		}
		copycounts = null;
		// Now sort the adjacents by using countsort on subarrays

		for (int i = 0; i < width - 1; i++) {
			// Quicksort better here for small adjacency
			if (startingPosition[i + 1] - startingPosition[i] < CUTOFF) {
				singleInsertionSort(adjacents, startingPosition[i],
						startingPosition[i + 1]);
			} else {
				java.util.Arrays.sort(adjacents, startingPosition[i],
						startingPosition[i + 1]);
			}

		}
		// last one
		if (adjacents.length - startingPosition[width - 1] < CUTOFF) {
			singleInsertionSort(adjacents, startingPosition[width - 1],
					adjacents.length);
		} else {
			// Counting sort is never efficient here for sorting small subarrays
			// go quicksort
			java.util.Arrays.sort(adjacents, startingPosition[width - 1],
					adjacents.length);
		}

		startingPosition = null;
		// if (logger.isDebugEnabled())
		// logger.debug("Adjacents " + Arrays.toString(adjacents));
		// //
		final LongPairArrayList duplicates = null;

		// Reconstruct the array without duplicates
		int j = 0;
		int l = 0;
		long lastAdjacent = -1;

		for (int i = 0; i < width; i++) {
			final int val = counts[i];
			final long subject = min + i;
			// logger.debug("Val " + val);
			for (int k = 0; k < val; k++) {
				final long adjacent = adjacents[l++];
				// logger.debug("Adjacent " + adjacent + " Subject " + subject);
				// logger.debug("LAdjacent " + lastAdjacent + " LSubject "
				// // + lastSubject);

				if (k == 0 || adjacent != lastAdjacent) {
					elements[j++] = subject;
					elements[j++] = adjacent;

				}
				lastAdjacent = adjacent;
			}

		}
		// Release auxiliary structures
		adjacents = null;
		counts = null;
		// Number of insertions is given by j
		// Violating @cern.colt.list.tlong.AbstractLongList
		// READ_ONLY principle, knowing what we do
		this.size = j;
		// null if nothing set
		return duplicates;
	}

	@Override
	public LongPairArrayList copy() {
		final LongPairArrayList copy = new LongPairArrayList(elements.clone());
		copy.setSizeRaw(size);
		copy.maxObject = this.maxObject;
		copy.maxSubject = this.maxSubject;
		return copy;

	}

	/**
	 * Segdewick quicksort, subject first, adapted to long pairs
	 */
	public void quicksortObject() {
		// Shuffle elements to guard from worst case
		shuffleFull();
		recursiveQsortObject(0, this.size);
	}

	/**
	 * Sort from from inclusive to to exclusive
	 *
	 * @param array
	 * @param from
	 * @param to
	 */
	private void partialCountingSort(final long[] array, final int from,
			final int to) {
		// Compute width
		long low = array[0];
		long high = array[0];
		for (int i = from; i < to; i++) {
			final long elem = array[i];
			low = low > elem ? elem : low;
			high = high < elem ? elem : high;
		}
		final int width = (int) (high - low + 1);
		final int[] counts = new int[width]; // this will hold
		// all possible
		// values, from low to high
		for (int i = from; i < to; i++) {
			final int val = (int) (array[i] - low);
			counts[val]++; // - low so the lowest possible
			// value is always 0
		}

		int current = from;
		for (int i = 0; i < counts.length; i++) {
			Arrays.fill(array, current, current + counts[i], i + low);
			current += counts[i]; // leap forward by counts[i] steps
		}
	}

	/**
	 * Sort a subarray using counting sort
	 *
	 * @param from
	 * @param to
	 */
	public void partialCountinSort(final int from, final int to) {
		partialCountingSort(elements, from, to);
	}

	/**
	 * Update of the growths from 3/2 to 2 gives a 10-15% performance gain.
	 */
	@Override
	public void ensureCapacity(final int minCapacity) {
		final int oldCapacity = this.elements.length;
		long[] newArray;
		if (minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 4) / 2;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}

			newArray = new long[newCapacity];
			System.arraycopy(elements, 0, newArray, 0, oldCapacity);
			elements = newArray;
		}
	}

	/**
	 * Insertion sort for sorting small arrays
	 *
	 * Algo from Wikipedia ;)
	 *
	 * @param array
	 *            array to sort
	 * @param from
	 *            starting index (inclusive)
	 * @param to
	 *            ending index (exclusive)
	 */
	private void singleInsertionSort(final long[] array, final int from,
			final int to) {
		for (int i = from; i < to; i++) {
			int j = i;
			final long tmp = array[i];
			while ((j > from) && (array[j - 1] > tmp)) {
				array[j] = array[j - 1];
				j--;
			}
			array[j] = tmp;
		}
	}

	public void pairInsertionSortNoDuplicate() {

		for (int i = 2; i < this.size - 1; i += 2) {
			int j = i;

			while ((j > 0)
					&& ((elements[j - 2] > elements[j]) || ((elements[j - 2]) == elements[j] && elements[j - 1] > elements[j + 1]))) {
				swap(j, j - 2);
				swap(j + 1, j - 1);
				j -= 2;
			}
		}
		// One pass for removing duplicates,same as quicksort
		long previouss = -1;
		long previouso = -1;
		int shifting = 0;
		for (int i = 0; i < this.size; i++) {
			final long s = elements[i];
			final long o = elements[++i];
			if (s == previouss && o == previouso) {
				shifting++;
				continue;
			}
			if (shifting > 0) {
				elements[(i - 1) - (shifting * 2)] = s;
				elements[i - (shifting * 2)] = o;
			}
			previouss = s;
			previouso = o;
		}
		this.size -= shifting * 2;

	}

	public void pairInsertionObjectSort() {
		for (int i = 3; i < this.size; i += 2) {
			int j = i;
			while ((j > 1)
					&& ((elements[j - 2] > elements[j]) || ((elements[j - 2]) == elements[j] && elements[j - 3] > elements[j - 1]))) {
				swap(j, j - 2);
				swap(j - 1, j - 3);
				j -= 2;
			}

		}

	}

	/**
	 *
	 * @return the maximal value of <code>subject</code> in this list
	 */
	public long getMaxSubject() {
		return maxSubject;
	}

	/**
	 *
	 * @return the maximal value of <code>object</code> in this list
	 */
	public long getMaxObject() {
		return maxObject;
	}

	public void updateMaxSubject() {
		this.maxSubject = this.elements[size - 2];
	}

	public int getProperty() {
		return property;
	}

	public void setProperty(final int property) {
		this.property = property;
	}

}
