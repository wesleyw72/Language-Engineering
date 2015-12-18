import java.io.*;
import java.util.*;
public class Node{
	
	public int outDegree()
	{
		return succ.size();
	}
	//public int inDegree();
	
	//public boolean goesTo(Node n);
	//public boolean comesFrom(Node n);
	//public boolean adj(Node n);
	//public String toString();
	private List<Node> succ;
	private List<Node> pred;
	private Graph g;
	private int degree;
	Node(Graph g){
		this.g = g;
		succ = new ArrayList<Node>();
		pred = new ArrayList<Node>();
		degree = 0;
	}
	public void addSucc(Node n)
	{
		succ.add(n);
	}
	public void addPred(Node n)
	{
		pred.add(n);
	}
	public int degree()
	{
		return degree;
	}
	public List<Node> succ(){
		return succ;
	}
	public List<Node> pred(){
		return pred;
	}


}