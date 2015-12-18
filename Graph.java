import java.io.*;
import java.util.*;
public class Graph{
	//public Graph();
	public List<Node> nodes(){
		return nodes;
	}
	public Node newNode()
	{
		Node nNode = new Node(this);
		nodes.add(nNode);
		return nNode;
	}
	public void addEdge(Node from, Node to){
		from.addSucc(to);
		to.addPred(from);
	}
	//public void rmEdge(Node from, Node to);
	List<Node> nodes = new LinkedList<Node>();

	Graph(){

	}
	

}