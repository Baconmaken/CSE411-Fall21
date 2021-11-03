package esjc.test;

import esjc.ast.ExtendedASTUtil;
import esjc.symboltable.ExtendedSymbolTableBuilder;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Assert;
import org.junit.Test;
import sjc.SJC;
import sjc.ast.ASTUtil;
import sjc.symboltable.SymbolTableBuilder;
import sjc.util.Util;

/**
 * Test cases for {@link SymbolTableBuilder}.
 *
 * @author <a href="mailto:robby@ksu.edu">Robby</a>
 */
public class ExtendedSymbolTableBuilderTest {
  public static void testPass(final String filename) {
    try {
      final CompilationUnit cu = ExtendedASTUtil.ast(Util.getResource(
          SJC.class,
          filename));
      System.out.print(ExtendedSymbolTableBuilder.build(cu));
      System.out.flush();
    } catch (final Exception e) {
      e.printStackTrace();
      Assert.assertTrue(e.getMessage(), false);
    }
  }

  @Test
  public void testTypeCheckFailAssignBooleanToInt() {
    ExtendedSymbolTableBuilderTest.testPass("TypeCheckFailAssignBooleanToInt.java");
  }

  @Test
  public void testTwoMethods() {
    ExtendedSymbolTableBuilderTest.testPass("TwoMethods.java");
  }

  @Test
  public void testNestedScope() {
    ExtendedSymbolTableBuilderTest.testPass("NestedScope.java");
  }

  @Test
  public void testFactorial() {
    ExtendedSymbolTableBuilderTest.testPass("Factorial.java");
  }

  @Test
  public void testPower() {
    ExtendedSymbolTableBuilderTest.testPass("Power.java");
  }

}
