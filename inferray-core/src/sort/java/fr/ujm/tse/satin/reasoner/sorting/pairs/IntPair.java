package fr.ujm.tse.satin.reasoner.sorting.pairs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julien
 * 
 */
public class IntPair implements Comparable<IntPair> {

	private final int subject;
	private final int object;

	public IntPair(final int subject, final int object) {
		super();
		this.subject = subject;
		this.object = object;
	}

	public int getSubject() {
		return subject;
	}

	public int getObject() {
		return object;
	}

	@Override
	public int compareTo(final IntPair o) {
		final int temp = Long.compare(this.subject, o.subject);
		return temp == 0 ? Long.compare(this.object, o.object) : temp;
	}

	public static List<IntPair> fromIntArray(final int[] array) {
		final List<IntPair> list = new ArrayList<>(array.length / 2);
		for (int i = 0; i < array.length;) {
			list.add(new IntPair(array[i++], array[i++]));
		}
		return list;
	}

	public static int[] toIntArray(final List<IntPair> list) {
		final int[] array = new int[list.size() * 2];
		int i = 0;
		for (final IntPair intPair : list) {
			array[i++] = intPair.getSubject();
			array[i++] = intPair.getObject();
		}
		return array;
	}
}
