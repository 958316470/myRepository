package com.nutch.util.domain;

import org.apache.hadoop.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.nutch.util.domain.TopLevelDomain.Type;
import com.nutch.util.domain.DomainSuffix.Status;
import java.io.IOException;
import java.io.InputStream;

public class DomainSuffixesReader {
    private static final Logger LOG = LoggerFactory.getLogger(DomainSuffixesReader.class);
    void read(DomainSuffixes tldEntries, InputStream input) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(input));
            Element root = document.getDocumentElement();
            if (root != null && root.getTagName().equals("domain")) {
                Element tlds = (Element) root.getElementsByTagName("tlds").item(0);
                Element suffixes = (Element) root.getElementsByTagName("suffixes").item(0);
                readTLDS(tldEntries, (Element) tlds.getElementsByTagName("itlds").item(0));
                readTLDS(tldEntries, (Element) tlds.getElementsByTagName("gtlds").item(0));
                readTLDS(tldEntries, (Element) tlds.getElementsByTagName("cctlds").item(0));
                readSuffixes(tldEntries, suffixes);
            } else {
                throw new IOException("xml file is not valid");
            }
        }catch (ParserConfigurationException e) {
            LOG.warn(StringUtils.stringifyException(e));
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            LOG.warn(StringUtils.stringifyException(e));
            throw new IOException(e.getMessage());
        }
    }
    void readTLDS(DomainSuffixes tldEntries, Element el) {
        NodeList children = el.getElementsByTagName("tld");
        for (int i = 0; i<children.getLength(); i++) {
            tldEntries.addDomainSuffix(readGTLD((Element) children.item(i),Type.INFRASTRUCTURE));
        }
    }
    void readGTLDs(DomainSuffixes tldEntries, Element el) {
        NodeList children = el.getElementsByTagName("tld");
        for (int i = 0; i<children.getLength(); i++) {
            tldEntries.addDomainSuffix(readGTLD((Element) children.item(i), Type.GENERIC));
        }
    }

    void readCCTLDs(DomainSuffixes tldEntries, Element el) throws IOException{
        NodeList children = el.getElementsByTagName("tld");
        for (int i = 0; i<children.getLength(); i++) {
            tldEntries.addDomainSuffix(readCCTLD((Element) children.item(i)));
        }
    }
    TopLevelDomain readGTLD(Element el, Type type) {
        String domain = el.getAttribute("domain");
        Status status = readStatus(el);
        float boost = readBoost(el);
        return new TopLevelDomain(domain, type, status, boost);
    }

    TopLevelDomain readCCTLD(Element el) throws IOException{
        String domain = el.getAttribute("domain");
        Status status = readStatus(el);
        float boost = readBoost(el);
        String conutryName = readCountryName(el);
        return new TopLevelDomain(domain,status,boost,conutryName);
    }

    Status readStatus(Element el){
        NodeList list = el.getElementsByTagName("status");
        if (list == null || list.getLength() == 0) {
            return DomainSuffix.DEFAULT_STATUS;
        }
        return Status.valueOf(list.item(0).getFirstChild().getNodeValue());
    }

    float readBoost(Element el) {
        NodeList list = el.getElementsByTagName("boost");
        if (list == null || list.getLength() == 0) {
            return DomainSuffix.DEFAULT_BOOST;
        }
        return Float.parseFloat(list.item(0).getFirstChild().getNodeValue());
    }
    String readCountryName(Element el) throws IOException {
        NodeList list = el.getElementsByTagName("country");
        if(list == null  || list.getLength() == 0) {
            throw new IOException("Country name should be given");
        }
        return list.item(0).getNodeValue();
    }

    void readSuffixes(DomainSuffixes tldEntries, Element el) {
        NodeList children = el.getElementsByTagName("suffix");
        for (int i = 0; i<children.getLength();i++) {
            tldEntries.addDomainSuffix(readSuffix((Element) children.item(i)));
        }
    }
    DomainSuffix readSuffix(Element el) {
        String domain = el.getAttribute("domain");
        Status status = readStatus(el);
        float boost = readBoost(el);
        return new DomainSuffix(domain, status, boost);
    }
}
