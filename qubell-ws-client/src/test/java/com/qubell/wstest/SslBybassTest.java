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

import com.qubell.client.ClientConfiguration;
import com.qubell.client.ws.api.OrganizationsApi;
import com.qubell.client.ws.model.Organization;
import junit.framework.TestCase;

import java.util.List;

public class SslBybassTest extends TestCase {

    public void testUnsecureConnection() throws Exception {
        ClientConfiguration configuration = new
                ClientConfiguration("https://secure.dev.qubell.com/", "a.krupnov@gmail.com", "123123123", true, true);

        OrganizationsApi organizationsApi = new OrganizationsApi(configuration);

        List<Organization> orgs = organizationsApi.listOrganizations();
    }
}
