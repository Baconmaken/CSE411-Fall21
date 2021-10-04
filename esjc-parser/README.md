# Milestone I: Scanner and Parser for Extended Static Java

**Due: October 20, 2021; 11:59pm KST**


## Problem Description

Your task in this milestone is to extend the Static Java ANTLR grammar to
accept Extended Static Java programs.
More specifically:

1. Modify [src/main/java/esjc/parser/ExtendedStaticJava.g4](src/main/java/esjc/parser/ExtendedStaticJava.g4)
   to accept the Extended Static Java grammar (see below).

2. Using the grammar, regenerate lexer, parser, listener, and visitor in 
   [src/main/java/esjc/parser/](src/main/java/esjc/parser/)

3. To test your solution, run [src/test/java/esjc/test/ExtendedParserTest.java](src/test/java/esjc/test/ExtendedParserTest.java)

Below is the concrete syntax of 
Extended Static Java with some comments that give some instructions on 
what you need to do (``***`` indicates new/modified production rule wrt. 
Static Java, and ``//`` indicates comment).

```
// Requirement 1: allows simple class declarations before and after main 
// class declaration
*** <program> ::= <simple-class-declaration>* 
                  <class-declaration> 
                  <simple-class-declaration>*

// Requirement 2: simple declaration can have zero or more public field 
// declaration (i.e., no methods)
*** <simple-class-declaration> ::= "class" ID "{" <public-field-declaration>* "}"

// Requirement 3: public field declaration has one field modifier: public
// followed by the field's type and name
*** <public-field-declaration> ::= "public" <type> ID ";"

<class-declaration> ::= "public" "class" ID "{"
                        <main-method-declaration> 
                        <field-or-method-declaration>*
                        "}"

<main-method-declaration> ::= "public" "static" "void" "main" "(" 
                              "String" "[" "]" ID ")" "{" <method-body> "}"

<field-or-method-declaration> ::= <field-declaration> | <method-declaration>

<field-declaration> ::= "static" <type> ID ";"

<method-declaration> ::= "static" <return-type> ID "(" <params>? ")"
                         "{" <method-body> "}"

// Requirement 4: SJ type is now renamed as basic-type, and ESJ type can 
// be basic-type, class type, or an array type (of basic-type or class 
// type) 
*** <type> ::= ( <basic-type> | ID ) ( "[" "]" )?

<basic-type> ::= "boolean" | "int"

<return-type> ::= <type> | "void"

<params> ::= <param> ( "," <param> )*

<param> ::= <type> ID

<method-body> ::= <local-declaration>* <statement>*

<local-declaration> ::= <type> ID ";"

// Requirement 5: add do-while-loop, for-loop, and increment/decrement 
// statements
*** <statement> ::= <assign-statement> | <if-statement> | <while-statement>
                  | <invoke-exp-statement> | <return-statement>
                  | <for-statement> | <do-while-statement> 
                  | <inc-dec-statement>

<assign-statement> ::= <assign> ";"

<if-statement> ::= "if" "(" <exp> ")" "{" <statement>* "}"
                   ( "else" "{" <statement>* "}" )?

<while-statement> ::= "while" "(" <exp> ")" "{" <statement>* "}"

<invoke-exp-statement> ::= <invoke-exp> ";"

<return-statement> ::= "return" <exp> ";"

// Requirement 6: 
*** <inc-dec-statement> := <inc-dec> ";"

// Requirement 7: allow general lhs instead of SJ's identifier as an 
// assignment's left hand side
*** <assign> ::= <lhs> "=" <exp>

// Requirement 8: lhs can be variable reference (identifier), a field 
// access, or an array access, respectively
*** <lhs> ::= ID | <exp> "." ID | <exp> "[" <exp> "]"

// Requirement 9: for statement: note that as in Java for-inits, loop 
// condition, and for-updates are optional
*** <for-statement> ::= "for" "(" <for-inits>? ";" <exp>? ";" <for-updates>? ")"
                        "{" <statement>* "}"

// Requirement 10: for inits is a comma separated assignments; note that 
// variable declaration is not allowed here
*** <for-inits> ::= <assign> ( "," <assign> )*

// Requirement 11: for updates is a comma separated increments/decrements
*** <for-updates> ::= <inc-dec> ( "," <inc-dec> )*

// Requirement 12: postfix increment/decrement
*** <inc-dec> ::= <lhs> "++" | <lhs> "--"

// Requirement 13: do while statement
*** <do-while-statement> ::= "do" "{" <statement>* "}" "while" "(" <exp> ")" ";"

// Requirement 14: add new expression, array access expression, field access expression, and
// conditional expression
*** <exp> ::= <literal-exp> | <unary-exp> | <binary-exp> | <paren-exp>
            | <invoke-exp> | <var-ref> | <new-exp> | <array-access-exp>
            | <field-access-exp> | <cond-exp>

// Requirement 15: add null literal
*** <literal-exp> ::= <boolean-literal> | NUM | "null"

<boolean-literal> ::= "true" | "false"

*** <unary-exp> ::= <unary-op> <exp>

// Requirement 16: add bit-complement operator ("~")
*** <unary-op> ::= "+" | "-" | "!" | "~"

*** <binary-exp> ::= <exp> <binary-op> <exp>

// Requirement 17: add shift operators ("<<" | ">>" | ">>>"), note that you need to enforce
// correct operator precedence (similar to Java's)
*** <binary-op> ::= "+" | "-" | "*" | "/" | "%" | ">" | ">=" | "==" | "<"
                  | "<=" | "!=" | "&&" | "||" | "<<" | ">>" | ">>>"

<paren-exp> ::= "(" <exp> ")"

<invoke-exp> ::= ( ID "." )? ID "(" <args>? ")"

<args> ::= <exp> ( "," <exp> )*

<var-ref> ::= ID

// Requirement 18: add conditional operator
*** <cond-exp> ::= <exp> "?" <exp> ":" <exp>

// Requirement 19: add simple class instance creation, new array creation with a specified 
// length, and new array creation with given array elements, respectively
*** <new-exp> ::= "new" ID "(" ")"
                | "new" <type> "[" <exp> "]" 
                | "new" <type> "[" "]" <array-init>

// Requirement 20: array init is a comma separated expressions in curly braces
*** <array-init> ::= "{" <exp> ( "," <exp> )* "}"

// Requirement 21: field access
*** <field-access-exp> ::= <exp> "." ID

// Requirement 22: array access
*** <array-access-exp> ::= <exp> "[" <exp> "]"

ID = ( 'a'..'z' | 'A'..'Z' | '_' | '$' )
     ( 'a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '$' )*

NUM = '0' | ('1'..'9') ('0'..'9')*
```

## Note

* Operator precedence is similar to [Java](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html)

