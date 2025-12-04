package proforma.varproforma.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import proforma.varproforma.Domain;

public class SchemaGenerator {

    static class MySchemaOutputResolver extends SchemaOutputResolver {

        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            File file = new File(suggestedFileName);
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            return result;
        }

    }
    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Domain.domain());
        SchemaOutputResolver sor = new MySchemaOutputResolver();
        jaxbContext.generateSchema(sor);

    }

}
