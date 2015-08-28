package fr.ujm.tse.satin.inferray.test.sorting.ints;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import fr.ujm.tse.satin.reasoner.sorting.pairs.IntPair;
import fr.ujm.tse.satin.reasoner.sorting.pairs.MSDIntPairs;
import fr.ujm.tse.satin.reasoner.sorting.pairs.MSDIntPairsDebugOPtim;

public class TestMSDPairSorting {


	@Test
	public void test() {
		final int[] a = { 8, 3, 1, 5, 1, 3, 536870912, 12, 536870912, 7,
				131072, 9, 1, 1 };
		int[] acopy = new int[a.length];
		// System.arraycopy(a, 0, acopy, 0, a.length);
		List<IntPair> pair = IntPair.fromIntArray(acopy);
		Collections.sort(pair);
		int[] sorted = IntPair.toIntArray(pair);

		// Insertion sort test at the limits for key
		final int[] c = { 8, 3, 1, 5, 1, 3, 1, 6, 1, 12, 536870912, 12,
				536870912, 7, 536870912, 46541,  1, 1 };
		acopy = new int[c.length];
		System.arraycopy(c, 0, acopy, 0, c.length);
		pair = IntPair.fromIntArray(acopy);
		Collections.sort(pair);
		sorted = IntPair.toIntArray(pair);
		MSDIntPairsDebugOPtim.sort(c);
		assertArrayEquals(sorted, c);

		// MSDIntPairs.sort(a);
		// assertArrayEquals(sorted, a);
		// -----------------------------------------------------------
		// final int[] b = { 8, 3, 1, 536870913, 1, 536870914, 536870912, 12,
		// 536870912, 7, 131072, 9, 1, 532354582 };
		// final int[] b = { 880, 47, 491, 349, 265, 360, 948, 450, 924, 726,
		// 12,
		// 5, 9, 320 };
		// final int[] b = { 17, 58, 2, 71, 83, 99, 76, 1, 79, 63, 61, 74, 3,
		// 99,
		// 35, 18, 9, 82, 45, 82, 56, 26, 45, 7, 11, 32, 54, 31, 29, 90,
		// 93, 23, 51, 72, 19, 42, 17, 61, 61, 14, 28, 40, 21, 89, 21, 51,
		// 59, 75, 97, 31, 64, 79, 25, 2, 91, 36, 62, 18, 64, 3, 22, 43,
		// 5, 74, 7, 61, 21, 15, 93, 16, 80, 55, 26, 17, 27, 42, 82, 6, 5,
		// 45, 46, 13, 46, 56, 9, 9, 68, 6, 14, 23, 82, 66, 85, 25, 54,
		// 52, 41, 33, 47, 25 };
		// acopy = new int[b.length];
		// System.arraycopy(b, 0, acopy, 0, b.length);
		// pair = IntPair.fromIntArray(acopy);
		// Collections.sort(pair);
		// sorted = IntPair.toIntArray(pair);
		// MSDIntPairsDebug.sort(b);
		// assertArrayEquals(sorted, b);

	}

	public void randomizedTest() {
		final Random r = new Random();
		final int size = 1_000;
		final int[] a = new int[size];
		for (int i = 0; i < 100_000; i++) {
			for (int j = 0; j < size; j++) {
				a[j] = r.nextInt(10000);
			}
			int[] acopy = new int[a.length];
			final int[] acopy2 = new int[a.length];
			System.arraycopy(a, 0, acopy, 0, a.length);
			System.arraycopy(a, 0, acopy2, 0, a.length);
			final List<IntPair> pair = IntPair.fromIntArray(acopy);
			Collections.sort(pair);
			acopy = IntPair.toIntArray(pair);
			MSDIntPairs.sort(a);
			if (!Arrays.equals(acopy, a)) {
				System.out.println(Arrays.toString(acopy2));
			}
			assertArrayEquals(acopy, a);

		}
	}
}
