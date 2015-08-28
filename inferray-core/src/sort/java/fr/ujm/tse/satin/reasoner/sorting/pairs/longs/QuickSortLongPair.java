package fr.ujm.tse.satin.reasoner.sorting.pairs.longs;

import java.util.Random;

public class QuickSortLongPair {
	long[] elements;
	private final int size;

	public QuickSortLongPair(final long[] elements) {
		super();
		this.elements = elements;
		this.size = elements.length;
	}

	public void sort() {
		shuffleFull();

		recursiveQsort(0, this.size);

	}

	/**
	 * Recursive quicksort for subject sorting
	 * 
	 * @param start
	 *            start index
	 * @param end
	 *            end index
	 */
	private void recursiveQsort(final int start, final int end) {

		if (end - start < 3) {
			// stop clause
			return;
		}
		int p = start + ((end - start) / 2);
		if (p % 2 != 0) {
			p++;
		}

		p = totalpartitionSubject(p, start, end);
		recursiveQsort(start, p);
		recursiveQsort(p + 2, end);
	}

	private void shuffleFull() {

		int N = this.size;
		N = N >> 1;
		final Random r = new Random();
		for (int i = 0; i < N; i++) {
			final int pos = r.nextInt(N) << 1;
			swap((i << 1), pos);
			swap((i << 1) + 1, pos + 1);
		}
	}

	/**
	 * Swap two values at the given indices
	 * 
	 * @param posa
	 * @param posb
	 */
	private void swap(final int posa, final int posb) {
		if (posa == posb) {
			return;
		}
		final long tmp = elements[posa];
		elements[posa] = elements[posb];
		elements[posb] = tmp;
	}

	/**
	 * Partition for the quicksort, using a s,o comparator
	 * 
	 * @param p
	 * @param start
	 * @param end
	 * @return
	 */
	private int totalpartitionSubject(final int p, final int start,
			final int end) {

		int l = start;
		int h = end - 2;
		final long piv = elements[p];
		final long pivnext = elements[p + 1];

		swap(p, end - 2);
		swap(p + 1, end - 1);

		while (l < h) {

			if (compareArrayElementsValueSubject(l, piv, pivnext) == -1) {
				l += 2;
			} else if (compareArrayElementsValueSubject(h, piv, pivnext) > -1) {
				h -= 2;
			} else {
				swap(l, h);
				swap(l + 1, h + 1);
			}
		}

		int idx = h;
		if (compareArrayElementsValueSubject(h, piv, pivnext) == -1) {
			idx += 2;
		}

		swap(end - 2, idx);
		swap(end - 1, idx + 1);

		return idx;
	}

	private int compareArrayElementsValueSubject(final int aindex,
			final long value, final long next) {
		if (elements[aindex] < value) {
			return -1;
		}
		if (elements[aindex] > value) {
			return 1;
		}
		if (elements[aindex + 1] < next) {
			return -1;
		}
		if (elements[aindex + 1] > next) {
			return 1;
		}
		return 0;
	}

}
