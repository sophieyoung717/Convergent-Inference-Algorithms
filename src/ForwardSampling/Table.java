package ForwardSampling;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

interface adder<Key> {
    Key zero(); // Adding zero items
    Key add(Key lhs, Key rhs); // Adding two items
}
public class Table<Key>{
	private int node; 
	private int[] nodes; 	
	private int[] range;
	private Key[][] cpts;
	private Key[] cptValues;
	
	public Table(int node,int[] nodes,int[] range,Key[][] cpts,Key[] cptValues){
		this.nodes=nodes;
		this.node=node;
		this.range=range;
		this.cpts=cpts;
		this.cptValues=cptValues;
	}
	public int getNode() {
		return node;
	}
	public void setNode(int node) {
		this.node = node;
	}
	public int[] getNodes() {
		return nodes;
	}
	public void setNodes(int[] nodes) {
		this.nodes = nodes;
	}
	public int[] getRange() {
		return range;
	}
	public void setRange(int[] range) {
		this.range = range;
	}
	public Key[][] getCpt() {
		return cpts;
	}
	public void setCpt(Key[][] cpts) {
		this.cpts = cpts;
	}
	public Key[] getCptValues() {
		return cptValues;
	}
	public void setCptValues(Key[] cptValues) {
		this.cptValues = cptValues;
	}
	public void addProb(Key value,Key prob,adder<Key> adder){
		int i=0;
		for(int j=0;j<cpts.length;j++){
			if(cpts[j][nodes.length-1].equals(value)){i=j;}
		}
		if(cptValues[i]==null) cptValues[i]=adder.zero();
		cptValues[i]=adder.add(cptValues[i],prob);
	}
//	public void printProb(){
//		if(cptValues.length==0){System.out.println("The length of cpt value is zero");return;}
//		for(int i=0;i<cptValues.length;i++){
//			if(i==cptValues.length-1) System.out.print(cptValues[i]);
//			else System.out.print(cptValues[i]+",");			
//		}
//		System.out.println();
//	}
	public void printProb(String fileName){
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			for(int i=0;i<cptValues.length;i++){
				if(i==cptValues.length-1) writer.print(cptValues[i]);
				else writer.print(cptValues[i]+",");			
			}
			writer.println();
			writer.close();
			} catch (Exception e) {
				e.printStackTrace();
		}
	}
	public void printCPT(){
		if(cpts.length==0){System.out.println("The length of cpt is zero");return;}
		for(int i=0;i<cpts.length;i++){
			System.out.print("{");
			for(int j=0;j<cpts[0].length;j++){
				if(j==cpts[0].length-1) System.out.print(cpts[i][j]+"}");
				else System.out.print(cpts[i][j]+",");	
			}
			System.out.print(",");
		}
		System.out.println();
	}
}