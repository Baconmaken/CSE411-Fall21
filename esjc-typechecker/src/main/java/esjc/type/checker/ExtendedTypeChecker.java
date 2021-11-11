package esjc.type.checker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.*;

import sjc.annotation.NonNull;
import sjc.annotation.NonNullElements;
import sjc.annotation.ReadOnlyElements;
import sjc.symboltable.SymbolTable;
import esjc.symboltable.ExtendedSymbolTable;
import sjc.type.ArrayType;
import sjc.type.BaseType;
import sjc.type.ClassType;
import sjc.type.NonPrimitiveType;
import sjc.type.NullType;
import sjc.type.Type;
import sjc.type.TypeFactory;
import sjc.type.checker.TypeChecker;
import sjc.util.Pair;

/**
 * This class is used to type check a StaticJava {@link CompilationUnit} with a
 * given {@link SymbolTable}.
 * 
 * @author <a href="mailto:robby@cis.ksu.edu">Robby</a>
 */
public class ExtendedTypeChecker extends TypeChecker {
  /**
   * The visitor for {@link ASTNode} to type check an ExtendedStaticJava
   * {@link CompilationUnit}.
   * 
   * @author <a href="mailto:robby@cis.ksu.edu">Robby</a>
   */
  protected static class Visitor extends TypeChecker.Visitor {
    protected @NonNullElements
    @ReadOnlyElements
    Map<String, TypeDeclaration> classMap;

    protected @NonNullElements
    @ReadOnlyElements
    Map<Pair<String, String>, FieldDeclaration> fieldMap;

    protected Visitor(final TypeFactory tf, final ExtendedSymbolTable est) {
      super(tf, est);
      this.classMap = est.classMap;
      this.fieldMap = est.fieldMap;
      for (final Pair<String, String> p : est.fieldMap.keySet()) {
        final FieldDeclaration fd = est.fieldMap.get(p);
        final Type t = convertType(fd, fd.getType());
        final String className = p.first;
        final String fieldName = p.second;
        final ClassType ct = tf.getClassType(className);
        ct.fieldTypeMap.put(fieldName, t);
      }
    }

    @Override
    protected Type convertType(final ASTNode node,
                               final org.eclipse.jdt.core.dom.Type t) {
      if (t instanceof SimpleType) {
        final SimpleType st = (SimpleType) t;
        final String name = st.getName().getFullyQualifiedName();
        if (this.classMap.containsKey(name)) {
          return this.tf.getClassType(name);
        }
      }
      return super.convertType(node, t);
    }

    @Override
    protected void dispose() {
      super.dispose();

      this.classMap = null;
      this.fieldMap = null;
    }

    @Override
    protected void typeCheckMethodInvocation(final MethodInvocation node,
                                             final String className, final String methodName, final Type[] argTypes,
                                             final MethodDeclaration md) {
      final int numOfParams = md.parameters().size();
      if (argTypes.length != numOfParams) {
        throw new Error(node, "Wrong number of arguments to invoke method \""
                + methodName + "\" in \"" + node + "\"");
      }
      for (int i = 0; i < numOfParams; i++) {
        final Type t = convertType(node, ((SingleVariableDeclaration) md
                .parameters().get(i)).getType());
        if ((argTypes[i] != this.tf.Null) && (t != argTypes[i])) {
          throw new Error(node, "Type mismatch the " + i + " argument in \""
                  + node + "\"");
        }
      }
      final Type returnType = convertType(node, md.getReturnType2());
      setResult(node, returnType);
    }

    @Override
    public boolean visit(final ArrayAccess node) {
      node.getIndex().accept(this);
      if (getResult() != this.tf.Int) {
        throw new Error(node,
                "Expecting an int type for the index of \'"
                        + node + "\"");
      }
      node.getArray().accept(this);
      final Type t = getResult();
      setResult(node, ((ArrayType) t).baseType);

      return false;
    }

    @Override
    public boolean visit(final ArrayCreation node) {
      for (final Object o : node.dimensions()) {
        ((Expression) o).accept(this);
        if (getResult() != this.tf.Int) {
          throw new Error(node,
                  "Expecting an int type expression for the array length in \'" + node + "\'");
        }
      }

      if (node.getInitializer() != null) {
        int i = 0;
        for (final Object o : node.getInitializer().expressions()) {
          ((Expression) o).accept(this);
          final BaseType bt = (BaseType) convertType(node, node.getType().getElementType());
          final Type t = getResult();
          if ((t != this.tf.Null) && (t != bt)) {
            throw new Error(node,
                    "Type mismatch in array initializer element " + i + " in \'"
                            + node + "\': " + bt + " : " + t);
          }
          i++;
        }
      }
      setResult(node, convertType(node, node.getType()));
      return false;
    }

