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

/**
 * Command adaptor for Qubell APIs which can be used for generalized plugin API invocation
 *
 * @param <T> type of command return value
 * @author Alex Krupnov
 */
public interface QubellApiCommand<T> {
    /**
     * Runs a specific command
     *
     * @return return value of underlying command API
     * @throws QubellServiceException when service failed to execute
     */
    T execute() throws QubellServiceException;
}
