/**
 * Copyright (C) 2008 Ovea <dev@testatoo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.testatoo;

import org.testatoo.config.AbstractTestatooModule;

import static org.testatoo.config.Scope.TEST_SUITE;
import static org.testatoo.container.TestatooContainer.JETTY;

/**
 * @author David Avenante
 */
public class ContainerModule extends AbstractTestatooModule {
    @Override
    protected void configure() {
        containers().register(createContainer()
                .implementedBy(JETTY)
                .webappRoot("src/test/webapp")
                .port(Integer.parseInt(System.getProperty("port")))
                .build())
                .scope(TEST_SUITE);
    }
}