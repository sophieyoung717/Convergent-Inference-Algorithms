package VE_Sampling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

public class VE_Sampling {

	public final Random rand = new Random(42);
	public Hashtable<Integer, Table<BigInteger>> tables;
	public Hashtable<Integer, Table<BigInteger>> taoTables = new Hashtable<Integer, Table<BigInteger>>();
	public LinkedList<JointTable<BigInteger>> jtables;
	public int[] ranges;
	public int maxIndex;

	public VE_Sampling(int maxIndex) {
		this.maxIndex = maxIndex;
	}

	public Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean shouldVisit(JointTable<BigInteger> jtable, int index) {
		Table<BigInteger> tao = tables.get(jtable.getTaoTableName());
		if (index != 0) {
			if (tao.getSister() != -1) {
				tao = tables.get(tao.getSister());
			}
		}
		if (tao.getSum().compareTo(new BigInteger(String.valueOf(tao.getTotal()))) == 0) {
			return false;
		}
		return true;
	}

	public boolean shouldCreateNewTao(JointTable<BigInteger> jtable) {
		// no tao false;
		// all tao finished true
		// other cases false.
		// already has tao sister
		if (tables.get(jtable.getTaoTableName()).getSister() != -1) {
			return false;
		}
		boolean hasTao = false;
		ArrayList<Integer> relatedTables = jtable.getRelatedTables();
		for (int i = 0; i < relatedTables.size(); i++) {
			Table<BigInteger> table = tables.get(relatedTables.get(i));
			if (table.isTao) {// if the table consists tao table and is just full, then we need a new tao
				hasTao = true;
				if (table.getSister() != -1) {
					table = tables.get(table.getSister());
				}
				if (table.getSum().compareTo(new BigInteger(String.valueOf(table.getTotal()))) != 0) {
					return false;
				}
			}
		}
		if (hasTao)
			return true;
		return false;
	}

	public void printResult(int resultIndex, String fileName) {
		Table<BigInteger> tao = tables.get(resultIndex);
		if (tao.getSister() != -1) {
			Table<BigInteger> sister = tables.get(tao.getSister());
			tao = (tao.getSum().compareTo(sister.getSum()) > 0) ? tao : sister;
		}
		tao.printCPT(fileName);
	}

