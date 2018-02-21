package CyclicPartitionSampling;

import java.util.LinkedList;
import java.util.Random;

public class ConstructTables {

	static final Random rand = new Random(42);
	@SuppressWarnings("rawtypes")
	public static Table[] constructTables(LinkedList<int[]> matrix,int[] nsize,int MaxRandomNumber){
		Table[] tables=new Table[matrix.size()];
		for(int i=0;i<matrix.size();i++){
			//for every node, the domain starts from 1.	
			if(matrix.get(i).length==1){	
				int[] nodes={i};
				Long[] p=new Long[nsize[i]];
				for(int j=0;j<nsize[matrix.get(i)[0]];j++){
					p[j]=(long)(rand.nextDouble()*MaxRandomNumber+1);
				}
				tables[i]=new Table<Long>(i,nodes,nsize,p);
			}
			else{
				int[] arr=matrix.get(i);
				int numRows=1;
				for(int k=0;k<arr.length;k++){
					numRows*=nsize[arr[k]];
				}
				Long[] prob=new Long[numRows];
				int parentN=numRows/nsize[i];//how many parent combinations for each target value.
				int normalize=parentN*MaxRandomNumber;//use to normalize for each child value
				for(int k=0;k<parentN;k++){
					//find random numbers for each unique range[0], record sum to renormalize
					int sum=0;
					for(int j=0;j<nsize[i];j++){
						prob[k+j*parentN]=(long)(rand.nextDouble()*MaxRandomNumber+1);
						sum+=prob[k+j*parentN];
					}
					int sumToOne=0;
					for(int r=0;r<nsize[i];r++){
						if(r==nsize[i]-1){
							prob[k+r*parentN]=(long)(normalize-sumToOne);
						}
						else{
							prob[k+r*parentN]=(long)(prob[k+r*parentN]*normalize/sum);
							sumToOne+=prob[k+r*parentN];
						}
					}
				}
				tables[i]=new Table<Long>(i,arr,nsize,prob);
			}
		}
		return tables;
	}
}
