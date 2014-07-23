package fr.ujm.tse.lt2c.satin.inferray.datastructure.transitive;

// simple union-find based on int[] arrays
// for  "parent" and "rank"
// implements the "disjoint-set forests" described at
// http://en.wikipedia.org/wiki/Disjoint-set_data_structure
// which have almost constant "amortized" cost per operation
// (actually O(inverse Ackermann))

import java.util.Arrays;

/**
 * Union-Find implementation taken from :
 * http://www.cs.waikato.ac.nz/~bernhard/317/source/graph/UnionFind.java
 *
 *
 */
public class UnionFind {

	final int[] _parent;
	final int[] _rank;

	public int find(final int i) {

		final int p = _parent[i];
		if (i == p) {
			return i;
		}
		return _parent[i] = find(p);

	}

	public void union(final int i, final int j) {

		final int root1 = find(i);
		final int root2 = find(j);

		if (root2 == root1) {
			return;
		}

		if (_rank[root1] > _rank[root2]) {
			_parent[root2] = root1;
		} else if (_rank[root2] > _rank[root1]) {
			_parent[root1] = root2;
		} else {
			_parent[root2] = root1;
			_rank[root1]++;
		}
	}

	public UnionFind(final int max) {

		_parent = new int[max];
		_rank = new int[max];

		for (int i = 0; i < max; i++) {
			_parent[i] = i;
		}
	}

	@Override
	public String toString() {
		return "<UnionFind\np " + Arrays.toString(_parent) + "\nr "
				+ Arrays.toString(_rank) + "\n>";
	}

}