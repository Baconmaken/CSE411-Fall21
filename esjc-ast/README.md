# Milestone II: Abstract Syntax Tree for Extended Static Java

**Due: November 3, 2021; 11:59pm KST**


## Problem Description

Your task in this milestone is to extend Static Java AST Builder 
using Eclipse JDT to build Extended Static Java program AST.

To complete this milestone, modify 
[src/main/java/esjc/ast/ExtendedStaticJavaASTBuilder.java](src/main/java/esjc/ast/ExtendedStaticJavaASTBuilder.java) 
to build Eclipse JDT AST 
representing ESJ programs and make sure your solution pass all the 
provided JUnit test cases in
[src/test/java/esjc/test/ExtendedASTParserTest.java](src/test/java/esjc/test/ExtendedASTParserTest.java).

You might need to modify your ANTLR v4 grammar 
[../esjc-parser/src/main/java/esjc/parser/ExtendedStaticJava.g4](../esjc-parser/src/main/java/esjc/parser/ExtendedStaticJava.g4)
and regenerate the parser and lexer in order to fix issues and make it easier to access elements of
the grammar production rules.
