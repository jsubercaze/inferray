package fr.ujm.tse.satin.inferray.test.sorting.longs.os;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.inferray.algorithms.sort.os.MSDLongPairsOptimAdaptativeOS;
import fr.ujm.tse.satin.reasoner.sorting.pairs.longs.os.LongPairOS;

public class TestMSDLongPairSortingOS {

	private static final int RANGE = 20_000_000;

	@Test
	public void test() {
		final long[] a = { 8, 3, 1, 5, 1, 3, 32, 12, 534, 7, 13, 9, 1, 1 };
		long[] acopy = new long[a.length];
		System.arraycopy(a, 0, acopy, 0, a.length);
		List<LongPairOS> pair = LongPairOS.fromLongArray(acopy);
		Collections.sort(pair);
		long[] sorted = LongPairOS.toLongArray(pair);
		MSDLongPairsOptimAdaptativeOS.sort(a, a.length);
		System.out.println("Expected " + Arrays.toString(sorted));
		System.out.println("Sorted MSD " + Arrays.toString(a));
		assertArrayEquals(sorted, a);
		// Insertion sort test at the limits for key
		final long[] c = { 17499590, 18140711, 18150104, 213698, 4297713,
				12214668, 15438828, 8086989, 4821357, 8352918, 7360612,
				12640692, 14102073, 14988865, 18639847, 9006527, 17429228,
				12658133, 3543691, 652517, 17553037, 7609533, 18978368,
				1981174, 11752722, 12577398, 7036381, 688603, 17503348,
				19446691, 8971717, 127478, 14552939, 1385789, 17266229,
				14788003, 12412363, 15922663, 7598638, 14021088 };
		acopy = new long[c.length];
		System.arraycopy(c, 0, acopy, 0, c.length);
		pair = LongPairOS.fromLongArray(acopy);
		Collections.sort(pair);
		sorted = LongPairOS.toLongArray(pair);
		MSDLongPairsOptimAdaptativeOS.sort(c, c.length);
		System.out.println(Arrays.toString(c));
		System.out.println(Arrays.toString(sorted));
		assertArrayEquals(sorted, c);

	}

	// @Test
	public void randomizedTest() {
		final Random r = new Random();
		final int size = 1000;
		final long[] a = new long[size];
		for (int i = 0; i < 1_000; i++) {
			for (int j = 0; j < size; j++) {
				a[j] = r.nextInt(RANGE);
			}
			long[] acopy = new long[a.length];
			final long[] acopy2 = new long[a.length];
			System.arraycopy(a, 0, acopy, 0, a.length);
			System.arraycopy(a, 0, acopy2, 0, a.length);
			final List<LongPairOS> pair = LongPairOS.fromLongArray(acopy);
			Collections.sort(pair);
			acopy = LongPairOS.toLongArray(pair);
			MSDLongPairsOptimAdaptativeOS.sort(a, a.length);
			if (!Arrays.equals(acopy, a)) {
				System.out.println(Arrays.toString(acopy2));
			}
			assertArrayEquals(acopy, a);

		}
	}
}
