package VE_NEW;

import java.math.BigInteger;
import java.util.*;

import util.Graph;

public class CalculateVEOrder {

  public static Set<Integer> filterTables(Hashtable<Integer,Table<BigInteger>> tables, int targetNode)
  {
    Graph g=new Graph(tables.size());
    for(Table<BigInteger> table:tables.values()){
      int self=table.getNode();
      for(int neighbor : table.getNodes()){
        if(neighbor!=self){
          g.addDirectedEdge(self, neighbor);
        }
      }
    }

    Set<Integer> ret = new HashSet<Integer>();
    Queue<Integer> todos = new ArrayDeque<Integer>();
    ret.add(targetNode);
    todos.add(targetNode);

    while(!todos.isEmpty()){
      int curr = todos.remove();
      for(int adj : g.adj[curr]){
        if(!ret.contains(adj)){
          ret.add(adj);
          todos.add(adj);
        }
      }
    }
    System.out.println("Filtered down to "+ret.size()+"/"+tables.size()+" tables");
    return ret;
  }

	public static int[] getVEOrder(VariableElimination ve, int target){
		Hashtable<Integer,Table<BigInteger>>tables = ve.tables;
		Set<Integer> requiredTables = filterTables(tables, target);
		int[] ranges=ve.ranges;
		Graph g=new Graph(tables.size());
		int[] weights= new int[requiredTables.size()];
		int[] order=new int[requiredTables.size()];
		for(int key : requiredTables){
			Table<BigInteger> table=tables.get(key);
			int self=table.getNode();
			for(int i=0;i<table.getNodes().size();i++){
				int neighbor=table.getNodes().get(i);	
				if(neighbor!=self){
					g.addEdge(self, neighbor);
				}
			}
		}
		int i = 0;
		for(int me : requiredTables){
			LinkedList<Integer> adj=g.adj[me];
			int total=1;
			for(int j=0;j<adj.size();j++){
				total*=ranges[adj.get(j)];
			}
			weights[i]=total;
			order[i] = me;
			i++;
		}
		sort(weights,order);
		System.out.println(Arrays.toString(order));
		order=orderremoveTarget(order,target);
		return order;
	}
	public static void sort(int[] weights, int[] order){
		for(int i=0;i<weights.length;i++){
			int v=weights[i];
			int j=i+1;
			int cursor=i;
			for(;j<weights.length;j++){
				if(weights[j]<v){
					v=weights[j];
					cursor=j;
				}
			}
			if(cursor!=i){
				exchange(weights,order,i,cursor);
			}
		}
	}
	public static void exchange(int[] weights, int[] order, int i, int j){
		int tmp=weights[i];
		int t=order[i];
		weights[i]=weights[j];
		order[i]=order[j];
		weights[j]=tmp;
		order[j]=t;
	}
	public static int[] orderremoveTarget(int[] order, int target){
		int[] result=new int[order.length-1];
		int count=0;
		for(int i=0;i<order.length;i++){
			if(order[i]!=target){
				result[count++]=order[i];
			}			
		}
		return result;
	}
	public static void main(String[] arges){
		int[] weights=new int[]{4,5,7,2,9};
		int[] order=new int[]{0,1,2,3,4};
		sort(weights,order);
		System.out.println();
	}
}
