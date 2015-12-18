// COMS22201: Syntax analyser

parser grammar Syn;

options {
  tokenVocab = Lex;
  output = AST;
  backtrack = true;
}
@header {
import java.util.HashMap;
}
@members
{
	HashMap memory = new HashMap();
	private String cleanString(String s){
		String tmp;
		tmp = s.replaceAll("^'", "");
		s = tmp.replaceAll("'$", "");
		tmp = s.replaceAll("''", "'");
		return tmp;
	}
}

program :
    statements
  ;

statements :
    statement ( SEMICOLON^ statement )*
  ;

statement :
    WRITE^ OPENPAREN! (boolexp | exp | INTNUM | string) CLOSEPAREN!
  | WRITELN^
  | ID ASSIGN^ exp
  | SKIP
  | READ^ OPENPAREN! ID CLOSEPAREN!
  | IF^ boolexp THEN! statement ELSE! statement
  | OPENPAREN! statements CLOSEPAREN!
  | WHILE^ boolexp DO! statement
  ;
bool:
    TRUE
  | FALSE
  | exp (EQUALSIGN | LTESIGN)^ exp
  | OPENPAREN! boolexp CLOSEPAREN!
  ;
boolexp:
    booltrm (ANDSIGN^ booltrm)*
    ;
booltrm:
    (NOTSIGN^)? bool
  ;
factor:
    ID
  | INTNUM
  | OPENPAREN! exp CLOSEPAREN!
  ;
term:
    factor (MULT^ factor)*
    ;
exp:
    term ((PLUS | MINUS)^ term)*
    ;
string
    scope { String tmp; }
    :
    s=STRING { $string::tmp = cleanString($s.text); }-> STRING[$string::tmp]
;
