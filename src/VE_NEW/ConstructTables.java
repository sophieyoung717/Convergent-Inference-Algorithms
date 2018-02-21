package VE_NEW;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

public class ConstructTables {
	static final Random rand = new Random(42);
	public static Hashtable<Integer,Table<BigInteger>> constructTables(LinkedList<int[]> matrix,int[] nsize,int MaxRandomNumber){
		Hashtable<Integer,Table<BigInteger>> tables=new Hashtable<Integer,Table<BigInteger>>();
		for(int i=0;i<matrix.size();i++){
			//for every node, the domain starts from 1.	
			if(matrix.get(i).length==1){	
				ArrayList<Integer> nodes=new ArrayList<Integer>();
				nodes.add(i);
				//BigInteger[] p=new BigInteger[nsize[i]];
				tables.put(i, new Table<BigInteger>(i,nodes,nsize,nsize[i]));
				Table<BigInteger> table=tables.get(i);
				for(int j=0;j<nsize[matrix.get(i)[0]];j++){
					String tmp=String.valueOf((long)(rand.nextDouble()*MaxRandomNumber+1));
					//p[j]=new BigInteger(tmp);
					table.setCpt(j+1, new BigInteger(tmp));					
				}
				
			}
			else{
				int[] arr=matrix.get(i);
				ArrayList<Integer> nodes=new ArrayList<Integer>();
				for(int k=0;k<arr.length;k++){
					nodes.add(arr[k]);
				}
				int numRows=1;
				for(int k=0;k<arr.length;k++){
					numRows*=nsize[arr[k]];
				}
				//BigInteger[] prob=new BigInteger[numRows];
				tables.put(i,new Table<BigInteger>(i,nodes,nsize,numRows));
				Table<BigInteger> table=tables.get(i);
				int parentN=numRows/nsize[i];//how many parent combinations for each target value.
				BigInteger normalize=new BigInteger(String.valueOf(parentN*MaxRandomNumber));//use to normalize for each child value
				for(int k=0;k<parentN;k++){
					//find random numbers for each unique range[0], record sum to renormalize
					BigInteger sum=new BigInteger("0");
					for(int j=0;j<nsize[i];j++){
						table.setCpt(k+j*parentN+1, new BigInteger(String.valueOf((long)(rand.nextDouble()*MaxRandomNumber+1))));
						//prob[k+j*parentN]=new BigInteger(String.valueOf((long)(rand.nextDouble()*MaxRandomNumber+1)));
						sum=sum.add(table.getCPT(k+j*parentN+1));
					}
					BigInteger sumToOne=new BigInteger("0");
					for(int r=0;r<nsize[i];r++){
						if(r==nsize[i]-1){
							table.setCpt(k+r*parentN+1,normalize.subtract(sumToOne));
						}
						else{
							table.setCpt(k+r*parentN+1,(table.getCPT(k+r*parentN+1).multiply(normalize)).divide(sum));
							sumToOne=sumToOne.add(table.getCPT(k+r*parentN+1));
						}
					}
				}
			}
		}
		return tables;
	}

}
