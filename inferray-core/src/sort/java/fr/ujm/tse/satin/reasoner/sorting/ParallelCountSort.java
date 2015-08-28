package fr.ujm.tse.satin.reasoner.sorting;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

/**
 * 
 * @author Julien Subercaze
 * 
 */
public class ParallelCountSort {

	static void parallelCountSort(final int[] array) {
		// Compute width
		int min = array[0];
		int max = array[0];
		for (int i = 0; i <= array.length - 1; i++) {
			final int elem = array[i];
			if (elem > max) {
				max = elem;
			} else if (elem < min) {
				min = elem;
			}
		}
		final int width = max - min + 1;
		// Instantiate the top forkcountingsort
		final ForkCountingSort fcs = new ForkCountingSort(array, 0,
				array.length - 1, min, width);
		// Initiate pool
		final ForkJoinPool pool = new ForkJoinPool();
		// Compute
		final int[] histogram = pool.invoke(fcs);
		// Unfold the histogram into the array
		int current = 0;
		for (int i = 0; i < histogram.length; i++) {
			Arrays.fill(array, current, current + histogram[i], i + min);
			current += histogram[i];
		}

	}

	public static void radixWayCountingSort(final int[] array){
		int low = array[0];
		int high = array[0];
		for (int i = 0; i <= array.length - 1; i++) {
			final int elem = array[i];
			low = elem < low ? elem : low;
			high = elem > high ? elem : high;

		}

	}

	public static void countingSort(final int[] array) {
		// Compute width
		int low = array[0];
		int high = array[0];
		for (int i = 0; i <= array.length - 1; i++) {
			final int elem = array[i];
			low = elem < low ? elem : low;
			high = elem > high ? elem : high;
		}

		final int[] counts = new int[(high - low + 1)]; // this will hold all
		// possible
		// values, from low to high
		for (final long x : array) {
			counts[(int)(x - low)]++; // - low so the lowest possible value is always 0
		}

		int current = 0;
		for (int i = 0; i < counts.length; i++) {
			Arrays.fill(array, current, current + counts[i], i + low);
			current += counts[i]; // leap forward by counts[i] steps
		}
	}

	public static void branchlesscountingSort1(final int[] array) {
		// Compute width
		int low = array[0];
		int high = array[0];
		for (int i = 0; i <= array.length - 1; i++) {
			final int elem = array[i];
			low = elem < low ? elem : low;
			high = elem > high ? elem : high;
		}
		final int[] counts = new int[high - low + 1]; // this will hold all
		// possible
		// values, from low to high
		for (final int x : array) {
			counts[x - low]++; // - low so the lowest possible value is always 0
		}

		int current = 0;
		for (int i = 0; i < counts.length; i++) {
			Arrays.fill(array, current, current + counts[i], i + low);
			current += counts[i]; // leap forward by counts[i] steps
		}
	}

	public static void partialCountingSort(final long[] array, final int from,
			final int to) {
		if ((to - from) < 2) {
			java.util.Arrays.sort(array, from, to);
			return;
		}
		// Compute width
		long low = array[from];
		long high = array[from];
		for (int i = from; i <= to; i++) {
			final long elem = array[i];
			if (elem > high) {
				high = elem;
			} else if (elem < low) {
				low = elem;
			}
		}
		final int[] counts = new int[(int) (high - low + 1)]; // this will hold
		// all
		// possible
		// values, from low to high
		for (int i = from; i <= to; i++) {
			try {
				counts[(int) (array[i] - low)]++; // - low so the lowest
				// possible value
				// is always 0
			} catch (final Exception e) {
				System.err.println(Arrays.toString(java.util.Arrays
						.copyOfRange(array, from, to)));
				System.err.println(counts.length);
				System.err.println(array[i]);
				System.err.println(low);
				System.err.println(high);
				System.err.println(array[i] - low);
				System.exit(-1);
			}
		}

		int current = from;
		for (int i = 0; i < counts.length; i++) {
			java.util.Arrays.fill(array, current, current + counts[i], i + low);
			current += counts[i]; // leap forward by counts[i] steps
		}
	}
}
