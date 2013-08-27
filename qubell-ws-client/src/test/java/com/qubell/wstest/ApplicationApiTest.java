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

package com.qubell.wstest;

import com.qubell.client.ws.api.ApplicationsApi;
import com.qubell.client.ws.model.Application;
import com.qubell.client.ws.model.Instance;
import com.qubell.client.ws.model.InstanceSpecification;

import java.util.HashMap;
import java.util.Map;

public class ApplicationApiTest
        extends BaseServiceTest {

    private String superSimpleManifest = "launch:\n" +
            "  steps:\n" +
            "        - destroy:\n" +
            "            action: compute.shrink-all\n" +
            "destroy:\n" +
            "  steps:\n" +
            "        - destroy:\n" +
            "            action: compute.shrink-all";

    private String superSimpleAppId = "5200df00e4b0758c3d25e2ac";

    public void testUpdateManifest() throws Exception {
        ApplicationsApi api = new ApplicationsApi(getTestConfiguration());
        Application app = api.updateManifest(superSimpleAppId, superSimpleManifest, "application/x-yaml");
        Integer version = app.getVersion();
    }

    public void testAppLaunch() throws Exception {
        ApplicationsApi api = new ApplicationsApi(getTestConfiguration());

        InstanceSpecification instanceSpecification = new InstanceSpecification();
        instanceSpecification.setVersion(1);
        instanceSpecification.setEnvironmentId("5200ddb8e4b0758c3d25e295");
        instanceSpecification.setDestroyInterval(new Long(400));

        Map<String, Object> customStuff = new HashMap<String, Object>();
        Map<String, Object> secondLevel = new HashMap<String, Object>();
        secondLevel.put("sample", 2);

        customStuff.put("secondLevel", secondLevel);
        customStuff.put("firstLevel", "test");

        instanceSpecification.setParameters(customStuff);


        Instance i = api.launchInstanceByAppId("5200df12e4b0758c3d25e2af", instanceSpecification);
        String a = "a";
    }
}
