package CyclicPartitionSampling;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class ConstructPartitionTable {

	static int name=0;
	public static Hashtable<Integer,Vector> constructPartitionTable(ArrayList<ArrayList<ArrayList<Integer>>> partitions,int[] nsize){
		//for partition parameter: first is target,second is index,involved table for the index table
		Hashtable<Integer,Vector> tables=new Hashtable<Integer,Vector>();
		//iterate partition, for each one, create target and index table pair, and put in tables.
		
		for(int j=0;j<partitions.size();j++){
			ArrayList<ArrayList<Integer>> partition=partitions.get(j);
			ArrayList<Integer> target=partition.get(0);
			ArrayList<Integer> index=partition.get(1);
			ArrayList<Integer> table=partition.get(2);
			//create table for target table.
			int[] nodesTar=Tools.convertIntegers(target);int nodeTar=name++;
			int cptsize=1;
			for(int i=0;i<target.size();i++){
				cptsize*=nsize[target.get(i)];
			}
			BigInteger[] pTar=new BigInteger[cptsize];
			Table<BigInteger> tar=new Table<BigInteger>(nodeTar,nodesTar,nsize,pTar);
			//PartitionTable<BigInteger> targetTable=new PartitionTable<BigInteger>(tar);
			//tables.put(nodeTar, targetTable);
			
			//create index arr index
			int[] nodesInd=Tools.convertIntegers(index);int nodeInd=name++;
			cptsize=1;
			for(int i=0;i<target.size();i++){
				cptsize*=nsize[target.get(i)];
			}
			BigInteger[] pInd=new BigInteger[cptsize];
			Table<BigInteger> ind=new Table<BigInteger>(nodeInd,nodesInd,nsize,pInd);
			//get total number of samples for index
			long totalSamplesInd=1; 
			for(int k=0;k<ind.getNodes().length;k++){
				totalSamplesInd*=nsize[ind.getNodes()[k]];
			}
			//calculate a,c,m, (ax+c%m)  long[] from int[]
			long[] tmp=new long[ind.getNodes().length];
			for(int w=0;w<ind.getNodes().length;w++){
				tmp[w]=nsize[ind.getNodes()[w]];
			}
			long para1Ind=Tools.getA(tmp,totalSamplesInd);//a
			long para2Ind=Tools.getCoPrime(totalSamplesInd);//c
			long para3Ind=totalSamplesInd;//m
			//index can start anywhere, make it start random.
			long indexInd=(int)(Math.random()*(totalSamplesInd-1)+1);
			
			ArrayList<Integer> orderArr=new ArrayList<Integer>();
			orderArr.addAll(target);
			orderArr.addAll(index);
			int[] order=Tools.convertIntegers(orderArr);	
			int[] tableInvolved=Tools.convertIntegers(table);
			PartitionTable<BigInteger> indexTable=new PartitionTable<BigInteger>(ind,totalSamplesInd,para1Ind,para2Ind,para3Ind,indexInd,name-1,order,tableInvolved);
			Vector v=new Vector();
			v.add(tar);
			v.add(indexTable);
			tables.put(j, v);
		}	
		return tables;
	}
}
