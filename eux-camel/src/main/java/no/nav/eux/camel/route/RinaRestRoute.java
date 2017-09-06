package no.nav.eux.camel.route;

import no.nav.eux.camel.processor.EnrichSedProcessor;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * Receiving a request from RINA. The origin might be a document-event or a case-event. In the case of a document-event the
 * request will contain metadata in addition to a SED. The SED will be picked from the payload, enriched and returned. RINA is
 * waiting for the response.
 *
 *
 */
public class RinaRestRoute extends RouteBuilder {

    private Processor enrichSed = new EnrichSedProcessor();

    @Override
    public void configure() throws Exception {

        try {
            from("servlet:DocumentsEvents")
                    .routeId("EnrichDocumentRoute")
                    .process(enrichSed)
                    .log(LoggingLevel.INFO, "\n===> SED in response (json) : \n" + body() + "\n--------------------");
        } catch (Exception e) {
            System.out.println("RinaRestRoute: ERROR in route");
        }
    }
}
