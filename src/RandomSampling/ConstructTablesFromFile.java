package RandomSampling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;

public class ConstructTablesFromFile {
	public static Result constructTablesFromFile(String fileName, int times) {
		Hashtable<Integer, Table<BigInteger>> tables = new Hashtable<Integer, Table<BigInteger>>();
		Hashtable<String, Integer> nameToIndex = new Hashtable<String, Integer>();
		Hashtable<Integer, String> indexToName = new Hashtable<Integer, String>();
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("node")) {// create table object.
					String[] splits = line.split(" ");
					int node = count;// node
					nameToIndex.put(splits[1], count);
					indexToName.put(count, splits[1]);
					Table<BigInteger> table = new Table<BigInteger>(node);
					tables.put(count, table);
					// range
					line = reader.readLine();
					int inbf = line.indexOf("[");
					int inaf = line.indexOf("]");
					int range = Integer.parseInt(line.substring(inbf + 1, inaf).trim());
					ranges.add(count, range);
					count++;
				} else if (line.startsWith("probability")) {// put cpts in table.
					// ranges
					int[] range = convertIntegers(ranges);
					// get nodes
					int inbf = line.indexOf("(");
					int inaf = line.indexOf(")");
					String nodesStr = line.substring(inbf + 1, inaf).trim();
					int inVLine = line.indexOf("|");

					if (inVLine == -1) {// singe node (prior)
						Table<BigInteger> table = tables.get(nameToIndex.get(nodesStr));
						ArrayList<Integer> nodes = new ArrayList<Integer>();// nodes
						nodes.add(nameToIndex.get(nodesStr));
						line = reader.readLine().trim();// cpt
						String[] splits = line.split(",");
						// BigInteger[] cpt=new BigInteger[splits.length];
						Table<BigInteger> table1 = new Table<BigInteger>(table.getNode(), convertIntegers(nodes), range,
								splits.length);

						for (int i = 0; i < splits.length; i++) {
							splits[i] = splits[i].trim().replaceAll(";", "");
							int v = (int) (Double.parseDouble(splits[i]) * times);
							// cpt[i]=new BigInteger(String.valueOf(v));
							table1.setCpt(i + 1, new BigInteger(String.valueOf(v)));
						}
						tables.put(nameToIndex.get(nodesStr), table1);
					} else {// cpt
						String nodeStr = line.substring(inbf + 1, inVLine).trim();// node name
						int node = nameToIndex.get(nodeStr);
						String parentStr = line.substring(inVLine + 1, inaf).trim();// parent str
						String[] parents = parentStr.split(",");
						Table<BigInteger> table = tables.get(nameToIndex.get(nodeStr));
						ArrayList<Integer> nodes = new ArrayList<Integer>();// nodes
						int c = 0;
						int cptsize = 1;
						for (int i = 0; i < parents.length; i++) {
							int nodeInd = nameToIndex.get(parents[i].trim());
							nodes.add(c++, nodeInd);
							cptsize *= ranges.get(nodeInd);
						}
						nodes.add(c, node);
						cptsize *= ranges.get(node);
						// cpt
						// BigInteger[] cpt=new BigInteger[cptsize];
						Table<BigInteger> table1 = new Table<BigInteger>(table.getNode(), convertIntegers(nodes), range,
								cptsize);
						int parentsize = cptsize / ranges.get(node);
						int nodeRange = ranges.get(node);
						for (int i = 0; i < parentsize; i++) {
							line = reader.readLine();// cpt
							inbf = line.indexOf(":");
							inaf = line.indexOf(";");
							String cptValues = line.substring(inbf + 1, inaf).trim();
							String[] splits = cptValues.split(",");
							for (int j = 0; j < splits.length; j++) {
								splits[j] = splits[j].trim();
								int v = (int) (Double.parseDouble(splits[j]) * times);
								// cpt[i+j*parentsize]=new BigInteger(String.valueOf(v));
								table1.setCpt(i + j * parentsize + 1, new BigInteger(String.valueOf(v)));
							}
						}
						tables.put(nameToIndex.get(nodeStr), table1);

					}
				}
				line = reader.readLine();// line for "}"
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int[] rangesFinal = convertIntegers(ranges);
		Result result = new Result(tables, rangesFinal, indexToName);
		return result;
	}

	public static int[] convertIntegers(ArrayList<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	public static void main(String[] args) {
		Result result = constructTablesFromFile("child.dsc", 100);
		result.tables.size();
	}
}