	public void getVEsampling(int resultIndex, long time, long period, String fileName) {
		/*
		 * 1.get prob from sampling in related table 2.for corresponding tao, if prime,
		 * else if should create prime, else add prob. 3.check break.
		 */
		long start = System.currentTimeMillis();
		long prev = start;
		int countnewTao = 0;
		int sampleNumber = 0;
		while (true) {
			for (JointTable<BigInteger> jtable : jtables) {
				// for experiment part2
				long current = System.currentTimeMillis();
				if (current - prev >= period) {
					prev = current;
					printResult(resultIndex, fileName);
					sampleNumber++;
					System.out.println("sample: " + sampleNumber);
					for (JointTable tab : jtables) {
						Table<BigInteger> tao = tables.get(tab.getTaoTableName());
						System.out.println("Table " + tab.getTaoTableName() + ": " + tab.samplesToReset + "/"
								+ tab.total + (tao.isComplete ? " (done)" : (tab.finalRound ? " (final round)" : "")));
					}

				}
				// for experiment part1 and part2.
				current = System.currentTimeMillis();
				if (current - start >= time) {
					printResult(resultIndex, fileName);
					return;
				}
				if (tables.get(jtable.getTaoTableName()).isComplete) {
					continue;
				}

				// step 1:

				ArrayList<Integer> relatedTables = jtable.getRelatedTables();

				// Compute the index of the next tuple that we're going to
				// sample from jtable's input.
				long index = ((jtable.para1 * jtable.index + jtable.para2) % (jtable.para3));
				jtable.index = index;

				// getProb
				int[] assigns = jtable.table.indexToAssignment(index + 1);
				int[] order = new int[jtable.table.getNodes().size()];
				for (int k = 0; k < jtable.table.getNodes().size(); k++) {
					order[k] = jtable.table.getNodes().get(k);
				}

				// Compute the probability term to add to the final result.
				// Iterate over each table feeding into jtable, and pick out the
				// probabilities estimated for the source tables.
				BigInteger prob = BigInteger.ONE;
				BigInteger count = BigInteger.ONE;

				for (int tableName : relatedTables) {
					Table<BigInteger> table = tables.get(tableName);

					// We keep around 2 tables for each tao (the table and its "sister"),
					// since we need to periodically reset the the table so that we can
					// store a fresh round of samples. The sister tao table includes
					// the last completed sampling round, and may be able to give us a better
					// estimate of the sampling result. We use the sample counts as a
					// rough heuristic to figure out whether the sister table should be
					// swapped in.

					// First check if a sister table exists in the first place.
					// also skip this step if the target table is already complete.
					if (!table.isComplete && table.isTao && table.getSister() != -1) {
						Table<BigInteger> sister = tables.get(table.getSister());

						// If the sister includes a complete result, clearly we should
						// be using it.
						if (sister.isComplete) {
							table = sister;
						} else {

							// If neither table is done, use whichever table is based on more
							// samples (since this is liable to produce a better result).
							if (table.getSum().compareTo(sister.getSum()) < 0) {
								table = sister;
							}

						}
					}

					// Convert the index into the jtable into an index into the table
					// that we're trying to read from currently.
					int[] subassign = table.getSubAssignmentOrderless(assigns, order);
					int ind = table.assignmentToIndex(subassign);
					if (ind <= 0) {
						System.out.println("ind: " + ind);
					}

					// And finally index into the table we're reading from. We're looking for
					// the (scaled) probability from the table:
					BigInteger k = (BigInteger) table.getCPT(ind);
					// ... as well as the number of samples used to create that probability.
					BigInteger n = (BigInteger) table.getCount(ind);

					// The joint probability is the product of the probabilities from all input
					// tables. The joint sample count is similarly a product of input counts.
					prob = prob.multiply(k);
					count = count.multiply(n);

					// Finish off with some sanity checks
					if (count.compareTo(BigInteger.ZERO) < 0 || prob.compareTo(BigInteger.ZERO) < 0) {
						System.out.println("count or prob is zero");
					}
				} // end loop over source tables

				// The variables `prob` and `count` now contain the joint probability and sample
				// count of the tuple that we just sampled from jtable. Next step: Incorporate
				// `prob` and `count` into the current aggregate (tao) table.
				Table<BigInteger> tao = tables.get(jtable.getTaoTableName());

				// Sanity checks...
				if (tao.getSum().compareTo(BigInteger.ZERO) < 0) {
					System.out.println("sum is negative");
				}
				if (tao == null) {
					System.out.println("tao is null");
				}

				// Figure out the position in tau that we're supposed to add to.
				int indx = getIndex(tao, assigns, order);

				// Incorporate the `prob`ability
				tao.addCPT(indx, prob, new adder<BigInteger>() {
					public BigInteger add(BigInteger a, BigInteger b) {
						return a.add(b);
					}

					public BigInteger zero() {
						return BigInteger.ZERO;
					}
				});

				// Incorporate the `count`
				BigInteger currentCount = tao.getCount(indx);
				tao.setCount(indx, currentCount.add(count));

				// And keep track of how many samples have been added to the tao table as a
				// whole.
				BigInteger currentSum = tao.getSum();
				tao.setSum(currentSum.add(count));

				// Register the sample...
				jtable.samplesToReset--;
				if (jtable.samplesToReset <= 0) {
					// And if we've completed a full cycle of the LCG, then we need to reset tao.

					// First thing to check for... If we decided this was the final round for the
					// current tao, then we might be outright done here.
					if (jtable.finalRound) {
						tao.isComplete = true;

						System.out.println("Finished " + jtable.getTaoTableName());
						// If the current join table is actually the result table... then
						// that means we're outright done.
						if (tao.getNode() == resultIndex) {
							// Dump the current state and return.
							printResult(resultIndex, fileName);
							return;
						}
					} else {

						// We might not have been in the final round previously, but check to see
						// if this new cycle is going to be the final round...
						// This will be the final round if all of the table's ancestors are done
						// at the start of the round.
						jtable.finalRound = true;
						for (int ancestor : jtable.getRelatedTables()) {
							// Turn off finalRound if any of the ancestors aren't done.
							if (!tables.get(ancestor).isComplete) {
								jtable.finalRound = false;
								break;
							}
						}

						// Now the heavy lifting: Allocate a new tao
						BigInteger[] p = new BigInteger[tao.getCPTs().length];
						for (int k = 0; k < tao.getCPTs().length; k++) {
							p[k] = BigInteger.ZERO;
						}

						// default count is zero.
						BigInteger[] countCpts = new BigInteger[tao.getCPTs().length];
						for (int k = 0; k < countCpts.length; k++) {
							countCpts[k] = BigInteger.ZERO;
						}
						// sum
						BigInteger sum = BigInteger.ZERO;
						Table<BigInteger> newTao = new Table<BigInteger>(tao.getNode(), tao.getNodes(), tao.getRange(),
								p, tao.getTotal(), countCpts, sum, tao.getInvolvedNodes());

						// Then offset the current tao to the sister position.
						// It may be necessary to create this position to begin with.
						int sister = tao.getSister();
						tao.setSister(jtable.getTaoTableName());
						if (sister == -1) {
							sister = ++maxIndex;
						}
						newTao.setSister(sister);

						tables.put(sister, tao);
						tables.put(jtable.getTaoTableName(), newTao);
						jtable.samplesToReset = jtable.total;
					}
				}
			}
		}
	}

