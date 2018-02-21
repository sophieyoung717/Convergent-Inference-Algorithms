package CyclicPartitionSampling;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Tools {

	public static void printIntermediateResult(Table<BigInteger>tarGS,Table<BigInteger>tarHGS){
		int C=0,D=1,I=2,S=3,G=4,L=5,J=6,H=7;
		int[] nodesTarH={H};int nodeTarH=H;
		BigInteger[] pTarH=new BigInteger[tarHGS.getRange(H)];
		Table<BigInteger> tarH=new Table<BigInteger>(nodeTarH,nodesTarH,tarHGS.getRange(),pTarH);
		
		int totalHGS=1;
		for(int i=0;i<tarHGS.getNodes().length;i++){
			totalHGS*=tarHGS.getRange(tarHGS.getNodes()[i]);
		}
		BigInteger prob=BigInteger.ONE;
		for(int i=0;i<totalHGS;i++){
			prob=prob.multiply(tarHGS.getCPT(i+1));
			int[] assign=tarHGS.indexToAssignment(i+1);
			int[] subassign=tarGS.getSubAssignmentOrderless(assign,tarHGS.getNodes());
			int ind=tarGS.assignmentToIndex(subassign);
			BigInteger k=tarGS.getCPT(ind);
			prob=prob.multiply(k);
			int[] subassignH=tarH.getSubAssignmentOrderless(assign,tarHGS.getNodes());
			tarH.addCPT(subassignH[0],prob,new adder<BigInteger>() {
	            public BigInteger add(BigInteger a, BigInteger b) {
	                return a.add(b);
	            }
	            public BigInteger zero() {
	                return  BigInteger.ZERO;
	            }
	        });
			prob=BigInteger.ONE;
		}
		tarH.printCPT();
	}
	public static ArrayList<Integer> initArrayList(int[] ints)
	{
	    ArrayList<Integer> list = new ArrayList<Integer>();
	    for (int i : ints)
	    {
	        list.add(i);
	    }
	    return list;
	}
	public static Set<Long> getPrimes(long[] arr){
		Set<Long> sets=new HashSet<Long>();
		for(int i=0;i<arr.length;i++){
			Set<Long> set=new HashSet<Long>();
			getPrimes(arr[i],set);
			sets.addAll(set);
		}
		return sets;
	}
	public static void getPrimes(long value,Set<Long>set){
		boolean isFirst=true;
		while(value%2==0){
			if(isFirst)set.add((long) 2);
			value=value/2;
		}
		for(int i=3;i<=Math.sqrt(value);i=i+2){
			while(value%i==0){
				set.add((long)i);
				value=value/i;
			}
		}
		if(value>2) set.add(value);		
	}
	public static boolean isPrime(Long value){
		if(value%2==0){return false;}
		for(int i=3;i<=Math.sqrt(value);i=i+2){
			if(value%i==0){return false;}
		}
		return true;
	}
	public static long getCoPrime(long value){
		Long result=value*value;
		Set<Long> set=new HashSet<Long>();
		getPrimes(value,set);
		Long max=Long.MIN_VALUE;
		for(Long v:set){
			if(max<v)max=v;
		}
		for(Long i=(Long)(max+1);i<value;i++){
			if(isPrime(i)){
				result=i;
				break;
			}
		}
		return result;
	}
	public static long getA(long[] arr,long totalSamples){
		long result=1;
		Set<Long>set=getPrimes(arr);
		for(Long s:set){
			result*=s;
		}
		if(totalSamples%4==0&&result%4!=0){
			result*=4;
		}
		return result+1;
	}
	public static void exchange(int[] arr,int i,int j){
		int tmp=arr[i];
		arr[i]=arr[j];
		arr[j]=tmp;
	}
	public static int[] convertIntegers(ArrayList<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = integers.get(i).intValue();
	    }
	    return ret;
	}
	
}