    @Override
    public boolean visit(final Assignment node) {
      node.getLeftHandSide().accept(this);
      final Type lhsType = getResult();
      node.getRightHandSide().accept(this);
      final Type rhsType = getResult();

      if(node.getRightHandSide() instanceof ConditionalExpression) {
        if ((rhsType != this.tf.Null) && (lhsType != rhsType)) {
          throw new Error(node, "Type mismatch for conditional expression \'"
                  + node.getRightHandSide()
                  + "\": " + rhsType
                  + " : " + lhsType);
        }
      }
      if ((rhsType != this.tf.Null) && (lhsType != rhsType)) {
        throw new Error(node, "Type mismatch in \"" + node + "\": " + lhsType
                + " = " + rhsType);
      }
      // no need to set the type result for assignments since
      // assignments in StaticJava are statements,
      // i.e., they are evaluated for their side-effects.
      return false;
    }

    @Override
    public boolean visit(final ClassInstanceCreation node) {
      final Type t = convertType(node, node.getType());
      setResult(node, t);
      this.symbolMap.put(node, this.classMap.get(t.name));
      return false;
    }

    @Override
    public boolean visit(final ConditionalExpression node) {
      node.getExpression().accept(this);
      final Type leftType = getResult();
      node.getThenExpression().accept(this);
      final Type midType = getResult();
      node.getElseExpression().accept(this);
      final Type rightType = getResult();

      if (leftType != this.tf.Boolean) {
        throw new Error(node,
                "Expecting a boolean type expression as the condition " +
                        "in \'" + node + "\"");
      }
      else if ((midType != this.tf.Null) && (rightType != this.tf.Null) && (midType != rightType)) {
        throw new Error(node,
                "Type mismatch for conditional expression \'" + node
                        + "\": " + midType + " : " + rightType);
      }

      if (midType != this.tf.Null) {
        setResult(node, midType);
      }
      else {
        setResult(node, rightType);
      }

      return false;
    }

    @Override
    public boolean visit(final DoStatement node) {
      node.getExpression().accept(this);
      final Type t = getResult();
      if (t != this.tf.Boolean) {
        throw new Error(node,
                "Expecting a boolean type expression as the condition of a while-statement: \""
                        + node.getExpression() + "\"");
      }

      node.getBody().accept(this);
      return false;
    }

    @Override
    public boolean visit(final ExpressionStatement node) {
      final Expression e = node.getExpression();
      e.accept(this);
      if (e instanceof PostfixExpression) {
        assert getResult() == this.tf.Int;
        return false;
      }

      return super.visit(node);
    }

    @Override
    public boolean visit(final FieldAccess node) {
      node.getExpression().accept(this);
      final Type t = getResult();
      final SimpleName name = node.getName();
      final FieldDeclaration fd = this.fieldMap.get(new Pair<>(t.toString(), name.getIdentifier()));
      setResult(node, convertType(node, fd.getType()));
      this.symbolMap.put(node, fd);
      return false;
    }

    @Override
    public boolean visit(final ForStatement node) {
      if (node.initializers() != null) {
        for (final Object o : node.initializers()) {
          ((Expression) o).accept(this);
        }
      }

      if (node.getExpression() != null) {
        node.getExpression().accept(this);
        if (getResult() != this.tf.Boolean) {
          throw new Error(node,
                  "Expecting a boolean type expression as the condition of a for-statement: \""
                          + node.getExpression().toString() + "\"");
        }
      }

      if (node.updaters() != null) {
        for (final Object o : node.updaters()) {
          ((Expression) o).accept(this);
        }
      }

      node.getBody().accept(this);
      return false;
    }

    @Override
    public boolean visit(final InfixExpression node) {
      node.getLeftOperand().accept(this);
      final Type lhsType = getResult();
      node.getRightOperand().accept(this);
      final Type rhsType = getResult();
      final InfixExpression.Operator op = node.getOperator();

      if ((op == InfixExpression.Operator.LEFT_SHIFT)
              || (op == InfixExpression.Operator.RIGHT_SHIFT_SIGNED)
              || (op == InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED)) {
        if (lhsType != this.tf.Int) {
          throw new Error(node,
                  "Expecting an int type expression as the left-hand operand of \""
                          + op + "\" in \"" + node + "\"");
        }
        if (rhsType != this.tf.Int) {
          throw new Error(node,
                  "Expecting an int type expression as the right-hand operand of \""
                          + op + "\" in \"" + node + "\"");
        }
        setResult(node, this.tf.Int);

        return false;
      }
      else if ((op == InfixExpression.Operator.EQUALS)
              || (op == InfixExpression.Operator.NOT_EQUALS)) {
        if ((lhsType != this.tf.Null) && (rhsType != this.tf.Null) && (lhsType != rhsType)) {
          throw new Error(node, "Type mismatch in \"" + node + "\": " + lhsType
                  + " " + op + " " + rhsType);
        }
        else if (((lhsType == this.tf.Null) && (rhsType == this.tf.Void))
                || ((lhsType == this.tf.Void) && (rhsType == this.tf.Null))) {
          throw new Error(node, "Type mismatch in \"" + node + "\": " + lhsType
                  + " " + op + " " + rhsType);
        }
        setResult(node, this.tf.Boolean);

        return false;
      }

      return super.visit(node);
    }

