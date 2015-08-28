package fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os;

/**
 * Adapted from Sedgewick lecture material. Changed to support pairs. Introduced
 * several optimizations:
 * <ul>
 * <li>Skip unsignicant most digits</li>
 * <li>Stop iterating on buckets when all elements are already sorted</li>
 * </ul>
 * 
 * Gain is about 50 to 100% to standard MSD sort version
 * 
 * @author Julien
 * 
 */
public class MSDLongPairsOptimAdaptativeOS {
	private static final int BITS_PER_BYTE = 8;
	private static final int BITS_PER_LONG = 64; // each Java int is 32 bits
	private static final int R = 256; // extended ASCII alphabet size
	private static final int CUTOFF = 20; // cutoff to insertion sort

	/**
	 * Sort the table a from 0 to n
	 * 
	 * @param a
	 *            the array to sort
	 * @param n
	 *            maximum index
	 * @return the new size after sorting and duplicate removal
	 */
	public static void sort(final long[] a, final int size) {
		final long range = computeRange(a, size);
		// MS Bytes that can be skipped
		final int d = bytesFor(range);

		// Auxiliary array, instantiate once
		final long[] aux = new long[size];
		sortValue(a, 1, size - 1, d, aux);
	}

	/**
	 * 
	 * @param a
	 * @param size
	 * @return
	 */
	private static long computeRange(final long[] a, final int size) {
		long min = a[1];
		long max = a[1];

		for (int i = 1; i < size; i += 2) {

			final long elem = a[i];
			max = elem > max ? elem : max;
			min = elem < min ? elem : min;
			// check only one every two
		}
		return (max - min);
	}

	/**
	 * 
	 * @param range
	 * @return the number of unsignificative bytes in this long
	 */
	private static int bytesFor(final long range) {
		return Math.max(
				(int) Math.floor(Long.numberOfLeadingZeros(range) / 8D) - 1, 0);
	}

	// MSD sort from a[lo] to a[hi], starting at the dth byte
	private static void sortKey(final long[] a, final int lo, final int hi,
			final int d, final long[] aux) {

		// cutoff to insertion sort for small subarrays
		if (hi <= lo + CUTOFF) {
			insertionKey(a, lo, hi);
			return;
		}

		// compute frequency counts (need R = 256)
		final int[] count = new int[R + 1];
		final int mask = R - 1; // 0xFF;
		final int shift = BITS_PER_LONG - BITS_PER_BYTE * d - BITS_PER_BYTE;

		for (int i = lo; i <= hi; i++) {

			final int c = (int) (a[i++] >> shift) & mask;

			count[c + 1]++;
		}

		// transform counts to indicies
		boolean broke = false;
		int stopped = 0;
		for (int r = 0; r < R; r++) {
			count[r + 1] += count[r];
			if (count[r + 1] == (hi - lo + 1) / 2) {
				broke = true;
				stopped = r;
				break;
			}
		}

		if (!(broke && count[stopped] == 0)) {
			// distribute
			for (int i = lo; i < hi; i++) {
				final int c = (int) (a[i] >> shift) & mask;

				aux[count[c] * 2] = a[i];
				aux[(count[c] * 2) + 1] = a[++i];
				count[c]++;

			}

			// copy back
			for (int i = lo; i <= hi; i++) {
				a[i] = aux[i - lo];
				a[i + 1] = aux[i + 1 - lo];
				i++;
			}
		}

		// no more bits
		if (d == (BITS_PER_LONG / BITS_PER_BYTE - 1)) {

			return;
		}
		// recursively sort for each byte
		if (count[0] > 1) {

			sortKey(a, lo, lo + (count[0] - 1) * 2 + 1, d + 1, aux);

		}
		for (int r = 0; r < R; r++) {
			if (count[r + 1] > count[r] && (count[r + 1] - count[r]) > 1) {

				final int start = lo + count[r] * 2;
				final int end = start + (count[r + 1] - count[r]) * 2 - 1;
				sortKey(a, start, end, d + 1, aux);
				// sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
			}
		}
	}

