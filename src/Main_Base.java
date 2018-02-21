import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Random;

public class Main_Base {

	int maxIndex;
	int targetNode;
	String graph;
	String veInputFileName;
	String ve_resultFileName;
	String ve_samplingFileName;
	String gibbs_FileName;
	String cyclic_fileName;
	String Random_fileName;
	int period;
	int max_sampling_time;
	int[] order;

	public Main_Base(int maxIndex, int targetNode, String graph, String veInputFileName, String ve_resultFileName,
			String ve_samplingFileName, String gibbs_FileName, String cyclic_fileName, String Random_fileName,
			int period, int max_sampling_time) {
		this.period = period;
		this.Random_fileName = Random_fileName;
		this.cyclic_fileName = cyclic_fileName;
		this.gibbs_FileName = gibbs_FileName;
		this.ve_samplingFileName = ve_samplingFileName;
		this.ve_resultFileName = ve_resultFileName;
		this.veInputFileName = veInputFileName;
		this.graph = graph;
		this.targetNode = targetNode;
		this.maxIndex = maxIndex;
		this.max_sampling_time = max_sampling_time;

		VE_NEW.Result result = VE_NEW.ConstructTablesFromFile.constructTablesFromFile(graph, 1000000); //
		VE_NEW.VariableElimination ve = new VE_NEW.VariableElimination(result.tables, result.ranges,
				result.indexToName);
		// write to a file for forward sampling.
		try {
			PrintWriter writer = new PrintWriter(veInputFileName, "UTF-8");
			for (int n = 0; n < result.tables.size(); n++) {
				writer.println(result.tables.get(n).getNode());
				writer.println(result.tables.get(n).printNodes());
				writer.println(result.tables.get(n).printRanges());
				writer.print(result.tables.get(n).printCPTforVE());
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.order = VE_NEW.CalculateVEOrder.getVEOrder(ve, targetNode);
	}

	public void doVE() {
		int[] order = null;
		long start = 0;
		long end = 0;
		System.out.println("++++++++++++++++++++++++++++++++++VE");
		// step 0: construct tables
		VE_NEW.Result result = VE_NEW.ConstructTablesFromFile.constructTablesFromFile(graph, 1000000); //
		VE_NEW.VariableElimination ve = new VE_NEW.VariableElimination(result.tables, result.ranges,
				result.indexToName);
		// write to a file for forward sampling.
		try {
			PrintWriter writer = new PrintWriter(veInputFileName, "UTF-8");
			for (int n = 0; n < result.tables.size(); n++) {
				writer.println(result.tables.get(n).getNode());
				writer.println(result.tables.get(n).printNodes());
				writer.println(result.tables.get(n).printRanges());
				writer.print(result.tables.get(n).printCPTforVE());
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		order = VE_NEW.CalculateVEOrder.getVEOrder(ve, targetNode);
		start = System.currentTimeMillis();
		VE_NEW.VariableElimination.infer(order, targetNode, maxIndex, ve_resultFileName);
		end = System.currentTimeMillis();
		System.out.println("VE time is: " + (end - start));
	}

	public void doLeakyJoins() {
		int[] order = null;
		long start = 0;
		long end = 0;
		System.out.println("+++++++++++++++++++++++++++++++++VE_Sampling");
		VE_Sampling.VE_Sampling ve_sampling = new VE_Sampling.VE_Sampling(maxIndex);
		// step 0: construct tables
		VE_Sampling.Result result1 = VE_Sampling.ConstructTablesFromFile.constructTablesFromFile(graph, 1000000); //
		ve_sampling.tables = result1.tables;
		ve_sampling.ranges = result1.ranges;
		Hashtable<Integer, VE_Sampling.Table<BigInteger>> tablesCopy = (Hashtable<Integer, VE_Sampling.Table<BigInteger>>) ve_sampling
				.deepClone(ve_sampling.tables);
		int resultIndex = 10000;
		order = VE_Sampling.CalculateVEOrder.getVEOrder(ve_sampling, targetNode);
		ve_sampling.jtables = ve_sampling.createJointTables(order, resultIndex, targetNode);
		ve_sampling.tables = (Hashtable<Integer, VE_Sampling.Table<BigInteger>>) ve_sampling.deepClone(tablesCopy);
		// add tao into tables.
		ve_sampling.tables.putAll(ve_sampling.taoTables);
		// step 2: sampling
		start = System.currentTimeMillis();
		ve_sampling.getVEsampling(resultIndex, max_sampling_time, period, ve_samplingFileName);
		end = System.currentTimeMillis();
		System.out.println("VE sampling time is: " + (end - start));
	}

	public void doGibbsSampling() {
		int[] order = null;
		long start = 0;
		long end = 0;
		System.out.println("+++++++++++++++++++++++++++++++++gibbs sampling");
		ForwardSampling.ForwardSampling fs = new ForwardSampling.ForwardSampling(veInputFileName);
		ForwardSampling.GibbsSampling mc = new ForwardSampling.GibbsSampling(veInputFileName);
		// create target table.
		int[] nsize = fs.rangesFS;
		int[] nodesTarH = { targetNode };
		int nodeTarH = targetNode;
		Long[][] cptH = new Long[nsize[nodeTarH]][1];
		for (int i = 0; i < nsize[nodeTarH]; i++) {
			cptH[i][0] = (long) (i + 1);
		}
		Long[] cptValueH = new Long[nsize[nodeTarH]];
		ForwardSampling.Table<Long> tarH = new ForwardSampling.Table<Long>(nodeTarH, nodesTarH, nsize, cptH, cptValueH);
		// infer
		order = ForwardSampling.ConstructTopologicalOrder.getTopologicalOrder(fs);
		start = System.currentTimeMillis();
		mc.infer(max_sampling_time, order, targetNode, tarH, period, gibbs_FileName);
		end = System.currentTimeMillis();
		System.out.println("gibbs sampling time is: " + (end - start) + " ms");
	}

	public void doCyclicSampling() {
		long start = 0;
		long end = 0;
		System.out.println("+++++++++++++++++++++++++++++++++cyclic sampling");
		// step1: read in data.
		CyclicSampling.Result result2 = CyclicSampling.ConstructTablesFromFile.constructTablesFromFile(graph, 1000000);
		long time = max_sampling_time;
		int[] nsize = result2.ranges;
		Hashtable<Integer, CyclicSampling.Table<BigInteger>> tables = result2.tables;
		// Step 2: create target table node 19.
		// get requisite info

		int N = order.length + 1;
		int target = targetNode;
		int[] nodesTarH = new int[N];
		for (int i = 0; i < order.length; i++) {
			nodesTarH[i] = order[i];
		}
		nodesTarH[nodesTarH.length - 1] = target;
		int nodeTarH = maxIndex + 1;

		BigInteger[] pTarH = new BigInteger[nsize[target]];
		CyclicSampling.Table<BigInteger> cyctarH = new CyclicSampling.Table<BigInteger>(nodeTarH, nodesTarH, nsize,
				nsize[target]);
		// COPY FROM HERE
		// get total number of samples
		BigInteger totalSamples = BigInteger.ONE;
		for (int k = 0; k < nodesTarH.length; k++) {
			totalSamples = totalSamples.multiply(new BigInteger("" + nsize[nodesTarH[k]]));
		}
		// calculate a,c,m, (ax+c%m) long[] from int[]
		long[] tmp = new long[nodesTarH.length];
		for (int w = 0; w < nodesTarH.length; w++) {
			tmp[w] = nsize[nodesTarH[w]];
		}
		BigInteger para1 = CyclicSampling.Tools.getA(tmp, totalSamples);// a
		BigInteger para2 = CyclicSampling.Tools.getCoPrime(totalSamples);// c
		BigInteger para3 = new BigInteger("" + totalSamples);// m
		// index can start anywhere, make it start random.
		// long index=(int)(Math.random()*(totalSamples-1)+1);
		BigInteger r;
		Random rnd = new Random();
		do {
			r = new BigInteger(totalSamples.bitLength(), rnd);
		} while (r.compareTo(totalSamples) >= 0);
		BigInteger index = r;
		// Step 3: start to sample
		start = System.currentTimeMillis();
		long prev = start;
		long current = System.currentTimeMillis();
		int count = 1;
		for (int m = 0; totalSamples.compareTo(new BigInteger("" + m)) > 0; m++) {
			current = System.currentTimeMillis();
			if (current - start <= time) {// time .
				current = System.currentTimeMillis();
				if (current - prev >= period) {
					prev = current;
					System.out.println("sample: " + count++);
					cyctarH.printCPT(cyclic_fileName);
				}
				index = ((index.multiply(para1).add(para2)).mod(para3));
				// index=m;
				System.out.println("index: " + index);
				int[] assigns = cyctarH.indexToAssignment(index.add(BigInteger.ONE));
				BigInteger prob = BigInteger.ONE;
				for (int n = 0; n < N; n++) {
					int[] subassign = tables.get(nodesTarH[n]).getSubAssignmentOrderless(assigns, nodesTarH);
					int ind = tables.get(nodesTarH[n]).assignmentToIndex(subassign);
					if (ind <= 0) {
						System.out.println("ind: " + ind + " n is: " + n + "  and N: " + N);
						System.out.println("index: " + (index.add(BigInteger.ONE)) + "  ");
					}
					BigInteger k = tables.get(nodesTarH[n]).getCPT(ind);
					prob = prob.multiply(k);
				}
				cyctarH.addCPT(assigns[nodesTarH.length - 1], prob, new CyclicSampling.adder<BigInteger>() {
					public BigInteger add(BigInteger a, BigInteger b) {
						return a.add(b);
					}

					public BigInteger zero() {
						return BigInteger.ZERO;
					}
				});
			} else {
				break;
			}
		}
		end = System.currentTimeMillis();
		System.out.println("time: " + (end - start) + " ms");
		cyctarH.printCPT(cyclic_fileName);
	}

	public void doRandomSampling() {
		System.out.println("+++++++++++++++++++++++++++++++++random sampling");
		long start = 0;
		long end = 0;
		long prev;
		long current;
		long time = max_sampling_time;
		// step1: read in data.
		RandomSampling.Result resultRan = RandomSampling.ConstructTablesFromFile.constructTablesFromFile(graph,
				1000000);
		int[] nsize = resultRan.ranges;
		Hashtable<Integer, RandomSampling.Table<BigInteger>> tablesRan = resultRan.tables;
		// Step 2: create target table node 19.
		int N = order.length + 1;
		int target = targetNode;
		int[] nodesTarH = new int[N];
		for (int i = 0; i < order.length; i++) {
			nodesTarH[i] = order[i];
		}
		nodesTarH[nodesTarH.length - 1] = target;
		int nodeTarH = maxIndex + 1;
		BigInteger[] pTarH = new BigInteger[nsize[target]];

		RandomSampling.Table<BigInteger> tarRan = new RandomSampling.Table<BigInteger>(nodeTarH, nodesTarH, nsize,
				nsize[target]);
		// COPY FROM HERE
		// get total number of samples
		BigInteger totalSamples = BigInteger.ONE;
		for (int k = 0; k < nodesTarH.length; k++) {
			totalSamples = totalSamples.multiply(new BigInteger("" + nsize[nodesTarH[k]]));
		}

		// Step 3: start to sample
		start = System.currentTimeMillis();
		prev = start;
		current = System.currentTimeMillis();
		int count = 1;
		for (int m = 0; totalSamples.compareTo(new BigInteger("" + m)) > 0; m++) {
			current = System.currentTimeMillis();
			if (current - start <= time) {// time .
				current = System.currentTimeMillis();
				if (current - prev >= period) {
					prev = current;
					System.out.println("sample: " + count++);
					tarRan.printCPT(Random_fileName);
				}
				Random rnd = new Random();
				BigInteger r;
				do {
					r = new BigInteger(totalSamples.bitLength(), rnd);
				} while (r.compareTo(totalSamples) >= 0);
				BigInteger index = r;
				int[] assigns = tarRan.indexToAssignment(index.add(BigInteger.ONE));
				BigInteger prob = BigInteger.ONE;

				for (int n = 0; n < N; n++) {
					int[] subassign = tablesRan.get(nodesTarH[n]).getSubAssignmentOrderless(assigns, nodesTarH);
					int ind = tablesRan.get(nodesTarH[n]).assignmentToIndex(subassign);
					if (ind <= 0) {
						System.out.println("ind: " + ind + " n is: " + n + "  and N: " + N);
						System.out.println("index: " + (index.add(BigInteger.ONE)) + "  ");
					}
					BigInteger k = tablesRan.get(nodesTarH[n]).getCPT(ind);
					prob = prob.multiply(k);
				}
				tarRan.addCPT(assigns[nodesTarH.length - 1], prob, new RandomSampling.adder<BigInteger>() {
					public BigInteger add(BigInteger a, BigInteger b) {
						return a.add(b);
					}

					public BigInteger zero() {
						return BigInteger.ZERO;
					}
				});
			} else {
				break;
			}
		}
		end = System.currentTimeMillis();
		System.out.println("time: " + (end - start) + " ms");
		tarRan.printCPT(Random_fileName);
	}
}
