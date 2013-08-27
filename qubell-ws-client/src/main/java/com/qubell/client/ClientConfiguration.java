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

package com.qubell.client;

import org.apache.commons.lang.StringUtils;

/**
 * Configuration settings for Qubell API
 *
 * @author Alex Krupnov
 */
public class ClientConfiguration {
    private String username;
    private String password;
    private Boolean bypassSSLCertificateCheck;
    private String basePath;
    private Boolean logPayload;

    /**
     * Initializes config object
     *
     * @param basePath                  {@link #getBasePath()}
     * @param username                  {@link #getUsername()}
     * @param password                  {@link #getPassword()}
     * @param bypassSSLCertificateCheck {@link #getBypassSSLCertificateCheck()}
     * @param logPayload                {@link #getLogPayload()}
     */
    public ClientConfiguration(String basePath, String username, String password, Boolean bypassSSLCertificateCheck, Boolean logPayload) {
        this.username = username;
        this.password = password;
        this.bypassSSLCertificateCheck = bypassSSLCertificateCheck;
        this.basePath = StringUtils.removeEnd(basePath, "/");
        this.logPayload = logPayload;
    }

    /**
     * Username for authentication
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Authentication password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Whether SSL certificate validation should be bypassed
     *
     * @return true when bypassing SSL check
     */
    public Boolean getBypassSSLCertificateCheck() {
        return bypassSSLCertificateCheck;
    }

    /**
     * Base path for API
     *
     * @return base URL
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * Whether requests/responses should be logged
     *
     * @return true when logging enabled
     */
    public Boolean getLogPayload() {
        return logPayload;
    }
}