	public int getIndex(Table<BigInteger> table, int[] assigns, int[] order) {
		int[] subassign = table.getSubAssignmentOrderless(assigns, order);
		int ind = table.assignmentToIndex(subassign);
		return ind;
	}

	public BigInteger getProb(JointTable<BigInteger> jtable, int[] assignOut, int[] orderOut) {
		BigInteger prob = BigInteger.ONE;
		ArrayList<Integer> relatedTables = jtable.getRelatedTables();
		long index = ((jtable.para1 * jtable.index + jtable.para2) % (jtable.para3));
		jtable.index = index;

		int[] assigns = jtable.table.indexToAssignment(index + 1);
		int[] order = new int[jtable.table.getNodes().size()];
		for (int k = 0; k < jtable.table.getNodes().size(); k++) {
			order[k] = jtable.table.getNodes().get(k);
		}
		orderOut = order;
		assignOut = assigns;
		for (int n = 0; n < relatedTables.size(); n++) {
			Table<BigInteger> table = tables.get(relatedTables.get(n));
			if (table.isTao && table.getSister() != -1) {
				Table<BigInteger> sister = tables.get(table.getSister());
				table = (table.getSum().compareTo(sister.getSum()) > 0) ? table : sister;
			}
			int[] subassign = table.getSubAssignmentOrderless(assigns, order);
			int ind = table.assignmentToIndex(subassign);
			if (ind <= 0) {
				System.out.println("ind: " + ind);
			}
			BigInteger k = (BigInteger) table.getCPT(ind);
			prob = prob.multiply(k);
		}
		return prob;
	}

	public LinkedList<JointTable<BigInteger>> createJointTables(int[] order, int resultIndex, int targetNode) {// create
																												// jointtables
																												// and
																												// add
																												// tao
																												// to
																												// tables
		LinkedList<JointTable<BigInteger>> jtables = new LinkedList<JointTable<BigInteger>>();
		maxIndex++;
		int taoName = maxIndex + 1;

		Set<Integer> invalidTables = new HashSet(tables.keySet());
		for (int table : order) {
			invalidTables.remove(table);
		}
		invalidTables.remove(targetNode);
		// System.out.print("Skipping:");
		for (int table : invalidTables) {
			// System.out.print(" "+table);
			tables.remove(table);
		}
		// System.out.println(";");

		for (int i = 0; i < order.length; i++) {
			long maxSamples = 0;
			if (i == order.length - 1) {
				taoName = resultIndex;
			}
			// JointTable<BigInteger> jtable=new JointTable<BigInteger>();
			int elim = order[i];
			Set<Integer> keys = tables.keySet();
			ArrayList<Integer> tablesWithElim = new ArrayList<Integer>();
			for (int key : keys) {
				Table<BigInteger> table = tables.get(key);
				if (table.getNodes().contains(elim)) {
					if (maxSamples < table.totalSamplesRequired) {
						maxSamples = table.totalSamplesRequired;
					}
					tablesWithElim.add(key);
				}
			} // obtain all related tables
			JointTable<BigInteger> jtable = getJoinTable(tablesWithElim, maxIndex, taoName);// joint
			Table<BigInteger> taoTable = getTaoTable(elim, tablesWithElim, taoName);// tao
			System.out.println("Eliminating " + elim + " -> " + taoTable.getTableSize() + " rows of tao");

			// Round up to the next full cycle
			maxSamples = maxSamples + (maxSamples % jtable.total) + jtable.total;
			taoTable.totalSamplesRequired = maxSamples;

			System.out.println("   " + taoName + " <- " + tablesWithElim + "  (" + jtable.total + " samples/cycle; "
					+ maxSamples + " samples to completion)");
			tables.put(taoName, taoTable);
			taoTables.put(taoName, taoTable);
			for (int j = 0; j < tablesWithElim.size(); j++) {
				tables.remove(tablesWithElim.get(j));// remove, so next round, they will not be considerred.
			}
			jtables.add(i, jtable);
			maxIndex = maxIndex + 2;
			taoName = maxIndex + 1;
		}
		return jtables;
	}

