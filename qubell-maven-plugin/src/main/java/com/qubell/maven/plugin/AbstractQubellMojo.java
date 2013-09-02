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


import com.qubell.client.ObjectParser;
import com.qubell.client.exceptions.QubellServiceException;
import com.qubell.client.exceptions.ResourceBusyException;
import com.qubell.client.exceptions.ServiceUnavailableException;
import com.qubell.client.ws.api.ApplicationsApi;
import com.qubell.client.ws.api.InstancesApi;
import com.qubell.client.ws.api.OrganizationsApi;
import com.qubell.client.ws.model.Instance;
import com.qubell.client.ws.model.InstanceStatus;
import com.qubell.client.ws.model.Workflow;
import com.qubell.client.ws.model.WorkflowStep;
import com.qubell.maven.plugin.commands.GetInstanceStatusCommand;
import com.qubell.maven.plugin.commands.QubellApiCommand;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Abstract base type for Qubell specific MOJOs
 *
 * @author Alex Krupnov
 */
public abstract class AbstractQubellMojo extends AbstractMojo {
    protected static final String INSTANCE_ID_KEY = "qubell.instance.id";
    public static final String PARAMETERS_NAME = "parameters";

    /**
     * Custom parameters for the execution, represents a raw JSON map. If supplied, overrides XML <code>parameters</code>
     * Example <code>{
     * "paramName" : "paramValue"
     * }</code>
     */
    @Parameter(required = false, property = "parametersJson")
    protected String parametersJson;

    /**
     * Custom parameters in XML format
     * Example <code>
     * &lt;paramName&gt;paramValue&lt;/paramName&gt;
     *     &lt;nestedParam&gt;
     *     &lt;subKey&gt;subValue&lt;/subKey&gt;
     * &lt;/nestedParam&gt;
     * </code>
     */
    @Parameter(required = false, property = "parameters")
    protected TreeMap<String, Object> parameters;

    /**
     * Path for file where instance output parametersJson will be saved
     */
    @Parameter(required = false, property = "outputRelativePath")
    protected String outputRelativePath;

    /**
     * Username for API<br/>
     * <b>Note:</b> cannot be OpenID username (such as Google account)
     * This value gets overridden if qubell.global.server pointing to valid server entry in global/user settings
     *
     */
    @Parameter(required = false, property = "apiUsername")
    private String apiUsername;

    /**
     * API password
     * This value gets overridden if qubell.global.server pointing to valid server entry in global/user settings
     * @required
     */
    @Parameter(required = false, property = "apiPassword")
    private String apiPassword;

    /**
     * Base URL for Qubell API including API version<br/>
     * example: https://express.qubell.com/
     */
    @Parameter(required = true, property = "apiURL")
    private URL apiURL;

    /**
     * Whether SSL certificate check has to be bypassed. Use this option for self-signed certificates.
     */
    @Parameter(required = false, defaultValue = "false", property = "bypassSSLCheck")
    private Boolean bypassSSLCheck = false;

    /**
     * Interval for instance status polling. Value is specified in seconds.
     */
    @Parameter(required = false, defaultValue = "5", property = "statusPollingInterval")
    private Integer statusPollingInterval;

    /**
     * Wait timeout for instance, turning into target status
     */
    @Parameter(required = false, defaultValue = "60", property = "statusWaitTimeout")
    private Integer statusWaitTimeout;

    /**
     * If set to true, API request/response pairs will be logged into standard out
     */
    @Parameter(required = false, defaultValue = "false", property = "logApiPayload")
    private Boolean logApiPayload;

