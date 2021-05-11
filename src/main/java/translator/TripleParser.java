package translator;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.SortCondition;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitorBase;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.path.Path;
import org.neo4j.cypherdsl.core.*;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.renderer.Renderer;

import java.util.*;

public class TripleParser extends OpVisitorBase {
    private HashMap<String, NodeUtils> Node2Utils = new HashMap<>();
    public static HashSet<String> GraphRelationshipSet = new HashSet<>();
    private Boolean optionalFlag = false;

    private ArrayList<RelationshipUtils> PendingRelationships = new ArrayList<>();
    private ArrayList<RelationshipUtils> OptionalPendingRelationships = new ArrayList<>();

    private ArrayList<RelationshipPattern> buildedRelationship = new ArrayList<>();
    private ArrayList<RelationshipPattern> buildedOptionalRelationship = new ArrayList<>();

    protected ArrayList<Node> buildedNode = new ArrayList<>();
    protected ArrayList<Node> buildedOptionalNode = new ArrayList<>();

    private ArrayList<Condition> WhereClause = new ArrayList<>();
    private ArrayList<Condition> OptionalWhereClause = new ArrayList<>();

    private ArrayList<Triple> NamedTriple = new ArrayList<>();

    public static HashMap<String, String[]> ExtendProject = new HashMap<>();
    public static HashMap<String, String> ExtendVariableMap = new HashMap<>();
    public static ArrayList<AliasedExpression> WithList = new ArrayList<>();

    private Boolean DistinctFlag = false;
    private Boolean SelectAllFlag = true;
    private Boolean PathFlag = false;

    private ArrayList<SortItem> orderByList = new ArrayList<>();
    private Long LimitLength = 0L;

    protected static Integer SpecificLength = 0;

    public static VariableMap varMap = new VariableMap();
    private List<String> ProjectVariable = new ArrayList<>();

    public void addRelationshipIntoGraph(String rel) {
        GraphRelationshipSet.add(rel);
    }

    public String parseSparqlQuery(String queryString) {
        Query query = QueryFactory.create(queryString);
        Op op = Algebra.compile(query);
        OpWalker.walk(op, this);
        if (SelectAllFlag)
            return Renderer.getDefaultRenderer()
                    .render(Cypher.match(Cypher.anyNode().named("n")).returning("n").build());
        if (ProjectVariable.isEmpty())
            setResultVar(query);
        var withoutWhere = buildMatch();
//        if (!WithList.isEmpty())
//            withoutWhere = withoutWhere.with(WithList.toArray(new AliasedExpression[0]));
        var withWhere = buildWhere(withoutWhere, WhereClause);
        var withOptionalMatch = buildOptionalMatch(withWhere);
        var withOptionalWhere = buildWhere(withOptionalMatch, OptionalWhereClause);
        StatementBuilder.OngoingReadingAndReturn withReturn;
        if (varMap.getVarMapDSL().get(ProjectVariable.get(0)) instanceof Named)
            withReturn = buildReturn(withOptionalWhere,
                    RebuildProject.rebuildNamedProject(ProjectVariable).toArray(new Named[0]));
        else
            withReturn = buildReturn(withOptionalWhere,
                    RebuildProject.rebuildExpressionProject(ProjectVariable, ExtendProject).toArray(new Expression[0]));
        var withOrder = buildOrderBy(withReturn);
        var withLimit = buildLimit((StatementBuilder.OngoingReadingAndReturn) withOrder);
        var s = withLimit.build();
        String cypher = Renderer.getDefaultRenderer().render(s);
        WithList.clear();
        return cypher;
    }

    public void setResultVar(Query query) {
        VarExprList resultExpr = query.getProject();
        List<Var> resultVar = resultExpr.getVars();
        resultVar.forEach(var -> ProjectVariable.add(var.toString()));
    }

    @Override
    public void visit(final OpFilter opFilter) {
        Expr expr = opFilter.getExprs().get(0);
        WhereClause.add(WhereClauseBuilder.transform(expr));
    }

    @Override
    public void visit(OpLeftJoin opLeftJoin) {
        if (opLeftJoin.getExprs() == null)
            return;
        Expr expr = opLeftJoin.getExprs().get(0);
        OptionalWhereClause.add(WhereClauseBuilder.transform(expr));
    }

