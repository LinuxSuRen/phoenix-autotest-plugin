package com.surenpi.jenkins.phoenix.steps;

import com.surenpi.jenkins.phoenix.model.XmlNodeResult;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author suren
 */
public class WriteXmlStep extends Step implements Serializable
{
    private final String xmlFile;
    private final Map<String, XmlNodeResult> nodeMap;

    @DataBoundConstructor
    public WriteXmlStep(String xmlFile, Map<String, XmlNodeResult> nodeMap)
    {
        this.xmlFile = xmlFile;
        this.nodeMap = nodeMap;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new Execution(this, context);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor
    {

        @Override
        public Set<? extends Class<?>> getRequiredContext()
        {
            return Collections.singleton(TaskListener.class);
        }

        @Override
        public String getFunctionName()
        {
            return "writeXml";
        }
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<Void>
    {
        WriteXmlStep writeXmlStep;

        protected Execution(WriteXmlStep writeXmlStep, @Nonnull StepContext context)
        {
            super(context);
            this.writeXmlStep = writeXmlStep;
        }

        @Override
        protected Void run() throws Exception
        {
            FilePath ws = getContext().get(FilePath.class);
            FilePath xmlPath = ws.child(this.writeXmlStep.getXmlFile());
            if(xmlPath.isDirectory() || !xmlPath.exists() || this.writeXmlStep.getNodeMap() == null)
            {
                return null;
            }

            SAXReader reader = new SAXReader();
            Document doc = reader.read(xmlPath.read());

            for(String key : this.writeXmlStep.getNodeMap().keySet())
            {
                XmlNodeResult node = this.writeXmlStep.getNodeMap().get(key);

                Element ele = (Element) doc.selectSingleNode(node.getXpath());

                ele.setText(node.getText());

                for(String attr : node.getAttrMap().keySet())
                {
                    ele.addAttribute(attr, node.getAttrMap().get(attr));
                }
            }

            try(OutputStream output = xmlPath.write())
            {
                XMLWriter writer = new XMLWriter(output);
                writer.write(doc);
            }

            return null;
        }
    }

    public String getXmlFile()
    {
        return xmlFile;
    }

    public Map<String, XmlNodeResult> getNodeMap()
    {
        return nodeMap;
    }
}