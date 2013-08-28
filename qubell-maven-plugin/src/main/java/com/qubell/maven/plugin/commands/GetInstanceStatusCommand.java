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
import com.qubell.client.ws.api.InstancesApi;
import com.qubell.client.ws.model.InstanceStatus;

/**
 * Command wrapper for {@link InstancesApi#getInstanceStatusById(String, String)}
 *
 * @author Alex Krupnov
 */
public class GetInstanceStatusCommand implements QubellApiCommand<InstanceStatus> {
    private InstancesApi instancesApi;
    private String instanceId;

    public GetInstanceStatusCommand(InstancesApi instancesApi, String instanceId) {
        this.instancesApi = instancesApi;
        this.instanceId = instanceId;
    }

    @Override
    public InstanceStatus execute() throws QubellServiceException {
        return instancesApi.getInstanceStatusById(instanceId);
    }
}
