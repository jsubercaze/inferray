package fr.ujm.tse.satin.reasoner.sorting.pairs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julien
 * 
 */
public class LongPair implements Comparable<LongPair> {

	private final long subject;
	private final long object;

	public LongPair(final long subject, final long object) {
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

	@Override
	public int compareTo(final LongPair o) {
		final int temp = Long.compare(this.subject, o.subject);
		return temp == 0 ? Long.compare(this.object, o.object) : temp;
	}

	public static List<LongPair> fromLongArray(final long[] array) {
		final List<LongPair> list = new ArrayList<>(array.length / 2);
		for (int i = 0; i < array.length;) {
			list.add(new LongPair(array[i++], array[i++]));
		}
		return list;
	}

	public static long[] toLongArray(final List<LongPair> list) {
		final long[] array = new long[list.size() * 2];
		int i = 0;
		for (final LongPair longPair : list) {
			array[i++] = longPair.getSubject();
			array[i++] = longPair.getObject();
		}
		return array;
	}
}
