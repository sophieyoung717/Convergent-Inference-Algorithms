package VE_Sampling;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;


public class ConstructTables {//construct tables from graph, return hashtable with table name as a key.

	public static final Random rand = new Random(42);
	public static Hashtable<Integer,Table<BigInteger>> constructTables(LinkedList<int[]> matrix,int[] nsize,int MaxRandomNumber){
		Hashtable<Integer,Table<BigInteger>> tables=new Hashtable<Integer,Table<BigInteger>>();
		for(int i=0;i<matrix.size();i++){
			//for every node, the domain starts from 1.	
			if(matrix.get(i).length==1){	
				ArrayList<Integer> nodes=new ArrayList<Integer>();
				nodes.add(i);
				BigInteger[] p=new BigInteger[nsize[i]];
				BigInteger[] count=new BigInteger[nsize[i]];
				for(int j=0;j<nsize[matrix.get(i)[0]];j++){
					String tmp=String.valueOf((long)(rand.nextDouble()*MaxRandomNumber+1));
					p[j]=new BigInteger(tmp);
					count[j]=BigInteger.ONE;
				}
				BigInteger sum=BigInteger.ZERO;
				tables.put(i, new Table<BigInteger>(i,nodes,nsize,p,count,sum));
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
				BigInteger[] prob=new BigInteger[numRows];
				BigInteger[] count=new BigInteger[numRows];
				int parentN=numRows/nsize[i];//how many parent combinations for each target value.
				BigInteger normalize=new BigInteger(String.valueOf(parentN*MaxRandomNumber));//use to normalize for each child value
				for(int k=0;k<parentN;k++){
					//find random numbers for each unique range[0], record sum to renormalize
					BigInteger sum=new BigInteger("0");
					for(int j=0;j<nsize[i];j++){
						prob[k+j*parentN]=new BigInteger(String.valueOf((long)(rand.nextDouble()*MaxRandomNumber+1)));
						count[k+j*parentN]=BigInteger.ONE;
						sum=sum.add(prob[k+j*parentN]);
					}
					BigInteger sumToOne=new BigInteger("0");
					for(int r=0;r<nsize[i];r++){
						if(r==nsize[i]-1){
							prob[k+r*parentN]=normalize.subtract(sumToOne);
							
						}
						else{
							prob[k+r*parentN]=((prob[k+r*parentN].multiply(normalize)).divide(sum));
							sumToOne=sumToOne.add(prob[k+r*parentN]);
						}
						count[k+r*parentN]=BigInteger.ONE;
					}
				}
				BigInteger sum=BigInteger.ZERO;
				tables.put(i,new Table<BigInteger>(i,nodes,nsize,prob,count,sum));
			}
		}
		return tables;
	}
}
