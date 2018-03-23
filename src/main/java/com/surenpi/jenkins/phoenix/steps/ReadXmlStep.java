package com.surenpi.jenkins.phoenix.steps;

import com.surenpi.jenkins.phoenix.utils.XmlUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.scriptsecurity.sandbox.Whitelist;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author suren
 */
public class ReadXmlStep extends Step implements Serializable
{
    private static final String SUREN_PKG = "com.surenpi.jenkins.phoenix.model";
    private final String xmlFile;
    private final Map<String, String> nodeMap;

    @DataBoundConstructor
    public ReadXmlStep(String xmlFile, Map<String, String> nodeMap)
    {
        this.xmlFile = xmlFile;
        this.nodeMap = nodeMap;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new ReadXmlStep.Execution(this, context);
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
            return "readXml";
        }
    }

    public static class Execution extends SynchronousNonBlockingStepExecution
    {
        private final ReadXmlStep readXmlStep;

        public Execution(@Nonnull ReadXmlStep readXmlStep, @Nonnull StepContext context)
        {
            super(context);

            this.readXmlStep = readXmlStep;
        }

        @Override
        protected Object run() throws Exception
        {
            FilePath ws = getContext().get(FilePath.class);
            PrintStream logger = getContext().get(TaskListener.class).getLogger();
            FilePath xmlPath = ws.child(this.readXmlStep.getXmlFile());

            logger.println(xmlPath);

            if(xmlPath.isDirectory() || !xmlPath.exists() || this.readXmlStep.getNodeMap() == null)
            {
                return null;
            }

            try(InputStream input = xmlPath.read())
            {
                return XmlUtils.parse(input, readXmlStep.getNodeMap());
            }
        }
    }

    /**
     * Whitelists all non static setters, getters and constructors in the package com.surenpi.jenkins.phoenix.model
     */
    @Extension
    public static class WhiteLister extends Whitelist
    {
        public WhiteLister() {
            super();
        }

        @Override
        public boolean permitsMethod(Method method, Object receiver, Object[] args) {
            if (receiver == null) {
                return false;
            }

            final Class<?> aClass = receiver.getClass();
            final Package aPackage = aClass.getPackage();

            if(aPackage == null) {
                return false;
            }

            final String name = aPackage.getName();
            return name.equals(SUREN_PKG)
                    && (   method.getName().startsWith("set")
                    || method.getName().startsWith("get")
                    || method.getName().startsWith("add")
                    || method.getName().startsWith("find") //in case we add some helpers to the meta object
            );
        }

        @Override
        public boolean permitsConstructor(@Nonnull Constructor<?> constructor, @Nonnull Object[] args) {
            if (constructor == null) {
                return false;
            }

            final Package aPackage = constructor.getDeclaringClass().getPackage();

            if (aPackage == null) {
                return false;
            }

            return aPackage.getName().equals(SUREN_PKG);
        }

        @Override
        public boolean permitsStaticMethod(@Nonnull Method method, @Nonnull Object[] args) {
            return false;
        }

        @Override
        public boolean permitsFieldGet(@Nonnull Field field, @Nonnull Object receiver) {

            if (receiver == null) {
                return false;
            }


            final Package aPackage = receiver.getClass().getPackage();
            if (aPackage == null) {
                return false;
            }

            return aPackage.getName().equals(SUREN_PKG);
        }

        @Override
        public boolean permitsFieldSet(@Nonnull Field field, @Nonnull Object receiver, Object value) {

            if (receiver == null) {
                return false;
            }

            final Package aPackage = receiver.getClass().getPackage();
            if (aPackage == null) {
                return false;
            }

            return aPackage.getName().equals(SUREN_PKG);
        }

        @Override
        public boolean permitsStaticFieldGet(@Nonnull Field field) {
            return false;
        }

        @Override
        public boolean permitsStaticFieldSet(@Nonnull Field field, Object value) {
            return false;
        }
    }

    public String getXmlFile()
    {
        return xmlFile;
    }

    public Map<String, String> getNodeMap()
    {
        return nodeMap;
    }
}