    @Override
    public void visit(OpPath opPath) {
        SelectAllFlag = false;
        PathFlag = true;
        final TriplePath triple = opPath.getTriplePath();
        org.apache.jena.graph.Node subject = triple.getSubject();
        org.apache.jena.graph.Node object = triple.getObject();
        String path = triple.getPath().toString();
        PathType pathType = BuildGraphPattern.getPathType(path);
        String[] rels;
        if (pathType.equals(PathType.Specific)) {
            path = path.replaceAll("\\(|\\)", "");
            rels = path.split("[>][/][<]");
            buildedRelationship.add(buildPathChain(subject, rels, object));
            return;
        }
        NodeUtils nodeUtils = new NodeUtils();
        nodeUtils = parsePath(triple, nodeUtils, PendingRelationships);
        Node2Utils.put(subject.getName(), nodeUtils);
        buildAllNode();
        buildPattern();
    }

    private RelationshipPattern buildPathChain(org.apache.jena.graph.Node subject, String[] rels, org.apache.jena.graph.Node object) {
        String subjectName = subject.isURI()? BuildUtils.getURINodeName(subject.getURI()): subject.getName();
        String objectName = object.isURI()? BuildUtils.getURINodeName(object.getURI()): object.getName();
        Node sub = buildPatternNode(buildedNode, subjectName);
        Node obj = buildPatternNode(buildedNode, objectName);
        RelationshipPattern relationshipPattern = null;
        for (int i = 0; i < rels.length; i++ ) {
            rels[i] = rels[i].replaceAll("\\<|\\>", "");
            if (i == 0)
                relationshipPattern = sub.relationshipTo(Cypher.anyNode(), rels[i]);
            else if ( i == rels.length - 1) {
                relationshipPattern = relationshipPattern.relationshipTo(obj, rels[i]);
                break;
            }
            else
                relationshipPattern = relationshipPattern.relationshipTo(Cypher.anyNode(), rels[i]);
        }
        if (!subject.isConcrete())
            ParseTriples.putVarToMap(subjectName, sub);
        if (!object.isConcrete())
            ParseTriples.putVarToMap(objectName, obj);
        BuildUtils.removeBuildedNode(buildedNode, subjectName);
        BuildUtils.removeBuildedNode(buildedNode, objectName);
        return relationshipPattern;
    }

    @Override
    public void visit(OpOrder opOrder) {
        List<SortCondition> sortConditions = opOrder.getConditions();
        sortConditions.forEach(sort -> {
            String order = sort.toString().substring(1, sort.toString().length()-1);
            order = order.replaceAll("\\(|\\)", " ");
            String[] subOrder = order.split(" ");

            if (subOrder.length == 2)
                addOrderByListASC(varMap.getVarMapDSL().get(subOrder[1]));
            else {
                switch (subOrder[1]) {
                    case "DESC":
                        addOrderByListDESC(varMap.getVarMapDSL().get(subOrder[2]));
                        break;
                    case "ASC":
                        addOrderByListASC(varMap.getVarMapDSL().get(subOrder[2]));
                        break;
                    default:
                        throw new IllegalStateException(String.format("Not support Order %s. ",subOrder[1]));
                }
            }
        });
    }

    private void addOrderByListASC(Object object) {
        if (object instanceof Expression)
            orderByList.add(((Expression) object).ascending());
        else
            orderByList.add(((Node) object).ascending());
    }

    private void addOrderByListDESC(Object object) {
        if (object instanceof Expression)
            orderByList.add(((Expression) object).descending());
        else
            orderByList.add(((Node) object).descending());
    }

    @Override
    public void visit(OpExtend opExtend) {
        VarExprList varExprList = opExtend.getVarExprList();
        varExprList.forEachExpr((var, expr)->{
            ExtendVariableMap.put(expr.toString(), var.toString());
            ExtendProject.put(var.toString(), ExtendProject.get(expr.toString()));
            ExtendProject.remove(expr.toString());
        });
        RebuildProject.rebuildExtendProject(ExtendProject);
    }

    @Override
    public void visit(OpGroup opGroup) {
        List<ExprAggregator> exprAggregators = opGroup.getAggregators();
        exprAggregators.forEach(expr -> {
            String str = expr.getAggregator().toString().replaceAll("\\(|\\)", " ");
            String[] agg = str.split(" ");
            ExtendProject.put(expr.getAggVar().toString(), agg);
        });
    }

    @Override
    public void visit(final OpSlice opSlice) {
        LimitLength = opSlice.getLength();
    }

