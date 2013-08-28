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

public class UpdateManifestCommand implements QubellApiCommand<Integer> {
    private ApplicationsApi applicationsApi;
    private String applicationId;
    private String manifest;

    public UpdateManifestCommand(ApplicationsApi applicationsApi, String applicationId, String manifest) {
        this.applicationsApi = applicationsApi;
        this.applicationId = applicationId;
        this.manifest = manifest;
    }

    @Override
    public Integer execute() throws QubellServiceException {
        return applicationsApi.updateManifest(applicationId, manifest, "application/x-yaml").getVersion();
    }
}
