package org.jenkinsci.plugins;

import com.surenpi.jenkins.phoenix.steps.HttpStep;
import hudson.util.IOUtils;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpStepTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    @WithoutJenkins
    public void basic() {
        assertEquals("http", new HttpStep.DescriptorImpl().getFunctionName());
    }

    @Test
    public void test() throws Exception {
        WorkflowJob wfJob = r.jenkins.createProject(WorkflowJob.class, "test");

        String rootUrl = r.jenkins.getRootUrl();

        assertNotNull("jenkins root url is null", rootUrl);

        String httpGroovy = null;
        try(InputStream input = this.getClass().getResourceAsStream("/http.groovy")){
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            IOUtils.copy(input, data);

            httpGroovy = data.toString();
        }

        assertNotNull("can load the test groovy file", httpGroovy);

        wfJob.setDefinition(createCpsFlowDefinition(httpGroovy.replace("${url}", rootUrl)));
        r.buildAndAssertSuccess(wfJob);

        // test special url cases
        wfJob.setDefinition(createCpsFlowDefinition(httpGroovy.replace("${url}", rootUrl + "?a=1 1")));
        r.buildAndAssertSuccess(wfJob);
    }

    private FlowDefinition createCpsFlowDefinition(String jenkinsfile) {
        return new CpsFlowDefinition(jenkinsfile,  true);
    }
}
