package VE_Sampling;

import java.util.HashSet;
import java.util.Set;

//contain functions for calculating prime, static function, call by function name.
public class Tools {

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
	
}
