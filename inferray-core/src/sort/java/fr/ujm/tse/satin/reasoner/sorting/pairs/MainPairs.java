package fr.ujm.tse.satin.reasoner.sorting.pairs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author Julien
 * 
 */
public class MainPairs {

	private static final int SIZE = 5_000_000;
	private static final int RANGE = 1_000_000;

	public static void main(final String[] args) {
		final Random r = new Random();
		final long[] test = new long[SIZE];
		for (int i = 0; i < SIZE; i++) {
			test[i] = (long) Integer.MAX_VALUE + r.nextInt(RANGE);
		}

		long[] test2 = Arrays.copyOf(test, test.length);
		final long[] test3 = Arrays.copyOf(test, test.length);
		final long[] test4 = Arrays.copyOf(test, test.length);

		long start = System.currentTimeMillis();
		final List<LongPair> list = LongPair.fromLongArray(test2);
		Collections.sort(list);
		long end = System.currentTimeMillis();
		test2 = LongPair.toLongArray(list);
		System.out.println("Java / Sort Objects" + (end - start));

		start = System.currentTimeMillis();
		new QuickSortLongPair(test3).sort();
		end = System.currentTimeMillis();
		System.out.println("Quicksort " + (end - start));

		start = System.currentTimeMillis();
		CountingSortPair.sort(test4);
		end = System.currentTimeMillis();
		System.out.println("Counting " + (end - start));

		//		System.out.println(Arrays.toString(test));
		//		System.out.println(Arrays.toString(test2));
		//		System.out.println(Arrays.toString(test3));
		//		System.out.println(Arrays.toString(test4));
		System.out.println(Arrays.equals(test2, test3));
		System.out.println(Arrays.equals(test2, test4));

	}
}
