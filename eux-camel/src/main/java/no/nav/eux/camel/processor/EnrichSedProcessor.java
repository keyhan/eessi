package no.nav.eux.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Picking out the SED from the incoming payload. Additional information are gathered based on information within the SED.
 * Fields in the SED are then updated with the new information.
 */
public class EnrichSedProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            // Create a JsonDOM
            ObjectMapper jsonMapper = new ObjectMapper();
            String requestJson = exchange.getIn().getBody(String.class);
            JsonNode requestSedDom = jsonMapper.readTree(requestJson);

            // Pick the SED from JsonDOM
            JsonNode responseSedDom = requestSedDom.path("payLoad").path("SED_DATA");

            // Enrich the SED
            // Alters the sex, just to show that an enrichment - a change - has been accomplished
            ObjectNode value = (ObjectNode) responseSedDom.path("A009").path("Person").path("PersonIdentification");
            if (value.get("sex").toString().equals("\"female\"")) {
                System.out.println("\n----------------\nChanged to male\n-----------------");
                value.put("sex", "male");
            } else {
                System.out.println("\n----------------\nChanged to female\n-----------------");
                value.put("sex", "female");
            }

            // Take the altered SED further
            exchange.getOut().setBody(jsonMapper.writeValueAsString(responseSedDom));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        } catch (Exception e) {
            System.out.println("===> EnrichSedProcessor: ERRRRRRRROR - Something went wrong...");
        }
    }
}
