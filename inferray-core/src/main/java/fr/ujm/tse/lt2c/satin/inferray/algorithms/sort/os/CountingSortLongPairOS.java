package fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so.CountingSortLongPairSO;

/**
 * Adapted counting-sort for sorting pairs. Also falls back on insertion when
 * dealing with small arrays.
 * 
 * @author Julien
 * 
 */
public class CountingSortLongPairOS {

	private final static int CUTOFF = 20;

	public static void objectCountSort(final long[] theElements,
			final int size) {
		final int from = 1;
		final int to = size - 1;
		long min = theElements[from];
		long max = theElements[from];

		for (int i = from; i <= to; i += 2) {
			final long elem = theElements[i];
			max = elem > max ? elem : max;
			min = elem < min ? elem : min;
		}
		if (size == 0) {
			return;
		}
		// checkRangeFromTo(from, to, size); Useless check removed

		int[] counts = new int[(int) (max - min + 1)];

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

		final long[] adjacents = new long[size / 2];

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
			if (startingPosition[i + 1] - startingPosition[i] < CUTOFF) {
				CountingSortLongPairSO.singleInsertionSort(adjacents,
						startingPosition[i], startingPosition[i + 1]);
			} else { // Got for Java hybrid sorting, much more efficient than
				// using a counting sort here
				java.util.Arrays.sort(adjacents, startingPosition[i],
						startingPosition[i + 1]);
			}
		}
		// Sort the last array part

		if (adjacents.length - startingPosition[startingPosition.length - 1] < CUTOFF) {
			CountingSortLongPairSO.singleInsertionSort(adjacents,
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
				theElements[j] = adjacents[l++];
				theElements[j + 1] = min + i;
				j += 2;
			}
		}
		counts = null;

	}
}
