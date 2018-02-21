package VE_Sampling;

import java.util.ArrayList;

public class JointTable<Key> {

	public Table<Key> table;
	private long[] count;//default is 1.
	private ArrayList<Integer> relatedTables;//the subset of input tables to join to obtain the joint table.
	private int taoTableName;//the tao table to eliminate to.
	public boolean finalRound = true; // set if at the start of a sampling cycle, all inputs are complete.
	public  long total;//used by RSA sampling
	public long samplesToReset;
	public long index;
	public long para1;
	public long para2;
	public long para3;
	public JointTable(Table<Key> table, ArrayList<Integer> jtables, int tao,int total){
		this.table=table;
		this.relatedTables=jtables;
		count=new long[table.getCPTs().length];
		for(int i=0;i<table.getCPTs().length;i++){
			count[i]=1L;
		}
		this.taoTableName=tao;
		this.total=total;
		this.samplesToReset = total;
	}
	
	public ArrayList<Integer> getRelatedTables() {
		return relatedTables;
	}
	public void setRelatedTables(ArrayList<Integer> relatedTables) {
		this.relatedTables = relatedTables;
	}

	public int getTaoTableName() {
		return taoTableName;
	}

	public void setTaoTableName(int taoTableName) {
		this.taoTableName = taoTableName;
	}
	
	
	
}
