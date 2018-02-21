package VE_Sampling;

import java.math.BigInteger;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import util.Graph;

public class CalculateVEOrder {

	public static int[] getVEOrder(VE_Sampling ve, int target) {
		Set<Integer> requiredTables = RequisiteTables.filterTables(ve.tables, target);
		System.out.println("Filtered: " + requiredTables.size() + " / " + ve.tables.size() + " tables remaining");
		Hashtable<Integer, Table<BigInteger>> tables = ve.tables;
		int[] ranges = ve.ranges;
		Graph g = new Graph(tables.size());
		int[] weights = new int[requiredTables.size()];
		int[] order = new int[requiredTables.size()];
		for (int me : requiredTables) {
			Table<BigInteger> table = tables.get(me);
			for (int neighbor : table.getNodes()) {
				if (neighbor != me) {
					g.addEdge(me, neighbor);
				}
			}
		}
		int i = 0;
		for (int me : requiredTables) {
			LinkedList<Integer> adj = g.adj[me];
			int total = 1;
			for (int j = 0; j < adj.size(); j++) {
				total *= ranges[adj.get(j)];
			}
			weights[i] = total;
			order[i] = me;
			i++;
		}
		sort(weights, order);
		order = orderremoveTarget(order, target);
		return order;
	}

	public static void sort(int[] weights, int[] order) {
		for (int i = 0; i < weights.length; i++) {
			int v = weights[i];
			int j = i + 1;
			int cursor = i;
			for (; j < weights.length; j++) {
				if (weights[j] < v) {
					v = weights[j];
					cursor = j;
				}
			}
			if (cursor != i) {
				exchange(weights, order, i, cursor);
			}
		}
	}

	public static void exchange(int[] weights, int[] order, int i, int j) {
		int tmp = weights[i];
		int t = order[i];
		weights[i] = weights[j];
		order[i] = order[j];
		weights[j] = tmp;
		order[j] = t;
	}

	public static int[] orderremoveTarget(int[] order, int target) {
		int[] result = new int[order.length - 1];
		int count = 0;
		for (int i = 0; i < order.length; i++) {
			if (order[i] != target) {
				result[count++] = order[i];
			}
		}
		return result;
	}

	public static void main(String[] arges) {
		int[] weights = new int[] { 4, 5, 7, 2, 9 };
		int[] order = new int[] { 0, 1, 2, 3, 4 };
		sort(weights, order);
		System.out.println();
	}
}
