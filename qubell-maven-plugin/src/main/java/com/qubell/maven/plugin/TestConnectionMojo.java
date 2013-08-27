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

import com.qubell.client.exceptions.InvalidCredentialsException;
import com.qubell.client.exceptions.ObjectNotFoundException;
import com.qubell.client.exceptions.QubellServiceException;
import com.sun.jersey.api.client.ClientHandlerException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import javax.net.ssl.SSLProtocolException;

/**
 * Verfies connection settings by attempting to get list of Qubell organizations for user account
 *
 * @author Alex Krupnov
 */
@Mojo(name = "test-connection")
public class TestConnectionMojo extends AbstractQubellMojo {
    public TestConnectionMojo() {
        super("");
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        showDebugInfo();

        logMessage("Getting list of organizations");
        try {
            getOrganizationsApi().listOrganizations();
        } catch (InvalidCredentialsException ice) {
            getLog().error("Unable to connect - invalid credentials", ice);
            throw new MojoExecutionException("Unable to connect - invalid credentials", ice);
        } catch (ObjectNotFoundException onfe) {
            getLog().error("Unable to connect - check if base API path is valid", onfe);
            throw new MojoExecutionException("Unable to connect - check if base API path is valid", onfe);
        } catch (QubellServiceException e) {
            getLog().error("Unable to connect to API - generic error", e);

            throw new MojoExecutionException("Unable to connect to API - generic error", e);
        } catch (ClientHandlerException che) {
            if (che.getCause() != null && che.getCause() instanceof SSLProtocolException) {
                getLog().error("SSL certificate validation failed, please verify whether bypassSSLCheck is true", che);
                throw new MojoExecutionException("SSL certificate validation failed, please whether verify bypassSSLCheck is true", che);
            }
            throw new MojoExecutionException("Connection error", che);
        }

        logMessage("Connection check successful");
    }
}
