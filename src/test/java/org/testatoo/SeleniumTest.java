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

import com.thoughtworks.selenium.Selenium;
import org.junit.Before;
import org.junit.Test;
import org.testatoo.config.annotation.Implementation;
import org.testatoo.core.Current;

import static org.testatoo.core.ComponentFactory.page;

public class SeleniumTest extends WebTest {

    // require ability to inject underlying implementation if annotation support is enabled
    @Implementation
    Current<Selenium> currentSelenium;

    @Before
    public void setUp() {
        page().open("Select.html");
    }

    @Test
    public void currentSelenium() {
        currentSelenium.get().isElementPresent("id=radio");
    }

}