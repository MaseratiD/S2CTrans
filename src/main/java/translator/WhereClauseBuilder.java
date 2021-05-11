package translator;

import org.apache.jena.sparql.expr.*;
import org.neo4j.cypherdsl.core.*;

public class WhereClauseBuilder {
    static Condition transform(final Expr expression) {
        if (expression instanceof E_Equals) return transform((E_Equals) expression);
        if (expression instanceof E_NotEquals) return transform((E_NotEquals) expression);
        if (expression instanceof E_LessThan) return transform((E_LessThan) expression);
        if (expression instanceof E_LessThanOrEqual) return transform((E_LessThanOrEqual) expression);
        if (expression instanceof E_GreaterThan) return transform((E_GreaterThan) expression);
        if (expression instanceof E_GreaterThanOrEqual) return transform((E_GreaterThanOrEqual) expression);
        if (expression instanceof E_LogicalAnd) return transform((E_LogicalAnd) expression);
        if (expression instanceof E_LogicalOr) return transform((E_LogicalOr) expression);
//        if (expression instanceof E_Exists) return transform((E_Exists) expression);
//        if (expression instanceof E_NotExists) return transform((E_NotExists) expression);
        throw new IllegalStateException(String.format("Unhandled expression: %s", expression));
    }

    private static Expression getExpression(ExprFunction2 expression) {
        TripleParser tripleParser = new TripleParser();
        String arg1 = expression.getArg1().toString();
        if (!VariableMap.isVariety(arg1))
            arg1 = tripleParser.ExtendVariableMap.get(arg1);
        Object object = tripleParser.varMap.getVarMapDSL().get(arg1);
        if (object instanceof AliasedExpression)
            return (AliasedExpression) object;
        else
            return (Property) object;
    }

    private static Condition transform(final E_Equals expression) {
        final Object value = expression.getArg2().getConstant().getNode().getLiteralValue();
        return getExpression(expression).eq(Cypher.literalOf(value));
    }

    private static Condition transform(final E_NotEquals expression) {
        final Object value = expression.getArg2().getConstant().getNode().getLiteralValue();
        return getExpression(expression).ne(Cypher.literalOf(value));
    }

    private static Condition transform(final E_LessThan expression) {
        final Object value = expression.getArg2().getConstant().getNode().getLiteralValue();
        return getExpression(expression).lt(Cypher.literalOf(value));
    }

    private static Condition transform(final E_LessThanOrEqual expression) {
        final Object value = expression.getArg2().getConstant().getNode().getLiteralValue();
        return getExpression(expression).lte(Cypher.literalOf(value));
    }

    private static Condition transform(final E_GreaterThan expression) {
        final Object value = expression.getArg2().getConstant().getNode().getLiteralValue();
        return getExpression(expression).gt(Cypher.literalOf(value));
    }

    private static Condition transform(final E_GreaterThanOrEqual expression) {
        final Object value = expression.getArg2().getConstant().getNode().getLiteralValue();
        return getExpression(expression).gte(Cypher.literalOf(value));
    }

    public static Condition transform(final E_LogicalAnd expression) {
        return transform(expression.getArg1()).and(transform(expression.getArg2()));
    }

    public static Condition transform(final E_LogicalOr expression) {
        return transform(expression.getArg1()).or(transform(expression.getArg2()));
    }

}
