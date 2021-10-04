// Generated from esjc-parser/src/main/java/esjc/parser/ExtendedStaticJava.g4 by ANTLR 4.8
package esjc.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExtendedStaticJavaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExtendedStaticJavaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(ExtendedStaticJavaParser.CompilationUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#classDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDefinition(ExtendedStaticJavaParser.ClassDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#memberDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberDeclaration(ExtendedStaticJavaParser.MemberDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#mainMethodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMainMethodDeclaration(ExtendedStaticJavaParser.MainMethodDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDeclaration(ExtendedStaticJavaParser.FieldDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#methodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDeclaration(ExtendedStaticJavaParser.MethodDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BooleanType}
	 * labeled alternative in {@link ExtendedStaticJavaParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanType(ExtendedStaticJavaParser.BooleanTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntType}
	 * labeled alternative in {@link ExtendedStaticJavaParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntType(ExtendedStaticJavaParser.IntTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VoidType}
	 * labeled alternative in {@link ExtendedStaticJavaParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVoidType(ExtendedStaticJavaParser.VoidTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NonVoidReturnType}
	 * labeled alternative in {@link ExtendedStaticJavaParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonVoidReturnType(ExtendedStaticJavaParser.NonVoidReturnTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(ExtendedStaticJavaParser.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(ExtendedStaticJavaParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#methodBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodBody(ExtendedStaticJavaParser.MethodBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#localDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalDeclaration(ExtendedStaticJavaParser.LocalDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(ExtendedStaticJavaParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#assignStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStatement(ExtendedStaticJavaParser.AssignStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(ExtendedStaticJavaParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(ExtendedStaticJavaParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#invokeExpStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvokeExpStatement(ExtendedStaticJavaParser.InvokeExpStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(ExtendedStaticJavaParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IdExp}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdExp(ExtendedStaticJavaParser.IdExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TrueLiteral}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueLiteral(ExtendedStaticJavaParser.TrueLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InvokeExp}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvokeExp(ExtendedStaticJavaParser.InvokeExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExp}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExp(ExtendedStaticJavaParser.ParenExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(ExtendedStaticJavaParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryExp}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExp(ExtendedStaticJavaParser.UnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NullLiteral}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullLiteral(ExtendedStaticJavaParser.NullLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BinaryExp}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExp(ExtendedStaticJavaParser.BinaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FalseLiteral}
	 * labeled alternative in {@link ExtendedStaticJavaParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseLiteral(ExtendedStaticJavaParser.FalseLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#invoke}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvoke(ExtendedStaticJavaParser.InvokeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExtendedStaticJavaParser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgs(ExtendedStaticJavaParser.ArgsContext ctx);
}