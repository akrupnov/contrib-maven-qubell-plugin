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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Destroys the Qubell application instance and waits instance to be in "Destroyed" state
 * Could use either instance,
 * created by launch-instance step or the one, provided in configuration.<br/>
 *
 * @author Alex Krupnov
 */
@Mojo(name = "destroy-instance")
public class DestroyInstanceMojo extends RunCommandMojo {

    @Parameter(required = false, property = "name", defaultValue = "destroy")
    private String name;

    public DestroyInstanceMojo() {
        super("Destroyed");
    }

    /**
     * Always returns "destroy"
     *
     * @return fixed destroy workflow name
     */
    @Override
    protected String getCommandName() {
        return "destroy";
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        logMessage("Destroying instance");

        super.execute();

        logMessage("Destroyed instance");
    }

}
