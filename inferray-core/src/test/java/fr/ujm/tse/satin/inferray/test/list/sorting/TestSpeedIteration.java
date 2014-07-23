package fr.ujm.tse.satin.inferray.test.list.sorting;

import java.util.Random;

import fr.ujm.tse.lt2c.satin.inferray.datastructure.LongPairArrayList;

public class TestSpeedIteration {

	public static final int SMALL_SIZE = 1_000;
	public static final int LARGE_SIZE = 1_000_000;

	public static final int MAX_VALUE = 100;

	public static void main(final String[] args) {
		final LongPairArrayList small = new LongPairArrayList(SMALL_SIZE);
		final LongPairArrayList large = new LongPairArrayList(LARGE_SIZE);
		final LongPairArrayList result = new LongPairArrayList(100);
		final Random r = new Random();

		for (int i = 0; i < SMALL_SIZE; i++) {
			small.add(r.nextInt(MAX_VALUE));
		}
		for (int i = 0; i < LARGE_SIZE; i++) {
			large.add(r.nextInt(MAX_VALUE));
		}
		small.totalSortingNoDuplicate();
		large.totalSortingNoDuplicate();
		final long t1 = System.currentTimeMillis();
		// Iterate on the values

		int indexLarge = 0;
		for (int i = 0; i < small.size(); i++) {
			final long s1 = small.getQuick(i);
			final long o1 = small.getQuick(++i);
			for (; indexLarge < large.size() - 1; indexLarge++) {
				final long s2 = large.getQuick(indexLarge);
				final long o2 = large.getQuick(++indexLarge);
				if (s1 == o2) {
					result.add(s2);
					result.add(o1);
				} else if (o2 > s1) {
					break;
				}
			}
		}

		// int indexLarge = 0;
		// for (int i = 0; i < large.size(); i++) {
		// long s1 = large.getQuick(i);
		// long o1 = large.getQuick(++i);
		// for (; indexLarge < small.size() - 1; indexLarge++) {
		// long s2 = small.getQuick(indexLarge);
		// long o2 = small.getQuick(++indexLarge);
		// if (s1 == o2) {
		// result.add(s2);
		// result.add(o1);
		// } else if (o2 > s1) {
		// break;
		// }
		// }
		// }

		System.out.println((System.currentTimeMillis() - t1) + "ms");
	}
}
