package RandomSampling;

import java.math.BigInteger;
import java.util.Hashtable;

public class Result{
	public Hashtable<Integer,Table<BigInteger>> tables=new Hashtable<Integer,Table<BigInteger>>();
	public int[] ranges;
	public Hashtable<Integer,String> indexToName=new Hashtable<Integer,String>();
	Result(Hashtable<Integer,Table<BigInteger>> tables,int[] ranges,Hashtable<Integer,String> indexToName){
		this.tables=tables;
		this.ranges=ranges;
		this.indexToName=indexToName;
	}
}