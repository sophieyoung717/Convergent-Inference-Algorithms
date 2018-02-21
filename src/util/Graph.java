package util;

import java.util.LinkedList;


import edu.princeton.cs.algs4.In;

public class Graph {
	private static final String NEWLINE = System.getProperty("line.separator");
	private int V;
	private int E;
	public LinkedList<Integer>[] adj;
	
	public Graph(int V){
		this.V=V;
		adj=(LinkedList<Integer>[])new LinkedList[V];
		for(int i=0;i<V;i++){
		 adj[i]=new LinkedList<Integer>();
		}
	}
	public Graph(In in){
		this(in.readInt());
		int E=in.readInt();
		for(int i=0;i<E;i++){
			int v=in.readInt();
			int w=in.readInt();
			addEdge(v,w);
		}
	}

	public int V(){
		return V;
	}
	public int E(){
		return E;
	}
	public void addEdge(int v, int w){
		E++;
		adj[v].add(0,w);
		adj[w].add(0,v);
	}
  public void addDirectedEdge(int v, int w){
    E++;
    adj[v].add(0,w);
  }
	public Iterable<Integer> adj(int v){
		return adj[v];
	}
	public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " vertices, " + E + " edges " + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (int w : adj[v]) {
                s.append(w + " ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
	public static void main(String[] args){
		In in=new In(args[0]);
		Graph g=new Graph(in);
		System.out.println(g.toString());
	    
	}
}