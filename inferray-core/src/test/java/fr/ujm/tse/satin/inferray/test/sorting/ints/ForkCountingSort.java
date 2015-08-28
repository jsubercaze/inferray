package fr.ujm.tse.satin.inferray.test.sorting.ints;

import java.util.concurrent.RecursiveTask;

public class ForkCountingSort extends RecursiveTask<int[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static int sThreshold = 10000;
	/**
	 * Histogram storing occurrences
	 */
	private final int[] localHistogram;
	/**
	 * Original Array
	 */
	private final int[] arrayTosort;
	/**
	 * Where to start sorting
	 */
	private final int begin;
	/**
	 * Where to finish
	 */
	private final int end;
	/**
	 * Width between min & max (given)
	 */
	private final int width;

	public ForkCountingSort(final int[] arrayTosort, final int begin,
			final int end, final int width) {
		super();
		this.arrayTosort = arrayTosort;
		this.begin = begin;
		this.end = end;
		this.width = width;
		this.localHistogram = new int[width];
	}

	@Override
	protected int[] compute() {
		final int width = end - begin;
		if (width < sThreshold) {
			computeDirectly();
			return null;
		}
		final ForkCountingSort task1 = new ForkCountingSort(arrayTosort, begin,
				((begin + end) / 2), width);
		final ForkCountingSort task2 = new ForkCountingSort(arrayTosort,
				(begin + end) / 2, end, width);
		invokeAll(task1, task2);

		merge(task1.join(), task2.join());
		return null;

	}

	/**
	 * Merge the two histograms into one
	 * 
	 * @param hist1
	 *            Histogram of the first subtask
	 * @param hist2
	 *            Histogram of the second subtask
	 */
	private void merge(final int[] hist1, final int[] hist2) {
		for (int i = 0; i < width; i++) {
			localHistogram[i] = hist1[i] + hist2[i];
		}

	}

	/**
	 * Compute the histogram
	 */
	private void computeDirectly() {
		for (int i = begin; i <= end; i++) {
			++localHistogram[arrayTosort[i]];
		}
	}
}
