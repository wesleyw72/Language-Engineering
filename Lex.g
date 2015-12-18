// COMS22201: Lexical analyser

lexer grammar Lex;

//---------------------------------------------------------------------------
// KEYWORDS
//---------------------------------------------------------------------------
WRITE      : 'write' ;
WRITELN    : 'writeln' ;
READ       : 'read' ;
DO         : 'do' ;
ELSE       : 'else' ;
FALSE      : 'false' ;
IF         : 'if' ;
SKIP       : 'skip' ;
THEN       : 'then' ;
TRUE       : 'true' ;
WHILE      : 'while' ;

//---------------------------------------------------------------------------
// OPERATORS
//---------------------------------------------------------------------------
SEMICOLON    : ';' ;
OPENPAREN    : '(' ;
CLOSEPAREN   : ')' ;
MULT         : '*' ;
PLUS         : '+' ;
MINUS        : '-' ;
NOTSIGN      : '!' ;
ANDSIGN      : '&' ;
EQUALSIGN    : '=' ;
INTNUM       : ('0'..'9')+ ;
ASSIGN       : ':=' ;
LTESIGN      : '<=' ;
ID           
@init { int N = 0; }
  : ('a'..'z'|'A'..'Z') ((('a'..'z'|'A'..'Z') | '0'..'9') { N++; } )* { N <=7 }?
;


STRING       : '\'' ('\'' '\'' | ~'\'')* '\'';

COMMENT      : '{' (~'}')* '}' {skip();} ;

WS           : (' ' | '\t' | '\r' | '\n' )+ {skip();} ;
