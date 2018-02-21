package VE_Sampling;

import java.math.BigInteger;
import java.util.Hashtable;

public class Result{
	public Hashtable<Integer,Table<BigInteger>> tables=new Hashtable<Integer,Table<BigInteger>>();
	public int[] ranges;
	Result(Hashtable<Integer,Table<BigInteger>> tables,int[] ranges){
		this.tables=tables;
		this.ranges=ranges;
	}
}
