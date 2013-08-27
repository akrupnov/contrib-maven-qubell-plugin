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

import com.qubell.client.ws.api.OrganizationsApi;
import com.qubell.client.ws.model.Environment;
import com.qubell.client.ws.model.Organization;
import com.qubell.client.ws.model.OrganizationApplication;

import java.util.ArrayList;
import java.util.List;

public class OrganizationsApiTest extends BaseServiceTest {
    public void testGetListOfOrganizations() throws Exception {
        OrganizationsApi api = new OrganizationsApi(getTestConfiguration());

        List<Organization> orgs = api.listOrganizations();

        assertNotSame(0, orgs.size());
    }

    public void testGetListOfApplications() throws Exception {
        List<OrganizationApplication> orgApps = new ArrayList<OrganizationApplication>();
        OrganizationsApi api = new OrganizationsApi(getTestConfiguration());

        List<Organization> orgs = api.listOrganizations();
        for (Organization org : orgs) {
            orgApps.addAll(api.listApplicationsByOrganization(org.getId()));
        }

        assertNotSame(0, orgApps.size());
    }

    public void testGetListOfEnvironments() throws Exception {
        List<Environment> orgEnvs = new ArrayList<Environment>();
        OrganizationsApi api = new OrganizationsApi(getTestConfiguration());

        List<Organization> orgs = api.listOrganizations();
        for (Organization org : orgs) {
            orgEnvs.addAll(api.listEnvironmentsByOrganization(org.getId()));
        }

        assertNotSame(0, orgEnvs.size());
    }

}
