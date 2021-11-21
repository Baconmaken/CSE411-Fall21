# Solo Project Milestone IV: Type Checker for Extended Static Java

**Due: December 6, 2021; 11:59pm KST**


## Problem Description

Your task in this milestone is to extend the Static Java (SJ) bytecode 
generator to handle Extended Static Java (ESJ) programs. 
This milestone requires you to override the 
``ByteCodeGenerator.Visitor``
visit methods in 
[src/main/java/esjc/codegen/ExtendedByteCodeGenerator.java](src/main/java/esjc/codegen/ExtendedByteCodeGenerator.java) 
to implement the new 
translation templates for ESJ. The standard JUnit test case is provided in
[src/test/java/esjc/test/ExtendedByteCodeGeneratorTest.java](src/test/java/esjc/test/ExtendedByteCodeGeneratorTest.java).

## Hints

* It is a good idea to write down the translation templates for ESJ
  statements or expressions before you implement them.

* If you do not know how to exactly compile ESJ a statement/expression, 
  you can use the ASM/Bytecode Outliner Plugin to see how Eclipse compiles
  the statement/expression.

* To help with tracing your code generator, you can use
  ``org.objectweb.asm.util.TraceClassVisitor`` and 
  ``org.objectweb.asm.util.TraceMethodVisitor`` temporarily.
