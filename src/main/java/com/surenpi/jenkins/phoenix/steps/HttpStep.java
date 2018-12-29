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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
 * @author suren
 */
public class HttpStep extends DurableStep implements Serializable
{
    private static final Logger LOGGER = Logger.getLogger(HttpStep.class.getName());

    private final String url;
    private String method;
    private String responseBody;

    @DataBoundConstructor
    public HttpStep(String url)
    {
        this.url = url;
    }

    @Override
    protected DurableTask task()
    {
        return new DurableHttpTask(this);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new DurableExecution(context, this);
    }

    @Extension
    public static final class DescriptorImpl extends DurableTaskStepDescriptor
    {
        @Override
        public String getFunctionName()
        {
            return "http";
        }

        @Override
        public String getDisplayName()
        {
            return "HttpStep";
        }

        public ListBoxModel doFillMethodItems() {
            ListBoxModel listBoxModel = new ListBoxModel();
            listBoxModel.add(HttpPost.METHOD_NAME);
            listBoxModel.add(HttpGet.METHOD_NAME);
            return listBoxModel;
        }
    }

    public String getUrl()
    {
        return url;
    }

    public String getMethod()
    {
        return method;
    }

    @DataBoundSetter
    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getResponseBody() {
        return responseBody;
    }

    @DataBoundSetter
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}