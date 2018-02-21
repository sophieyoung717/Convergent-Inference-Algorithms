package ForwardSampling;

import java.util.Hashtable;
import java.util.Iterator;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Stack;

public class ConstructTopologicalOrder {

	public static int[] getTopologicalOrder(ForwardSampling mc){
		Hashtable<Integer,Table<Long>> tables=mc.tables;
		int[] ranges=mc.rangesFS;
		Digraph dg=new Digraph(tables.size());//build digraph
		for(int i=0;i<dg.V();i++){
			Table<Long> table=tables.get(i);
			int[] nodes=table.getNodes();
			int self=table.getNode();
			for(int j=0;j<nodes.length;j++){
				if(nodes[j]!=self){
					dg.addEdge(nodes[j], self);
				}
			}
		}
		//topological order
		Topological top=new Topological(dg);
		int[] order=new int[top.getRank().length];
		int count=0;
		Stack<Integer> tmp= (Stack<Integer>) top.order();
		while(!tmp.isEmpty()){
			order[count++]=tmp.pop();
		}
		return order;
	}
}
