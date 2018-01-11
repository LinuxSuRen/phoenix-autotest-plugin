package com.surenpi.jenkins.phoenix.steps;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.google.common.collect.ImmutableSet;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.FreeStyleProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * This step allow user get config or script files from scm(git,svn).
 * @author suren
 */
public class WithSCMStep extends Step implements Serializable
{
    private final SCM scm;

    @DataBoundConstructor
    public WithSCMStep(SCM scm)
    {
        this.scm = scm;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception
    {
        return new WithSCMExecution(context, this);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor
    {

        @Override
        public Set<? extends Class<?>> getRequiredContext()
        {
            return ImmutableSet.of(TaskListener.class, FilePath.class, Launcher.class, EnvVars.class, Run.class);
        }

        @Override
        public String getFunctionName()
        {
            return "withSCM";
        }

        public ListBoxModel doFillCredentialsIdItems() {
            FreeStyleProject project = new FreeStyleProject(Jenkins.getInstance(), "fake-" + UUID.randomUUID().toString());

            return new StandardListBoxModel().includeEmptyValue()
                    .includeMatchingAs(ACL.SYSTEM, project,
                            StandardUsernameCredentials.class,
                            new ArrayList<DomainRequirement>(),
                            CredentialsMatchers.withScopes(CredentialsScope.GLOBAL));
        }

        @Override
        public boolean takesImplicitBlockArgument()
        {
            return true;
        }
    }

    public SCM getScm()
    {
        return scm;
    }
}