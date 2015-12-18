# Whilst Compiler - CAMLE
As part of a 2nd year coursework at the University of Bristol, the task was to make a compiler for a simple programming language by compiling it to assembly code for an abstract machine. The language is called Whilst is based on the While language from Principles of Program Analysis (Nielson, Nielson and Hankin).

The compiler is capable of the following:
* Language Features
  * Arithmetic expressions(+,-,\*)
  * Variables and assignment
  * read statements
  * if and skip statements
  * while loops
  * Boolean expressions (true,false,!,&)
* Liveness analysis

It is structured into a pipeline of four phases.
1. Lexical analysis
2. Syntax analysis
3. IR tree construction
4. Code generation

## Running the compiler
1. run make
2. run ./antlr3 camle testi.w
This will generate two .ass files, the 'nonOptomised' version doesn't have liveness analysis applied where the other does.
To run the emulator on the generated code run ./assmule testi.ass
