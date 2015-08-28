package fr.ujm.tse.satin.reasoner.sorting.longs;

import java.util.Arrays;

public class LongCounting {

	private static void countingSort(final long[] array, final int from,
			final int to) {
		// Compute width
		long low = array[0];
		long high = array[0];
		for (int i = from; i < to; i++) {
			final long elem = array[i];
			low = elem < low ? elem : low;
			high = elem > high ? elem : high;
		}

		final int[] counts = new int[(int) (high - low + 1)]; // this will hold
		// all
		// possible
		// values, from low to high
		for (final long x : array) {
			counts[(int) (x - low)]++; // - low so the lowest possible value is
			// always 0
		}

		int current = 0;
		for (long i = 0; i < counts.length; i++) {

			Arrays.fill(array, current, current + counts[(int) i], i + low);
			current += counts[(int) i]; // leap forward by counts[i] steps
		}
	}

	/**
	 * In place in counting sort
	 * 
	 * @param array
	 */
	public static void singleInsertionSort(final long[] array) {
		final int from = 0;
		final int to = array.length;
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

	public static void countingSort(final long[] array) {
		countingSort(array, 0, array.length);

	}
}
