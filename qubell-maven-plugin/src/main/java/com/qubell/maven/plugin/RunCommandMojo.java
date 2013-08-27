/*
 * Copyright 2013 Qubell, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qubell.maven.plugin;

import com.qubell.client.exceptions.QubellServiceException;
import com.qubell.client.ws.model.Instance;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Map;

/**
 * Executes a workflow on Qubell instance. Could use either instance,
 * created by launch-instance step or the one, provided in configuration.<br/>
 * <p/>
 * After workflow is executed, waits instance to turn into "Running" state
 *
 * @author Alex Krupnov
 */
@SuppressWarnings("UnusedDeclaration")
@Mojo(name = "run-command")
public class RunCommandMojo extends AbstractQubellMojo {

    /**
     * Instance id if no launch-instance step was executed before
     */
    @Parameter(required = false, property = "instanceId")
    private String instanceId;

    /**
     * Name of command/workflow to execute
     */
    @Parameter(required = true, property = "name")
    private String name;

    /**
     * Initializes MOJO with desired target status
     *
     * @param expectedStatus status to be waiting for
     */
    protected RunCommandMojo(String expectedStatus) {
        super(expectedStatus);
    }

    /**
     * Default constructor
     */
    public RunCommandMojo() {
        this("Running");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        showDebugInfo();

        runCommand();

        logMessage("Finished command execution");
    }

    private void runCommand() throws MojoExecutionException {
        String targetInstanceId = tryGetInstanceId();

        logMessage("Running command %s on instance %s", getCommandName(), targetInstanceId);


        Map<String, Object> customParams = parseCustomParameters();

        try {
            Instance i = getInstancesApi().runWorkflow(targetInstanceId, getCommandName(), customParams);
            waitForInstanceStatus(i);
            saveReturnValues(i);
        } catch (QubellServiceException e) {
            getLog().error("Unable to run workflow", e);
            throw new MojoExecutionException("Unable to run workflow", e);
        }
    }

    /**
     * Attempts to read instance id, first from configuration step, if not found then from project properties,
     * where it could be written by launch-instance step
     *
     * @return instance id
     * @throws MojoExecutionException when failed to read instance
     */
    protected String tryGetInstanceId() throws MojoExecutionException {
        if (!StringUtils.isBlank(instanceId)) {
            return instanceId;
        }

        if (getProject() != null && getProject().getProperties() != null && getProject().getProperties().containsKey(INSTANCE_ID_KEY)) {
            return getProject().getProperties().getProperty(INSTANCE_ID_KEY);
        }

        throw new MojoExecutionException("Unable to get instance id: launch instance was not run and property instanceId was not supplied");

    }

    /**
     * {@inheritDoc}
     */
    protected void showDebugInfo() throws MojoExecutionException {
        super.showDebugInfo();

        getLog().debug(String.format("Instance id %s, Calculated instance id %s, " +
                "Name %s, Parameters %s",
                instanceId,
                tryGetInstanceId(),
                name,
                parameters)
        );
    }

    /**
     * Command name to be executed during MOJ run
     *
     * @return name of the command
     */
    protected String getCommandName() {
        return name;
    }
}

