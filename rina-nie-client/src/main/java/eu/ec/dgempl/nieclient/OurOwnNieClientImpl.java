package eu.ec.dgempl.nieclient;

import eu.ec.dgempl.nieclient.exception.CaseProcessingException;
import eu.ec.dgempl.nieclient.exception.DocumentProcessingException;
import eu.ec.dgempl.nieclient.model.CaseEventType;
import eu.ec.dgempl.nieclient.model.DocumentEventType;
import eu.ec.dgempl.nieclient.model.PropertyType;
import eu.ec.dgempl.nieclient.net.RestClient;
import eu.ec.dgempl.nieclient.rest.model.ClientCaseEvent;
import eu.ec.dgempl.nieclient.rest.model.ClientDocumentEvent;
import java.util.Map;

/**
 * To use own implementation of the NieClient-interface in Rina do the following:
 * 1: Create a class that implements the NieClient-interface and build a jar-file.
 * 2.1: Stop the EESSI REST API and the EESSI BonitaBPM -services.
 * 2.2: Copy the jar-file file to the C:\EESSI\BonitaBPM\webapps\bonita\WEB-INF\lib folder.
 * 2.3: Start the EESSI REST API and the EESSI BonitaBPM -services.
 * 3: Log in to the admin-module and in the 'NIE Settings'-tab alter the classname to the name of the implementation-class.
 * RINA should now use the the new implementation. Eventually see logs.
 */
public class OurOwnNieClientImpl implements NieClient {
    private static Map<String, String> configuration;
    private static NieClient instance = null;

    private static void setConfiguration(Map<String, String> aConfiguration) {
        configuration = aConfiguration;
    }

    private OurOwnNieClientImpl(Map<String, String> configuration) {
        configuration = configuration;
    }

    public static NieClient instance(Map<String, String> configuration) {
        if(instance == null) {
            instance = new OurOwnNieClientImpl(configuration);
        } else {
            setConfiguration(configuration);
        }

        return instance;
    }

    public boolean hasCaseListener(CaseEventType caseType, String buc, String version) {
        String key = caseType.name() + "_" + buc + "_" + version;
        return configuration.get(key) != null;
    }

    public boolean hasDocumentListener(DocumentEventType documentType, String buc, String version, String document) {
        String key = documentType.name() + "_" + document;
        return configuration.get(key) != null;
    }

    public Map<String, Object> fireCaseEvent(CaseEventType caseType, String buc, String version, Map<PropertyType, Map<String, Object>> payLoad) throws CaseProcessingException {
        ClientCaseEvent restCaseEvent = new ClientCaseEvent();
        restCaseEvent.caseEventType = caseType;
        restCaseEvent.payLoad = payLoad;
        String key = caseType.name() + "_" + buc + "_" + version;
        String url = (String)configuration.get(key);
        return RestClient.postCaseEvent(url, restCaseEvent);
    }

    public Map<String, Object> fireDocumentEvent(DocumentEventType documentEventType, String buc, String version, String document, Map<PropertyType, Map<String, Object>> payLoad) throws DocumentProcessingException {
        ClientDocumentEvent restDocumentEvent = new ClientDocumentEvent();
        restDocumentEvent.documentEventType = documentEventType;
        restDocumentEvent.buc = buc;
        restDocumentEvent.version = version;
        restDocumentEvent.payLoad = payLoad;
        String key = documentEventType.name() + "_" + document;
        String url = (String)configuration.get(key);
        System.out.println("========= Document event has been fired!!!! ===");
        System.out.println("========= Our own nieclient is used ===========");
        System.out.println("========= BUC\n" + buc);
        System.out.println("================================================");
        return RestClient.postDocumentEvent(url, restDocumentEvent);
    }
}
