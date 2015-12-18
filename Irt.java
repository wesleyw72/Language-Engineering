// COMS22201: IR tree construction

import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Irt
{
// The code below is generated automatically from the ".tokens" file of the 
// ANTLR syntax analysis, using the TokenConv program.
//
// CAMLE TOKENS BEGIN
  public static final String[] tokenNames = new String[] {
"NONE", "NONE", "NONE", "NONE", "ANDSIGN", "ASSIGN", "CLOSEPAREN", "COMMENT", "DO", "ELSE", "EQUALSIGN", "FALSE", "ID", "IF", "INTNUM", "LTESIGN", "MINUS", "MULT", "NOTSIGN", "OPENPAREN", "PLUS", "READ", "SEMICOLON", "SKIP", "STRING", "THEN", "TRUE", "WHILE", "WRITE", "WRITELN", "WS"};
  public static final int ANDSIGN=4;
  public static final int ASSIGN=5;
  public static final int CLOSEPAREN=6;
  public static final int COMMENT=7;
  public static final int DO=8;
  public static final int ELSE=9;
  public static final int EQUALSIGN=10;
  public static final int FALSE=11;
  public static final int ID=12;
  public static final int IF=13;
  public static final int INTNUM=14;
  public static final int LTESIGN=15;
  public static final int MINUS=16;
  public static final int MULT=17;
  public static final int NOTSIGN=18;
  public static final int OPENPAREN=19;
  public static final int PLUS=20;
  public static final int READ=21;
  public static final int SEMICOLON=22;
  public static final int SKIP=23;
  public static final int STRING=24;
  public static final int THEN=25;
  public static final int TRUE=26;
  public static final int WHILE=27;
  public static final int WRITE=28;
  public static final int WRITELN=29;
  public static final int WS=30;
// CAMLE TOKENS END
  public static int labelID = 0;
  public static HashMap<String,Integer> varMem =  new HashMap<String, Integer>();
  public static int trueLoc;
  public static int falseLoc;
  public static int trueFalseUsed = 0;
  public static IRTree convert(CommonTree ast)
  {
    IRTree irt = new IRTree();
    program(ast, irt);
    return irt;
  }
  public static void initTrueFalse(){
    
    String t = "true";
    String f = "false";
    trueLoc = Memory.allocateString(t);
    falseLoc = Memory.allocateString(f);
    trueFalseUsed++;
  }
  public static void program(CommonTree ast, IRTree irt)
  {
    statements(ast, irt);
  }

  public static void statements(CommonTree ast, IRTree irt)
  {
    int i;
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == SEMICOLON) {
      IRTree irt1 = new IRTree();
      IRTree irt2 = new IRTree();
      CommonTree ast1 = (CommonTree)ast.getChild(0);
      CommonTree ast2 = (CommonTree)ast.getChild(1);
      statements(ast1, irt1);
      statements(ast2, irt2);
      irt.setOp("SEQ");
      irt.addSub(irt1);
      irt.addSub(irt2);
    }
    else {
      statement(ast, irt);
    }
  }

  public static void statement(CommonTree ast, IRTree irt)
  {
    CommonTree ast1, ast2, ast3;
    IRTree irt1 = new IRTree(), irt2 = new IRTree(), irt3 = new IRTree();
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == WRITE) {
      ast1 = (CommonTree)ast.getChild(0);
      String type = arg(ast1, irt1);
      if (type.equals("int")) {
        irt.setOp("WR");
        irt.addSub(irt1);
      }
      else if(type.equals("bool"))
      {
        irt.setOp("SEQ");
        irt.addSub(irt1);
        irt.addSub(new IRTree("NOP"));
      }
      else {
        irt.setOp("WRS");
        irt.addSub(irt1);
      }
    }
    else if (tt == WRITELN) {
      String a = String.valueOf(Memory.allocateString("\n"));
      irt.setOp("WRS");
      irt.addSub(new IRTree("MEM", new IRTree("CONST", new IRTree(a))));
    }
    else if (tt == SKIP)
    {
      irt.setOp("SKIP");
    }
    else if (tt == ASSIGN)
    {
      
      ast1 = (CommonTree)ast.getChild(0);
      ast2 = (CommonTree)ast.getChild(1);
      irt.setOp("MOVE");
      var(ast1,irt1);
      expression(ast2,irt2);
      irt.addSub(irt1);
      irt.addSub(irt2);

    }
    else if (tt == READ)
    {
      
      ast1 = (CommonTree)ast.getChild(0);
      irt.setOp("READ");
      var(ast1,irt1);
      irt.addSub(irt1);

    }
    else if(tt == WHILE)
    {
      IRTree irt4 = new IRTree();
      ast1 = (CommonTree)ast.getChild(0);
      ast2 = (CommonTree)ast.getChild(1);
      int label0,label1;
      label0 = labelID;
      labelID++;
      label1=labelID;
      labelID++;
      irt.setOp("SEQ");
      irt.addSub(new IRTree("LABEL",new IRTree("L"+label1)));
      irt.addSub(irt2);
      irt2.setOp("SEQ");
      irt2.addSub(irt3);
      irt2.addSub(irt4);
      int end = whileTree(ast2,irt4,label0,label1);
      condOpBuild(ast1,irt3,label0,end);
          }
    else if(tt == IF)
    {
      ast1 = (CommonTree)ast.getChild(0);
      ast2 = (CommonTree)ast.getChild(1);
      ast3 = (CommonTree)ast.getChild(2);
      irt.setOp("SEQ");
      irt.addSub(irt1);
      irt.addSub(irt2);
      int label0,label1;
      label0 = labelID;
      labelID++;
      label1=labelID;
      labelID++;
      int end = condTree(ast2,ast3,irt2,label0,label1);
    
      condOpBuild(ast1,irt1,label0,label1);
    }

    else {
      
      error(tt);

    }
  }
  public static void condOpBuild(CommonTree ast, IRTree irt, int name0,int name1)
  {
    CommonTree ast1, ast2, ast3;
    IRTree irt1 = new IRTree(), irt2 = new IRTree();
    ast1 = (CommonTree)ast.getChild(0);
    ast2 = (CommonTree)ast.getChild(1);
    Token t = ast.getToken();
    int tt = t.getType();
    if(tt==LTESIGN)
    {
      irt.setOp("CJUMP");
      irt.addSub(new IRTree("LTE"));
      expression(ast1,irt1);
      expression(ast2,irt2);
      irt.addSub(irt1);
      irt.addSub(irt2);
      irt.addSub(new IRTree("NAME",new IRTree("L"+name0)));
      irt.addSub(new IRTree("NAME",new IRTree("L"+name1)));
    }
    else if(tt==EQUALSIGN)
    {
      irt.setOp("CJUMP");
      irt.addSub(new IRTree("EQUAL"));
      expression(ast1,irt1);
      expression(ast2,irt2);
      irt.addSub(irt1);
      irt.addSub(irt2);
      irt.addSub(new IRTree("NAME",new IRTree("L"+name0)));
      irt.addSub(new IRTree("NAME",new IRTree("L"+name1)));
    }
    else if(tt==ANDSIGN)
    {
      IRTree irt3 = new IRTree();
      irt.setOp("SEQ");
      int linkLabel = labelID;
      labelID++;
      condOpBuild((CommonTree)ast.getChild(0),irt1,linkLabel,name1);
      irt.addSub(irt1);
      irt.addSub(irt2);
      irt2.setOp("SEQ");
      irt2.addSub(new IRTree("LABEL",new IRTree("L"+linkLabel)));
      irt2.addSub(irt3);
      condOpBuild((CommonTree)ast.getChild(1),irt3,name0,name1);
    }
    else if(tt==NOTSIGN)
    {
      //SWitch the labels
      
      condOpBuild(ast1,irt,name1,name0);
    }

  }
  public static void boolOpBuild(CommonTree ast, IRTree irt,int name0,int name1)
  {
    CommonTree ast1, ast2, ast3;
    IRTree irt1 = new IRTree(), irt2 = new IRTree();
    
    Token t = ast.getToken();
    int tt = t.getType();
    if(tt==LTESIGN)
    {
      irt.setOp("CJUMP");
      irt.addSub(new IRTree("LTE"));
      ast1 = (CommonTree)ast.getChild(0);
      ast2 = (CommonTree)ast.getChild(1);
      expression(ast1,irt1);
      expression(ast2,irt2);
      irt.addSub(irt1);
      irt.addSub(irt2);
      irt.addSub(new IRTree("NAME",new IRTree("L"+name0)));
      irt.addSub(new IRTree("NAME",new IRTree("L"+name1)));
    }
    else if(tt==EQUALSIGN)
    {
      irt.setOp("CJUMP");
      irt.addSub(new IRTree("EQUAL"));
      ast1 = (CommonTree)ast.getChild(0);
      ast2 = (CommonTree)ast.getChild(1);
      expression(ast1,irt1);
      expression(ast2,irt2);
      irt.addSub(irt1);
      irt.addSub(irt2);
      irt.addSub(new IRTree("NAME",new IRTree("L"+name0)));
      irt.addSub(new IRTree("NAME",new IRTree("L"+name1)));
    }
    else if(tt==ANDSIGN)
    {
     
      IRTree irt3 = new IRTree();
      irt.setOp("SEQ");
      int linkLabel = labelID;
      labelID++;
      boolOpBuild((CommonTree)ast.getChild(0),irt1,linkLabel,name1);
      irt.addSub(irt1);
      irt.addSub(irt2);
      irt2.setOp("SEQ");
      irt2.addSub(new IRTree("LABEL",new IRTree("L"+linkLabel)));
      irt2.addSub(irt3);
      boolOpBuild((CommonTree)ast.getChild(1),irt3,name0,name1);
    }
    else if(tt==NOTSIGN)
    {
      //SWitch the labels
      ast1 = (CommonTree)ast.getChild(0);
      boolOpBuild(ast1,irt,name1,name0);
    }
    else if(tt==TRUE)
    {
      irt.setOp("JUMP");
      irt.addSub(new IRTree("L" + name0));

    }
    else if(tt==FALSE)
    {
      irt.setOp("JUMP");
      irt.addSub(new IRTree("L" + name1));
    }
  }
  public static void boolTree(IRTree irt,int label0,int label1)
  {
    IRTree irt1 = new IRTree(), irt2 = new IRTree(),irt3 = new IRTree(),irt4 = new IRTree(),irt5 = new IRTree(),irt6 = new IRTree(),irt7 = new IRTree(),irt8 = new IRTree();
    IRTree irt9 = new IRTree(),irt10 = new IRTree(),irt11 = new IRTree(),irt12 = new IRTree();
    if(trueFalseUsed==0)
    {
      //Place true false strings in memory
      initTrueFalse();
    }
    int endLabel = labelID;
    labelID++;
    IRTree irt1child = new IRTree(),irt6child = new IRTree(),irt8child = new IRTree();
    irt.setOp("SEQ");
    irt1.setOp("LABEL");
    irt1child.setOp("L" + label0);
    irt1.addSub(irt1child);
    irt.addSub(irt1);
    irt.addSub(irt3);
    irt3.setOp("SEQ");
    createStringTree(irt4,trueLoc);   
    irt3.addSub(irt4);
    irt3.addSub(irt5);
    irt5.setOp("SEQ");
    irt6.setOp("JUMP");
    irt6child.setOp("L" + endLabel);
    irt6.addSub(irt6child);
    irt5.addSub(irt6);
    irt5.addSub(irt7);
    irt7.setOp("SEQ");
    irt7.addSub(irt8);
    irt7.addSub(irt9);
    irt8.setOp("LABEL");
    irt8child.setOp("L"+label1);
    irt8.addSub(irt8child);
    irt9.setOp("SEQ");
    createStringTree(irt10,falseLoc);
    irt9.addSub(irt10);
    irt11.setOp("LABEL");
    irt12.setOp("L" + endLabel);
    irt11.addSub(irt12);
    irt9.addSub(irt11);
    
    
    
  }
  public static void createStringTree(IRTree irt,int stringLoc)
  {
    //Used for writing a string where we already know the location of it in memory(without parsing tree)
    //used for booleans
    IRTree irt1 = new IRTree();
    irt.setOp("WRS");
    irt.addSub(irt1);
    String st = String.valueOf(stringLoc);

    irt1.setOp("MEM");
    irt1.addSub(new IRTree("CONST", new IRTree(st)));
  }
  public static int condTree(CommonTree mainIf,CommonTree mainElse, IRTree irt, int label0, int label1)
  {
    IRTree irt1 = new IRTree(), irt2 = new IRTree(),irt3 = new IRTree(),irt4 = new IRTree(),irt5 = new IRTree(),irt6 = new IRTree(),irt7 = new IRTree(),irt8 = new IRTree();
    IRTree irt9 = new IRTree(),irt10 = new IRTree(),irt11 = new IRTree(),irt12 = new IRTree();
      
    int endLabel = labelID;
    labelID++;
    IRTree irt1child = new IRTree(),irt6child = new IRTree(),irt8child = new IRTree();
    irt.setOp("SEQ");
    irt1.setOp("LABEL");
    irt1child.setOp("L" + label0);
    irt1.addSub(irt1child);
    irt.addSub(irt1);
    irt.addSub(irt3);
    irt3.setOp("SEQ");
    statements(mainIf,irt4);
    irt3.addSub(irt4);
    
    

      irt3.addSub(irt5);
      irt5.setOp("SEQ");
    irt6.setOp("JUMP");
    irt6child.setOp("L" + endLabel);
    irt6.addSub(irt6child);
    irt5.addSub(irt6);
    irt5.addSub(irt7);
    irt7.setOp("SEQ");
    irt7.addSub(irt8);
    irt7.addSub(irt9);
    irt8.setOp("LABEL");
    irt8child.setOp("L"+label1);
    irt8.addSub(irt8child);
    irt9.setOp("SEQ");
    statements(mainElse,irt10);
    irt9.addSub(irt10);
    irt11.setOp("LABEL");
    irt12.setOp("L" + endLabel);
    irt11.addSub(irt12);
    irt9.addSub(irt11);
    
    
    return endLabel;




  }
  public static int whileTree(CommonTree body, IRTree irt, int label0, int label1)
  {
    IRTree irt1 = new IRTree(), irt2 = new IRTree(),irt3 = new IRTree(),irt4 = new IRTree(),irt5 = new IRTree(),irt6 = new IRTree(),irt7 = new IRTree(),irt8 = new IRTree();
    IRTree irt9 = new IRTree(),irt10 = new IRTree(),irt11 = new IRTree(),irt12 = new IRTree();
      
    int endLabel = labelID;
    labelID++;
    IRTree irt1child = new IRTree(),irt6child = new IRTree(),irt8child = new IRTree();
    irt.setOp("SEQ");
    irt1.setOp("LABEL");
    irt1child.setOp("L" + label0);
    irt1.addSub(irt1child);
    irt.addSub(irt1);
    irt.addSub(irt3);
    irt3.setOp("SEQ");
    statements(body,irt4);
    irt3.addSub(irt4);
    
    

      irt3.addSub(irt5);
      irt5.setOp("SEQ");
    irt6.setOp("JUMP");
    irt6child.setOp("L" + label1);
    irt6.addSub(irt6child);
    irt5.addSub(irt6);
    irt5.addSub(irt7);
  
    irt7.setOp("LABEL");
    irt8child.setOp("L"+endLabel);
    irt7.addSub(irt8child);
    
    
    return endLabel;




  }
  public static void boolExp(CommonTree ast, IRTree irt)
  {
    CommonTree ast1, ast2;
    IRTree irt1 = new IRTree(), irt2 = new IRTree();
    Token t = ast.getToken();
    int tt = t.getType();
    if(tt==EQUALSIGN)
    {
      ast1= (CommonTree)ast.getChild(0);
      ast2=(CommonTree)ast.getChild(1);
      expression(ast1,irt1);
      expression(ast2,irt2);
      irt.setOp("EQUALS");
      irt.addSub(irt1);
      irt.addSub(irt2);
    }
    else if(tt == NOTSIGN)
    {
      irt.setOp("NOT");
      ast1=(CommonTree)ast.getChild(0);
      boolExp(ast1,irt1);
      irt.addSub(irt1); 
    }
  }
  public static void var(CommonTree ast, IRTree irt)
  {
    Token t  = ast.getToken();
    int tt = t.getType();
    if(tt == ID)
    {
      String tx = t.getText();
      irt.setOp("MEM");
      if(varMem.get(tx) == null)
      {
      
        int addr = Memory.allocateVaraible(tx);
        varMem.put(tx,addr);
        //irt.addSub(new IRTree(String.valueOf(addr)));
        
        
      }
      //System.out.println(varMem.get(tx));

        irt.addSub(new IRTree("CONST",new IRTree(String.valueOf(varMem.get(tx))))); 
      
      
    }
  }
  public static void temp(CommonTree ast, IRTree irt)
  {
    Token t = ast.getToken();
    int tt = t.getType();
    if(tt == ID)
    {
      String tx = t.getText();
      irt.setOp("TEMP");
      IRTree irt1 = new IRTree(tx);
      irt.addSub(irt1);
    }
  }
  public static String arg(CommonTree ast, IRTree irt)
  {
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == STRING) {
      String tx = t.getText();
      int a = Memory.allocateString(tx); 
      //Memory.allocateVaraible();
      String st = String.valueOf(a);
      irt.setOp("MEM");
      irt.addSub(new IRTree("CONST", new IRTree(st)));
      return "string";
    }
    else if(tt==TRUE || tt==FALSE || tt==EQUALSIGN || tt==LTESIGN || tt==NOTSIGN || tt==ANDSIGN)
    {
      IRTree irt1 = new IRTree(),irt2 = new IRTree();
      irt.setOp("SEQ");
      irt.addSub(irt1);
      irt.addSub(irt2);
      int label0,label1;
      label0 = labelID;
      labelID++;
      label1=labelID;
      labelID++;
      boolTree(irt2,label0,label1);
      boolOpBuild(ast,irt1,label0,label1);
      return "bool";
    }
    else {
      expression(ast, irt);
      return "int";
    }
  }

  public static void expression(CommonTree ast, IRTree irt)
  {
    CommonTree ast1,ast2;
    IRTree irt1 = new IRTree();
    IRTree irt2 = new IRTree();
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == INTNUM) {
      constant(ast, irt1);
      irt.setOp("CONST");
      irt.addSub(irt1);
    }
    else if (tt == ID) {
      var(ast,irt);

    }
    else if (tt == PLUS)
    {
      irt.setOp("PLUS");
      ast1 = (CommonTree)ast.getChild(0);
      ast2 = (CommonTree)ast.getChild(1);
      expression(ast1,irt1);
      expression(ast2,irt2);
      irt.addSub(irt1);
      irt.addSub(irt2);
    }
    else if (tt == MULT)
    {
      irt.setOp("MULT");
      ast1 = (CommonTree)ast.getChild(0);
      ast2 = (CommonTree)ast.getChild(1);
      expression(ast1,irt1);
      expression(ast2,irt2);
      irt.addSub(irt1);
      irt.addSub(irt2);
    }
     else if (tt == MINUS)
    {
      irt.setOp("MINUS");
      ast1 = (CommonTree)ast.getChild(0);
      ast2 = (CommonTree)ast.getChild(1);
      expression(ast1,irt1);
      expression(ast2,irt2);
      irt.addSub(irt1);
      irt.addSub(irt2);
    }
  }

  public static void constant(CommonTree ast, IRTree irt)
  {
    Token t = ast.getToken();
    int tt = t.getType();
    if (tt == INTNUM) {
      String tx = t.getText();
      irt.setOp(tx);
    }
    else {
      error(tt);
    }
  }

  private static void error(int tt)
  {
    System.out.println("IRT error: "+tokenNames[tt]);
    System.exit(1);
  }
}
