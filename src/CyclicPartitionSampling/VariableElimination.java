package CyclicPartitionSampling;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class VariableElimination {
	public static Hashtable<Integer,Table<BigInteger>> tables=new Hashtable<Integer,Table<BigInteger>>();
	public static int[] ranges;
	
	public VariableElimination(Hashtable<Integer,Table<BigInteger>> tables,int[] ranges){
		this.tables=tables;
		this.ranges=ranges;
	}

	public static void infer(int[] order, int target){
		for(int i=0;i<order.length;i++){
			int elim=order[i];
			Set<Integer> keys=tables.keySet();
			ArrayList<Integer> tablesWithElim=new ArrayList<Integer>();
			for(int key:keys){
				Table<BigInteger> table=tables.get(key);
				ArrayList<Integer>tmpNodes=Tools.initArrayList(table.getNodes());
				if(tmpNodes.contains(elim)){
					tablesWithElim.add(key);
				}
			}
			//join
			if(tablesWithElim.size()>1){
				join(tablesWithElim,++target);
				eliminate(target,elim);	
			}
			else{
				eliminate(tablesWithElim.get(0),elim);
			}
		}
		if(tables.size()==1){
			Set<Integer> keys=tables.keySet();
			for(int key:keys){
				Table<BigInteger> table=tables.get(key);
				table.printCPT();
			}
		}
		else{System.err.println("Something is wrong with infer");}
	}
	public static void join(ArrayList<Integer> tablesWithElim, int name){
		System.out.println("++++++++++++++++++++joint table: "+tablesWithElim.toString()+" and create a new table named: "+name);
		//create new generated table-intermediate table.
		//nodes
		HashSet<Integer> sets=new HashSet<Integer>();
		for(int i=0;i<tablesWithElim.size();i++){
			sets.addAll(Tools.initArrayList(tables.get(tablesWithElim.get(i)).getNodes()));
		}
		ArrayList<Integer> nodes=new ArrayList<Integer>(sets);
		int[] nodesArr=Tools.convertIntegers(nodes);
		int cptsize=1;
		for(int i=0;i<nodes.size();i++){
			cptsize*=ranges[nodes.get(i)];
		}
		//cpts
		BigInteger[] p=new BigInteger[cptsize];
		Table<BigInteger> phi=new Table<BigInteger>(name,nodesArr,ranges,p);
		for(int j=0;j<p.length;j++){
			BigInteger prob=BigInteger.ONE;
			int[] assigns=phi.indexToAssignment(j+1);
			int[] order=new int[nodes.size()];
			for(int k=0;k<nodes.size();k++){
				order[k]=nodes.get(k);
			}
			for(int i=0;i<tablesWithElim.size();i++){
				Table<BigInteger> table=tables.get(tablesWithElim.get(i));			
				int[] subAssign=table.getSubAssignmentOrderless(assigns, order);
				int ind=table.assignmentToIndex(subAssign);
				BigInteger k=table.getCPT(ind);
				prob=prob.multiply(k);
			}
			phi.addCPT((j+1),prob,new adder<BigInteger>() {
				public BigInteger add(BigInteger a, BigInteger b) {
	                return a.add(b);
	            }
	            public BigInteger zero() {
	                return  BigInteger.ZERO;
	            }
	        });
		}		
		//add new table and remove tablesWithElim in tables
		tables.put(name, phi);
		for(int i=0;i<tablesWithElim.size();i++){
			tables.remove(tablesWithElim.get(i));
		}	
	}	
	public static void eliminate(int target, int elim){
		System.out.println("++++++++++++++++++eliminate table: "+target+" on node: "+elim);
		Table<BigInteger> table=tables.get(target);		
		//create new generated table-intermediate table.
		//nodes
		ArrayList<Integer> nodes=new ArrayList<Integer> (Tools.initArrayList(table.getNodes()));
		nodes.remove(Integer.valueOf(elim));
		int[] nodesArr=Tools.convertIntegers(nodes);
		//cpts
		int cptsize=table.getCPTs().length/ranges[elim];
		BigInteger[] p=new BigInteger[cptsize];
		Table<BigInteger> tao=new Table<BigInteger>(table.getNode(),nodesArr,ranges,p);		
		for(int i=0;i<table.getCPTs().length;i++){
			int[] assigns=table.indexToAssignment(i+1);
			BigInteger prob=table.getCPT(i+1);
			int[] order=new int[table.getNodes().length];
			for(int k=0;k<table.getNodes().length;k++){
				order[k]=table.getNodes()[k];
			}
			int[] subAssign=tao.getSubAssignmentOrderless(assigns, order);
			int ind=tao.assignmentToIndex(subAssign);
		//	System.out.println("index: "+ind+"tao: "+tao.getNode());
			tao.addCPT(ind,prob,new adder<BigInteger>() {
				public BigInteger add(BigInteger a, BigInteger b) {
				//	System.out.println("a: "+a+" b: "+b);
	                return a.add(b);
	            }
	            public BigInteger zero() {
	                return  BigInteger.ZERO;
	            }
			});
		}
		tables.put(target, tao);
	}
	
}
