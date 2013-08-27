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

    /**
     * Initializes the config
     *
     * @param apiURL            {@link #apiURL}
     * @param apiUsername       {@link #apiUsername}
     * @param apiPassword       {@link #apiPassword}
     * @param bypassSSLCheck    {@link #bypassSSLCheck}
     * @param pollingInterval   {@link #statusPollingInerval}
     * @param statusWaitTimeout {@link #statusWaitTimeout}
     * @param logApiPayload     {@link #logApiPayload}
     */
    public Configuration(URL apiURL, String apiUsername, String apiPassword, Boolean bypassSSLCheck, Integer pollingInterval, Integer statusWaitTimeout, Boolean logApiPayload) {
        this.apiURL = apiURL;
        this.apiUsername = apiUsername;
        this.apiPassword = apiPassword;
        this.bypassSSLCheck = bypassSSLCheck;
        this.statusPollingInerval = pollingInterval;
        this.statusWaitTimeout = statusWaitTimeout;
        this.logApiPayload = logApiPayload;
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
}
