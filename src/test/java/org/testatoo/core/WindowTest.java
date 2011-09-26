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

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.testatoo.core.Language.assertThat;

public class WindowTest {

    // TODO to implement with browser window !!!!
    @Test
    public void dummyTest() {
        // Dummy test the Window concept is not present in HTML
        assertThat(true, is(true));
    }
}
