import java.util.*;
public class Liveness extends Graph{
	private Map<Node,Set<String>> alive;
	AssemFlowGraph flow;
	Map<Node,Set<String>> outSets;
	Map<Node,Set<String>> inSets;
	Map<String,Node> nodeMap;
	Map<Node,String> reverseMap;
	Set<Node> inGraph;
	Map<Node,Integer> colour;
	public Liveness(AssemFlowGraph flow){
		this.flow=flow;
		inGraph = new HashSet<Node>();
		outSets = new HashMap<Node,Set<String>>();
		inSets = new HashMap<Node,Set<String>>();
		nodeMap = new HashMap<String,Node>();
		reverseMap =new HashMap<Node,String>();
		colour = new HashMap<Node,Integer>();
		int sameCount = 0;
		for(Node n:flow.nodes())
		{
			Set<String> tempsOut = new HashSet<String>();
			Set<String> tempsIn = new HashSet<String>();
			outSets.put(n,tempsOut);
			inSets.put(n,tempsIn);
			//alive.put(n,tempsAlive);
		}
		while(sameCount<flow.nodes().size())
		{
		//	System.out.println("CO");
			sameCount = 0;
			for(Node n:flow.nodes())
			{
			

				Set<String> newOut = new HashSet<String>();
				Set<String> newIn = new HashSet<String>();
				newOut.addAll(outSets.get(n));
				newIn.addAll(inSets.get(n));
				inSets.get(n).addAll(flow.use(n));
				Set<String> temp = new HashSet<String>();
				temp.addAll(outSets.get(n));
				temp.removeAll(flow.def(n));
				inSets.get(n).addAll(temp);
		
				for(Node n2:n.succ())
				{
				
					outSets.get(n).addAll(inSets.get(n2));
				}
				if(newOut.equals(outSets.get(n)) && newIn.equals(inSets.get(n)))
				{
					sameCount++;

				}
				
			}


		}
		// for(Node n:flow.nodes())
		// {
		// 	System.out.println(flow.instr(n));
		// 	for(String s:outSets.get(n))
		// 	{
		// 		System.out.print(s+" ");
		// 	}
		// 	System.out.println();
		// }
		constructInterGraph();
		for(Node n:nodes)
		{
			inGraph.add(n);
		}
		//colourGraph(6);
		int bestColouring= reverseMap.size();

		Boolean colouringFine = true;
		if(bestColouring>1){
		while(colouringFine)
		{

			colourGraph(bestColouring);
			for(Node n:nodes)
			{
				if(colour.get(n)==0)
				{
					//failed
					colouringFine=false;
				}

			}
			if(colouringFine)
			{
				bestColouring--;
			}
		}
		colourGraph(bestColouring+1);
	}
	

	}
	public String getMap(String regIn)
	{
		if(nodeMap.get(regIn)==null)
		{
			//System.out.println(regIn);
			return "R0";
		}
		return "R"+(colour.get(nodeMap.get(regIn)));
	}
	private int getDegree(Node n)
	{
		int count = 0;
		for(Node a:n.succ())
		{
			if(inGraph.contains(a))
			{
				count++;
			}
		}
		return count;
	}
	private void colourGraph(int k)
	{
		if(!inGraph.isEmpty())
		{
			Node v=null;
			Node holder =null;
			Boolean found = false;
			for(Node u:inGraph)
			{
				if(getDegree(u)<k)
				{
					v = u;
					found = true;

				}
				holder = u;
			}
			if(!found)
			{
				//Potential Spill
				v = holder;
			}
			inGraph.remove(v);
			colourGraph(k);
			inGraph.add(v);
			if(getDegree(v)<k)
			{
				Boolean colourFree = true;
				for(int i =1;i<=k;i++)
				{
					colourFree = true;
					for(Node suc:v.succ())
					{
						if(colour.containsKey(suc) && colour.get(suc).equals(i))
						{
							colourFree = false;
						}
					}
					if(colourFree)
					{
						colour.put(v,i);
						break;
					}
				}
			}
			else
			{
				Boolean colourFree = true;
				for(int i =1;i<=k;i++)
				{

					colourFree = true;
					for(Node suc:v.succ())
					{
						if(colour.containsKey(suc) && colour.get(suc).equals(i))
						{
							colourFree = false;
						}
					}
					if(colourFree)
					{
						colour.put(v,i);
						break;
					}
				}
				if(colourFree==false)
				{
					
					colour.put(v,0);
				}
			}

		}
		
	}
	private void constructInterGraph()
	{
		for(Node n: flow.nodes())
		{

			for(String s:outSets.get(n))
			{
				for(String t:outSets.get(n))
				{
					
						if(!nodeMap.containsKey(s))
						{
							Node q= newNode();
							nodeMap.put(s,q);
							reverseMap.put(q,s);

						}
						if(!nodeMap.containsKey(t))
						{
							Node q= newNode();
							nodeMap.put(t,q);
							reverseMap.put(q,t);
						}
						if(!s.equals(t))
					{
						addEdge(nodeMap.get(s),nodeMap.get(t));
						//System.out.println(s + " " + t);
					}

				}
			}
		}
	}

}