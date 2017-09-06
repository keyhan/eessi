package no.nav.eux.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelTest extends CamelTestSupport {

    /**
     * Simple Camel tests - without the Camel test-framework
     */
    @Test
    public void sendAMessageTest() throws Exception {
        // 1: Create a route
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:input")
                        .to("mock:result") ;
            }
        });

        // 2: Start Camel
        camelContext.start();
        System.out.println("--- Camel has started");
        ProducerTemplate template = camelContext.createProducerTemplate();

        // 3: Do some routing
        template.sendBody("direct:input", "...the body of the message..."); // ..and the body floats through Camel...
        System.out.println("--- Camel sent a message");

        // 4: Stop Camel
        System.out.println("--- Camel has stopped");
        camelContext.stop();
    }

    @Test
    public void sendHeaderOgBodyTest() throws Exception {

        // 1: Create a route
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .setHeader("title", constant("This is a header"))
                        .setBody().constant("This is the body of the message")
                        .log(LoggingLevel.INFO, "Header: ${header.title}")
                        .log(LoggingLevel.INFO, "Body: ${body}")
                ;
            }
        });

        // 2: Start Camel
        camelContext.start();
        System.out.println("--- Camel has started");
        ProducerTemplate template = camelContext.createProducerTemplate();

        // 3: Send in a message
        template.sendBody("direct:start", "... the messagebody ..."); // ..and the body floats through Camel...
        System.out.println("--- Camel sent a message");

        // 4: Stop Camel
        System.out.println("--- Camel has stopped");
        camelContext.stop();
    }
}
