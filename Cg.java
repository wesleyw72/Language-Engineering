// COMS22201: Code generation

import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Cg
{
  public static int regCount = 2;
  public static int tempReg = 1;
  public static Map<String,String> tempMap = new HashMap<String,String>();
  public static void program(IRTree irt, PrintStream o)
  {
    HashMap<String, Integer> vars = new HashMap<String, Integer>();
    emit(o, "XOR R0,R0,R0");   // Initialize R0 to 0
    statement(irt, o);
    emit(o, "HALT");           // Program must end with HALT
    Memory.dumpData(o);        // Dump DATA lines: initial memory contents
  }

  private static void statement(IRTree irt, PrintStream o)
  {
    if (irt.getOp().equals("SEQ")) {
      statement(irt.getSub(0), o);
      statement(irt.getSub(1), o);
    }
    else if(irt.getOp().equals("SKIP"))
    {

    }
    else if(irt.getOp().equals("LABEL")){
      String a = irt.getSub(0).getOp();
      emit(o,a+":");
    }
    else if(irt.getOp().equals("JUMP"))
    {
      String label = irt.getSub(0).getOp();
      emit(o, "JMP "+label);
    }
    else if(irt.getOp().equals("CJUMP"))
    {
      CJump(irt,o);
    }
    else if(irt.getOp().equals("NOP"))
    {

    }
    else if (irt.getOp().equals("WRS") && irt.getSub(0).getOp().equals("MEM") && irt.getSub(0).getSub(0).getOp().equals("CONST")) {
      String a = irt.getSub(0).getSub(0).getSub(0).getOp();
      emit(o, "WRS "+a);
    }
    else if (irt.getOp().equals("WR")) {
      String e = expression(irt.getSub(0), o);
      emit(o, "WR "+e);
    }
    else if(irt.getOp().equals("READ") && irt.getSub(0).getOp().equals("TEMP"))
    {
      String temp1  = getTemp(irt.getSub(0));
      emit(o, "RD " +temp1);
    }
    else if (irt.getOp().equals("READ")) {
      String result = "";
      result = "R"+regCount;
      regCount++;
      emit(o, "RD "+result);

      String e = expression(irt.getSub(0).getSub(0), o);
      emit(o, "STORE "+result+","+e+",0");
    }
    // else if(irt.getOp().equals("MEM"))
    // {

    // }

    else if(irt.getOp().equals("MOVE") && irt.getSub(1).getOp().equals("CONST") && irt.getSub(0).getOp().equals("MEM"))
    {
      String e = expression(irt.getSub(1), o);
      String l = expression(irt.getSub(0).getSub(0),o);
      emit(o, "STORE "+e+","+l+",0");
    }
    else if(irt.getOp().equals("MOVE") && irt.getSub(1).getOp().equals("CONST") && irt.getSub(0).getOp().equals("TEMP"))
    {
      //Putting a constant into a temp
      String l = getTemp(irt.getSub(0));
      String number = irt.getSub(1).getSub(0).getOp();
      emit(o,"ADDI "+l+",R0,"+number);
    }
    else if(irt.getOp().equals("MOVE") && irt.getSub(1).getOp().equals("PLUS") && irt.getSub(0).getOp().equals("MEM") && irt.getSub(1).getSub(0).getOp().equals("MEM") && irt.getSub(1).getSub(1).getOp().equals("CONST"))
    {
      
      String s = expression(irt.getSub(1).getSub(0),o);
      String t = expression(irt.getSub(0).getSub(0),o);
      String result = "";
      result = "R"+regCount;
      regCount++;
      emit(o, "ADDI "+result+","+s+","+irt.getSub(1).getSub(1).getSub(0).getOp());
      
      
      emit(o, "STORE "+result+","+t+",0");

    }
    else if(irt.getOp().equals("MOVE") && irt.getSub(1).getOp().equals("MINUS") && irt.getSub(0).getOp().equals("MEM") && irt.getSub(1).getSub(0).getOp().equals("MEM") && irt.getSub(1).getSub(1).getOp().equals("CONST"))
    {
      
      String s = expression(irt.getSub(1).getSub(0),o);
      String t = expression(irt.getSub(0).getSub(0),o);
      String result = "";
      result = "R"+regCount;
      regCount++;
      emit(o, "SUBI "+result+","+s+","+irt.getSub(1).getSub(1).getSub(0).getOp());
      
      
      emit(o, "STORE "+result+","+t+",0");

    }

    else if(irt.getOp().equals("MOVE") && irt.getSub(1).getOp().equals("MEM") && irt.getSub(0).getOp().equals("MEM") && irt.getSub(0).getSub(0).getOp().equals("CONST") && irt.getSub(1).getSub(0).getOp().equals("CONST"))
    {
      
      String s = expression(irt.getSub(0).getSub(0),o);
      String t = expression(irt.getSub(1).getSub(0),o);
      String result = "";
      result = "R"+regCount;
      regCount++;
      emit(o, "LOAD "+result+","+t+",0");
      emit(o, "STORE "+result+","+s+",0");

    }

    else if(irt.getOp().equals("MOVE") && irt.getSub(0).getOp().equals("TEMP") && irt.getSub(1).getOp().equals("PLUS")&& irt.getSub(1).getSub(0).getOp().equals("TEMP")&& irt.getSub(1).getSub(1).getOp().equals("CONST") && irt.getSub(0).getSub(0).getOp().equals(irt.getSub(1).getSub(0).getSub(0).getOp()))
    {
      if(irt.getSub(0).getSub(0).getOp().equals(irt.getSub(1).getSub(0).getSub(0).getOp()))
      {
        String s = getTemp(irt.getSub(0));
        String t = irt.getSub(1).getSub(1).getSub(0).getOp();
        emit(o, "ADDI "+ s + "," + s + "," + t);
      }
    }
    else if(irt.getOp().equals("MOVE") && irt.getSub(0).getOp().equals("TEMP") && irt.getSub(1).getOp().equals("MINUS")&& irt.getSub(1).getSub(0).getOp().equals("TEMP")&& irt.getSub(1).getSub(1).getOp().equals("CONST") && irt.getSub(0).getSub(0).getOp().equals(irt.getSub(1).getSub(0).getSub(0).getOp()))
    {
      if(irt.getSub(0).getSub(0).getOp().equals(irt.getSub(1).getSub(0).getSub(0).getOp()))
      {
        String s = getTemp(irt.getSub(0));
        String t = irt.getSub(1).getSub(1).getSub(0).getOp();
        emit(o, "SUBI "+ s + "," + s + "," + t);
      }
    }
    else if(irt.getOp().equals("MOVE") && irt.getSub(0).getOp().equals("TEMP") && irt.getSub(1).getOp().equals("MULT")&& irt.getSub(1).getSub(0).getOp().equals("TEMP")&& irt.getSub(1).getSub(1).getOp().equals("TEMP") && irt.getSub(0).getSub(0).getOp().equals(irt.getSub(1).getSub(0).getSub(0).getOp()))
    {
      if(irt.getSub(0).getSub(0).getOp().equals(irt.getSub(1).getSub(0).getSub(0).getOp()))
      {
        String s = getTemp(irt.getSub(0));
        String t = getTemp(irt.getSub(1).getSub(1));
        emit(o, "MUL "+ s + "," + s + "," + t);
      }
    }
    else if(irt.getOp().equals("MOVE") && irt.getSub(1).getOp().equals("TEMP") && irt.getSub(0).getOp().equals("TEMP"))
    {
      String temp1 = getTemp(irt.getSub(0));
      String temp2 = getTemp(irt.getSub(1));
      emit(o, "ADDI "+temp1+","+temp2+",0");
    }
    else if(irt.getOp().equals("MOVE") && irt.getSub(0).getOp().equals("TEMP"))
    {
      String temp1 =  getTemp(irt.getSub(0));
      String e  = expression(irt.getSub(1),o);
      emit(o, "ADDI "+temp1+","+e+",0");
    }
    else if(irt.getOp().equals("MOVE"))
    {
      String e = expression(irt.getSub(1), o);
      String l = expression(irt.getSub(0).getSub(0),o);
      emit(o, "STORE "+e+","+l+",0");
    }
    else {
      //System.out.println("SAdsa");
      error(irt.getOp());
    }

  }
  private static String getTemp(IRTree irt)
  {
    String loc = "";
    if(irt.getOp()=="TEMP")
    {
      loc = irt.getSub(0).getOp();
      if(!tempMap.containsKey(loc))
      {
        String result = "";
        result = "R"+regCount;
        regCount++;
        tempMap.put(loc,result);
     }
    }
    else{
      error(irt.getOp());
    }
    //Gets the register given to a temp, if not allocated one, it does so
    
    return tempMap.get(loc);
  }
  private static void CJump(IRTree irt, PrintStream o)
  {
    String e1 = expression(irt.getSub(1),o);
    String e2 = expression(irt.getSub(2),o);
    if(irt.getSub(0).getOp() == "LTE")
    {
      String result = "R"+regCount;
      regCount++;
      String temp = "R"+regCount;
      regCount++;
      //Add 1 to compensate for the fact it is a LT operation instead of LTE
     // emit(o, "ADDI "+temp+","+e2+",1");
      emit(o,"SUB "+result+","+e2+","+e1);
      emit(o,"BGEZ "+result+","+irt.getSub(3).getSub(0).getOp());
      emit(o,"JMP " + irt.getSub(4).getSub(0).getOp());

    }
    if(irt.getSub(0).getOp() == "EQUAL")
    {
      String result = "R"+regCount;
      regCount++;
      emit(o,"SUB "+result+","+e1+","+e2);
      emit(o,"BEQZ "+result+","+irt.getSub(3).getSub(0).getOp());
      emit(o,"JMP " + irt.getSub(4).getSub(0).getOp());

    }
  }
  private static String expression(IRTree irt, PrintStream o)
  {
    String result = "";
    if (irt.getOp().equals("CONST")) {
      String t = irt.getSub(0).getOp();
      result = "R"+regCount;
      regCount++;
      emit(o, "ADDI "+result+",R0,"+t);
    }
    else if(irt.getOp().equals("MEM") && irt.getSub(0).getOp().equals("CONST")){
        result = "R"+regCount;
        regCount++;
        String t = expression(irt.getSub(0),o);
        emit(o, "LOAD "+result+","+t+",0");
    }
    else if(irt.getOp().equals("TEMP")){
        result = getTemp(irt);
    }
    else if(irt.getOp().equals("MINUS")&&irt.getSub(0).getOp().equals("CONST")&&irt.getSub(1).getOp().equals("CONST"))
    {
        int a = Integer.parseInt(irt.getSub(0).getSub(0).getOp());
        int b  = Integer.parseInt(irt.getSub(1).getSub(0).getOp());
        int resultNum = a+b;
        result = "R"+regCount;
        regCount++;
        emit(o,"SUBI "+result+",R0,"+resultNum);

    }
    else if(irt.getOp().equals("MULT")&&irt.getSub(0).getOp().equals("CONST")&&irt.getSub(1).getOp().equals("CONST"))
    {
        int a = Integer.parseInt(irt.getSub(0).getSub(0).getOp());
        int b  = Integer.parseInt(irt.getSub(1).getSub(0).getOp());
        int resultNum = a+b;
        result = "R"+regCount;
        regCount++;
        emit(o,"MULI "+result+",R0,"+resultNum);

    }
    
    else if(irt.getOp().equals("MINUS"))
    {

        //Adding two cosntants
        //One needs to be placed in memory
        //System.out.println("Adsasd");
        String s = expression(irt.getSub(0),o);
        String t = expression(irt.getSub(1),o);
        result = "R"+regCount;
        regCount++;
        emit(o, "SUB "+ result + "," + s + "," + t);
     
    }
   
    else if(irt.getOp().equals("MULT")&&irt.getSub(0).getOp().equals("CONST")&&irt.getSub(1).getOp().equals("MEM"))
    {
      result = "R"+regCount;
        regCount++;
      String s = expression(irt.getSub(1),o);
      emit(o,"MULI "+result+","+s+","+irt.getSub(0).getSub(0).getOp());

    }

    else if(irt.getOp().equals("MULT"))
    {
    
        //Adding two cosntants
        //One needs to be placed in memory
        //System.out.println("Adsasd");
        String s = expression(irt.getSub(0),o);
        String t = expression(irt.getSub(1),o);
        result = "R"+regCount;
        regCount++;
        emit(o, "MUL "+ result + "," + s + "," + t);
      }
      
    else if(irt.getOp().equals("PLUS")&&irt.getSub(0).getOp().equals("CONST")&&irt.getSub(1).getOp().equals("CONST"))
    {
        int a = Integer.parseInt(irt.getSub(0).getSub(0).getOp());
        int b  = Integer.parseInt(irt.getSub(1).getSub(0).getOp());
        int resultNum = a+b;
        result = "R"+regCount;
        regCount++;
        emit(o,"ADDI "+result+",R0,"+resultNum);

    }
    else if(irt.getOp().equals("PLUS"))
    {
     
        //Adding two cosntants
        //One needs to be placed in memory
        //System.out.println("Adsasd");
        String s = expression(irt.getSub(0),o);
        String t = expression(irt.getSub(1),o);
        result = "R"+regCount;
        regCount++;
        emit(o, "ADD "+ result + "," + s + "," + t);
      }
     
    
    else {
      error(irt.getOp());
    }
    return result;
  }

  private static void emit(PrintStream o, String s)
  {
    o.println(s);
  }

  private static void error(String op)
  {
    System.out.println("CG error: "+op);
    System.exit(1);
  }
}
