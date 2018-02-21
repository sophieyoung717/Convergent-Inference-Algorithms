package CyclicPartitionSampling;

public class PartitionTable<Key> {

	private Table<Key> table;
	private long totalSamples;
	private long counter;
	private long para1;
	private long para2;
	private long para3;
	private long index;
	
	int targetTable;
	int[] order;//order of getting a sample from index+target.
	int[] tableInvolved;
	
	PartitionTable(Table<Key> table){// for target table
		this.table=table;
		totalSamples=0L;
		counter=0L;
		para1=0L;
		para2=0L;
		para3=0L;
		targetTable=table.getNode();
		order=table.getNodes();
		tableInvolved=table.getNodes();
	}
	PartitionTable(Table<Key> table,long total,long para1, long para2,long para3,long index,int targetTable,int[] order,int[] tableInvolved){// for index table
		this.table=table;
		this.totalSamples=total;
		this.para1=para1;
		this.para2=para2;
		this.para3=para3;
		this.targetTable=targetTable;
		this.order=order;
		this.tableInvolved=tableInvolved;
		counter=0L;
	}
	public Table<Key> getTable() {
		return table;
	}
	public void setTable(Table<Key> table) {
		this.table = table;
	}
	public long getTotalSamples() {
		return totalSamples;
	}
	public void setTotalSamples(long totalSamples) {
		this.totalSamples = totalSamples;
	}
	public long getCounter() {
		return counter;
	}
	public void setCounter(long counter) {
		this.counter = counter;
	}
	public long getPara1() {
		return para1;
	}
	public void setPara1(long para1) {
		this.para1 = para1;
	}
	public long getPara2() {
		return para2;
	}
	public void setPara2(long para2) {
		this.para2 = para2;
	}
	public long getPara3() {
		return para3;
	}
	public void setPara3(long para3) {
		this.para3 = para3;
	}
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}
	public int getTargetTable() {
		return targetTable;
	}
	public void setTargetTable(int targetTable) {
		this.targetTable = targetTable;
	}
	public int[] getOrder() {
		return order;
	}
	public void setOrder(int[] order) {
		this.order = order;
	}
	
	
}