    @Override
    public void visit(OpDistinct opDistinct) {
        DistinctFlag = true;
    }



    @Override
    public void visit(OpProject opProject) {
        opProject.getVars().forEach(var -> ProjectVariable.add(var.toString()));
    }

    @Override
    public void visit(final OpBGP opBGP) {
        SelectAllFlag = false;
        List<Triple> triples = opBGP.getPattern().getList();
        triples = removeOtherTriples(triples);
        if (triples.isEmpty())
            return;
        org.apache.jena.graph.Node subject;
        String subjectName;
        NodeUtils nodeUtils;
        for (final Triple triple : triples) {
            subject = triple.getSubject();
            subjectName = subject.isURI()? BuildUtils.getURINodeName(subject.getURI()): subject.getName();
            nodeUtils = Node2Utils.get(subjectName) == null? new NodeUtils(): Node2Utils.get(subjectName);
            nodeUtils = parseNodeBGP(triple, nodeUtils);
            Node2Utils.put(subjectName, nodeUtils);
        }
        ParseTriples.parseNamedTriple(NamedTriple, PendingRelationships);
        buildAllNode();
        buildPattern();
        optionalFlag = true;
    }

    private void buildAllNode() {
        for (String nodeName: Node2Utils.keySet())
            buildNode(nodeName);
        Node2Utils.clear();
    }

    private List<Triple> removeOtherTriples(List<Triple> triples) {
        for (int i = 0; i < triples.size(); i++ ){
            if (triples.get(i).getSubject().isNodeTriple()) {
                NamedTriple.add(triples.get(i));
                triples.remove(i);
                i--;
            }
            else if (BuildUtils.getPredicateType(triples.get(i).getPredicate()).equals("Relationship")) {
                buildPendingRelationships(triples.get(i));
                triples.remove(i);
                i--;
            }
        }
        return triples;
    }

    private void buildPendingRelationships(Triple triple) {
        org.apache.jena.graph.Node subject = triple.getSubject();
        org.apache.jena.graph.Node predicate = triple.getPredicate();
        org.apache.jena.graph.Node object = triple.getObject();
        String subjectName = subject.isURI()? BuildUtils.getURINodeName(subject.getURI()): subject.getName();
        String predicateURI = predicate.isConcrete()? predicate.getURI(): "";
        String predicateName = predicate.isURI()? BuildUtils.getURINodeName(predicate.getURI()): predicate.getName();
        String objectName = object.isURI()? object.getURI(): object.getName();
        if (!predicate.isConcrete()) {
            Relationship rel = Cypher.anyNode(subjectName).relationshipTo(Cypher.anyNode(object.getName()), predicateURI)
                    .named(predicateName);
            ParseTriples.putVarToMap(predicate.getName(), Functions.type(rel));
        }
        if (!optionalFlag)
            PendingRelationships.add(new RelationshipUtils(subjectName, predicateName, predicateURI, objectName));
        else
            OptionalPendingRelationships.add(new RelationshipUtils(subjectName,  predicateName, predicateURI, objectName));
        buildRelationshipNode(subjectName, subject);
        buildRelationshipNode(objectName, object);
    }

    private void buildRelationshipNode(String name, org.apache.jena.graph.Node node) {
        if (node.isBlank())
            throw new IllegalStateException(String.format("Not support Blank Node. "));
        if (varMap.getVarMapDSL().containsKey(name))
            return;
        Node obj;
        if (node.isURI()) {
            obj = Cypher.anyNode(BuildUtils.getURINodeName(name)).withProperties("uri", Cypher.literalOf(node.getURI()));
            ParseTriples.putVarToMap(name, obj);
            if (!optionalFlag || PathFlag)
                buildedNode.add(obj);
            else
                buildedOptionalNode.add(obj);
        }
        else
            ParseTriples.putVarToMap(name, Cypher.anyNode(name));
    }

    private Node buildNode(String name) {
        if (!Node2Utils.containsKey(name))
            return Cypher.anyNode(name);
        List<String> NodeLabels = Node2Utils.get(name).getLabels();
        HashMap<String, Object> NodeProperties = Node2Utils.get(name).getProperties();
        Node tmpNode = Cypher.anyNode(name);
        if (!NodeLabels.isEmpty())
            tmpNode = Cypher.node(NodeLabels.get(0), NodeLabels.subList(1, NodeLabels.size())).named(name);
        if (!NodeProperties.isEmpty())
            tmpNode = tmpNode.withProperties(NodeProperties);
        if (!optionalFlag || PathFlag)
            buildedNode.add(tmpNode);
        else
            buildedOptionalNode.add(tmpNode);
        return tmpNode;
    }

