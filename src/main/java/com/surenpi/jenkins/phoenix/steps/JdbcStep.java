package com.surenpi.jenkins.phoenix.steps;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.surenpi.jenkins.phoenix.DurableExecution;
import com.surenpi.jenkins.phoenix.DurableStep;
import com.surenpi.jenkins.phoenix.DurableTaskStepDescriptor;
import hudson.Extension;
import hudson.model.FreeStyleProject;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.durabletask.DurableTask;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Execute sql script through jdbc.
 * @author suren
 */
public class JdbcStep extends DurableStep implements Serializable
{
    private static final Logger LOGGER = Logger.getLogger(JdbcStep.class.getName());

    private String url;
    private String credentialsId;
    private String sql;
    private String encoding = "utf-8";

    private boolean isText = false;

    @DataBoundConstructor
    public JdbcStep(String url, String credentialsId)
    {
        this.url = url;
        this.credentialsId = credentialsId;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new DurableExecution(context, this);
    }

    @Override
    public DurableTask task()
    {
        return new DurableJdbcTask(this);
    }

    @Extension
    public static final class DescriptorImpl extends DurableTaskStepDescriptor
    {
        @Override
        public String getFunctionName()
        {
            return "jdbc";
        }

        @Override
        public String getDisplayName()
        {
            return "Execute sql script through jdbc.";
        }

        public ListBoxModel doFillCredentialsIdItems() {
            FreeStyleProject project = new FreeStyleProject(Jenkins.getInstance(), "fake-" + UUID.randomUUID().toString());

            return new StandardListBoxModel().includeEmptyValue()
                    .includeMatchingAs(ACL.SYSTEM, project,
                            StandardUsernameCredentials.class,
                            new ArrayList<DomainRequirement>(),
                            CredentialsMatchers.withScopes(CredentialsScope.GLOBAL));
        }
    }

    public String getUrl()
    {
        return url;
    }

    @DataBoundSetter
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCredentialsId()
    {
        return credentialsId;
    }

    @DataBoundSetter
    public void setCredentialsId(String credentialsId)
    {
        this.credentialsId = credentialsId;
    }

    public String getSql()
    {
        return sql;
    }

    @DataBoundSetter
    public void setSql(String sql)
    {
        this.sql = sql;
    }

    public boolean isText()
    {
        return isText;
    }

    @DataBoundSetter
    public void setText(boolean text)
    {
        isText = text;
    }

    public String getEncoding()
    {
        return encoding;
    }

    @DataBoundSetter
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
}