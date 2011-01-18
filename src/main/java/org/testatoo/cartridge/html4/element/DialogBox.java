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

package org.testatoo.cartridge.html4.element;

import org.testatoo.core.Evaluator;

/**
 * This class allows the testing of javascript DialogBox properties (on call of "confirm()" method).
 * The DialogBox is a special window that appears to display information or ask question to the user.
 * There are 2 buttons in the DialogBox (if one button : it's an AlertBox).
 * *
 *
 * @author dev@testatoo.org
 */

public final class DialogBox extends org.testatoo.core.component.DialogBox {

    /**
     * Class constructor specifying the evaluator to use and the id of the dialogBox we want to test.
     *
     * @param evaluator a UI Test engine specific html evaluator
     */
    public DialogBox(Evaluator evaluator, String id) {
        super(evaluator, id);
    }
}
