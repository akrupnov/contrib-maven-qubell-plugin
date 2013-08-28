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

import com.qubell.client.ClientConfiguration;

import java.net.URL;

/**
 * Maven configuration objec
 *
 * @author Alex Krupnov
 */
public class Configuration {
    private final URL apiURL;
    private final String apiUsername;
    private final String apiPassword;
    private final Boolean bypassSSLCheck;
    private final Integer statusPollingInerval;
    private final Integer statusWaitTimeout;
    private final Boolean logApiPayload;
    private final Integer retryTimeout;
    private final Integer retryAttempts;

    /**
     * Initializes the config
     *
     * @param apiURL            {@link #getApiURL()}
     * @param apiUsername       {@link #getApiUsername()}
     * @param apiPassword       {@link #getApiPassword()}
     * @param bypassSSLCheck    {@link #getBypassSSLCheck()}
     * @param pollingInterval   {@link #getStatusPollingInterval()}
     * @param statusWaitTimeout {@link #getStatusWaitTimeout()}
     * @param logApiPayload     specifies whether API payload should be logged
     * @param retryTimeout      {@link #getRetryTimeout()}
     * @param retryAttempts     {@link #getRetryAttempts()}
     */
    public Configuration(URL apiURL, String apiUsername, String apiPassword, Boolean bypassSSLCheck, Integer pollingInterval, Integer statusWaitTimeout, Boolean logApiPayload, Integer retryTimeout, Integer retryAttempts) {
        this.apiURL = apiURL;
        this.apiUsername = apiUsername;
        this.apiPassword = apiPassword;
        this.bypassSSLCheck = bypassSSLCheck;
        this.statusPollingInerval = pollingInterval;
        this.statusWaitTimeout = statusWaitTimeout;
        this.logApiPayload = logApiPayload;
        this.retryTimeout = retryTimeout;
        this.retryAttempts = retryAttempts;
    }

    /**
     * @return base API URL
     */
    public URL getApiURL() {
        return apiURL;
    }

    /**
     * @return API username
     */
    public String getApiUsername() {
        return apiUsername;
    }

    /**
     * @return API password
     */
    public String getApiPassword() {
        return apiPassword;
    }

    /**
     * @return when true, SSL certificate check is ignored
     */
    public Boolean getBypassSSLCheck() {
        return bypassSSLCheck;
    }

    /**
     * @return seconds interval for status polling
     */
    public Integer getStatusPollingInterval() {
        return statusPollingInerval;
    }

    /**
     * @return Seconds wait timeout for status
     */
    public Integer getStatusWaitTimeout() {
        return statusWaitTimeout;
    }

    /**
     * Converts itself to API client configuration
     *
     * @return API client config
     */
    public ClientConfiguration toApiClientConfiguration() {
        return new ClientConfiguration(apiURL.toString(), apiUsername, apiPassword, bypassSSLCheck, logApiPayload);
    }

    /**
     * Number of attempts for retrying API call when service is busy or unavailable
     *
     * @return number of attempts
     */
    public Integer getRetryAttempts() {
        return retryAttempts;
    }

    /**
     * Timeout (in seconds) for retrying API call when service is busy or unavailable
     *
     * @return value of timeout
     */
    public Integer getRetryTimeout() {
        return retryTimeout;
    }
}
