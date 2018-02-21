package ForwardSampling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import edu.princeton.cs.algs4.Digraph;

public class ForwardSampling{
	public static final Random rand = new Random(36);
	public Hashtable<Integer,Table<Long>> tables=new Hashtable<Integer,Table<Long>>();
	public int[] rangesFS;
	public ForwardSampling(String fileName){
		try {
			boolean getRange=true;
			BufferedReader reader = new BufferedReader(new FileReader (fileName));
			String line=null;
			int count=0;
			ArrayList<Integer> rangesAll=new ArrayList<Integer>();
			while( ( line = reader.readLine() ) != null ) {
				if(!line.equals("=====")){
				int node=Integer.parseInt(line);//node
				String nodestmp=reader.readLine();
				String[] nodesarr=nodestmp.split(",");
				int[] nodes=new int[nodesarr.length];
				for(int i=0;i<nodes.length;i++){
					nodes[i]=Integer.parseInt(nodesarr[i]);//nodes
				}
				String rangestmp=reader.readLine();
				String[] rangesarr=rangestmp.split(",");
				
				if(getRange){//get Range for all nodes.
					getRange=false;
					rangesFS=new int[rangesarr.length];
					for(int i=0;i<rangesFS.length;i++){
						rangesFS[i]=Integer.parseInt(rangesarr[i]);
					}
				}
				
				int[] ranges=new int[nodes.length];
				for(int i=0;i<nodes.length;i++){
					ranges[i]=Integer.parseInt(rangesarr[nodes[i]]);//ranges
				}
				int numRows=1;
				for(int i=0;i<nodes.length;i++){
					numRows*=ranges[i];
				}
				Long[][] cpt=new Long[numRows][nodes.length];//cpt
				for(int j=0;j<numRows;j++){
					String cpttmp=reader.readLine();
					String[] cptarr=cpttmp.split(",");
					//System.out.println("+++++++++++++");
					for(int i=0;i<nodes.length;i++){
						//System.out.println("i: "+i+"  j: "+j);
						cpt[j][i]=Long.parseLong(cptarr[i]);
					}
				}
				Long[] cptValues=new Long[numRows]; //cptValues
				String cptvalue=reader.readLine();
				String[] cptvalueArr=cptvalue.split(",");
				for(int i=0;i<cptvalueArr.length;i++){
					cptValues[i]=Long.parseLong(cptvalueArr[i]);
				}
				tables.put(node, new Table<Long>(node,nodes,ranges,cpt,cptValues));
			}
				else{break;}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void infer(int numSamples,int[] order,int target,Table<Long> targetTable,String fileName){
		long start=System.currentTimeMillis();
		long prev=start;
		long current=System.currentTimeMillis();
	//	for(int i=0;i<numSamples;i++){
		while(current-start<=numSamples){//numSamples is time now.
			// for experiment part2
			current=System.currentTimeMillis();
			if(current-prev>=200){
				prev=current;
				targetTable.printProb(fileName);
			}
			Hashtable<Integer,Long> tuple=new Hashtable<Integer,Long>(); Long prob=1L;//key:node index, value:node value
			for(int j=0;j<order.length;j++){
				Table<Long>table=tables.get(order[j]);
				Long p=getRandomValue(tuple,table);
				prob*=p;//NOT PROB COUNT THE FREQUENCY, SO 1.
			}
			//targetTable.addProb(tuple.get(target), prob, new adder<Long>() {
			targetTable.addProb(tuple.get(target), 1L, new adder<Long>() {
	            public Long add(Long a, Long b) {
	                return a+b;
	            }
	            public Long zero() {
	                return (long) 0;
	            }
	        });
			current=System.currentTimeMillis();
//			try {PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("sampleMCMC.txt", true)));
//				for(int k=0;k<targetTable.getCptValues().length;k++){
//					if(k==targetTable.getCptValues().length-1) writer.print(targetTable.getCptValues()[k]);
//					else writer.print(targetTable.getCptValues()[k]+",");			
//				}
//			writer.println();
//			writer.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		}
		
		//targetTable.printCPT();
		targetTable.printProb(fileName);
	}
	public static Long getRandomValue(Hashtable<Integer,Long> tuple,Table<Long>table){
		Long result;
		Table<Long> candidate=getCandidateTuples(tuple,table);
		result=addAttribute(tuple,candidate);
		if(result==-1L){System.err.println("did not get random value, something is wrong");}
		return result;
	}
	public static Table<Long> getCandidateTuples(Hashtable<Integer,Long> tuple,Table<Long> table){
		Table<Long> candidate=table;
		if(table.getNodes().length>1){
		int tarIndex=table.getRange().length-1;
		Long[][] cpts=new Long[table.getRange()[tarIndex]][1];
		Long[] cptValues=new Long[table.getRange()[tarIndex]];
		int count=0;
		for(int i=0;i<table.getCpt().length;i++){
			int j=0;
			for(;j<table.getNodes().length-1;j++){
				int nodeIndex=table.getNodes()[j];
				if(table.getCpt()[i][j]!=tuple.get(nodeIndex)){break;}
			}
			if(j==table.getNodes().length-1){
				cpts[count][0]=table.getCpt()[i][j];
				cptValues[count]=table.getCptValues()[i];
				count++;
			}
		}
		int[] nodes=new int[]{table.getNodes()[tarIndex]};
		int[] ranges=new int[]{table.getRange()[tarIndex]};
		candidate=new Table<Long>(table.getNode(),nodes,ranges,cpts,cptValues);
		}
		return candidate;
		
	}
	
	public static Long addAttribute(Hashtable<Integer,Long> tuple,Table<Long>table){
		Long result=-1L;
		Long[] bucket=new Long[table.getCpt().length+1];
		bucket[0]=0L;
		Long sum=0L;
		for(int i=0;i<table.getCptValues().length;i++){//copy
			bucket[i+1]=table.getCptValues()[i];
			sum+=table.getCptValues()[i];
		}
		for(int i=1;i<bucket.length;i++){//cumulate
			bucket[i]+=bucket[i-1];
		}
		//Long random=(long)(Math.random()*sum+1);
		Long random=(long)(rand.nextDouble()*sum+1);
		for(int i=0;i<bucket.length-1;i++){
			 if(random>bucket[i]&&random<=bucket[i+1]){
				tuple.put(table.getNodes()[table.getNodes().length-1], table.getCpt()[i][0]);
				result=table.getCptValues()[i];
				break;
			}
		}
		return result;
	}

}
