package RandomSampling;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;


/*
 * Table represents the CPT. Node is the one that this CPT is about, 
 * and nodes contain this CPT and his parents,
 * range is the node size, the order of nodes in CPT is:
 * The order should be: the parameter is the last one, the parent is increasing order, 
 * In the graph, C = 1; D = 2; I = 3; S = 4; G=5; L=6; J=7; H=8; So,
		e.g. cptJ=S,L,J, since S<L, J should be at the end. 

*The assignmentToIndex and IndexToAssignment is taking the orders similar to examples below:
*% -+-----+-----+-----+-------------------+   
%  | X_3 | X_1 | X_2 | phi(X_3, X_1, X_2)|
% -+-----+-----+-----+-------------------+
%  |  1  |  1  |  1  |     phi.val(1)    |
% -+-----+-----+-----+-------------------+
%  |  2  |  1  |  1  |     phi.val(2)    |
% -+-----+-----+-----+-------------------+
%  |  1  |  2  |  1  |     phi.val(3)    |
% -+-----+-----+-----+-------------------+
%  |  2  |  2  |  1  |     phi.val(4)    |
% -+-----+-----+-----+-------------------+
%  |  1  |  1  |  2  |     phi.val(5)    |
% -+-----+-----+-----+-------------------+
%  |  2  |  1  |  2  |     phi.val(6)    |
% -+-----+-----+-----+-------------------+
%  |  1  |  2  |  2  |     phi.val(7)    |
% -+-----+-----+-----+-------------------+
%  |  2  |  2  |  2  |     phi.val(8)    |
% -+-----+-----+-----+-------------------+
(see factor tutorial in PGM class PA1 for details)*/
public class Table<Key>{
	private final long CHUNK_SIZE=1024*1024*1024;
	private int node; 
	private int[] nodes; 	
	private int[] range;
	private Key[][] cpt;   //cpt assume range is from 1. 
	private long size;
	@SuppressWarnings("unchecked")
	public Table(int node,int[] nodes,int[] range,long size){
		this.nodes=nodes;
		this.node=node;
		this.range=range;
		this.size=size;
		if(size==0){cpt=null;}//create cpt.
		else{
			int chunks = (int)(size/CHUNK_SIZE);
            int remainder = (int)(size - ((long)chunks)*CHUNK_SIZE);
            cpt=(Key[][])new Object[chunks+(remainder==0?0:1)][];
            for( int idx=chunks; --idx>=0; ) {
                cpt[idx] = (Key[])new Object[(int)CHUNK_SIZE];
            }
            if( remainder != 0 ) {
                cpt[chunks] = (Key[])new Object[remainder];
            }
		}
	}
	public Table(int node){
		this.node=node;
		if(size==0){cpt=null;}//create cpt.
		else{
			int chunks = (int)(size/CHUNK_SIZE);
            int remainder = (int)(size - ((long)chunks)*CHUNK_SIZE);
            cpt=(Key[][])new Object[chunks+(remainder==0?0:1)][];
            for( int idx=chunks; --idx>=0; ) {
                cpt[idx] = (Key[])new Object[(int)CHUNK_SIZE];
            }
            if( remainder != 0 ) {
                cpt[chunks] = (Key[])new Object[remainder];
            }
		}
	}
	public int getNode() {
		return node;
	}
	public void setNode(int node) {
		this.node = node;
	}
	public int[] getNodes() {
		return nodes;
	}
	public void setNodes(int[] nodes) {
		this.nodes = nodes;
	}
	public Key getCPT(long index) {
		if(index<0||index>size){
			throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+size+" elements long.");
		}
		index=index-1;
		int chunk=(int)(index/CHUNK_SIZE);
		int offset=(int)(index-(((long)chunk))*CHUNK_SIZE);
		return cpt[chunk][offset];
	}
	public Key[][] getCpt() {
		return cpt;
	}
	public void setCpt(long index, Key b) {
		if(index<0||index>size){
			throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+size+" elements long.");
		}
		index=index-1;
		int chunk=(int)(index/CHUNK_SIZE);
		int offset=(int)(index-chunk*CHUNK_SIZE);
		//System.out.println("chunk: "+chunk+" offset: "+offset);
		cpt[chunk][offset]=b;
	}
	public void setRange(int[] range) {
		this.range = range;
	}
	public Key[][] getCPTs(){
		return cpt;
	}
	public int getRange(int index){
		return range[index];
	}
	public int[] getRange(){
		return range;
	}
	public int[] indexToAssignment(BigInteger index){
		int[] result=new int[nodes.length];
		BigInteger div= BigInteger.ONE;
		for(int i=0;i<nodes.length;i++){
			BigInteger t=new BigInteger(""+(range[nodes[i]]));
//			System.out.println("t: "+t);
			if(i==0){
				
				result[i]=((index.subtract(BigInteger.ONE)).mod(t).add(BigInteger.ONE)).intValue();
//				System.out.println("result[i]"+result);
			}
			else{
				if(div.compareTo(BigInteger.ZERO)<0){
					System.out.println("div is out of bound");
					}
//				System.out.println("old div: "+div+"   range[nodes[i-1]]: "+range[nodes[i-1]]);
				div=div.multiply(new BigInteger(""+range[nodes[i-1]]));
//				System.out.println("new div: "+div);
				result[i]=((index.subtract(BigInteger.ONE).divide(div)).mod(t).add(BigInteger.ONE)).intValue();
			}
		}
		return result;
	}
	
	public int[] getSubAssignmentOrderless(int[] assigns,int[] orders){
		int[] result=new int[nodes.length];
		for(int i=0;i<nodes.length;i++){
			int value=nodes[i];
			for(int j=0;j<orders.length;j++){
				if(orders[j]==value){
					result[i]=assigns[j];
					break;
				}	
			}
		}
		return result;
	}
	
	public int[] getSubAssignment(int[] assigns){
		int[] result=new int[nodes.length];
		for(int i=0;i<nodes.length;i++){
			result[i]=assigns[nodes[i]];
		}
		return result;
	}
	public int[] getSubArrange(int[] range){
		int[] result=new int[nodes.length];
		for(int i=0;i<nodes.length;i++){
			result[i]=range[nodes[i]];
		}
		return result;
	}
	public int assignmentToIndex(int[] assigns){
		int result=1;
		long div=1;
		for(int i=0;i<assigns.length;i++){
			if(i==0){
				result+=(assigns[i]-1)*1;
			}
			else{
				div*=range[nodes[i-1]];
				result+=(assigns[i]-1)*div;
			}
		}
		return result;
	}
	
	public void addCPT(long index, Key prob,adder<Key> adder){
		if(this.getCPT(index)==null)setCpt(index,adder.zero());//set cpt.
		setCpt(index,adder.add(getCPT(index), prob));
		
	}
	
	public void printCPT(String fileName){
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			for(int i=0;i<cpt.length;i++){
				for(int j=0;j<cpt[i].length;j++){
					writer.print(cpt[i][j]+",");		
				}
			}
			writer.println();
			writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}	
	}
	public String printCPTInMatlabFormat(String[] names){
		StringBuilder sb=new StringBuilder();
		sb.append("bnet.CPD{"+names[node]+"}=tabular_CPD(bnet,"+names[node]+",[");
		for(int i=0;i<cpt.length;i++){
			if(i==cpt.length-1) sb.append(cpt[i]);
			else sb.append(cpt[i]+",");			
		}
		sb.append("]);");
		return sb.toString();
	}
	public String printNodes(){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<nodes.length;i++){
			if(i!=nodes.length-1)
			sb.append(nodes[i]+",");
			else sb.append(nodes[i]);
		}
		return sb.toString();
	}
	public String printRanges(){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<range.length;i++){
			if(i!=range.length-1)
			sb.append(range[i]+",");
			else sb.append(range[i]);
		}
		return sb.toString();
	}
