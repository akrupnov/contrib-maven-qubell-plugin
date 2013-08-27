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
import com.qubell.client.ws.api.InstancesApi;
import com.qubell.client.ws.model.Instance;
import com.qubell.client.ws.model.InstanceSpecification;
import com.qubell.client.ws.model.InstanceStatus;

import java.util.HashMap;
import java.util.Map;

public class InstanceApiTest
        extends BaseServiceTest {
    public void testInstanceStatusGet() throws Exception {
        ApplicationsApi appApi = new ApplicationsApi(getTestConfiguration());

        InstanceSpecification instanceSpecification = new InstanceSpecification();
        instanceSpecification.setVersion(1);
        instanceSpecification.setEnvironmentId("5200ddb8e4b0758c3d25e295");
        instanceSpecification.setDestroyInterval(new Long(1000000));
        instanceSpecification.setInstanceName("InstanceApiTest.testInstanceStatusGet");

        Map<String, Object> customStuff = new HashMap<String, Object>();
        Map<String, Object> secondLevel = new HashMap<String, Object>();
        secondLevel.put("sample", 2);

        customStuff.put("secondLevel", secondLevel);
        customStuff.put("customInstanceLaunch", "test");
        customStuff.put("customInstanceLaunchStrikesBack", "test2");

        instanceSpecification.setParameters(customStuff);


        Instance instance = appApi.launchInstanceByAppId("5200df12e4b0758c3d25e2af", instanceSpecification);


        InstancesApi api = new InstancesApi(getTestConfiguration());
        HashMap<String, Object> custom = new HashMap<String, Object>();
        custom.put("someMoreParams", "its value");
        Thread.sleep(2000);

        instance = api.runWorkflow(instance.getId(), "returnstuff", custom);
        Thread.sleep(2000);
        instance = api.runWorkflow(instance.getId(), "holdon", custom);
        Thread.sleep(2000);
        InstanceStatus status = api.getInstanceStatusById(instance.getId());
        String i = "";

    }
}
