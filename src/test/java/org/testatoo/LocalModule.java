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
import org.testatoo.config.Scope;
import org.testatoo.config.cartridge.TestatooCartridge;

final class LocalModule extends AbstractTestatooModule {
    @Override
    protected void configure() {
        seleniumServers().register(createSeleniumServer()
                .port(4444)
                .useSingleWindow(true)
                .build())
                .scope(Scope.TEST_SUITE);

        seleniumSessions()
                .register(createSeleniumSession()
                        .website("http://127.0.0.1:7896/")
//                        .browser("*iexploreproxy")
                        .browser("*firefox")
//                        .browser("*safari")
//                        .browser("*opera")
//                        .browser("*custom /usr/lib/chromium-browser/chromium-browser")
//                        .browser("*googlechrome")
                        .serverHost("127.0.0.1")
                        .serverPort(4444)
                        .build())
                .scope(Scope.TEST_SUITE)
                .withTimeout(20000)
                .inCartridge(TestatooCartridge.HTML4);
    }
}