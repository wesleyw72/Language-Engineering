import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class AssemFlowGraph extends FlowGraph {
	Map<Node,String> instrMap;
	Map<Node,Set<String>> defs;
	Map<Node,Set<String>> uses;
	Map<String,Node> labelMap;
	Map<String,String> labelReplace;
	Node lastNode;
	//Jumps which we need to need add links for
	Map<Node,String> jumpsToProcess;
	public Set<String> def(Node n)
	{
		return defs.get(n);
	}
	public Set<String> use(Node n)
	{
		return uses.get(n);
	}
	public String instr(Node n)
	{
		return instrMap.get(n);
	}
	private void processJumps()
	{
		for(Map.Entry<Node,String> entry : jumpsToProcess.entrySet())
		{
			Node key=entry.getKey();
			String value = entry.getValue();
			while(labelReplace.containsKey(value))
			{
				//Deal with two labels after each other
				value=labelReplace.get(value);
			}
			Node to = labelMap.get(value);
			if(to==null)
			{
				System.out.println(value);
			}
			addEdge(key,to);

		}
		
	}
	public AssemFlowGraph(String filename)
	{	
		instrMap = new HashMap<Node,String>();
		labelReplace = new HashMap<String,String>();
		labelMap = new HashMap<String,Node>();
		Boolean addLast = true;
		defs = new HashMap<Node,Set<String>>();
		uses = new HashMap<Node,Set<String>>();
		jumpsToProcess = new HashMap<Node,String>();
		Boolean addLabelToNext = false;
		String labelToAdd="";
		BufferedReader br;
		try{
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));

			while((sCurrentLine = br.readLine()) != null)
			{
				if(sCurrentLine.startsWith("L") & !sCurrentLine.startsWith("LOAD"))
				{
					if(addLabelToNext==true)
					{
						//Two labels after each other
						labelReplace.put(labelToAdd,sCurrentLine);
					}
					labelToAdd = sCurrentLine;
					addLabelToNext=true;
						//Add it to the next Instr
						//String[] temp=sCurrentLine.split("WR ");
						//int reg = Integer.parseInt(temp[1]);
						//currUse.add();
						//System.out.println(temp[1]);	
				}
				else
				{

				if(!sCurrentLine.startsWith("DATA")){
					Node nNode = newNode();
					if(addLast && lastNode!=null)
					{
						addEdge(lastNode,nNode);
					}
					addLast = true;
					instrMap.put(nNode,sCurrentLine);
					if(addLabelToNext)
					{
						labelMap.put(labelToAdd,nNode);
						addLabelToNext = false;
					}
					Set<String> currDefs = new HashSet<String>();
					defs.put(nNode,currDefs);
					Set<String> currUse = new HashSet<String>();
					uses.put(nNode,currUse);

					//System.out.println(instr(nNode));
					if(sCurrentLine.startsWith("WR "))
					{
						String[] temp=sCurrentLine.split("WR ");
						//int reg = Integer.parseInt(temp[1]);
						currUse.add(temp[1]);
						//System.out.println(temp[1]);	
					}
					
					else if(sCurrentLine.startsWith("WRS "))
					{
						String[] temp=sCurrentLine.split("WRS ");
						//int reg = Integer.parseInt(temp[1]);
						//System.out.println(temp[1]);
						
					}
					else if(sCurrentLine.startsWith("JMP "))
					{
						String[] temp=sCurrentLine.split("JMP ");
						//int reg = Integer.parseInt(temp[1]);
						jumpsToProcess.put(nNode, temp[1]+":");
						addLast = false;
						
					}
					else if(sCurrentLine.startsWith("STORE ")){
						String[] temp=sCurrentLine.split("STORE ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currUse.add(temp2[0]);
						//System.out.println(temp2[1]);
						currUse.add(temp2[1]);
						//System.out.println(temp2[2]);
					}
					else if(sCurrentLine.startsWith("LOAD ")){
						String[] temp=sCurrentLine.split("LOAD ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currDefs.add(temp2[0]);
						// System.out.println(temp2[1]);
						currUse.add(temp2[0]);
						//System.out.println(temp2[2]);
					}
					else if(sCurrentLine.startsWith("BGEZ ")){
						String[] temp=sCurrentLine.split("BGEZ ");
						String[] temp2=temp[1].split(",");
						// System.out.println(temp2[0]);
						currUse.add(temp2[0]);
						jumpsToProcess.put(nNode, temp2[1]+":");
						//System.out.println(temp2[1]); //Label
						//System.out.println(temp2[2]);
					}
					else if(sCurrentLine.startsWith("BEQZ ")){
						String[] temp=sCurrentLine.split("BEQZ ");
						String[] temp2=temp[1].split(",");
						// System.out.println(temp2[0]);
						currUse.add(temp2[0]);
						jumpsToProcess.put(nNode, temp2[1]+":");
						//System.out.println(temp2[1]); //Label
						//System.out.println(temp2[2]);
					}
					else if(sCurrentLine.startsWith("XOR ")){
						String[] temp=sCurrentLine.split("XOR ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currUse.add(temp2[0]);
						//System.out.println(temp2[1]); //Label
						currUse.add(temp2[1]);
						currUse.add(temp2[2]);
						//System.out.println(temp2[2]);
					}
					else if(sCurrentLine.startsWith("ADDI ")){
						String[] temp=sCurrentLine.split("ADDI ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currDefs.add(temp2[0]);
						currUse.add(temp2[1]);
						//System.out.println(temp2[1]); //Label
						//System.out.println(temp2[2]);
					}
					else if(sCurrentLine.startsWith("MULI ")){
						String[] temp=sCurrentLine.split("MULI ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currDefs.add(temp2[0]);
						currUse.add(temp2[1]);
						//System.out.println(temp2[1]); //Label
						//System.out.println(temp2[2]);
					}
					else if(sCurrentLine.startsWith("SUBI ")){
						String[] temp=sCurrentLine.split("SUBI ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currDefs.add(temp2[0]);
						currUse.add(temp2[1]);
						//System.out.println(temp2[1]); //Label
						//System.out.println(temp2[2]);
					}
					else if(sCurrentLine.startsWith("MUL ")){
						String[] temp=sCurrentLine.split("MUL ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currDefs.add(temp2[0]);
						currUse.add(temp2[1]);
						currUse.add(temp2[2]);
					}
					else if(sCurrentLine.startsWith("RD ")){
						String[] temp=sCurrentLine.split("RD ");
						//String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currDefs.add(temp[1]);
						//currUse.add(temp2[1]);
						//currUse.add(temp2[2]);
					}
					else if(sCurrentLine.startsWith("ADD ")){
						String[] temp=sCurrentLine.split("ADD ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currDefs.add(temp2[0]);
						currUse.add(temp2[1]);
						currUse.add(temp2[2]);
					}
					else if(sCurrentLine.startsWith("SUB ")){
						String[] temp=sCurrentLine.split("SUB ");
						String[] temp2=temp[1].split(",");
						//System.out.println(temp2[0]);
						currDefs.add(temp2[0]);
						currUse.add(temp2[1]);
						currUse.add(temp2[2]);
					}
					else if(sCurrentLine.startsWith("HALT") || sCurrentLine.startsWith("DATA")){}

					else{

						System.out.println("UNRECOGNISED INSTRUCTION");
						System.out.println(sCurrentLine);
						System.exit(1);
					}
					
					lastNode = nNode;
				}}
			}
		} catch(IOException e){

		}
		processJumps();
		//System.out.println("Woo Calced");
		
		// for(Node n1 :nodes)
		// {
		// 	System.out.println(instrMap.get(n1));
		// 	System.out.println("defs:");
		// 	for(String s:defs.get(n1))
		// 	{
		// 		System.out.println(s);
		// 	}
		// 	System.out.println("uses:");
		// 	for(String s:uses.get(n1))
		// 	{
		// 		System.out.println(s);
		// 	}
		// 	System.out.println("jumps:");
		// 	for(Node s:n1.succ())
		// 	{
		// 		System.out.println(instrMap.get(s));
		// 	}
		// 	System.out.println();

		// }
	}
}