package com.surenpi.jenkins.phoenix;

import hudson.Extension;
import hudson.LocalPluginManager;
import hudson.model.Action;
import hudson.model.Label;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Extension
public class UpdateCenterAction extends TransientActionFactory<LocalPluginManager> implements Action
{
    private LocalPluginManager target;

    @CheckForNull
    @Override
    public String getIconFileName()
    {
        return null;
    }

    @CheckForNull
    @Override
    public String getDisplayName()
    {
        return null;
    }

    @CheckForNull
    @Override
    public String getUrlName()
    {
        return "surenUpdate";
    }

    public void doTest()
    {
        System.out.println(target + "==");
    }

    public Set<Label> doHello()
    {
        return Jenkins.getInstance().getLabels();
    }

    @Override
    public Class<LocalPluginManager> type()
    {
        return LocalPluginManager.class;
    }

    @Override
    public Collection<? extends Action> createFor(@Nonnull LocalPluginManager target)
    {
        try
        {
            UpdateCenterAction instance = getClass().newInstance();
            instance.target = target;

            return Collections.singleton(instance);
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}