package VE_NEW;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import util.Graph;

public class VariableElimination {
	private static final long CHUNK_SIZE = 1024 * 1024 * 1024;
	public static Hashtable<Integer, Table<BigInteger>> tables = new Hashtable<Integer, Table<BigInteger>>();
	public static int[] ranges;
	public static String fileName;
	public static Hashtable<Integer, String> indexToName;

	public VariableElimination(Hashtable<Integer, Table<BigInteger>> tables, int[] ranges,
			Hashtable<Integer, String> indexToName) {
		this.tables = tables;
		this.ranges = ranges;
		this.indexToName = indexToName;
	}

	public static void infer(int[] order, int target, int maxNodeName, String fileName) {
		System.out.println("" + order.length + " variables to be eliminated");

		Set<Integer> invalidTables = new HashSet(tables.keySet());
		for (int table : order) {
			invalidTables.remove(table);
		}
		invalidTables.remove(target);
		for (int table : invalidTables) {
			tables.remove(table);
		}

		for (int i = 0; i < order.length; i++) {
			int elim = order[i];
			Set<Integer> keys = tables.keySet();
			ArrayList<Integer> tablesWithElim = new ArrayList<Integer>();
			for (int key : keys) {
				Table<BigInteger> table = tables.get(key);
				if (table.getNodes().contains(elim)) {
					tablesWithElim.add(key);
				}
			}
			// join
			if (tablesWithElim.size() > 1) {
				long start = System.currentTimeMillis();
				join(tablesWithElim, ++maxNodeName);
				eliminate(maxNodeName, elim);
				long end = System.currentTimeMillis();
				if (end - start > 1000) {
					System.out.println("    -> " + (end - start) + " ms");
				}
			} else {
				System.out.println("    -> Fastpath");
				eliminate(tablesWithElim.get(0), elim);
			}
		}
		if (tables.size() == 1) {
			Set<Integer> keys = tables.keySet();
			for (int key : keys) {
				Table<BigInteger> table = tables.get(key);
				table.printCPT(fileName);
			}
		} else {
			System.err
					.println("The exact inference result contains more than one table, something is wrong with infer");
		}
	}

	public static void join(ArrayList<Integer> tablesWithElim, int name) {
		// create new generated table-intermediate table.
		// nodes
		HashSet<Integer> sets = new HashSet<Integer>();
		for (int i = 0; i < tablesWithElim.size(); i++) {
			sets.addAll(tables.get(tablesWithElim.get(i)).getNodes());
		}
		ArrayList<Integer> nodes = new ArrayList<Integer>(sets);
		long cptsize = 1L;
		for (int i = 0; i < nodes.size(); i++) {
			cptsize *= ranges[nodes.get(i)];
		}
		// cpts
		// BigInteger[] p=new BigInteger[cptsize];
		Table<BigInteger> phi = new Table<BigInteger>(name, nodes, ranges, cptsize);
		Object[][] cpt = phi.getCpt();

		int[] order = new int[nodes.size()];
		for (int k = 0; k < nodes.size(); k++) {
			order[k] = nodes.get(k);
		}

		for (int j = 0; j < cpt.length; j++) {
			for (int l = 0; l < cpt[j].length; l++) {
				long index = j * CHUNK_SIZE + l;
				BigInteger prob = BigInteger.ONE;
				int[] assigns = phi.indexToAssignment(index + 1);
				for (int i = 0; i < tablesWithElim.size(); i++) {
					Table<BigInteger> table = tables.get(tablesWithElim.get(i));
					int[] subAssign = table.getSubAssignmentOrderless(assigns, order);// ++++++ subAssign is according
																						// to table's nodes order. So
																						// nodes in table does not need
																						// to be in order.

					long start = System.currentTimeMillis();
					int ind = table.assignmentToIndex(subAssign);
					long end = System.currentTimeMillis();

					start = System.currentTimeMillis();
					BigInteger k = table.getCPT(ind);
					end = System.currentTimeMillis();

					start = System.currentTimeMillis();
					prob = prob.multiply(k);
					end = System.currentTimeMillis();
				}

				long start = System.currentTimeMillis();
				phi.addCPT((index + 1), prob, new adder<BigInteger>() {
					public BigInteger add(BigInteger a, BigInteger b) {
						return a.add(b);
					}

					public BigInteger zero() {
						return BigInteger.ZERO;
					}
				});
				long end = System.currentTimeMillis();
			}

		}
		// add new table and remove tablesWithElim in tables
		tables.put(name, phi);
		for (int i = 0; i < tablesWithElim.size(); i++) {
			tables.remove(tablesWithElim.get(i));
		}
	}

	public static void eliminate(int target, int elim) {// target is the index of the table in tables we want to work
														// on, elim is the index of node we want to eliminate.
		Table<BigInteger> table = tables.get(target);
		// create new generated table-intermediate table.
		// nodes
		ArrayList<Integer> nodes = new ArrayList<Integer>(table.getNodes());
		nodes.remove(Integer.valueOf(elim));
		// cpts
		long cptsize = table.size / ((long) ranges[elim]);
		// BigInteger[] p=new BigInteger[cptsize];
		Table<BigInteger> tao = new Table<BigInteger>(table.getNode(), nodes, ranges, cptsize);
		Object[][] cpt = table.getCpt();
		for (int i = 0; i < cpt.length; i++) {
			for (int j = 0; j < cpt[i].length; j++) {
				long index = i * CHUNK_SIZE + j;
				int[] assigns = table.indexToAssignment(index + 1);
				BigInteger prob = table.getCPT(index + 1);
				int[] order = new int[table.getNodes().size()];
				for (int k = 0; k < table.getNodes().size(); k++) {
					order[k] = table.getNodes().get(k);
				}
				int[] subAssign = tao.getSubAssignmentOrderless(assigns, order);
				int ind = tao.assignmentToIndex(subAssign);
				tao.addCPT(ind, prob, new adder<BigInteger>() {
					public BigInteger add(BigInteger a, BigInteger b) {
						// System.out.println("a: "+a+" b: "+b);
						return a.add(b);
					}

					public BigInteger zero() {
						return BigInteger.ZERO;
					}
				});
			}
		}
		tables.put(target, tao);
	}

}
