import org.junit.Test;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Functions;
import org.neo4j.cypherdsl.core.renderer.Renderer;

import java.util.HashMap;

public class testCypherDSL {
    @Test
    public void testCypher() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", Cypher.literalOf("Tom Hanks"));
        hashMap.put("age",Cypher.literalOf(10));
        var tom = Cypher.node("Person").named("tom");
        tom.withProperties(hashMap);
        var name = tom.property("name").as("haha");
        var tomHanksMovies = Cypher.anyNode("tomHanksMovies");
        var relationship = tom.relationshipTo(tomHanksMovies).named("r");
        var tomHanksMusics = Cypher.anyNode("tomHanksMusics");
        var total = Functions.sum(name).as("total");
        var function = Functions.type(relationship);
        var statement = Cypher
                .match(relationship, tom.relationshipTo(tomHanksMusics, "Sing"))
//                .with(total)
                .where(total.gt(Cypher.literalOf(10)).and(total.eq(Cypher.literalOf(10))))
//                .where(Cypher.property)
                .returning(name)
                .build();
//        Cypher.match().with().with().where().optionalMatch().with().where().returning().orderBy().limit();
        System.out.println(Renderer.getDefaultRenderer().render(statement));
        var bsbm = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/";
        var bsbm_inst = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/";
        var offer = Cypher.node(bsbm + "Offer").named("offer");
        var price = offer.property(bsbm + "price");
        var product1 = Cypher.node(bsbm_inst + "ProductType1").named("product1");
        var product = offer.relationshipTo(product1, bsbm + "product").named("product");
        var productNumeric1 = product1.property(bsbm + "productPropertyNumeric1");
        var productNumeric2 = product1.property(bsbm + "productPropertyNumeric2");
        var maxPrice = Functions.max(price).as("maxPrice");
        statement = Cypher.match(tom.relationshipTo(Cypher.anyNode(), "created").named("created")
                .relationshipTo(Cypher.anyNode(),"created").named("created")
                .relationshipTo(tomHanksMovies, "created").named("created")).returning(tomHanksMovies).build();
        System.out.println(statement.getCypher());


        statement = Cypher.match(product)
                .where(productNumeric1.gt(Cypher.literalOf(600))
                        .and(productNumeric1.lt(Cypher.literalOf(800))))
                .optionalMatch(product1.withProperties(productNumeric2,Cypher.literalOf(1000)))
                .returningDistinct(productNumeric1, maxPrice)
                .orderBy(product1.ascending())
                .limit(10).build()
        ;
        System.out.println(Renderer.getDefaultRenderer().render(statement));
        System.out.println(statement);
    }
}
