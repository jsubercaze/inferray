package fr.ujm.tse.satin.reasoner.sorting;

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

 * 
 ***********************************************************************************/

public class MSD {
	private static final int BITS_PER_BYTE = 8;
	private static final int BITS_PER_INT = 32; // each Java int is 32 bits
	private static final int R = 256; // extended ASCII alphabet size
	private static final int CUTOFF = 1; // cutoff to insertion sort

	public static void sort(final int[] a) {
		final int N = a.length;
		final int[] aux = new int[N];
		sort(a, 0, N - 1, 0, aux);
	}

	// MSD sort from a[lo] to a[hi], starting at the dth byte
	private static void sort(final int[] a, final int lo, final int hi,
			final int d, final int[] aux) {
		System.out.println();
		System.out
		.println("-------------------------------------------------------------");
		System.out.println(d + "-ieme byte");
		System.out.println("Array " + Arrays.toString(a));
		System.out.println("Low : " + lo);
		System.out.println("High : " + hi);
		// cutoff to insertion sort for small subarrays
		if (hi <= lo + CUTOFF) {
			insertion(a, lo, hi, d);
			return;
		}

		// compute frequency counts (need R = 256)
		final int[] count = new int[R + 1];
		final int mask = R - 1; // 0xFF;
		final int shift = BITS_PER_INT - BITS_PER_BYTE * d - BITS_PER_BYTE;
		for (int i = lo; i <= hi; i++) {
			System.out.println("i " + i + " : " + a[i]);
			final int c = (a[i] >> shift) & mask;
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
		for (int i = lo; i <= hi; i++) {

			final int c = (a[i] >> shift) & mask;
			System.out.println("c " + c + " " + Arrays.toString(aux));
			System.out.println("Count c = " + count[c]);
			aux[count[c]++] = a[i];
			System.out.println(Arrays.toString(aux));
		}
		System.out.println("Aux");
		System.out.println(Arrays.toString(aux));
		// copy back
		for (int i = lo; i <= hi; i++) {
			a[i] = aux[i - lo];
		}
		System.out.println("Copy back");
		System.out.println(Arrays.toString(a));
		// no more bits
		if (d == 4) {
			//
			return;
		}

		// recursively sort for each character
		if (count[0] > 0) {
			System.out.println("ok go no check " + count[0]);
			sort(a, lo, lo + count[0] - 1, d + 1, aux);
		}
		for (int r = 0; r < R; r++) {
			if (count[r + 1] > count[r]) {
				System.out.println("ok go for r" + r);
				sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
			}
		}




	}

	// insertion sort a[lo..hi], starting at dth character
	private static void insertion(final int[] a, final int lo, final int hi,
			final int d) {
		for (int i = lo; i <= hi; i++) {
			for (int j = i; j > lo && a[j] < a[j - 1]; j--) {
				exch(a, j, j - 1);
			}
		}
	}

	// exchange a[i] and a[j]
	private static void exch(final int[] a, final int i, final int j) {
		final int temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	public static void main(final String[] args) {
		final int[] a = { 29, 15, 235682145, 1445142, 12 };
		sort(a);

	}
}