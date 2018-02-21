package ForwardSampling;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class GibbsSampling {
	public final Random rand = new Random(36);
	public Hashtable<Integer, Table<Long>> tables = new Hashtable<Integer, Table<Long>>();
	public int[] rangesFS;

	public GibbsSampling(String fileName) {
		try {
			boolean getRange = true;
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			int count = 0;
			ArrayList<Integer> rangesAll = new ArrayList<Integer>();
			while ((line = reader.readLine()) != null) {
				if (!line.equals("=====")) {
					int node = Integer.parseInt(line);// node
					String nodestmp = reader.readLine();
					String[] nodesarr = nodestmp.split(",");
					int[] nodes = new int[nodesarr.length];
					for (int i = 0; i < nodes.length; i++) {
						nodes[i] = Integer.parseInt(nodesarr[i]);// nodes
					}
					String rangestmp = reader.readLine();
					String[] rangesarr = rangestmp.split(",");

					if (getRange) {// get Range for all nodes.
						getRange = false;
						rangesFS = new int[rangesarr.length];
						for (int i = 0; i < rangesFS.length; i++) {
							rangesFS[i] = Integer.parseInt(rangesarr[i]);
						}
					}

					int[] ranges = new int[nodes.length];
					for (int i = 0; i < nodes.length; i++) {
						ranges[i] = Integer.parseInt(rangesarr[nodes[i]]);// ranges
					}
					int numRows = 1;
					for (int i = 0; i < nodes.length; i++) {
						numRows *= ranges[i];
					}
					Long[][] cpt = new Long[numRows][nodes.length];// cpt
					for (int j = 0; j < numRows; j++) {
						String cpttmp = reader.readLine();
						String[] cptarr = cpttmp.split(",");
						for (int i = 0; i < nodes.length; i++) {
							cpt[j][i] = Long.parseLong(cptarr[i]);
						}
					}
					Long[] cptValues = new Long[numRows]; // cptValues
					String cptvalue = reader.readLine();
					String[] cptvalueArr = cptvalue.split(",");
					for (int i = 0; i < cptvalueArr.length; i++) {
						cptValues[i] = Long.parseLong(cptvalueArr[i]);
					}
					tables.put(node, new Table<Long>(node, nodes, ranges, cpt, cptValues));
				} else {
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// order is the sampling order. since there is no evidence, we can predefine an
	// arbitrary orders
	public void infer(long numSamples, int[] order, int target, Table<Long> targetTable, long period, String fileName) {
		long start = System.currentTimeMillis();
		long prev = start;
		long current = System.currentTimeMillis();

		// for the 1st sample by forward sampling.
		Hashtable<Integer, Long> tuple = new Hashtable<Integer, Long>(); // key:node index, value:node value
		for (int j = 0; j < order.length; j++) {
			Table<Long> table = tables.get(order[j]);
			Long p = ForwardSampling.getRandomValue(tuple, table);
		}
		// for gibbs chain trajectory.
		int sampleNumber = 0;
		while (current - start <= numSamples) {// numSamples is time.
			// for experiment part2
			current = System.currentTimeMillis();
			if (current - prev >= period) {
				prev = current;
				targetTable.printProb(fileName);
				sampleNumber++;
				System.out.println(sampleNumber);
			}
			for (int j = 0; j < order.length; j++) {
				// find all the tables contain order[j], join them together.
				Long[] distribution = new Long[rangesFS[order[j]]];
				for (int k = 0; k < distribution.length; k++) {
					distribution[k] = 1L;
				}
				for (int key : tables.keySet()) {
					Table<Long> table = tables.get(key);
					int[] nodes = table.getNodes();
					for (int i = 0; i < nodes.length; i++) {
						if (nodes[i] == order[j]) {
							distribution = getRowsGivenEvidence(distribution, tuple, table, order[j]);
							break;
						}
					}
				}
				// sample one for order[j].node and add to tuple.
				for (int i = 1; i < distribution.length; i++) {// cumulate
					distribution[i] += distribution[i - 1];
				}
				Long random = (long) (rand.nextDouble() * distribution[distribution.length - 1] + 1);
				for (int i = 0; i < distribution.length - 1; i++) {
					if (random < distribution[i]) {
						tuple.put(order[j], Long.valueOf((int) (i + 1)));
						break;
					} else if (random > distribution[i] && random <= distribution[i + 1]) {
						tuple.put(order[j], Long.valueOf((int) (i + 1 + 1)));
						break;
					}
				}
			}
			// System.out.println(tuple.toString());
			targetTable.addProb(tuple.get(target), 1L, new adder<Long>() {
				public Long add(Long a, Long b) {
					return a + b;
				}

				public Long zero() {
					return (long) 0;
				}
			});
			current = System.currentTimeMillis();
		}
		targetTable.printProb(fileName);
	}

	public Long[] getRowsGivenEvidence(Long[] distribution, Hashtable<Integer, Long> tuple, Table<Long> table,
			int target) {
		Long[] result = new Long[distribution.length];
		Long[][] cpts = table.getCpt();
		int targetLocal = 0;
		for (int i = 0; i < cpts.length; i++) {
			int count = 0;
			for (int j = 0; j < cpts[0].length; j++) {
				if (table.getNodes()[j] != target) {
					if (cpts[i][j] == tuple.get(table.getNodes()[j])) {
						count++;
					}
				} else {
					targetLocal = j;
				}
			}
			if (count == table.getNodes().length - 1) {
				int index = (int) (long) (cpts[i][targetLocal]) - 1;
				Long prob = table.getCptValues()[i];
				result[index] = distribution[index] * prob;
			}
		}
		if (result[0] == null) {
			System.out.println("what?");
		}
		return result;
	}
}