//	public String printCPTforVE(){
//		StringBuilder sb=new StringBuilder();
//		for(int i=0;i<cpt.length;i++){
//			int[] row=indexToAssignment(i+1);
//			for(int j=0;j<row.length;j++){
//				if(j!=row.length-1)
//				sb.append(row[j]+",");
//				else sb.append(row[j]+"\n");
//			}
//		}
//		for(int i=0;i<cpt.length;i++){
//			if(i!=cpt.length-1){sb.append(cpt[i]+",");}
//			else sb.append(cpt[i]+"\n");
//		}
//		return sb.toString();
//	}
	public static void main(String[] args){
		int A=0,B=1,C=2;
		int node=C;int[] nodes={C,A,B};int[] range={2,2,2};Long[] cpt=new Long[]{(long)10,(long) 20,(long)20,(long)10,(long)15,(long)5,(long)10,(long)10};		
		Table<Long> table=new Table<Long>(node,nodes,range,100);
		int[] assigns={1,1,2};
		int index=table.assignmentToIndex(assigns);
		System.out.println(index);	
		table.addCPT(index, (long)5,new adder<Long>() {
            public Long add(Long a, Long b) {
                return a+b;
            }
            public Long zero() {
                return (long) 0;
            }
        });
		long k=table.getCPT(index);
		System.out.println(k);
		int[] assignsAll={1,1,2,3,4,5,6,7,244,3};
		int[] sub=table.getSubAssignment(assignsAll);
		//int[] ass=table.indexToAssignment(index);
		//table.printCPT();
		//System.out.println(52760160*73);
		String[] names={"C","D","I","S","G","L","J","H"};
		//table.printCPTInMatlabFormat(names);
	}
}
