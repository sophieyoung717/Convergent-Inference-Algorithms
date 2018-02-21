package VE_Sampling;

import java.math.BigInteger;
import java.util.*;

import util.Graph;

public class RequisiteTables {
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

    return ret;
  }
}