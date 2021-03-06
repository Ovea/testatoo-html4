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

package org.testatoo.cartridge.html4.language;

import org.junit.Before;
import org.junit.Test;
import org.testatoo.WebTest;
import org.testatoo.cartridge.html4.element.Form;
import org.testatoo.cartridge.html4.element.InputText;

import static org.testatoo.cartridge.html4.By.$;
import static org.testatoo.cartridge.html4.Language.*;
import static org.testatoo.core.ComponentFactory.*;
import static org.testatoo.core.Language.assertThat;
import static org.testatoo.core.matcher.Matchers.*;

public class FormTest extends WebTest {

    @Before
    public void setUp() {
        page().open("Form.html");
    }

    @Test
    public void can_reset_a_form() {
        type("Joe", on(component(InputText.class, $("#firstname"))));
        type("Blow", into(component(InputText.class, $("#lastname"))));
        enter("email@noname.com", into(component(InputText.class, $("#email"))));

        reset(component(Form.class, $("#myForm")));

        assertThat(component(InputText.class, $("#firstname")), has(value("")));
    }

    @Test
    public void can_submit_a_form() {
        assertThat(page(), has(title("Form tests")));
        submit(component(Form.class, $("#myForm")));
        assertThat(page(), has(title("Exit page")));
    }
}
