package fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.so;

/**
 * Adapted counting-sort for sorting pairs. Also falls back on insertion when
 * dealing with small arrays.
 * 
 * @author Julien
 * 
 */
public class CountingSortLongPairSO {

	private final static int CUTOFF = 20;

	/**
	 * 
	 * @param elements
	 * @param initial
	 *            size before sorting and duplicate removal
	 * @return the new size of the array
	 */
	public static int sort(final long[] elements, final int size) {
		// Counting sort
		final int from = 0;
		final int to = size - 1;
		long min = elements[from];
		long max = elements[from];

		for (int i = from + 2; i <= to; i += 2) {
			final long elem = elements[i];
			max = elem > max ? elem : max;
			min = elem < min ? elem : min;
			// check only one every two
		}
		return sort(elements, size, min, max, from, to);
	}

	public static int sort(final long[] theElements, final int size,
			final long min, final long max, final int from, final int to) {
		final int width = (int) (max - min + 1);

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
		long[] adjacents = new long[size >> 1];

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
		// Now sort the adjacents by sorting subarrays with an adaptative
		// algorithm

		for (int i = 0; i < width - 1; i++) {
			// Insertion sort always better for small range
			if (startingPosition[i + 1] - startingPosition[i] < CUTOFF) {
				singleInsertionSort(adjacents, startingPosition[i],
						startingPosition[i + 1]);
			} else { // Got for Java hybrid sorting, much more efficient than
				// using a counting sort here
				java.util.Arrays.sort(adjacents, startingPosition[i],
						startingPosition[i + 1]);
			}

		}
		// last one, same as before
		if (adjacents.length - startingPosition[width - 1] < CUTOFF) {
			singleInsertionSort(adjacents, startingPosition[width - 1],
					adjacents.length);
		} else {

			java.util.Arrays.sort(adjacents, startingPosition[width - 1],
					adjacents.length);
		}

		startingPosition = null;

		// Reconstruct the array without duplicates
		int j = 0;
		int l = 0;
		long lastAdjacent = -1;
		// for (int i = 0; i < width; i++) {
		// final int val = counts[i];
		// final long subject = min + i;
		// long adjacent = adjacents[l++];
		// theElements[j++] = subject;
		// theElements[j++] = adjacent;
		// for (int k = 1; k < val; k++) {
		// adjacent = adjacents[l++];
		// final int opt = adjacent != lastAdjacent ? 0 : 2;
		// j -= opt;
		// theElements[j++] = subject;
		// theElements[j++] = adjacent;
		// lastAdjacent = adjacent;
		// }
		//
		// }

		for (int i = 0; i < width; i++) {
			final int val = counts[i];
			final long subject = min + i;
			for (int k = 0; k < val; k++) {
				final long adjacent = adjacents[l++];
				//False branch, paid once only
				if (k == 0) {
					theElements[j++] = subject;
					theElements[j++] = adjacent;
				} else {
					final int opt = adjacent != lastAdjacent ? 0 : 2;
					j -= opt;
					theElements[j++] = subject;
					theElements[j++] = adjacent;

				}
				lastAdjacent = adjacent;

			}

		}

		// for (int i = 0; i < width; i++) {
		// final int val = counts[i];
		// final long subject = min + i;
		// for (int k = 0; k < val; k++) {
		// final long adjacent = adjacents[l++];
		// if (k == 0 || adjacent != lastAdjacent) {
		// theElements[j++] = subject;
		// theElements[j++] = adjacent;
		//
		// }
		// lastAdjacent = adjacent;
		//
		// }
		//
		// }

		// Release auxiliary structures
		adjacents = null;
		counts = null;
		return j;

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
	public static void singleInsertionSort(final long[] array, final int from,
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
}
