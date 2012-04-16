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

package org.testatoo.core;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testatoo.WebTest;
import org.testatoo.core.component.Panel;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.testatoo.core.ComponentFactory.component;
import static org.testatoo.core.ComponentFactory.page;
import static org.testatoo.core.Language.assertThat;

public class PanelTest extends WebTest {

    @BeforeClass
    public static void setUp() {
        page().open("Panel.html");
    }

    @Test
    public void can_find_panel_by_id() {
        component(Panel.class, "panelId");

        try {
            component(Panel.class, "otherPanel");
            fail();
        } catch (ComponentException e) {
            assertThat(e.getMessage(), is("Cannot find component defined by id=otherPanel"));
        }
    }

    @Test
    public void exception_thrown_if_component_not_a_panel() {
        try {
            component(Panel.class, "radio");
            fail();
        } catch (ComponentException e) {
            assertThat(e.getMessage(), is("The component with id=radio is not a Panel but a Radio"));
        }
    }

    @Test
    public void can_test_title() {
        assertThat(component(Panel.class, "panelId").title(), is("panelTitle"));
    }

    @Test
    public void test_toString() {
        assertThat(component(Panel.class, "panelId").toString(), is("class org.testatoo.core.component.Panel with state : enabled:true, visible:true, title:panelTitle"));
    }
}