    private void buildGraphPattern(ArrayList<Node> nodes,
                                   ArrayList<RelationshipUtils> pendingRelationships,
                                   ArrayList<RelationshipPattern> buildedRelationships) {
        for (int i = 0; i < pendingRelationships.size(); i++ ) {
            String subjectName = pendingRelationships.get(i).getStartNode();
            String objectName = pendingRelationships.get(i).getEndNode();
            String pType = pendingRelationships.get(i).getRelationshipType();
            String pName = pendingRelationships.get(i).getRelationshipName();
            HashMap<String, Object> relProperties = pendingRelationships.get(i).getProperties();
            Node subject = buildPatternNode(nodes, subjectName);
            Node object = buildPatternNode(nodes, objectName);
            PathType pathType = BuildGraphPattern.getPathType(pType);
            if (pathType.equals(PathType.Specific))
                break;
            if (!pathType.equals(PathType.Normal))
                pType = BuildUtils.getRelationshipFromPath(pType);
            BuildUtils.removeBuildedNode(nodes, subjectName);
            BuildUtils.removeBuildedNode(nodes, objectName);
            RelationshipPattern rel = BuildGraphPattern.buildRelationship(subject, pName, pType, object, pathType, relProperties);
            for (int j = i + 1; j < pendingRelationships.size(); j++ ) {
                String otherSubjectName = pendingRelationships.get(j).getStartNode();
                String otherRelName;
                String otherRelType;
                String otherObjectName;
                if (objectName.equals(otherSubjectName)) {
                    otherRelName = pendingRelationships.get(j).getRelationshipName();
                    otherRelType = pendingRelationships.get(j).getRelationshipType();
                    otherObjectName = pendingRelationships.get(j).getEndNode();
                    rel = otherRelType.equals("")?
                            rel.relationshipTo(BuildUtils.getNode(nodes, otherSubjectName)).named(otherRelName):
                            rel.relationshipTo(BuildUtils.getNode(nodes, otherObjectName), otherRelType).named(otherRelName);
                    BuildUtils.removeBuildedNode(nodes, otherObjectName);
                    objectName = otherObjectName;
                    pendingRelationships.remove(j);
                    j--;
                }
            }
            if (!relProperties.isEmpty()) {
                if (rel instanceof Relationship)
                    rel = ((Relationship) rel).withProperties(relProperties);
                else if (rel instanceof RelationshipChain)
                    rel = ((RelationshipChain) rel).properties(relProperties);
            }
            buildedRelationships.add(rel);
        }
    }

    private void buildPattern() {
        if (!optionalFlag || PathFlag)
            buildGraphPattern(buildedNode, PendingRelationships, buildedRelationship);
        else
            buildGraphPattern(buildedOptionalNode, OptionalPendingRelationships, buildedOptionalRelationship);
    }

    private StatementBuilder.OngoingReadingWithoutWhere buildMatch() {
        ArrayList<PatternElement> patternElements = new ArrayList<>();
        patternElements.addAll(buildedRelationship);
        patternElements.addAll(buildedNode);
        return Cypher.match(patternElements.toArray(new PatternElement[0]));
    }

    private StatementBuilder.OngoingReadingWithWhere buildWhere(StatementBuilder.OngoingReadingWithoutWhere withoutWhere,
                                                                ArrayList<Condition> where) {
        if (where.isEmpty())
            return (StatementBuilder.OngoingReadingWithWhere) withoutWhere;
        Condition firstWhere = where.get(0);
        List<Condition> otherWhere = where.subList(1, where.size());
        StatementBuilder.OngoingReadingWithWhere withWhere = withoutWhere.where(firstWhere);
        Iterator<Condition> it = otherWhere.iterator();
        while (it.hasNext())
            withWhere = withWhere.and(it.next());
        return withWhere;
    }

    private StatementBuilder.OngoingReadingWithoutWhere buildOptionalMatch(StatementBuilder.OngoingReadingWithWhere withWhere) {
        ArrayList<PatternElement> patternElements = new ArrayList<>();
        patternElements.addAll(buildedOptionalRelationship);
        patternElements.addAll(buildedOptionalNode);
        if (patternElements.isEmpty())
            return (StatementBuilder.OngoingReadingWithoutWhere) withWhere;
        return withWhere.optionalMatch(patternElements.toArray(new PatternElement[0]));
    }

