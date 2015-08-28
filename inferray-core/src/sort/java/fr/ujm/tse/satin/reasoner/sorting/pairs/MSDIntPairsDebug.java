package fr.ujm.tse.satin.reasoner.sorting.pairs;

import java.util.Arrays;

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

public class MSDIntPairsDebug {
	private static final int BITS_PER_BYTE = 8;
	private static final int BITS_PER_INT = 32; // each Java int is 32 bits
	private static final int R = 256; // extended ASCII alphabet size
	public static int CUTOFF = 4; // cutoff to insertion sort

	// MSD sort array of integers
	public static void sort(final int[] a) {
		System.out
		.println("-------------Sorting---------------------------------");
		System.out.println();
		System.out.println();
		final int N = a.length;
		final int[] aux = new int[N];
		sortKey(a, 0, N - 1, 0, aux);
	}

	// MSD sort from a[lo] to a[hi], starting at the dth byte
	private static void sortKey(final int[] a, final int lo, final int hi,
			final int d, final int[] aux) {
		Arrays.fill(aux, 0);// FIXME for check only - remove
		System.out.println();
		System.out
		.println("------------------SORT ON KEY---------------------------");
		System.out.println((d) + "-ieme byte");
		System.out.println("Array " + Arrays.toString(a));
		System.out.println("Low : " + lo);
		System.out.println("High : " + hi);
		// cutoff to insertion sort for small subarrays
		if (hi <= lo + CUTOFF) {
			System.out.println("Insertion");
			insertionKey(a, lo, hi);
			return;
		}

		// compute frequency counts (need R = 256)
		final int[] count = new int[R + 1];
		final int mask = R - 1; // 0xFF;
		final int shift = BITS_PER_INT - BITS_PER_BYTE * d - BITS_PER_BYTE;
		System.out.println("Shift " + shift);
		for (int i = lo; i <= hi; i++) {
			System.out.println("i " + i + " : " + a[i]);
			final int c = (a[i++] >> shift) & mask;
			System.out.println("c " + c);
			count[c + 1]++;
		}
		System.out.println("Frequency count");
		System.out.println(Arrays.toString(count));
		// transform counts to indicies

		for (int r = 0; r < R; r++) {
			count[r + 1] += count[r];

		}

		System.out.println("Indicies");
		System.out.println(Arrays.toString(count));
		// distribute
		for (int i = lo; i < hi; i++) {
			final int c = (a[i] >> shift) & mask;
			System.out.println("Moving " + a[i]);
			System.out.println("c " + c + " " + Arrays.toString(aux));
			System.out.println("Count c = " + count[c]);
			aux[count[c] * 2] = a[i];
			aux[(count[c] * 2) + 1] = a[++i];
			count[c]++;
			System.out.println(Arrays.toString(aux));
		}
		System.out.println("Aux");
		System.out.println(Arrays.toString(aux));
		// copy back
		for (int i = lo; i <= hi; i++) {
			a[i] = aux[i - lo];
			a[i + 1] = aux[i + 1 - lo];
			i++;
		}

		System.out.println("Copy back");
		System.out.println(Arrays.toString(a));
		// no more bits
		if (d == 3) {
			if (count[0] > 0) {
				System.out.println("ok go value property " + count[0]);

				sortValue(a, lo + 1, lo + (count[0] - 1) * 2 + 1, 0, aux);
				// sort(a, lo, lo + count[0] - 1, d + 1, aux);
			}
			// Check everywhere it fails to have 1
			for (int r = 0; r < R; r++) {
				if (count[r + 1] > count[r] && (count[r + 1] - count[r]) > 1) {
					// Go for r
					System.out.println("ok go value for r" + r);
					System.out.println(count[r]);
					System.out.println(count[r + 1]);

					final int start = lo + count[r] * 2 + 1;
					final int end = start + (count[r + 1] - count[r] - 1) * 2;
					sortValue(a, start, end, 0, aux);
				}
			}
			return;
		}

		// recursively sort for each character
		if (count[0] > 1) {
			System.out.println("(" + d + ") ok go no check " + count[0]);
			sortKey(a, lo, lo + (count[0] - 1) * 2 + 1, d + 1, aux);
			// sort(a, lo, lo + count[0] - 1, d + 1, aux);
			System.out.println("-------------------");
		}
		for (int r = 0; r < R; r++) {
			if (count[r + 1] > count[r] && (count[r + 1] - count[r]) > 1) {
				System.out.println(Arrays.toString(count));
				System.out.println("(" + d + ") ok go for r" + r);
				System.out.println("lo " + lo);

				System.out.println(count[r]);
				System.out.println(count[r + 1]);
				final int start = lo + count[r] * 2;
				final int end = start + (count[r + 1] - count[r]) * 2 - 1;
				sortKey(a, start, end, d + 1, aux);
				// sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
			}
		}
	}