    @Override
    public boolean visit(final MethodInvocation node) {
      // note that we don't visit the MethodInvocation's simple name
      // because we want visit(SimpleName) to resolve variable references
      // instead of method names
      final String className = node.getExpression() == null ? this.className
              : ((SimpleName) node.getExpression()).getIdentifier();
      final String methodName = node.getName().getIdentifier();
      final int numOfArgs = node.arguments().size();
      final Type[] argTypes = new Type[numOfArgs];
      for (int i = 0; i < numOfArgs; i++) {
        ((Expression) node.arguments().get(i)).accept(this);
        argTypes[i] = getResult();
      }
      final Object o = this.symbolMap.get(node);
      if ((o == null) || (o instanceof Method)) {
        final Method m = o == null ? resolveMethod(
                node,
                className,
                methodName,
                argTypes) : (Method) o;
        typeCheckMethodInvocation(node, className, methodName, argTypes, m);
      } else {
        typeCheckMethodInvocation(
                node,
                className,
                methodName,
                argTypes,
                (MethodDeclaration) o);
      }
      return false;
    }

    @Override
    public boolean visit(final NullLiteral node) {
      setResult(node, this.tf.Null);
      return false;
    }

    @Override
    public boolean visit(final PostfixExpression node) {
      node.getOperand().accept(this);
      final Type t = getResult();
      final PostfixExpression.Operator op = node.getOperator();

      if ((op == PostfixExpression.Operator.DECREMENT)
              || (op == PostfixExpression.Operator.INCREMENT)) {
        if (t != this.tf.Int) {
          throw new Error(node,
                  "Expecting an int type expression as the operand of \'" + op
                          + "\': \"" + node.getOperand() + "\"");
        }
        setResult(node.getOperand(), this.tf.Int);
      } else {
        throw new Error(node, "Unexpected PostfixExpression: \'" + node + "\'");
      }
      return false;
    }

    @Override
    public boolean visit(final PrefixExpression node) {
      node.getOperand().accept(this);
      final Type t = getResult();
      final PrefixExpression.Operator op = node.getOperator();

      if (op == PrefixExpression.Operator.COMPLEMENT) {
        if (t != this.tf.Int) {
          throw new Error(node,
                  "Expecting an int type expression as the operand of \"" + op
                          + "\" in \"" + node + "\"");
        }
        setResult(node, this.tf.Int);
        return false;
      }

      return super.visit(node);
    }

    @Override
    public boolean visit(final ReturnStatement node) {
      final Expression e = node.getExpression();
      if ((this.methodReturnType == this.tf.Void) && (e != null)) {
        throw new Error(node, "Unexpected return's expression in \"" + node
                + "\"");
      } else if ((this.methodReturnType != this.tf.Void) && (e == null)) {
        throw new Error(node, "Expecting a return's expression in \"" + node
                + "\"");
      } else if ((this.methodReturnType != this.tf.Void) && (e != null)) {
        e.accept(this);
        final Type t = getResult();
        if ((t != this.tf.Null) && (t != this.methodReturnType)) {
          throw new Error(node, "Expecting " + this.methodReturnType.name
                  + " return expression in \"" + node + "\"");
        }
      }
      return false;
    }
  }


  /**
   * Type checks an ExtendedStaticJava {@link CompilationUnit} with the given
   * {@link ExtendedSymbolTable} and the given {@link TypeFactory}. It also
   * resolves {@link MethodInvocation} of library call (and put its mapping in
   * the {@link ExtendedSymbolTable}).
   * 
   * @param tf
   *          The {@link TypeFactory}.
   * @param cu
   *          The StaticJava {@link CompilationUnit}.
   * @param symbolTable
   *          The {@link ExtendedSymbolTable} of the {@link CompilationUnit}
   * @return The {@link ExtendedTypeTable}.
   * @throws TypeChecker.Error
   *           If the type checker encounter type error in the
   *           {@link CompilationUnit}.
   */
  public static @NonNull
  ExtendedTypeTable check(@NonNull final TypeFactory tf,
      @NonNull final CompilationUnit cu,
      @NonNull final ExtendedSymbolTable symbolTable) throws TypeChecker.Error {
    assert (tf != null) && (cu != null) && (symbolTable != null);

    final Visitor v = new Visitor(tf, symbolTable);
    cu.accept(v);
    final ExtendedTypeTable result = new ExtendedTypeTable(v.resultTypeMap,
        v.resultMethodTypeMap);
    v.dispose();
    return result;
  }

  /**
   * Declared as protected to disallow creation of this object outside from the
   * methods of this class.
   */
  protected ExtendedTypeChecker() {
  }
}
