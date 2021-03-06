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
import org.testatoo.core.component.Radio;
import org.testatoo.core.input.Mouse;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;
import static org.testatoo.core.ComponentFactory.component;
import static org.testatoo.core.ComponentFactory.page;
import static org.testatoo.core.Language.assertThat;
import static org.testatoo.core.Language.clickOn;
import static org.testatoo.core.matcher.Matchers.*;

public class RadioTest extends WebTest {

    @BeforeClass
    public static void setUp() {
        page().open("Radio.html");
    }

    @Test
    public void can_find_radio_by_id() {
        component(Radio.class, "male");

        try {
            component(Radio.class, "otherRadio");
            fail();
        } catch (ComponentException e) {
            assertThat(e.getMessage(), is("Cannot find component defined by id=otherRadio"));
        }
    }

    @Test
    public void exception_thrown_if_component_not_a_radio() {
        try {
            component(Radio.class, "radiobuttonError");
            fail();
        } catch (ComponentException e) {
            assertThat(e.getMessage(), is("The component with id=radiobuttonError is not a Radio but a TextField"));
        }
    }

    @Test
    public void can_check() {
        // No group
        Radio male = component(Radio.class, "male");

        assertThat(male, is(not(checked())));
        Mouse.clickOn(male);
        assertThat(male, is(checked()));

        Radio female = component(Radio.class, "female");
        assertThat(female, is(not(checked())));
        female.check();
        assertThat(female, is(checked()));

        assertThat(male, is(checked()));

        // Group
        Radio yes = component(Radio.class, "yes");
        Radio no = component(Radio.class, "no");

        assertThat(yes, is(not(checked())));
        clickOn(yes);
        assertThat(yes, is(checked()));

        assertThat(no, is(not(checked())));
        no.check();
        assertThat(no, is(checked()));

        assertThat(yes, is(not(checked())));
    }

    @Test
    public void test_label() {
        assertThat(component(Radio.class, "male"), has(label("Male")));
        assertThat(component(Radio.class, "female"), has(label("Female")));
    }

    @Test
    public void test_toString() {
        assertThat(component(Radio.class, "radio").toString(), is("class org.testatoo.core.component.Radio with state : enabled:true, visible:true, label:Radio label, checked:false"));
    }
}