	public JointTable<BigInteger> getJoinTable(ArrayList<Integer> tablesWithElim, int name, int taoName) {
		// create new generated joint table table.
		// nodes
		HashSet<Integer> sets = new HashSet<Integer>();
		for (int i = 0; i < tablesWithElim.size(); i++) {
			sets.addAll(tables.get(tablesWithElim.get(i)).getNodes());
		}
		ArrayList<Integer> nodes = new ArrayList<Integer>(sets);
		int cptsize = 1;
		for (int i = 0; i < nodes.size(); i++) {
			cptsize *= ranges[nodes.get(i)];
		}
		// cpts
		BigInteger[] p = new BigInteger[cptsize];
		for (int i = 0; i < p.length; i++) {
			p[i] = BigInteger.ZERO;
		}
		BigInteger[] count = new BigInteger[p.length];
		for (int i = 0; i < count.length; i++) {
			count[i] = BigInteger.ONE;
		}
		BigInteger sum = BigInteger.ZERO;
		Table<BigInteger> phi = new Table<BigInteger>(name, nodes, ranges, p, count, sum);
		JointTable<BigInteger> jtable = new JointTable<BigInteger>(phi, tablesWithElim, taoName, cptsize);

		long[] tmp = new long[phi.getNodes().size()];
		for (int w = 0; w < phi.getNodes().size(); w++) {
			tmp[w] = ranges[phi.getNodes().get(w)];
		}
		jtable.para1 = Tools.getA(tmp, cptsize);// a
		jtable.para2 = Tools.getCoPrime(cptsize);// c
		jtable.para3 = cptsize;// m
		jtable.index = (int) (rand.nextDouble() * (cptsize - 1) + 1);
		for (int table : tablesWithElim) {
			if (tables.get(table).isTao) {
				jtable.finalRound = false;
				break;
			}
		}
		return jtable;
	}

	public Table<BigInteger> getTaoTable(int elim, ArrayList<Integer> tablesWithElim, int name) {
		// create new generated tao table,eliminate elim.
		// nodes
		HashSet<Integer> sets = new HashSet<Integer>();
		for (int i = 0; i < tablesWithElim.size(); i++) {
			sets.addAll(tables.get(tablesWithElim.get(i)).getNodes());
		}
		ArrayList<Integer> nodes = new ArrayList<Integer>(sets);
		nodes.remove((Integer) elim);

		int cptsize = 1;
		for (int i = 0; i < nodes.size(); i++) {
			cptsize *= ranges[nodes.get(i)];
		}
		// cpts
		BigInteger[] p = new BigInteger[cptsize];
		for (int i = 0; i < p.length; i++) {
			p[i] = BigInteger.ZERO;
		}
		// count
		// default count is zero.
		BigInteger[] count = new BigInteger[cptsize];
		for (int i = 0; i < cptsize; i++) {
			count[i] = BigInteger.ZERO;
		}
		// sum
		BigInteger sum = BigInteger.ZERO;
		// total and involvedNodesArr.
		HashSet<Integer> involvedNodes = new HashSet<Integer>();
		for (int i = 0; i < tablesWithElim.size(); i++) {
			involvedNodes.addAll(tables.get(tablesWithElim.get(i)).getInvolvedNodes());
		}
		ArrayList<Integer> involvedNodesArr = new ArrayList<Integer>(involvedNodes);
		long total = 1L;
		for (int i = 0; i < involvedNodesArr.size(); i++) {
			total *= ranges[involvedNodesArr.get(i)];
		}
		Table<BigInteger> tao = new Table<BigInteger>(name, nodes, ranges, p, total, count, sum, involvedNodesArr);
		tao.isComplete = false;
		return tao;
	}
}
