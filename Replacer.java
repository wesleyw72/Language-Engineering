import java.util.*;
import java.io.*;
public class Replacer{
	String inFile;
	String outFile;
	PrintStream o;
	Replacer(String inFile, String outFile)
	{
		try{
		o = new PrintStream(new FileOutputStream(outFile));
	}
	catch(Exception e ){}
		this.inFile=inFile;
		this.outFile=outFile;
	}
	void emit(String s)
	{
		o.println(s);
	}
	void replaceRegisters(Liveness lg)
	{
		BufferedReader br;
		try{


		String sCurrentLine;

		br = new BufferedReader(new FileReader(inFile));
		while((sCurrentLine=br.readLine())!=null)
		{
			
				if(sCurrentLine.startsWith("WR "))
					{
						String[] temp=sCurrentLine.split("WR ");
						emit("WR "+lg.getMap(temp[1]));
					}
					
					else if(sCurrentLine.startsWith("WRS "))
					{
						emit(sCurrentLine);

					}
					else if(sCurrentLine.startsWith("JMP "))
					{
						emit(sCurrentLine);
						
					}
					else if(sCurrentLine.startsWith("STORE ")){
						String[] temp=sCurrentLine.split("STORE ");
						String[] temp2=temp[1].split(",");
						emit("STORE " + lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+temp2[2]);
					}
					else if(sCurrentLine.startsWith("LOAD ")){
						String[] temp=sCurrentLine.split("LOAD ");
						String[] temp2=temp[1].split(",");

						emit("LOAD " + lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+temp2[2]);
					}
					else if(sCurrentLine.startsWith("BGEZ ")){
						String[] temp=sCurrentLine.split("BGEZ ");
						String[] temp2=temp[1].split(",");
						emit("BGEZ "+lg.getMap(temp2[0])+","+temp2[1]);
					}
					else if(sCurrentLine.startsWith("BEQZ ")){
						String[] temp=sCurrentLine.split("BEQZ ");
						String[] temp2=temp[1].split(",");
						emit("BEQZ "+lg.getMap(temp2[0])+","+temp2[1]);
					}
					else if(sCurrentLine.startsWith("XOR ")){
						String[] temp=sCurrentLine.split("XOR ");
						String[] temp2=temp[1].split(",");
						emit("XOR "+lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+lg.getMap(temp2[2]));
					}
					else if(sCurrentLine.startsWith("ADDI ")){
						String[] temp=sCurrentLine.split("ADDI ");
						String[] temp2=temp[1].split(",");
						emit("ADDI "+lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+temp2[2]);
					}
					else if(sCurrentLine.startsWith("MULI ")){
						String[] temp=sCurrentLine.split("MULI ");
						String[] temp2=temp[1].split(",");
						emit("MULI "+lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+temp2[2]);
					}
					else if(sCurrentLine.startsWith("SUBI ")){
						String[] temp=sCurrentLine.split("SUBI ");
						String[] temp2=temp[1].split(",");
						emit("SUBI "+lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+temp2[2]);
					}
					else if(sCurrentLine.startsWith("MUL ")){
						String[] temp=sCurrentLine.split("MUL ");
						String[] temp2=temp[1].split(",");
						emit("MUL "+lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+lg.getMap(temp2[2]));
						
					}
					else if(sCurrentLine.startsWith("RD ")){
						String[] temp=sCurrentLine.split("RD ");
						emit("RD "+lg.getMap(temp[1]));
					}
					else if(sCurrentLine.startsWith("ADD ")){
						String[] temp=sCurrentLine.split("ADD ");
						String[] temp2=temp[1].split(",");
						emit("ADD "+lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+lg.getMap(temp2[2]));
						
					}
					else if(sCurrentLine.startsWith("SUB ")){
						String[] temp=sCurrentLine.split("SUB ");
						String[] temp2=temp[1].split(",");
						emit("SUB "+lg.getMap(temp2[0])+","+lg.getMap(temp2[1])+","+lg.getMap(temp2[2]));
					}
					else{emit(sCurrentLine);}
			
		}
	}
	catch(IOException e){}

	}
}