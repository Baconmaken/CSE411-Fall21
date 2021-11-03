# Milestone III: Type Checker for Extended Static Java

**Due: November 17, 2021; 11:59pm KST**


## Problem Description

Your task in this milestone is to extend the Static Java (SJ) type 
checker to handle Extended Static Java (ESJ) programs in 
[src/main/java/esjc/type/checker/ExtendedTypeChecker.java](src/main/java/esjc/type/checker/ExtendedTypeChecker.java).

The standard JUnit test suite is provided in 
[src/test/java/esjc/test/ExtendedTypeCheckingTest.java](src/test/java/esjc/test/ExtendedTypeCheckingTest.java). 


Hints
=====
* It is a good idea to write the type rule for each new construct in ESJ
  first before implementing it.

* In some cases, you need to override some methods that have already been
  implemented in the SJ TypeChecker, but you should try to reuse them as
  much as you can.

* To resolve field types, use the fieldTypeMap in the ClassType class
  (see the constructor of the ExtendedTypeChecker.Visitor class).

* Use the ExtendedTypeChecker.Visitor.convertType method to convert JDT
  AST Type to SJ/ESJ Type.
