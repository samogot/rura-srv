package ru.ruranobe.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ConfigurationManager
{

    public static ApplicationContext getApplicationContext(String configurationFilePath, String xsdValidatorFilePath)
    {
        if (applicationContext != null)
        {
            return applicationContext;
        }

        Document document = parseXml(configurationFilePath, xsdValidatorFilePath);
        applicationContext = new ApplicationContext(document);
        return applicationContext;
    }

    private static Document parseXml(String configurationFilePath, String xsdValidatorFilePath)
    {
        try
        {
            InputStream is;
            try
            {
                is = new FileInputStream(configurationFilePath);
            }
            catch (FileNotFoundException ex)
            {
                throw new RuntimeException("Config file " + configurationFilePath + " is absent on server:" + ex);
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();

            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            File xsdValidator = new File(xsdValidatorFilePath);
            if (!xsdValidator.isFile())
            {
                throw new RuntimeException("Config file " + xsdValidatorFilePath + " is absent on server:");
            }

            SAXReader reader;
            try
            {
                Schema schema = schemaFactory.newSchema(xsdValidator);
                factory.setSchema(schema);
                SAXParser parser = factory.newSAXParser();

                reader = new SAXReader(parser.getXMLReader());
            }
            catch (ParserConfigurationException ex)
            {
                throw new RuntimeException(ex);
            }
            catch (SAXException ex)
            {
                throw new RuntimeException(ex);
            }
            reader.setValidation(false);
            return reader.read(is);
        }
        catch (DocumentException ex)
        {
            throw new RuntimeException("Bad configuration path: "+configurationFilePath, ex);
        }
    }

    private static ApplicationContext applicationContext;
}