	private static void sortValue(final long[] a, final int lo, final int hi,
			final int d, final long[] aux) {

		// cutoff to insertion sort for small subarrays
		if (hi <= lo + CUTOFF) {

			insertionValue(a, lo, hi);
			return;
		}

		// compute frequency counts (need R = 256)
		final int[] count = new int[R + 1];
		final int mask = R - 1; // 0xFF;
		final int shift = BITS_PER_LONG - BITS_PER_BYTE * d - BITS_PER_BYTE;

		for (int i = lo; i <= hi; i++) {

			final int c = (int) (a[i++] >> shift) & mask;

			count[c + 1]++;
		}

		// transform counts to indicies
		boolean broke = false;
		int stopped = 0;

		for (int r = 0; r < R; r++) {
			count[r + 1] += count[r];
			if (count[r + 1] == (hi - lo + 2) / 2) {
				broke = true;
				stopped = r;
				break;
			}
		}

		if (!(broke && count[stopped] == 0)) {

			for (int i = lo; i <= hi; i++) {
				final int c = (int) (a[i] >> shift) & mask;
				aux[(count[c] << 1) + 1] = a[i];
				aux[(count[c] << 1)] = a[(i - 1)];
				count[c]++;
				if (i < a.length) {
					i++;
				}
			}

			// copy back
			for (int i = 0; i < (hi - lo) + 2; i++) {
				a[lo - 1 + i] = aux[i];

			}
		}

		// no more bits
		if (d == (BITS_PER_LONG / BITS_PER_BYTE - 1)) {
			if (count[0] > 0) {

				// FIXME Indices here, to go from key to values
				sortKey(a, lo - 1, lo + (count[0] - 1) * 2 - 1, 0, aux);

			}
			// Check everywhere it fails to have 1
			for (int r = 0; r < R; r++) {
				if (count[r + 1] > count[r] && (count[r + 1] - count[r]) > 1) {

					// Go for r
					// FIXME Indices here, to go from key to values
					final int start = lo + count[r] * 2 - 1;
					final int end = start + (count[r + 1] - count[r] - 1) * 2
							+ 1;
					sortKey(a, start, end, 0, aux);
				}
			}
			return;
		}

		// recursively sort for each character
		if (count[0] > 0) {

			sortValue(a, lo, lo + (count[0] - 1) * 2, d + 1, aux);
			// sort(a, lo, lo + count[0] - 1, d + 1, aux);
		}
		for (int r = 0; r < R; r++) {
			if (count[r + 1] > count[r] && (count[r + 1] - count[r]) > 1) {

				final int start = lo + count[r] * 2;
				final int end = start + (count[r + 1] - count[r] - 1) * 2;
				sortValue(a, start, end, d + 1, aux);
				// sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
			}
		}

	}

	private static void insertionKey(final long[] elements, final int lo,
			final int hi) {

		for (int i = lo + 2; i < hi; i += 2) {
			int j = i;

			while ((j > lo)
					&& ((elements[j - 2] > elements[j]) || ((elements[j - 2]) == elements[j] && elements[j - 1] > elements[j + 1]))) {

				swap(elements, j, j - 2);
				swap(elements, j + 1, j - 1);

				j -= 2;
			}
		}

	}

	private static void insertionValue(final long[] elements, final int lo,
			final int hi) {

		for (int i = lo + 2; i <= hi; i += 2) {
			int j = i;

			while ((j > lo)
					&& ((elements[j - 2] > elements[j]) || ((elements[j - 2]) == elements[j] && elements[j - 3] > elements[j - 1]))) {

				swap(elements, j, j - 2);
				swap(elements, j - 1, j - 3);

				j -= 2;
			}

		}

	}

	private static void swap(final long[] elements, final int posa,
			final int posb) {
		if (posa == posb) {
			return;
		}
		final long tmp = elements[posa];
		elements[posa] = elements[posb];
		elements[posb] = tmp;
	}

}