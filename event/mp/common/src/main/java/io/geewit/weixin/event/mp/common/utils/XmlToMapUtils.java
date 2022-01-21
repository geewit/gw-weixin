package io.geewit.weixin.event.mp.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by geewit on 2016/7/13.
 */
public class XmlToMapUtils {
    private final static Logger logger = LoggerFactory.getLogger(XmlToMapUtils.class);

    /**
     * 将dom对象转换成hashmap对象(备用函数)
     *
     * @param document
     * @return
     */
    public static Map<String, Object> xmlToMap(Document document) {
        Element root = document.getDocumentElement();
        NodeList iterator = root.getChildNodes();

        Stack<Triple> stack = new Stack<>();
        Integer j = 0;
        Map<String, Object> tmp = new HashMap<>();
        do {
            while (j < iterator.getLength()) {
                Node childNode = iterator.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = childNode.getNodeName();

                    if (childNode.getChildNodes().getLength() == 1) {
                        tmp.put(nodeName, childNode.getTextContent());
                    } else {
                        HashMap<String, Object> cc = new HashMap<>();
                        tmp.put(nodeName, cc);
                        stack.push(new Triple(tmp, iterator, (j + 1)));
                        tmp = cc;
                        j = 0;
                        iterator = childNode.getChildNodes();
                    }
                }
                j++;
            }
            if(stack.isEmpty()) {
                break;
            }
            Triple triple = stack.pop();
            tmp = triple.first;
            iterator = triple.second;
            j = triple.third;
        } while (!stack.isEmpty());

        return tmp;
    }

    public static Map<String, Object> xmlToMap(InputStream inputStream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            return xmlToMap(document);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, Object> xmlToMap(String xml, Charset charset) {
        return xmlToMap(new ByteArrayInputStream(xml.getBytes(charset)));
    }

    private static class Triple {
        Triple(Map<String, Object> first, NodeList second, Integer third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        Map<String, Object> first;
        NodeList second;
        Integer third;
    }
}