    /**
     * Current maven project, used to store values between MOJOs
     */
    @Parameter(required = false, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(required = false, defaultValue = "${session}")
    private MavenSession session;

    /**
     * Current plugin MOJO execution information
     */
    @Parameter(required = false, defaultValue = "${mojoExecution}")
    private MojoExecution mojoExecution;

    /**
     * Timeout (in seconds) for retrying API call when service is busy or unavailable
     */
    @Parameter(required = false, defaultValue = "10", property = "retryTimeout")
    private Integer retryTimeout;

    /**
     * Number of attempts for retrying API call when service is busy or unavailable
     */
    @Parameter(required = false, defaultValue = "5", property = "retryAttempts")
    private Integer retryAttempts;

    private String expectedStatus;

    /**
     * Maven settings
     */
    @Parameter(required = false, defaultValue = "${settings}")
    private Settings settings;

    @Parameter(required = false, defaultValue = "${qubell.global.server}")
    private String serverId;

    /**
     * Initializes instance with expected status
     *
     * @param expectedStatus status which is expected for successful MOJO execution
     */
    protected AbstractQubellMojo(String expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    /**
     * Builds a configuration object based on MOJO settings
     *
     * @return a config object
     */
    protected Configuration getConfiguration() {
        getLog().debug("Trying to get global server settings for  id " + serverId);

        Server server = getServer();
        if(server == null){
            getLog().debug("No server configuration found");
            return new Configuration(apiURL, apiUsername, apiPassword, bypassSSLCheck, statusPollingInterval, statusWaitTimeout, logApiPayload, retryTimeout, retryAttempts);
        }

        return new Configuration(apiURL, server.getUsername(), server.getPassword(), bypassSSLCheck, statusPollingInterval, statusWaitTimeout, logApiPayload, retryTimeout, retryAttempts);
    }

    /**
     * Outputs debug information for mojo
     *
     * @throws MojoExecutionException when values could not be read
     */
    protected void showDebugInfo() throws MojoExecutionException {
        Configuration config = getConfiguration();

        getLog().debug(String.format("Configuration - Username %s, Password %s, URI %s, Status polling %s, Timeout %s",
                config.getApiUsername(),
                config.getApiPassword(),
                config.getApiURL().toString(),
                config.getStatusPollingInterval().toString(),
                config.getStatusWaitTimeout().toString()));

    }

    /**
     * Manually parses configuration XML of {@link #parameters} and returns parsed map
     * @return parsed map or null
     */
    private Map<String, Object> getXmlParameters() {
        if (mojoExecution != null && mojoExecution.getConfiguration() != null) {
            Xpp3Dom paramsDoc = mojoExecution.getConfiguration().getChild(PARAMETERS_NAME);

            if (paramsDoc != null) {
                getLog().debug("Custom XML params " + paramsDoc.toString());
                return ObjectParser.parseXmlMap(paramsDoc.toString().replace(String.format("${%s}", PARAMETERS_NAME), ""));
            }
        }

        return null;
    }

    /**
     * Waits for instance to turn into desired status or timeout to expire
     *
     * @param instance instance to check
     * @throws MojoExecutionException when API throws an error
     */
    protected void waitForInstanceStatus(Instance instance) throws MojoExecutionException {
        Integer timeout = getConfiguration().getStatusWaitTimeout();

        logMessage("Waiting for instance status %s with timeout of %d seconds", expectedStatus, timeout);

        StopWatch sw = new StopWatch();
        sw.start();

        int attempt = 0;

        while (true) {

            attempt++;
            logMessage("Attempt #%d", attempt);
            if (sw.getTime() >= timeout * 1000) {
                logMessage("Timeout exceeded");
                throw new MojoExecutionException("Timeout exceeded");
            }

            InstanceStatus instanceStatus = getInstanceStatus(instance);

            if (instanceStatus.getStatus().equals(expectedStatus)) {
                return;
            }

            try {
                Thread.sleep(getConfiguration().getStatusPollingInterval() * 1000);
            } catch (InterruptedException e) {
                getLog().info("Wait interrupted");
                throw new MojoExecutionException("Flow interrupted on status wait");
            }

        }
    }

    /**
     * Returns status of the instance
     *
     * @param instance instance to query
     * @return status of the instances
     * @throws MojoExecutionException when API throws an error
     */
    private InstanceStatus getInstanceStatus(Instance instance) throws MojoExecutionException {
        InstanceStatus status = null;
        try {
            status = runCommand(new GetInstanceStatusCommand(getInstancesApi(), instance.getId()));
        } catch (QubellServiceException e) {
            getLog().error("Unable to get instance status", e);
            throw new MojoExecutionException("Unable to get instance status", e);
        }

        logMessage("Instance status %s", status.getStatus());

        Workflow currentWorkflow = status.getWorkflow();
        if (currentWorkflow != null) {
            logMessage("Current workflow %s is in status %s", currentWorkflow.getName(), currentWorkflow.getStatus());
            if (currentWorkflow.getSteps() != null && currentWorkflow.getSteps().size() > 0) {
                logMessage("Workflow steps");
                for (WorkflowStep step : currentWorkflow.getSteps()) {
                    logMessage("Step: %s, Status %s, complete: %d percent", step.getName(), step.getStatus(), step.getPercentComplete());
                }
            }

            Map<String, Object> returnValues = (Map<String, Object>) status.getReturnValues();

        }
        if (!StringUtils.isBlank(status.getErrorMessage())) {
            logMessage("instance status returned error %s", status.getErrorMessage());
        }

        return status;
    }

    /**
     * Saves instance return values into file, if {@link #outputRelativePath}
     *
     * @param instance instance to get return values from
     * @throws MojoExecutionException when API throws and error
     */
    protected void saveReturnValues(Instance instance) throws MojoExecutionException {
        if (StringUtils.isEmpty(outputRelativePath)) {
            logMessage("No output path specified, skipping return values save");
            return;
        }

        logMessage("Saving output data to file %s", outputRelativePath);

        InstanceStatus status = getInstanceStatus(instance);

        Map<String, Object> returnValues = (Map<String, Object>) status.getReturnValues();

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("instanceId", status.getId());
        resultMap.put("applicationId", status.getApplicationId());
        resultMap.put("status", status.getStatus());

        if (returnValues != null && returnValues.size() > 0) {
            logMessage("Instance contains %d return values", returnValues.size());
            logMessage("Return values dump: \n %s", ObjectParser.serializeToJson(returnValues));

            resultMap.put("returnValues", returnValues);
        }

        String outputContents = ObjectParser.serializeToJson(resultMap);
        writeToFile(outputContents, outputRelativePath);
    }

    private void writeToFile(String contents, String outputFilePath) throws MojoExecutionException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outputFilePath);
            IOUtils.write(contents, fileOutputStream);
        } catch (IOException e) {
            getLog().error("Unable to write output file", e);
            throw new MojoExecutionException("Unable to write output file", e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    getLog().error("Unable to close the output file, ignoring");
                }
            }
        }
    }

    /**
     * Logs a message with info level to maven log
     *
     * @param message message to write with format placeholders
     * @param args    custom arguments
     */
    protected void logMessage(String message, Object... args) {
        getLog().info(String.format(message, args));
    }

    /**
     * Application API handle
     *
     * @return instance of Applications API
     */
    protected ApplicationsApi getApplicationsApi() {
        return new ApplicationsApi(getConfiguration().toApiClientConfiguration());
    }

    /**
     * Organization API handle
     *
     * @return instance of Organizations API
     */
    protected OrganizationsApi getOrganizationsApi() {
        return new OrganizationsApi(getConfiguration().toApiClientConfiguration());
    }

    /**
     * Instance API handle
     *
     * @return instance of Instance API
     */
    protected InstancesApi getInstancesApi() {
        return new InstancesApi(getConfiguration().toApiClientConfiguration());
    }

    /**
     * Current maven project
     *
     * @return maven project reference
     */
    protected MavenProject getProject() {
        return project;
    }

    /**
     * Attempts to get XML parameters first, if not set attempts to parse json parameters, otherwise returns empty map
     *
     * @return parameters map or empty map
     */
    protected Map<String, Object> getCustomParameters() {
        Map<String, Object> returnValue = getJsonParameters();
        if (returnValue == null) {
            returnValue =  getXmlParameters();
        }

        if (returnValue == null) {
            returnValue = new HashMap<String, Object>();
        }

        return returnValue;
    }

    /**
     * Parses custom parametersJson field into map of string to object.
     *
     * @return parsed map or null
     */
    private Map<String, Object> getJsonParameters() {
        Map<String, Object> customParams = ObjectParser.parseJsonMap(parametersJson);
        if (customParams == null) {
            getLog().warn("Unable to parse custom parameters json, ignoring. Values was:" + parametersJson);
        }
        return customParams;
    }

    protected <TReturn, TCommand extends QubellApiCommand<TReturn>> TReturn runCommand(TCommand command) throws QubellServiceException, MojoExecutionException {
        getLog().debug("Running command " + command);

        Integer attempt = 0;
        Configuration configuration = getConfiguration();

        while (true) {

            try {
                attempt++;
                return command.execute();
            } catch (ServiceUnavailableException sae) {
                getLog().error("Service is unavailable");
                if (attempt >= configuration.getRetryAttempts()) {
                    getLog().error("Failing after attempt #" + attempt);
                    throw sae;
                }
            } catch (ResourceBusyException rbe) {
                getLog().error("Resource is busy");
                if (attempt >= configuration.getRetryAttempts()) {
                    getLog().error("Failing after attempt #" + attempt);
                    throw rbe;
                }
            }
            Random r = new Random();
            Integer waitPeriod = r.nextInt(configuration.getRetryTimeout()) + 1;

            try {
                logMessage("Waiting %s seconds until next retry", waitPeriod);
                Thread.sleep(waitPeriod * 1000);
            } catch (InterruptedException e) {
                getLog().info("Wait interrupted");
                throw new MojoExecutionException("Flow interrupted on status wait");
            }

            logMessage("Retrying");

        }
    }

    /**
     * Get server with given id
     *
     * @return server or null if none matching
     */
    protected Server getServer() {

        if (settings == null || StringUtils.isEmpty(serverId))
            return null;

        List<Server> servers = settings.getServers();
        if (servers == null || servers.isEmpty())
            return null;

        for (Server server : servers)
            if (serverId.equals(server.getId()))
                return server;
        return null;
    }
}