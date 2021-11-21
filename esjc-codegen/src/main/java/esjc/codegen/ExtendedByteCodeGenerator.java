package esjc.codegen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import sjc.annotation.NonNull;
import sjc.annotation.NonNullElements;
import sjc.annotation.ReadOnlyElements;
import sjc.codegen.ByteCodeGenerator;
import esjc.symboltable.ExtendedSymbolTable;
import sjc.type.ArrayType;
import sjc.type.BooleanType;
import sjc.type.ClassType;
import sjc.type.IntType;
import sjc.type.PrimitiveType;
import sjc.type.Type;
import esjc.type.checker.ExtendedTypeTable;
import sjc.util.Pair;

/**
 * This class is used to translate an ExtendedStaticJava {@link CompilationUnit}
 * to {@link ExtendedClassByteCodes} that represent a Java 1.5 class files.
 * 
 * @author <a href="mailto:robby@cis.ksu.edu">Robby</a>
 */
public class ExtendedByteCodeGenerator extends ByteCodeGenerator {
  /**
   * The visitor for {@link ASTNode} to generate bytecodes.
   * 
   * @author <a href="mailto:robby@cis.ksu.edu">Robby</a>
   */
  protected static class Visitor extends ByteCodeGenerator.Visitor {
    public @NonNullElements
    Map<String, byte[]> otherClasses = new HashMap<String, byte[]>();

    protected @NonNullElements
    @ReadOnlyElements
    Map<String, TypeDeclaration> classMap;

    protected @NonNullElements
    @ReadOnlyElements
    Map<Pair<String, String>, FieldDeclaration> fieldMap;

    protected Visitor(@NonNull final ExtendedSymbolTable st,
        @NonNull final ExtendedTypeTable tt) {
      super(st, tt);
      this.classMap = st.classMap;
      this.fieldMap = st.fieldMap;
    }

    @Override
    protected void dispose() {
      super.dispose();
      this.classMap = null;
      this.fieldMap = null;
    }

    /**
     * Determines whether a {@link List} of {@link Modifier}s has a public
     * modifier (a {@link Modifier}'s whose {@link ModifierKeyword} is
     * {@link ModifierKeyword.PUBLIC_KEYWORD}).
     * 
     * @param modifiers
     * @return True, if the {@link List} contains a public modifier.
     */
    @SuppressWarnings("JavadocReference")
    protected boolean hasPublicModifier(
        @SuppressWarnings("rawtypes") final List modifiers) {
      for (final Object o : modifiers) {
        if ((o instanceof Modifier)
            && (((Modifier) o).getKeyword() == ModifierKeyword.PUBLIC_KEYWORD)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Generates a {@link ExtendedClassByteCodes} that represents the class files
   * for the given ExtendedStaticJava {@link CompilationUnit} with the given
   * {@link ExtendedSymbolTable} and {@link ExtendedTypeTable}.
   * 
   * @param cu
   *          The StaticJava {@link CompilationUnit}.
   * @param est
   *          The {@link ExtendedSymbolTable} of the {@link CompilationUnit}.
   * @param ett
   *          The {@link ExtendedTypeTable} of the {@link CompilationUnit}.
   * @return The {@link ExtendedClassByteCodes} that represents the class files
   *         for the given ExtendedStaticJava {@link CompilationUnit} with the
   *         given {@link ExtendedSymbolTable} and {@link ExtendedTypeTable}.
   * @throws ByteCodeGenerator.Error
   *           If the generator encounter unexpected error.
   */
  public static @NonNull
  ExtendedClassByteCodes generate(@NonNull final CompilationUnit cu,
      @NonNull final ExtendedSymbolTable est,
      @NonNull final ExtendedTypeTable ett) throws ByteCodeGenerator.Error {
    assert (cu != null) && (est != null) && (ett != null);

    final Visitor v = new Visitor(est, ett);
    cu.accept(v);
    final ExtendedClassByteCodes result = new ExtendedClassByteCodes(
        v.mainClassName, v.mainClassBytes, v.otherClasses);
    v.dispose();
    return result;
  }

  /**
   * Declared as protected to disallow creation of this object outside from the
   * methods of this class.
   */
  protected ExtendedByteCodeGenerator() {
  }
}
