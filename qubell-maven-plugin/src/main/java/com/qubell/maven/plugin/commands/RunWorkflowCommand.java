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
import com.qubell.client.ws.model.Instance;

import java.util.Map;

/**
 * Command wrapper for {@link InstancesApi#runWorkflow(String, String, Object)}
 *
 * @author Alex Krupnov
 */
public class RunWorkflowCommand implements QubellApiCommand<Instance> {
    private InstancesApi instancesApi;
    private String instanceId;
    private String workflow;
    private Map<String, Object> customParameters;

    public RunWorkflowCommand(InstancesApi instancesApi, String instanceId, String workflow, Map<String, Object> customParameters) {
        this.instancesApi = instancesApi;
        this.instanceId = instanceId;
        this.workflow = workflow;
        this.customParameters = customParameters;
    }

    @Override
    public Instance execute() throws QubellServiceException {
        return instancesApi.runWorkflow(instanceId, workflow, customParameters);
    }
}