    private StatementBuilder.OngoingReadingAndReturn buildReturn(StatementBuilder.OngoingReadingWithWhere withWhere,
                                                                 Expression[] variables) {
        return DistinctFlag ? withWhere.returningDistinct(variables): withWhere.returning(variables);
    }

    private StatementBuilder.OngoingReadingAndReturn buildReturn(StatementBuilder.OngoingReadingWithWhere withWhere,
                                                                 Named[] variables) {
        return DistinctFlag ? withWhere.returningDistinct(variables): withWhere.returning(variables);
    }

    private StatementBuilder.BuildableStatement buildLimit(StatementBuilder.OngoingReadingAndReturn withReturn) {
        return LimitLength == 0 ? withReturn: withReturn.limit(LimitLength);
    }

    private StatementBuilder.BuildableStatement buildOrderBy(StatementBuilder.OngoingReadingAndReturn withReturn) {
        return orderByList.isEmpty() ? withReturn: withReturn.orderBy(orderByList.toArray(new SortItem[0]));
    }

    private Node buildPatternNode(ArrayList<Node> nodes, String name) {
        if (BuildUtils.isURI(name))
            return Cypher.anyNode(BuildUtils.getURINodeName(name)).withProperties("uri", Cypher.literalOf(name));
        else
            return BuildUtils.getNode(nodes, name) == null? buildNode(name) : BuildUtils.getNode(nodes, name);
    }

    private NodeUtils parseNodeBGP(Triple triple, NodeUtils nodeUtils) {
        org.apache.jena.graph.Node subject = triple.getSubject();
        org.apache.jena.graph.Node predicate = triple.getPredicate();
        org.apache.jena.graph.Node object = triple.getObject();
        String subjectName = subject.isURI()? BuildUtils.getURINodeName(subject.getURI()): subject.getName();
        String predicateURI = predicate.isConcrete()? predicate.getURI(): "";
        Node subjectURINode = null;
        if (!subject.isConcrete() && !VariableMap.isVariety(subject.toString()))
            throw new IllegalStateException(String.format("Not support Blank Node. "));
        if (subject.isURI()) {
            nodeUtils.addProperties("uri", subject.getURI());
            subjectURINode = Cypher.anyNode(subjectName);
        }
        switch (BuildUtils.getPredicateType(predicate)) {
            case "Type" :
                if (object.isConcrete()) {
                    nodeUtils.addLabel(object.getURI());
                    if (subject.isURI())
                        ParseTriples.putVarToMap(subjectName, subjectURINode.hasLabels(object.getURI()));
                    else
                        ParseTriples.putVarToMap(subjectName, Cypher.node(object.getURI()).named(subjectName));
                }
                else
                    ParseTriples.putVarToMap(object.getName(), Functions.labels(Cypher.anyNode(subjectName)));
                break;
            case "Property" :
                if (object.isConcrete()) {
                    if (object.isLiteral())
                        nodeUtils.addProperties(predicateURI, object.getLiteralValue());
                    if (!varMap.getVarMapDSL().containsKey("?"+subjectName))
                        ParseTriples.putVarToMap(subjectName,
                                Cypher.anyNode(subjectName).withProperties(nodeUtils.getProperties()));
                }
                else {
                    Node sub = subject.isURI()? subjectURINode: Cypher.anyNode(subjectName);
                    Property objProperty = sub.property(predicate.getURI());
                    ParseTriples.putVarToMap(object.getName(), objProperty);
                }
                break;
            default:
                break;
        }
        return nodeUtils;
    }

    private NodeUtils parsePath(TriplePath triplePath, NodeUtils nodeUtils,
                                ArrayList<RelationshipUtils> relationshipUtils) {
        org.apache.jena.graph.Node subject = triplePath.getSubject();
        Path path = triplePath.getPath();
        org.apache.jena.graph.Node object = triplePath.getObject();
        String subjectName = subject.getName();
        String predicateType = path.toString();
        String predicateName = BuildUtils.getURINodeName(BuildUtils.getRelationshipFromPath(predicateType));
        relationshipUtils.add(new RelationshipUtils(subjectName, predicateName, predicateType, object.getName()));
        ParseTriples.putVarToMap(object.getName(), buildNode(object.getName()));
        return nodeUtils;
    }
}
