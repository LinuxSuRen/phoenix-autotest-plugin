package com.surenpi.jenkins.phoenix.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author suren
 */
public class XmlNodeResult implements Serializable
{
    private String text;
    private String xpath;
    private Map<String, String> attrMap;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getXpath()
    {
        return xpath;
    }

    public void setXpath(String xpath)
    {
        this.xpath = xpath;
    }

    public Map<String, String> getAttrMap()
    {
        return attrMap;
    }

    public void setAttrMap(Map<String, String> attrMap)
    {
        this.attrMap = attrMap;
    }
}