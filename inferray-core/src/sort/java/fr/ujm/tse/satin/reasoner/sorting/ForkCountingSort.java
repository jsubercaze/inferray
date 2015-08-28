package fr.ujm.tse.satin.reasoner.sorting;

import java.util.concurrent.RecursiveTask;

public class ForkCountingSort extends RecursiveTask<int[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5039635857243293647L;
	protected static int sThreshold = 250_000;
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
	/**
	 * Min value in the array
	 */
	private final int min;

	public ForkCountingSort(final int[] arrayTosort, final int begin,
			final int end, final int min, final int width) {
		super();
		this.arrayTosort = arrayTosort;
		this.begin = begin;
		this.end = end;
		this.width = width;
		this.localHistogram = new int[width];
		this.min = min;
	}

	@Override
	protected int[] compute() {
		if ((end - begin) < sThreshold) {
			computeDirectly();
			return localHistogram;
		}
		final ForkCountingSort task1 = new ForkCountingSort(arrayTosort, begin,
				((begin + end) / 2), min, width);
		final ForkCountingSort task2 = new ForkCountingSort(arrayTosort,
				((begin + end) / 2) + 1, end, min, width);
		invokeAll(task1, task2);

		merge(task1.join(), task2.join());
		return localHistogram;

	}

	/**
	 * Merge the two histograms into one
	 * 
	 * @param hist1
	 *            Histogram of the first subtask
	 * @param hist2
	 *            Histogram of the second subtask
	 */
	private void merge(int[] hist1, int[] hist2) {
		for (int i = 0; i < width; i++) {
			localHistogram[i] = hist1[i] + hist2[i];
		}
		hist1 = null;
		hist2 = null;
	}

	/**
	 * Compute the histogram
	 */
	private void computeDirectly() {
		for (int i = begin; i <= end; i++) {
			localHistogram[arrayTosort[i] - this.min]++;
		}
	}
}
