package org.jenkinsci.plugins;

import com.surenpi.jenkins.phoenix.steps.HttpStep;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

import static org.junit.Assert.assertEquals;

public class HttpStepTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    @WithoutJenkins
    public void basic() {
        assertEquals("http", new HttpStep.DescriptorImpl().getFunctionName());
    }

    @Test
//    @Ignore
    public void test() throws Exception {
        WorkflowJob wfJob = r.jenkins.createProject(WorkflowJob.class, "test");

        String rootUrl = r.jenkins.getRootUrl();

        wfJob.setDefinition(new CpsFlowDefinition("http url: \"" + rootUrl + "\"",  true));

        r.buildAndAssertSuccess(wfJob);
    }
}
