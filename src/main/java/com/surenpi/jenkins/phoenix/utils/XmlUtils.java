package com.surenpi.jenkins.phoenix.utils;

import com.surenpi.jenkins.phoenix.model.XmlNodeResult;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suren
 */
public abstract class XmlUtils
{
    public static Map<String, XmlNodeResult> parse(InputStream input, Map<String, String> nodeMap) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(input);

        Map<String, XmlNodeResult> resultMap = new HashMap<>();
        for(String node : nodeMap.keySet())
        {
            String xpath = nodeMap.get(node);
            Element ele = (Element) doc.selectSingleNode(xpath);
            if(ele == null)
            {
                continue;
            }

            XmlNodeResult result = parse(ele);
            result.setXpath(xpath);
            resultMap.put(node, result);
        }

        return resultMap;
    }

    public static Map<String, XmlNodeResult> parse(File xmlFile, Map<String, String> nodeMap) throws DocumentException, IOException
    {
        try(InputStream input = new FileInputStream(xmlFile))
        {
            return parse(input, nodeMap);
        }
    }

    public static XmlNodeResult parse(Element ele)
    {
        Map<String, String> attrMap = new HashMap<String, String>();
        List attributes = ele.attributes();
        if(attributes != null)
        {
            for(Object a : attributes)
            {
                if(!(a instanceof Attribute))
                {
                    continue;
                }

                Attribute attr = (Attribute) a;

                attrMap.put(attr.getName(), attr.getValue());
            }
        }

        XmlNodeResult xmlNodeResult = new XmlNodeResult();
        xmlNodeResult.setText(ele.getText() != null ? ele.getText().trim() : "");
        xmlNodeResult.setAttrMap(attrMap);

        return xmlNodeResult;
    }
}