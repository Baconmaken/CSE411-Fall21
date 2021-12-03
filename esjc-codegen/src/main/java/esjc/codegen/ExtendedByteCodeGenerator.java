package esjc.codegen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import scala.Int;
import sjc.annotation.NonNull;
import sjc.annotation.NonNullElements;
import sjc.annotation.ReadOnlyElements;
import sjc.codegen.ByteCodeGenerator;
import esjc.symboltable.ExtendedSymbolTable;
import sjc.type.*;
import esjc.type.checker.ExtendedTypeTable;
import sjc.type.ArrayType;
import sjc.type.PrimitiveType;
import sjc.type.Type;
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

    @Override
    public boolean visit(final ArrayAccess node) {
      final Type t = this.typeMap.get(node);

      node.getArray().accept(this);
      node.getIndex().accept(this);

      if (t instanceof IntType) {
        this.mv.visitInsn(Opcodes.IALOAD);
      }
      else if (t instanceof BooleanType) {
        this.mv.visitInsn(Opcodes.BALOAD);
      }
      else {
        this.mv.visitInsn(Opcodes.AALOAD);
      }

      return false;
    }

    @Override
    public boolean visit(final ArrayCreation node) {
      final Type t = ((ArrayType) this.typeMap.get(node)).baseType;
      final String Name = t.name;

      if (node.dimensions().size() != 0) {
        for (final Object o : node.dimensions()) {
          ((ASTNode) o).accept(this);
        }
      }
      else if (node.getInitializer() != null) {
        generateIntConst(node.getInitializer().expressions().size());
      }

      if (t instanceof IntType) {
        this.mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
      }
      else if (t instanceof BooleanType) {
        this.mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN);
      }
      else {
        this.mv.visitTypeInsn(Opcodes.ANEWARRAY, Name);
      }

      if (node.getInitializer() != null) {
        int i = 0;

        for (final Object o : node.getInitializer().expressions()) {
          this.mv.visitInsn((Opcodes.DUP));
          generateIntConst(i);
          ((ASTNode) o).accept(this);
          if (t instanceof IntType) {
            this.mv.visitInsn(Opcodes.IASTORE);
          }
          else if (t instanceof BooleanType) {
            this.mv.visitInsn(Opcodes.BASTORE);
          }
          else {
            this.mv.visitInsn(Opcodes.AASTORE);
          }
          i++;
        }
      }

      return false;
    }

    @Override
    public boolean visit(final Assignment node) {
      final ASTNode lhsNode = node.getLeftHandSide();

      if (lhsNode instanceof FieldAccess) {
        final FieldDeclaration fd = (FieldDeclaration) this.symbolMap.get(lhsNode);
        final FieldAccess fa = (FieldAccess) lhsNode;
        final String className = ((TypeDeclaration) fd.getParent()).getName().getIdentifier();

        fa.getExpression().accept(this);

        node.getRightHandSide().accept(this);

        this.mv.visitFieldInsn(
                Opcodes.PUTFIELD,
                className,
                fa.getName().getIdentifier(),
                convertType(this.typeMap.get(fd)));
      }
      else if (lhsNode instanceof ArrayAccess) {
        final ArrayAccess AA = ((ArrayAccess) lhsNode);
        final Type t = this.typeMap.get(AA);

        AA.getArray().accept(this);
        AA.getIndex().accept(this);

        node.getRightHandSide().accept(this);

        if (t instanceof IntType) {
          this.mv.visitInsn(Opcodes.IASTORE);
        }
        else if (t instanceof BooleanType) {
          this.mv.visitInsn(Opcodes.BASTORE);
        }
        else {
          this.mv.visitInsn(Opcodes.AASTORE);
        }
      }
      else {
        node.getRightHandSide().accept(this);
        final String varName = ((SimpleName) lhsNode).getIdentifier();
        final Type t = this.typeMap.get(lhsNode);
        if ((t instanceof IntType) || (t instanceof BooleanType)) {
          this.mv.visitVarInsn(Opcodes.ISTORE, this.localIndexMap.get(varName));
        }
        else {
          this.mv.visitVarInsn(Opcodes.ASTORE, this.localIndexMap.get(varName));
        }
      }

      return false;
    }

    @Override
    public boolean visit(final ClassInstanceCreation node) {
      final String Name = this.typeMap.get(node).name;
      this.mv.visitTypeInsn(Opcodes.NEW, Name);
      this.mv.visitInsn(Opcodes.DUP);
      this.mv.visitMethodInsn(
              Opcodes.INVOKESPECIAL,
              Name,
              "<init>",
              "()V",
              false);

      return false;
    }

    @Override
    public boolean visit(final ConditionalExpression node) {
      node.getExpression().accept(this);

      final Label elselabel = new Label();
      this.mv.visitJumpInsn(Opcodes.IFEQ, elselabel);

      node.getThenExpression().accept(this);

      final Label endlabel = new Label();
      this.mv.visitJumpInsn(Opcodes.GOTO, endlabel);

      this.mv.visitLabel(elselabel);
      node.getElseExpression().accept(this);

      this.mv.visitLabel(endlabel);

      return false;
    }

    @Override
    public boolean visit(final DoStatement node) {
      node.getExpression().accept(this);

      final Label loopLabel = new Label();
      final Label endLabel = new Label();

      this.mv.visitLabel(loopLabel);
      node.getExpression().accept(this);
      this.mv.visitJumpInsn(Opcodes.IFEQ, endLabel);
      node.getBody().accept(this);
      this.mv.visitJumpInsn(Opcodes.GOTO, loopLabel);
      this.mv.visitLabel(endLabel);

      return false;
    }

    @Override
    public boolean visit(final FieldAccess node) {
      final FieldDeclaration fd = (FieldDeclaration) this.symbolMap.get(node);
      final String className = ((TypeDeclaration) fd.getParent()).getName().getIdentifier();
      final Type t = this.typeMap.get(fd);

      node.getExpression().accept(this);

      this.mv.visitFieldInsn(
              Opcodes.GETFIELD,
              className,
              node.getName().getIdentifier(),
              convertType(t));

      return false;
    }

    @Override
    public boolean visit(final ForStatement node) {
      for (final Object o : node.initializers()) {
        ((Assignment) o).accept(this);
      }

      final Label looplabel = new Label();
      final Label endlabel = new Label();

      this.mv.visitLabel(looplabel);
      if (node.getExpression() != null) {
        node.getExpression().accept(this);
        this.mv.visitJumpInsn(Opcodes.IFEQ, endlabel);
      }

      if (node.getBody() != null) {
        node.getBody().accept(this);
      }

      for (final Object o : node.updaters()) {
        ((PostfixExpression) o).accept(this);
      }
      this.mv.visitJumpInsn(Opcodes.GOTO, looplabel);
      this.mv.visitLabel(endlabel);

      return false;
    }

    @Override
    public boolean visit(final InfixExpression node) {
      final InfixExpression.Operator op = node.getOperator();

      if (op == InfixExpression.Operator.LEFT_SHIFT) {
        node.getLeftOperand().accept(this);
        node.getRightOperand().accept(this);
        this.mv.visitInsn(Opcodes.ISHL);
      }
      else if (op == InfixExpression.Operator.RIGHT_SHIFT_SIGNED) {
        node.getLeftOperand().accept(this);
        node.getRightOperand().accept(this);
        this.mv.visitInsn(Opcodes.ISHR);
      }
      else if (op == InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED) {
        node.getLeftOperand().accept(this);
        node.getRightOperand().accept(this);
        this.mv.visitInsn(Opcodes.IUSHR);
      }
      else if (op == InfixExpression.Operator.EQUALS) {
        node.getLeftOperand().accept(this);
        node.getRightOperand().accept(this);

        final Type t = this.typeMap.get(node.getLeftOperand());
        if ((t instanceof NullType) ||
                (t instanceof ClassType)) {
          generateRelationalCode(Opcodes.IF_ACMPEQ);
        }
        else {
          generateRelationalCode(Opcodes.IF_ICMPEQ);
        }
      } else if (op == InfixExpression.Operator.NOT_EQUALS) {
        node.getLeftOperand().accept(this);
        node.getRightOperand().accept(this);

        final Type t = this.typeMap.get(node.getLeftOperand());
        if ((t instanceof NullType) ||
                (t instanceof ClassType))  {
          generateRelationalCode(Opcodes.IF_ACMPNE);
        }
        else {
          generateRelationalCode(Opcodes.IF_ICMPNE);
        }
      }
      else {
        return super.visit(node);
      }

      return false;
    }

    @Override
    public boolean visit(final NullLiteral node) {
      this.mv.visitInsn(Opcodes.ACONST_NULL);
      return false;
    }

    @Override
    public boolean visit(final PostfixExpression node) {
      final ASTNode opr = node.getOperand();
      final PostfixExpression.Operator op = node.getOperator();

      if (opr instanceof FieldAccess) {
        final FieldDeclaration fd = (FieldDeclaration) this.symbolMap.get(opr);
        final FieldAccess fa = (FieldAccess) opr;
        final String className = ((TypeDeclaration) fd.getParent()).getName().getIdentifier();

        fa.getExpression().accept(this);
        this.mv.visitInsn(Opcodes.DUP);

        this.mv.visitFieldInsn(
                Opcodes.GETFIELD,
                className,
                fa.getName().getIdentifier(),
                convertType(this.typeMap.get(fd)));

        if(op == PostfixExpression.Operator.INCREMENT) {
          this.mv.visitInsn(Opcodes.ICONST_1);
          this.mv.visitInsn(Opcodes.IADD);
        }
        else if(op == PostfixExpression.Operator.DECREMENT) {
          this.mv.visitInsn(Opcodes.ICONST_1);
          this.mv.visitInsn(Opcodes.ISUB);
        }

        this.mv.visitFieldInsn(
                Opcodes.PUTFIELD,
                className,
                fa.getName().getIdentifier(),
                convertType(this.typeMap.get(fd)));
      }

      else if(opr instanceof ArrayAccess) {
        final ArrayAccess AA = ((ArrayAccess) opr);
        final Type t = this.typeMap.get(AA);

        AA.getArray().accept(this);
        AA.getIndex().accept(this);
        this.mv.visitInsn(Opcodes.DUP2);

        if (t instanceof IntType) {
          this.mv.visitInsn(Opcodes.IALOAD);
          this.mv.visitInsn(Opcodes.ICONST_1);
          this.mv.visitInsn(Opcodes.IADD);
          this.mv.visitInsn(Opcodes.IASTORE);
        }
        else if (t instanceof BooleanType) {
          this.mv.visitInsn(Opcodes.BALOAD);
          this.mv.visitInsn(Opcodes.ICONST_1);
          this.mv.visitInsn(Opcodes.IADD);
          this.mv.visitInsn(Opcodes.BASTORE);
        }
        else {
          this.mv.visitInsn(Opcodes.AALOAD);
          this.mv.visitInsn(Opcodes.ICONST_1);
          this.mv.visitInsn(Opcodes.IADD);
          this.mv.visitInsn(Opcodes.AASTORE);
        }
      }

      else {
        final String Name = ((SimpleName) opr).getIdentifier();
        if (op == PostfixExpression.Operator.INCREMENT) {
          this.mv.visitIincInsn(this.localIndexMap.get(Name), 1);
        } else if (op == PostfixExpression.Operator.DECREMENT) {
          this.mv.visitIincInsn(this.localIndexMap.get(Name), -1);
        }
      }

      return false;
    }

    @Override
    public boolean visit(final PrefixExpression node) {
      final PrefixExpression.Operator op = node.getOperator();
      if(op == PrefixExpression.Operator.COMPLEMENT){
        node.getOperand().accept(this);
        this.mv.visitInsn(Opcodes.ICONST_M1);
        this.mv.visitInsn(Opcodes.IXOR);
      }
      else {
        return super.visit(node);
      }

      return false;
    }

    @Override
    public boolean visit(final ReturnStatement node) {
      final Expression e = node.getExpression();
      if (e == null) {
        this.mv.visitInsn(Opcodes.RETURN);
      } else {
        e.accept(this);

        final Type t = this.typeMap.get(e);
        if ((t instanceof NullType) ||
                (t instanceof ClassType) ||
                (t instanceof ArrayType)) {
          this.mv.visitInsn(Opcodes.ARETURN);
        }
        else {
          this.mv.visitInsn(Opcodes.IRETURN);
        }
      }
      return false;
    }

    @Override
    public boolean visit(final TypeDeclaration node) {
      for (Object m : node.modifiers()) {
        if (((Modifier) m).getKeyword() == ModifierKeyword.PUBLIC_KEYWORD) {
          return super.visit(node);
        }
      }

      this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES
              | ClassWriter.COMPUTE_MAXS);
      this.cw.visit(
              Opcodes.V1_5,
              Opcodes.ACC_SUPER,
              node.getName().getIdentifier(),
              null,
              "java/lang/Object",
              null);
      this.cw.visitSource(null, null);
      generateConstructor(this.mainClassName);
      for (final Object o : node.bodyDeclarations()) {
        ((ASTNode) o).accept(this);
      }
      this.cw.visitEnd();

      this.otherClasses.put(node.getName().getIdentifier(), this.cw.toByteArray());
      this.cw = null;

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
