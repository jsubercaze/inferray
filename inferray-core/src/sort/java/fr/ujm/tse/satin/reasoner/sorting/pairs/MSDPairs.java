package fr.ujm.tse.satin.reasoner.sorting.pairs;

/**
 * 
 * 
 */
public class MSDPairs {
	private static final int BITS_PER_BYTE = 8;
	private static final int BITS_PER_LONG = 64; // each Java int is 32 bits
	private static final int R = 256;
	private static final int CUTOFF = 15; // cutoff to insertion sort

	// MSD sort array of integers
	public static void sort(final long[] a) {
		final int N = a.length;
		final long[] aux = new long[N];
		sort(a, 0, N - 1, 0, aux);
	}

	// MSD sort from a[lo] to a[hi], starting at the dth byte
	/**
	 * @param a
	 * @param lo
	 * @param hi
	 * @param d
	 * @param aux
	 */
	private static void sort(final long[] a, final int lo, final int hi,
			final int d, final long[] aux) {

		// cutoff to insertion sort for small subarrays
		if (hi <= lo + CUTOFF) {
			insertion(a, lo, hi, d);
			return;
		}

		// compute frequency counts (need R = 256)
		final int[] count = new int[R + 1];
		final int mask = R - 1; // 0xFF;
		final int shift = BITS_PER_LONG - BITS_PER_BYTE * d - BITS_PER_BYTE;
		for (int i = lo; i <= hi; i++) {
			final int c = (int) (a[i] >> shift) & mask;
			count[c + 1]++;
		}

		// transform counts to indicies
		for (int r = 0; r < R; r++) {
			count[r + 1] += count[r];
		}

		/*************
		 * BUGGGY // for most significant byte, 0x80-0xFF comes before 0x00-0x7F
		 * if (d == 0) { int shift1 = count[R] - count[R/2]; int shift2 =
		 * count[R/2]; for (int r = 0; r < R/2; r++) count[r] += shift1; for
		 * (int r = R/2; r < R; r++) count[r] -= shift2; }
		 ************************************/
		// distribute
		for (int i = lo; i <= hi; i++) {
			final int c = (int) (a[i] >> shift) & mask;
			aux[count[c]++] = a[i];
		}

		// copy back
		for (int i = lo; i <= hi; i++) {
			a[i] = aux[i - lo];
		}

		// no more bits
		if (d == (BITS_PER_LONG / BITS_PER_BYTE)) {
			return;
		}

		// recursively sort for each character
		if (count[0] > 0) {
			sort(a, lo, lo + count[0] - 1, d + 1, aux);
		}
		for (int r = 0; r < R; r++) {
			if (count[r + 1] > count[r]) {
				sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
			}
		}
	}

	// insertion sort a[lo..hi], starting at dth character
	private static void insertion(final long[] a, final int lo, final int hi,
			final int d) {
		for (int i = lo; i <= hi; i++) {
			for (int j = i; j > lo && a[j] < a[j - 1]; j--) {
				exch(a, j, j - 1);
			}
		}
	}

	// exchange a[i] and a[j]
	private static void exch(final long[] a, final int i, final int j) {
		final long temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
}
