package fr.ujm.tse.satin.reasoner.sorting;

public class RadixAlternate {

	public static int[] radixSort(int[] a) {
		final int w =32;
		final int d=16;
		int[] b = null;
		for (int p = 0; p < w / d; p++) {
			final int c[] = new int[1 << d];
			// the next three for loops implement counting-sort
			b = new int[a.length];
			for (int i = 0; i < a.length; i++) {
				c[(a[i] >> d * p) & ((1 << d) - 1)]++;
			}
			for (int i = 1; i < 1 << d; i++) {
				c[i] += c[i - 1];
			}
			for (int i = a.length - 1; i >= 0; i--) {
				b[--c[(a[i] >> d * p) & ((1 << d) - 1)]] = a[i];
			}
			a = b;
		}
		return b;
	}
}
