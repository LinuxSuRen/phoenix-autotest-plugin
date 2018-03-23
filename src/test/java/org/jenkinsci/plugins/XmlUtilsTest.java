package org.jenkinsci.plugins;

import com.surenpi.jenkins.phoenix.model.XmlNodeResult;
import com.surenpi.jenkins.phoenix.utils.XmlUtils;
import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class XmlUtilsTest
{
    @Test
    public void parse() throws DocumentException
    {
        File xmlFile = new File("/home/surenpi/IdeaProjects/Github/phoenix.plugin/pom.xml");
        Map<String, String> list = new HashMap<>();
        list.put("version", "/*[name()='project']/*[name()='version']");
        list.put("packaging", "/*[name()='project']/*[name()='packaging']");

        Map<String, XmlNodeResult> result = XmlUtils.parse(xmlFile, list);
        Assert.assertTrue(result.size() == 2);

        for(String key : result.keySet())
        {
            Assert.assertNotNull(result.get(key).getText());
        }
    }
}