package fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.hybrid;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os.CountingSortLongPairOS;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os.InsertionSortPairsOS;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os.MSDLongPairsOptimAdaptativeOS;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so.CountingSortLongPairSO;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so.InsertionSortPairsSO;
import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so.MSDLongPairsOptimAdaptativeSO;

/**
 * Hybrid algorithm using of on the three following algorithm depending on the
 * nature of the data
 * 
 * <ul>
 * <li>{@link InsertionSortPairsSO} : insertion sort for small arrays</li>
 * <li>{@link CountingSortLongPairSO} : pair counting sort</li>
 * <li>{@link MSDLongPairsOptimAdaptativeSO} : MSD Radix sort with adaptative
 * strategy</li>
 * </ul>
 * 
 * @author Julien
 * 
 */
public class HybridInsertionCountingMSDSort {

	private static final int CUTOFF = 25;

	/**
	 * 
	 * @param elements
	 * @param size
	 * @return
	 */
	public static int sortSO(final long[] elements, final int size) {
		if (size == 0) {
			return 0;
		}
		if (size < CUTOFF) {
			return InsertionSortPairsSO.pairInsertionSortNoDuplicate(elements,
					size);
		}
		// Check the range
		final int from = 0;
		final int to = size - 1;
		long min = elements[from];
		long max = elements[from];

		final long[] theElements = elements;
		for (int i = from + 2; i <= to; i += 2) {

			final long elem = theElements[i];
			max = elem > max ? elem : max;
			min = elem < min ? elem : min;

		}

		final int width = (int) (max - min + 1);
		if (((double) width / (double) size) < 1.5) {
			return CountingSortLongPairSO.sort(elements, size, min, max, from,
					to);
		}
		return MSDLongPairsOptimAdaptativeSO.sort(elements, size);
	}

	/**
	 * 
	 * @param elements
	 * @param size
	 */
	public static void sortOS(final long[] elements, final int size) {
		if (size == 0) {
			return;
		}
		if (size < CUTOFF) {
			InsertionSortPairsOS.pairInsertionObjectSort(elements, size);
			return;
		}
		// Check the range
		final int from = 1;
		final int to = size - 1;
		long min = elements[from];
		long max = elements[from];
		final long[] theElements = elements;
		for (int i = from + 2; i <= to; i += 2) {

			final long elem = theElements[i];
			max = elem > max ? elem : max;
			min = elem < min ? elem : min;
			// check only one every two
		}
		final int width = (int) (max - min + 1);
		if (((double) width / (double) size) < 1.5) {
			CountingSortLongPairOS.objectCountSort(elements, size);
			return;
		}
		MSDLongPairsOptimAdaptativeOS.sort(elements, size);
		return;
	}

}