	private static void sortValue(final int[] a, final int lo, final int hi,
			final int d, final int[] aux) {
		Arrays.fill(aux, 0);
		System.out.println();
		System.out
		.println("------------------SORT ON VALUE---------------------------");
		System.out.println(d + "-ieme byte");
		System.out.println("Array " + Arrays.toString(a));
		System.out.println("Low : " + lo);
		System.out.println("High : " + hi);
		// cutoff to insertion sort for small subarrays
		if (hi <= lo + CUTOFF) {
			System.out.println("Insert");
			insertionValue(a, lo, hi);
			return;
		}

		// compute frequency counts (need R = 256)
		final int[] count = new int[R + 1];
		final int mask = R - 1; // 0xFF;
		final int shift = BITS_PER_INT - BITS_PER_BYTE * d - BITS_PER_BYTE;
		System.out.println("Shift " + shift);
		for (int i = lo; i <= hi; i++) {
			System.out.println("i " + i + " : " + a[i]);
			final int c = (a[i++] >> shift) & mask;
			System.out.println("c " + c);
			count[c + 1]++;
		}
		System.out.println("Frequency count");
		System.out.println(Arrays.toString(count));
		// transform counts to indicies
		for (int r = 0; r < R; r++) {
			count[r + 1] += count[r];
		}
		System.out.println("Indicies");
		System.out.println(Arrays.toString(count));

		for (int i = lo; i <= hi; i++) {
			System.out.println(i);
			final int c = (a[i] >> shift) & mask;
			System.out.println("c " + c + " " + Arrays.toString(aux));
			System.out.println("Count c = " + count[c]);
			aux[(count[c] << 1) + 1] = a[i];
			System.out.println("add obj " + Arrays.toString(aux));
			aux[(count[c] << 1)] = a[(i - 1)];
			System.out.println("add subj " + Arrays.toString(aux));
			count[c]++;
			if (i < a.length) {
				i++;
			}
		}
		System.out.println("Aux");
		System.out.println(Arrays.toString(aux));
		// copy back
		for (int i = 0; i < (hi - lo) + 2; i++) {
			a[lo - 1 + i] = aux[i];

		}
		System.out.println("Copy back " + d);
		System.out.println(Arrays.toString(a));

		// no more bits
		if (d == 3) {

			return;
		}

		// recursively sort for each character
		if (count[0] > 0) {
			System.out.println("(" + d + ")v ok go no check " + count[0]);
			sortValue(a, lo, lo + (count[0] - 1) * 2, d + 1, aux);
			// sort(a, lo, lo + count[0] - 1, d + 1, aux);
		}
		for (int r = 0; r < R; r++) {
			if (count[r + 1] > count[r] && (count[r + 1] - count[r]) > 1) {
				System.out.println("(" + d + ")v ok go for r" + r);
				// FIXME test this
				final int start = lo + count[r] * 2;
				final int end = start + (count[r + 1] - count[r] - 1) * 2;
				sortValue(a, start, end, d + 1, aux);
				// sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
			}
		}

	}

	public static void main(final String[] args) {
		final int[] test = { 8, 3, 1, 5, 1, 3, 536870912, 12, 536870912, 7,
				131072, 9, 1, 1 };
		sort(test);
		System.out.println(Arrays.toString(test));

	}

	private static void insertionKey(final int[] elements, final int lo,
			final int hi) {

		for (int i = lo + 2; i < hi; i += 2) {
			int j = i;
			System.out.println("i=" + i);
			while ((j > lo)
					&& ((elements[j - 2] > elements[j]) || ((elements[j - 2]) == elements[j] && elements[j - 1] > elements[j + 1]))) {
				System.out.println("j " + j);
				System.out.println(Arrays.toString(elements));
				swap(elements, j, j - 2);
				swap(elements, j + 1, j - 1);
				System.out.println(Arrays.toString(elements));
				j -= 2;
			}
		}

	}

	private static void insertionValue(final int[] elements, final int lo,
			final int hi) {
		System.out.println("In Insert");
		for (int i = lo + 2; i <= hi; i += 2) {
			int j = i;
			System.out.println("i=" + i);
			while ((j > lo)
					&& ((elements[j - 2] > elements[j]) || ((elements[j - 2]) == elements[j] && elements[j - 3] > elements[j - 1]))) {
				System.out.println("j " + j);
				System.out.println(Arrays.toString(elements));
				swap(elements, j, j - 2);
				swap(elements, j - 1, j - 3);
				System.out.println(Arrays.toString(elements));
				j -= 2;
			}

		}

	}

	private static void swap(final int[] elements, final int posa,
			final int posb) {
		if (posa == posb) {
			return;
		}
		final int tmp = elements[posa];
		elements[posa] = elements[posb];
		elements[posb] = tmp;
	}
}