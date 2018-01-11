package com.surenpi.jenkins.phoenix.steps;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import hudson.scm.SCMRevisionState;
import hudson.slaves.WorkspaceList;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import java.io.File;
import java.io.IOException;

/**
 * @author suren
 */
class WithSCMExecution extends StepExecution
{
    private final WithSCMStep withSCMStep;
    private final FilePath ws;

    public WithSCMExecution(StepContext context, WithSCMStep withSCMStep) throws IOException, InterruptedException
    {
        super(context);
        this.withSCMStep = withSCMStep;

        ws = getContext().get(FilePath.class);;
    }

    @Override
    public boolean start() throws Exception
    {
        FilePath tempBinDir = WorkspaceList.tempDir(ws);

        scmCheckout(tempBinDir);

        getContext().newBodyInvoker().withCallback(new SCMCallback(tempBinDir)).start();

        return false;
    }

    private void scmCheckout(FilePath tempBinDir) throws IOException, InterruptedException
    {
        SCM scm = withSCMStep.getScm();

        Run<?, ?> run = getContext().get(Run.class);
        SCMRevisionState baseline = null;
        File changelogFile = null;
        Launcher launcher = getContext().get(Launcher.class);
        TaskListener taskListener = getContext().get(TaskListener.class);

        scm.checkout(run, launcher, tempBinDir, taskListener, changelogFile, baseline);
    }

    class SCMCallback extends BodyExecutionCallback.TailCall
    {
        private final FilePath tempBinDir;

        public SCMCallback(FilePath tempBinDir)
        {
            this.tempBinDir = tempBinDir;
        }

        @Override
        protected void finished(StepContext context) throws Exception
        {
            tempBinDir.deleteRecursive();
        }
    }
}