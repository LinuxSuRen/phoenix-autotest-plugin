package com.surenpi.jenkins.phoenix.steps;

import com.surenpi.jenkins.phoenix.DurableController;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jenkinsci.plugins.durabletask.Controller;
import org.jenkinsci.plugins.durabletask.DurableTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author suren
 */
public class DurableHttpTask extends DurableTask implements Serializable
{
    private final HttpStep httpStep;

    public DurableHttpTask(HttpStep httpStep)
    {
        this.httpStep = httpStep;
    }

    @Override
    public Controller launch(EnvVars env, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException
    {
        String method = httpStep.getMethod();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpUriRequest request = null;
        URI uri = null;

        try
        {
            uri = new URI(httpStep.getUrl());
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        if("post".equalsIgnoreCase(method))
        {
            request = new HttpPost(uri);
        } else {
            request = new HttpGet(uri);
        }

        CloseableHttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if(StringUtils.isNotBlank(httpStep.getResponseBody()) && statusCode == 200) {
            HttpEntity entity = response.getEntity();
            FilePath file = new FilePath(workspace, httpStep.getResponseBody());


            try(InputStream input = entity.getContent()) {
                file.copyFrom(input);

                listener.getLogger().println("write response file done.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            listener.getLogger().println(String.format("statusCode is %d", statusCode));
        }

        return new DurableController();
    }
}