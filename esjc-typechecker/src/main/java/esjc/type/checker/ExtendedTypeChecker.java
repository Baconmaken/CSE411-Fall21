package esjc.type.checker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

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
