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

package org.testatoo.multisession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testatoo.cartridge.html4.element.*;
import org.testatoo.config.annotation.ConcurrentEvaluation;
import org.testatoo.config.annotation.TestatooModules;
import org.testatoo.config.junit.TestatooJunitRunner;
import org.testatoo.core.ComponentException;
import org.testatoo.core.input.Mouse;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.testatoo.cartridge.html4.By.$;
import static org.testatoo.cartridge.html4.Language.clickOn;
import static org.testatoo.core.ComponentFactory.*;
import static org.testatoo.core.Language.assertThat;
import static org.testatoo.core.matcher.Matchers.*;

@RunWith(TestatooJunitRunner.class)
@TestatooModules(ParallelMultiSessionModule.class)
@ConcurrentEvaluation
public class ParallelMultiSessionTest {

    @Before
    public void setUp() {
        page().open("Form.html");
    }

    @Test
    public void can_find_form_by_id() {
        component(Form.class, $("#myForm"));

        try {
            component(Form.class, $("#otherForm"));
            fail();
        } catch (ComponentException e) {
            assertThat(e.getMessage(), is("Cannot find component defined by jQueryExpression=$('#otherForm')"));
        }
    }

    @Test
    public void exception_thrown_if_component_not_a_html_form() {
        try {
            component(Form.class, $("#email"));
            fail();
        } catch (ComponentException e) {
            assertThat(e.getMessage(), is("The component with id=email is not a Form but a InputText"));
        }
    }

    @Test
    public void test_i18nAttributes() {
        Form myForm = component(Form.class, $("#myForm"));

        assertThat(myForm.direction(), is(Direction.lefttoright));
        assertThat(myForm.language(), is("fr"));
    }

    @Test
    public void test_coreAttributes() {
        Form myForm = component(Form.class, $("#myForm"));

        assertThat(myForm.id(), is("myForm"));
        assertThat(myForm.classname(), is("myClass"));
        assertThat(myForm.style(), containsString("color:black"));
        assertThat(myForm.title(), is("formTitle"));
    }

    @Test
    public void test_specifics_attributes() {
        Form myForm = component(Form.class, $("#myForm"));

        assertThat(myForm.method(), is(Method.post));
        assertThat(myForm.action(), is("Exit.html"));
        assertThat(myForm.enctype(), is("application/x-www-form-urlencoded"));
        assertThat(myForm.accept(), is("all"));
        assertThat(myForm.name(), is("formName"));
        // Not supported by IE
//        assertThat(myForm.acceptCharset(), is("UTF-8"));
    }

    @Test
    public void if_attribute_are_not_defined_then_return_default_values() {
        Form myForm3 = component(Form.class, $("#myForm3"));

        assertThat(myForm3.method(), is(Method.get));
        assertThat(myForm3.enctype(), is("application/x-www-form-urlencoded"));
        assertThat(myForm3.accept(), is(""));
        assertThat(myForm3.name(), is(""));
        assertThat(myForm3.acceptCharset(), is("UNKNOWN"));
    }

    @Test
    public void test_contains() {
        Form myForm = component(Form.class, $("#myForm"));

        Select citiesList = component(Select.class, $("#cities"));
        assertThat(myForm, contains(citiesList));

        InputText inputText = component(InputText.class, $("#lastname"));
        assertThat(myForm, contains(inputText));

        Radio radio = component(Radio.class, $("#male"));
        assertThat(myForm, contains(radio));

        CheckBox checkBox = component(CheckBox.class, $("[name=yes]"));
        assertThat(myForm, contains(checkBox));

        Button button = component(Button.class, $("#submitImage"));
        assertThat(myForm, contains(button));
    }

    @Test
    public void can_submit_a_form() {
        assertThat(page(), has(title("Form tests")));

        Button submitButton = component(Button.class, $("#submitImage"));
        clickOn(submitButton);

        assertThat(page(), has(title("Exit page")));

        page().open("Form.html");
        assertThat(page(), has(title("Form tests")));
        Mouse.clickOn(submitButton);

        assertThat(page(), has(title("Exit page")));

        page().open("Form.html");
        assertThat(page(), has(title("Form tests")));

        component(Form.class, $("#myForm")).submit();

        assertThat(page(), has(title("Exit page")));
    }

    @Test
    public void test_toString() {
        assertThat(component(Form.class, $("#myForm")).toString(), is("class org.testatoo.cartridge.html4.element.Form with state : enabled:true, visible:true, action:Exit.html"));
    }
}
