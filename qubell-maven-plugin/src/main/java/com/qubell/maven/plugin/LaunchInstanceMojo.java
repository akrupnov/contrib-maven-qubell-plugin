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
import com.qubell.client.ws.model.InstanceSpecification;
import com.qubell.maven.plugin.commands.LaunchInstanceCommand;
import com.qubell.maven.plugin.commands.UpdateManifestCommand;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Launches Qubell application instance on given environment.
 * Optionally supports manifest update by specified yaml file.
 * Expects launched instance to turn into Running state.
 * After instance is launched, id is saved in Maven project so it can be read by successor steps
 *
 * @author Alex Krupnov
 */
@Mojo(name = "launch-instance")
public class LaunchInstanceMojo extends AbstractQubellMojo {

    /**
     * Application id to be launched
     */
    @Parameter(required = true, property = "applicationId")
    private String applicationId;

    /**
     * Environment id where instance will be launched
     */
    @Parameter(required = false, property = "environmentId")
    private String environmentId;

    /**
     * Relative path to application manifest. If not supplied, manifest update API is not called
     */
    @Parameter(required = false, property = "manifestRelativePath")
    private String manifestRelativePath;

    /**
     * Instance destroy interval in milliseconds. Use -1 if you don't want to destroy th instnace
     */
    @Parameter(required = false, property = "destroyInterval", defaultValue = "60000")
    private Long destroyInterval;

    /**
     * Basic constructor
     */
    public LaunchInstanceMojo() {
        super("Running");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        showDebugInfo();

        logMessage("Launching an instance of application %s", applicationId);

        int version = updateManifest();

        Instance i = launchInstance(version);

        waitForInstanceStatus(i);

        saveReturnValues(i);

        persistInstanceId(i.getId());

        logMessage("Completed launch instance");
    }

    private void persistInstanceId(String id) {
        if (getProject() != null && getProject().getProperties() != null) {
            getProject().getProperties().setProperty(INSTANCE_ID_KEY, id);
        }
    }

    private Instance launchInstance(int version) throws MojoExecutionException {
        InstanceSpecification instanceSpecification = new InstanceSpecification();
        instanceSpecification.setDestroyInterval(destroyInterval);
        instanceSpecification.setParameters(parseCustomParameters());

        if (version > 0) {
            instanceSpecification.setVersion(version);
        }
        if (!StringUtils.isBlank(environmentId)) {
            instanceSpecification.setEnvironmentId(environmentId);
        }


        try {
            Instance i = runCommand(new LaunchInstanceCommand(getApplicationsApi(), applicationId, instanceSpecification));
            logMessage("Launched instance %s", i.getId());

            return i;
        } catch (QubellServiceException e) {
            getLog().error("Unable to launch instance", e);
            throw new MojoExecutionException("Unable to launch instance", e);
        }
    }

    private Integer updateManifest() throws MojoExecutionException {
        if (!StringUtils.isBlank(manifestRelativePath)) {
            String manifest = readManifest();
            getLog().debug("Manifest content: " + manifest);
            try {
                Integer version = runCommand(new UpdateManifestCommand(getApplicationsApi(), applicationId, manifest));

                logMessage("Updated manifest, new version is %s", version);

                return version;
            } catch (QubellServiceException e) {
                getLog().error("Unable to update manifest ", e);
                throw new MojoExecutionException("Unable to update manifest", e);
            }

        } else {
            logMessage("No manifest relative path supplied, skipping manifest update");
            return 0;
        }
    }

    private String readManifest() throws MojoExecutionException {
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(manifestRelativePath);
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            getLog().error("Unable to read manifest", e);
            throw new MojoExecutionException("Unable to read manifest", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                getLog().error("Unable to close manifest stream, ignoring error", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void showDebugInfo() throws MojoExecutionException {
        super.showDebugInfo();

        getLog().debug(String.format("AppId: %s, Env id: %s, Extra params: %s, " +
                "Manifest relative path: %s, Output path %s, Destroy interval %s",
                applicationId,
                environmentId,
                parameters,
                manifestRelativePath,
                outputRelativePath,
                destroyInterval

        )
        );
    }
}
