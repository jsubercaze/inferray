package fr.ujm.tse.satin.reasoner.sorting.pairs.longs.os;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julien
 * 
 */
public class LongPairOS implements Comparable<LongPairOS> {

	private final long subject;
	private final long object;

	public LongPairOS(final long subject, final long object) {
		super();
		this.subject = subject;
		this.object = object;
	}

	public long getSubject() {
		return subject;
	}

	public long getObject() {
		return object;
	}

	/**
	 * Object first
	 */
	@Override
	public int compareTo(final LongPairOS o) {
		final int temp = Long.compare(this.object, o.object);
		return temp == 0 ? Long.compare(this.subject, o.subject) : temp;
	}

	public static List<LongPairOS> fromLongArray(final long[] array) {
		final List<LongPairOS> list = new ArrayList<>(array.length / 2);
		for (int i = 0; i < array.length;) {
			list.add(new LongPairOS(array[i++], array[i++]));
		}
		return list;
	}

	public static long[] toLongArray(final List<LongPairOS> list) {
		final long[] array = new long[list.size() * 2];
		int i = 0;
		for (final LongPairOS longPair : list) {
			array[i++] = longPair.getSubject();
			array[i++] = longPair.getObject();
		}
		return array;
	}
}
