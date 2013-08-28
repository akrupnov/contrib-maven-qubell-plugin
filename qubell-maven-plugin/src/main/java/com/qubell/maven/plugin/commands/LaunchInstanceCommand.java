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

package com.qubell.maven.plugin.commands;

import com.qubell.client.exceptions.QubellServiceException;
import com.qubell.client.ws.api.ApplicationsApi;
import com.qubell.client.ws.model.Instance;
import com.qubell.client.ws.model.InstanceSpecification;

/**
 * Command wrapper for {@link com.qubell.client.ws.api.ApplicationsApi#launchInstanceByAppId(String, com.qubell.client.ws.model.InstanceSpecification)}
 *
 * @author Alex Krupnov
 */

public class LaunchInstanceCommand implements QubellApiCommand<Instance> {
    private ApplicationsApi applicationsApi;
    private InstanceSpecification instanceSpecification;
    private String applicationId;

    public LaunchInstanceCommand(ApplicationsApi applicationsApi, String applicationId, InstanceSpecification instanceSpecification) {
        this.applicationsApi = applicationsApi;
        this.instanceSpecification = instanceSpecification;
        this.applicationId = applicationId;
    }

    @Override
    public Instance execute() throws QubellServiceException {
        return applicationsApi.launchInstanceByAppId(applicationId, instanceSpecification);
    }
}
