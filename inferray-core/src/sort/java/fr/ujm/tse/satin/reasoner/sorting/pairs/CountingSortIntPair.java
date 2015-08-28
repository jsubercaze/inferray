package fr.ujm.tse.satin.reasoner.sorting.pairs;

public class CountingSortIntPair {

	private final static int CUTOFF = 20;

	static void sort(final int[] elements) {
		// Counting sort
		final int from = 0;
		final int to = elements.length - 1;
		int min = elements[from];
		int max = elements[from];

		final int[] theElements = elements;
		for (int i = from + 2; i <= to; i += 2) {

			final int elem = theElements[i];
			max = elem > max ? elem : max;
			min = elem < min ? elem : min;
			// check only one every two
		}

		final int width = max - min + 1;

		int[] counts = new int[width];
		for (int i = from; i <= to; i += 2) {
			counts[theElements[i] - min]++;
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
		int[] adjacents = new int[elements.length >> 1];

		// logger.debug("Startin position " +
		// Arrays.toString(startingPosition));
		// Here we add the next values
		for (int i = from; i <= to; i += 2) {
			// Position in histogram
			final int position = startingPosition[theElements[i] - min];
			// number of this value remaining
			final long remaining = copycounts[theElements[i] - min];
			copycounts[theElements[i] - min]--;
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

		for (int i = 0; i < width; i++) {
			final int val = counts[i];
			final int subject = min + i;
			// logger.debug("Val " + val);
			for (int k = 0; k < val; k++) {
				final int adjacent = adjacents[l++];
				// logger.debug("Adjacent " + adjacent + " Subject " + subject);
				// logger.debug("LAdjacent " + lastAdjacent + " LSubject "
				// // + lastSubject);

				elements[j++] = subject;
				elements[j++] = adjacent;

			}

		}
		// Release auxiliary structures
		adjacents = null;
		counts = null;

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
	private static void singleInsertionSort(final int[] array, final int from,
			final int to) {
		for (int i = from; i < to; i++) {
			int j = i;
			final int tmp = array[i];
			while ((j > from) && (array[j - 1] > tmp)) {
				array[j] = array[j - 1];
				j--;
			}
			array[j] = tmp;
		}
	}
}
