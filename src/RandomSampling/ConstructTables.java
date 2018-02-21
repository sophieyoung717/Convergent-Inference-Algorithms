package RandomSampling;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Random;

public class ConstructTables {

	static final Random rand = new Random(42);
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Table[] constructTables(LinkedList<int[]> matrix,int[] nsize,int MaxRandomNumber){
		Table<BigInteger>[] tables=new Table[matrix.size()];
		for(int i=0;i<matrix.size();i++){
			//for every node, the domain starts from 1.	
			if(matrix.get(i).length==1){	
				int[] nodes={i};
				tables[i]=new Table<BigInteger>(i,nodes,nsize,nsize[i]);
				//Long[] p=new Long[nsize[i]];
				for(int j=0;j<nsize[matrix.get(i)[0]];j++){
					String tmp=String.valueOf((long)(rand.nextDouble()*MaxRandomNumber+1));
					//p[j]=new BigInteger(tmp);
					tables[i].setCpt(j+1, new BigInteger(tmp));	
				}
				//tables[i]=new Table<Long>(i,nodes,nsize,p);
			}
			else{
				int[] arr=matrix.get(i);
				int numRows=1;
				for(int k=0;k<arr.length;k++){
					numRows*=nsize[arr[k]];
				}
				//Long[] prob=new Long[numRows];
				tables[i]=new Table<BigInteger>(i,arr,nsize,numRows);
				int parentN=numRows/nsize[i];//how many parent combinations for each target value.
				BigInteger normalize=new BigInteger(String.valueOf(parentN*MaxRandomNumber));//use to normalize for each child value
				for(int k=0;k<parentN;k++){
					//find random numbers for each unique range[0], record sum to renormalize
					BigInteger sum=new BigInteger("0");
					for(int j=0;j<nsize[i];j++){
						tables[i].setCpt(k+j*parentN+1, new BigInteger(String.valueOf((long)(rand.nextDouble()*MaxRandomNumber+1))));
						sum=sum.add(tables[i].getCPT(k+j*parentN+1));
					}
					BigInteger sumToOne=new BigInteger("0");
					for(int r=0;r<nsize[i];r++){
						if(r==nsize[i]-1){
							tables[i].setCpt(k+r*parentN+1,normalize.subtract(sumToOne));
						}
						else{
							tables[i].setCpt(k+r*parentN+1,(( tables[i].getCPT(k+r*parentN+1)).multiply(normalize)).divide(sum));
							sumToOne=sumToOne.add(tables[i].getCPT(k+r*parentN+1));
						}
					}
				}
				//tables[i]=new Table<Long>(i,arr,nsize,prob);
			}
		}
		return tables;
	}
}
