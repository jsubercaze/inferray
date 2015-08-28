package fr.ujm.tse.satin.reasoner.sorting.pairs.longs;

/***********************************************************************************
 * Compilation: javac MSD.java Execution: java MSD < input.txt
 * 
 * - Sort a String[] array of N extended ASCII strings (R = 256), each of length
 * W.
 * 
 * - Sort an int[] array of N 32-bit integers, treating each integer as a
 * sequence of W = 4 bytes (R = 256).
 * 
 * 
 * % java MSD < shells.txt are by sea seashells seashells sells sells she she
 * shells shore surely the the
 * 
 ***********************************************************************************/

public class MSDLongPairs {
	private static final int BITS_PER_BYTE = 8;
	private static final int BITS_PER_INT = 32; // each Java int is 32 bits
	private static final int R = 256; // extended ASCII alphabet size
	private static final int CUTOFF = 20; // cutoff to insertion sort

	// MSD sort array of integers
	public static void sort(final long[] a) {

		final int N = a.length;
		final long[] aux = new long[N];
		sortKey(a, 0, N - 1, 0, aux);
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
		final int shift = BITS_PER_INT - BITS_PER_BYTE * d - BITS_PER_BYTE;

		for (int i = lo; i <= hi; i++) {

			final int c = (int)(a[i++] >> shift) & mask;

			count[c + 1]++;
		}

		// transform counts to indicies
		for (int r = 0; r < R; r++) {
			count[r + 1] += count[r];
		}

		// distribute
		for (int i = lo; i < hi; i++) {
			final int c = (int)(a[i] >> shift) & mask;

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

		// no more bits
		if (d == 3) {
			if (count[0] > 0) {

				sortValue(a, lo + 1, lo + (count[0] - 1) * 2 + 1, 0, aux);
				// sort(a, lo, lo + count[0] - 1, d + 1, aux);
			}
			// Check everywhere it fails to have 1
			for (int r = 0; r < R; r++) {
				if (count[r + 1] > count[r] && (count[r + 1] - count[r]) > 1) {
					// Go for r

					final int start = lo + count[r] * 2 + 1;
					final int end = start + (count[r + 1] - count[r] - 1) * 2;
					sortValue(a, start, end, 0, aux);
				}
			}
			return;
		}

		// recursively sort for each character
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
		final int shift = BITS_PER_INT - BITS_PER_BYTE * d - BITS_PER_BYTE;

		for (int i = lo; i <= hi; i++) {

			final int c = (int)(a[i++] >> shift) & mask;

			count[c + 1]++;
		}

		// transform counts to indicies
		for (int r = 0; r < R; r++) {
			count[r + 1] += count[r];
		}

		for (int i = lo; i <= hi; i++) {

			final int c = (int)(a[i] >> shift) & mask;

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

		// no more bits
		if (d == 3) {

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