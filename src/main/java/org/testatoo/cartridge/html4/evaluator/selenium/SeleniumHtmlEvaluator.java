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

package org.testatoo.cartridge.html4.evaluator.selenium;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.Wait;
import org.testatoo.cartridge.html4.Bootstraper;
import org.testatoo.cartridge.html4.By;
import org.testatoo.cartridge.html4.EvaluatorException;
import org.testatoo.cartridge.html4.HtmlEvaluator;
import org.testatoo.cartridge.html4.component.ListBox;
import org.testatoo.cartridge.html4.element.Area;
import org.testatoo.cartridge.html4.element.Attribute;
import org.testatoo.cartridge.html4.element.Caption;
import org.testatoo.cartridge.html4.element.Col;
import org.testatoo.cartridge.html4.element.Colgroup;
import org.testatoo.cartridge.html4.element.Form;
import org.testatoo.cartridge.html4.element.HtmlElementType;
import org.testatoo.cartridge.html4.element.Map;
import org.testatoo.cartridge.html4.element.Object;
import org.testatoo.cartridge.html4.element.Option;
import org.testatoo.cartridge.html4.element.OptionGroup;
import org.testatoo.cartridge.html4.element.Param;
import org.testatoo.cartridge.html4.element.Select;
import org.testatoo.cartridge.html4.element.TBody;
import org.testatoo.cartridge.html4.element.TFoot;
import org.testatoo.cartridge.html4.element.THead;
import org.testatoo.cartridge.html4.element.Table;
import org.testatoo.cartridge.html4.element.Td;
import org.testatoo.cartridge.html4.element.Th;
import org.testatoo.cartridge.html4.element.Tr;
import org.testatoo.core.AbstractEvaluator;
import org.testatoo.core.ComponentType;
import org.testatoo.core.ListSelection;
import org.testatoo.core.Selection;
import org.testatoo.core.component.AbstractTextField;
import org.testatoo.core.component.AbstractWindow;
import org.testatoo.core.component.AlertBox;
import org.testatoo.core.component.Button;
import org.testatoo.core.component.Component;
import org.testatoo.core.component.DialogBox;
import org.testatoo.core.component.Field;
import org.testatoo.core.component.FileDialog;
import org.testatoo.core.component.Link;
import org.testatoo.core.component.ListModel;
import org.testatoo.core.component.Page;
import org.testatoo.core.component.Prompt;
import org.testatoo.core.component.datagrid.Cell;
import org.testatoo.core.component.datagrid.CellContainer;
import org.testatoo.core.component.datagrid.Column;
import org.testatoo.core.component.datagrid.DataGrid;
import org.testatoo.core.component.datagrid.Row;
import org.testatoo.core.input.Click;
import org.testatoo.core.input.Key;
import org.testatoo.core.nature.Checkable;
import org.testatoo.core.nature.Container;
import org.testatoo.core.nature.IconSupport;
import org.testatoo.core.nature.LabelSupport;
import org.testatoo.core.nature.TextSupport;
import org.testatoo.core.nature.TitleSupport;
import org.testatoo.core.nature.ValueSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.testatoo.core.ComponentType.AlertBox;
import static org.testatoo.core.input.KeyModifier.ALT;
import static org.testatoo.core.input.KeyModifier.CONTROL;
import static org.testatoo.core.input.KeyModifier.SHIFT;

/**
 * This class is the implementation of an evaluator for html4 elements with Selenium as UI Test engine.
 *
 * @author dev@testatoo.org
 */
public final class SeleniumHtmlEvaluator extends AbstractEvaluator<Selenium> implements HtmlEvaluator {

    private final Selenium selenium;
    private final String name;
    private Component currentFocusedComponent;

    private java.util.Map<String, String> alertBoxMessage = new HashMap<String, String>();

    /**
     * Class constructor specifying the used selenium engine
     *
     * @param selenium the selenium engine
     */
    public SeleniumHtmlEvaluator(String name, Selenium selenium) {
        this.name = name;
        this.selenium = selenium;
    }

    public SeleniumHtmlEvaluator(Selenium selenium) {
        this(DEFAULT_NAME, selenium);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Selenium implementation() {
        return selenium;
    }

    @Override
    public String name() {
        return name;
    }


    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Boolean existComponent(String id) {
        if (id.startsWith(org.testatoo.cartridge.html4.element.AlertBox.ID)) {
            alertBoxMessage.clear();
            return selenium.isAlertPresent();
        }
        // Cannot use jQuery cause only present after page loaded (not the case when existComponent is a page)
        return selenium.isElementPresent("id=" + id);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String text(TextSupport textSupport) {
        Component component = (Component) textSupport;
        String nodeName = nodename(component);
        if (nodeName.equalsIgnoreCase("input")) {
            return attribute(component.id(), Attribute.value);
        }
        return nodeTextContent(component);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String icon(IconSupport iconSupport) {
        Component component = (Component) iconSupport;
        String nodeName = nodename(component);
        if (nodeName.equalsIgnoreCase("input")) {
            return attribute(component.id(), Attribute.src);
        }
        if (nodeName.equalsIgnoreCase("button")) {
            // the button tag is used
            return attribute(elementId("jquery:#" + component.id() + " img"), Attribute.src);
        }
        return "";
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Boolean isVisible(Component component) {
        return component instanceof AbstractWindow
                || selenium.isVisible(component.id());
//            || (Boolean.valueOf(evaluate("window.tQuery('#" + component.id() + "').is(':visible') || !(window.tQuery('#" + component.id() + "').is(':hidden')" +
//            " || window.tQuery('#" + component.id() + "').css('visibility') == 'hidden' || window.tQuery('#" + component.id() + "').css('display') == 'none')")));
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Boolean isEnabled(Component component) {
        return !Boolean.valueOf(evaluate("window.tQuery('#" + component.id() + "').is(':disabled');"))
                && !Boolean.valueOf(evaluate("window.tQuery('#" + component.id() + "').attr('readonly') == true;"));
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void check(Checkable checkable) {
        if (!checkable.isChecked())
            click((Component) checkable, Click.left);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Boolean isChecked(Checkable checkable) {
        return Boolean.valueOf(evaluate("window.tQuery('#" + ((Component) checkable).id() + "').is(':checked');"));
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void unCheck(org.testatoo.core.component.CheckBox checkbox) {
        if (checkbox.isChecked())
            click(checkbox, Click.left);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String value(ValueSupport valueSupport) {
        if (valueSupport instanceof AbstractTextField) {
            return selenium.getValue(((Component) valueSupport).id());
        }
        if (valueSupport instanceof Cell) {
            return nodeTextContent(((Component) valueSupport));
        }

        return attribute(((Component) valueSupport).id(), Attribute.value);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String source(org.testatoo.core.component.Image image) {
        return attribute(image.id(), Attribute.src);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String label(LabelSupport labelSupport) {
        try {
            Component label = new Component(this, By.jQuery("label[for=" + ((Component) labelSupport).id() + "]").id(this));
            return nodeTextContent(label);
        } catch (EvaluatorException e) {
            try {
                Component label = new Component(this, By.xpath("//label/*[@id='" + ((Component) labelSupport).id() + "']/parent::*").id(this));
                return nodeTextContent(label);
            } catch (EvaluatorException ex) {
                return "";
            }
        }
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Integer maxLength(AbstractTextField textfield) {
        if (attribute(textfield.id(), Attribute.maxlength).equals(""))
            return Integer.MAX_VALUE;
        return Integer.valueOf(attribute(textfield.id(), Attribute.maxlength));
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void reset(AbstractTextField textField) {
        evaluate("window.tQuery('#" + textField.id() + "').val('');");
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void selectFilePath(String filePath, FileDialog fileDialog) {
        throw new EvaluatorException("Not available for security constraints");
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String selectedFilePath(FileDialog fileDialog) {
        throw new EvaluatorException("Not available for security constraints");
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void unselect(String value, ListModel listModel) {
        Select select = findEmbeddedSelect(listModel);
        String[] values = parseCSV(evaluate("window.tQuery.map(window.tQuery('#" + select.id() + " :selected'), function(e) { return window.tQuery(e).val(); });"));
        for (Option option : select.options()) {
            List<String> selectedValues = Arrays.asList(values);
            if (option.value().equals(value)) {
                if (selectedValues.contains(option.value())) {
                    evaluate("window.tQuery('#" + option.id() + "').attr('selected', '')");
                    evaluate("window.tQuery('#" + select.id() + "').simulate('change');");
                }
            }
        }
        waitForCondition();
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void unselectAll(ListModel listModel) {
        Select select = findEmbeddedSelect(listModel);
        String[] values = parseCSV(evaluate("window.tQuery.map(window.tQuery('#" + select.id() + " :selected'), function(e) { return window.tQuery(e).val(); });"));
        for (Option option : select.options()) {
            List<String> selectedValues = Arrays.asList(values);
            if (selectedValues.contains(option.value())) {
                evaluate("window.tQuery('#" + option.id() + "').attr('selected', '')");
                evaluate("window.tQuery('#" + select.id() + "').simulate('change')");
            }
        }
        waitForCondition();
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void select(String value, ListModel listModel) {
        Select select = findEmbeddedSelect(listModel);
        for (Option option : select.options()) {
            if (option.value().equals(value)) {
                evaluate("window.tQuery('#" + option.id() + "').attr('selected', 'selected');");
                // use fix for IE
                evaluate("if (window.tQuery.browser.msie) {window.tQuery('#" + select.id() + "').simulate('click');} " +
                        "else {window.tQuery('#" + select.id() + "').simulate('change');}");
            }
        }
        waitForCondition();
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Boolean contains(Container container, Component... component) {
        boolean containsAllElements = true;
        for (Component cmp : component) {
            if (!selenium.isElementPresent("//*[@id='" + ((Component) container).id() + "']//*[@id='" + cmp.id() + "']")) {
                containsAllElements = false;
            }
        }
        return containsAllElements;
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String label(Option option) {
        return attribute(option.id(), Attribute.label);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String label(OptionGroup optionGroup) {
        return attribute(optionGroup.id(), Attribute.label);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Boolean selected(Option option) {
        String id = option.id();
        return existComponent(id) && (attribute(id, Attribute.selected).toLowerCase().equalsIgnoreCase("selected")
                || attribute(id, Attribute.selected).toLowerCase().equals("true"));
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Boolean hasFocus(Component component) {
        return currentFocusedComponent != null && currentFocusedComponent.equals(component);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String title(TitleSupport titleSupport) {
        if (titleSupport instanceof Page) {
            return selenium.getTitle();
        }

        if (titleSupport instanceof FileDialog) {
            return "";
        }

        if (titleSupport instanceof AlertBox || titleSupport instanceof Prompt || titleSupport instanceof DialogBox) {
            selenium.isAlertPresent();
            return "";
        }

        if (titleSupport instanceof Column) {
            return nodeTextContent((Component) titleSupport);
        }
        return attribute(((Component) titleSupport).id(), Attribute.title);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String message(AlertBox alertbox) {
        String key = alertbox.id().substring(12);
        if (selenium.isAlertPresent()) {
            alertBoxMessage.put(key, selenium.getAlert());
        }
        return (alertBoxMessage.get(key) != null) ? alertBoxMessage.get(key) : "";
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Selection<Column> columns(DataGrid datagrid) {
        String xpath = "//table[@id='" + datagrid.id() + "']/thead/tr[last()]/th";
        int numberOfColumns = selenium.getXpathCount(xpath).intValue();

        List<Column> columns = new ArrayList<Column>();

        for (int rowNum = 1; rowNum <= numberOfColumns; rowNum++) {
            Column column = new Column(this, elementId("xpath:" + xpath + "[" + rowNum + "]"));
            columns.add(column);
        }
        return ListSelection.from(columns);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public Selection<Row> rows(DataGrid datagrid) {
        List<Row> rows = new ArrayList<Row>();

        String xpath = "//table[@id='" + datagrid.id() + "']/tbody/tr";
        int numberOfRows = selenium.getXpathCount(xpath).intValue();

        for (int rowNum = 1; rowNum <= numberOfRows; rowNum++) {
            Row row = new Row(this, elementId("xpath:" + xpath + "[" + rowNum + "]"));
            rows.add(row);
        }
        return ListSelection.from(rows);
    }

    @Override
    public Selection<Cell> cells(CellContainer cellContainer) {
        List<Cell> cells = new ArrayList<Cell>();

        if (cellContainer instanceof Row) {
            String xpath = "//tr[@id='" + ((Component) cellContainer).id() + "']/td";
            int numberOfCells = selenium.getXpathCount(xpath).intValue();

            for (int cellNum = 1; cellNum <= numberOfCells; cellNum++) {
                Cell cell = new Cell(this, elementId("xpath:" + xpath + "[" + cellNum + "]"));
                cells.add(cell);
            }
        }

        if (cellContainer instanceof Column) {
            // Find column number
            String xpath = "//th[@id='" + ((Component) cellContainer).id() + "']/parent::*/th";
            int numberOfColumns = selenium.getXpathCount(xpath).intValue();

            int selectedColumnNum = 0;
            boolean columnNumFind = false;

            for (int colNum = 1; colNum <= numberOfColumns; colNum++) {
                if (elementId("xpath:" + xpath + "[" + colNum + "]").equals(((Component) cellContainer).id())) {

                    selectedColumnNum = colNum;
                    columnNumFind = true;
                }
            }

            if (!columnNumFind) {
                throw new EvaluatorException("Unable to find the Column");
            }

            xpath = "//th[@id='" + ((Component) cellContainer).id() + "']/ancestor::table/tbody/tr";
            int numberOfRows = selenium.getXpathCount(xpath).intValue();

            for (int rowNum = 1; rowNum <= numberOfRows; rowNum++) {
                Cell cell = new Cell(this, elementId("xpath:" + xpath + "[" + rowNum + "]/td[" + selectedColumnNum + "]"));
                cells.add(cell);
            }
        }
        return ListSelection.from(cells);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public ComponentType componentType(String id) {
        if (id.equals(org.testatoo.cartridge.html4.element.AlertBox.ID))
            return AlertBox;
        return ComponentType.valueOf(evaluate("window.tQuery('#" + id + "').componentType()"));
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void click(Component component, Click which) {
        try {
            setFocus(component);
            if (which == Click.right) {
                evaluate("window.tQuery('#" + component.id() + "').simulate('rightclick');");
            } else {
                evaluate("window.tQuery('#" + component.id() + "').simulate('click');");
            }
            waitForCondition();
            // If component is link we need to open the expected target
            // But some framework like Wicket use js and set ref to # (in this case we let the framework make the job)
            if (component instanceof Link && !((Link) component).reference().equals("#")) {
                open(((Link) component).reference());
            }
        } catch (Exception e) {
            // Continue .. if the click change page
            waitForCondition();
        }
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void doubleClick(Component component) {
        evaluate("window.tQuery('#" + component.id() + "').simulate('dblclick')");
        setFocus(component);
        waitForCondition();
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void mouseOver(Component component) {
        evaluate("window.tQuery('#" + component.id() + "').simulate('mouseover')");
        waitForCondition();
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void mouseOut(Component component) {
        evaluate("window.tQuery('#" + component.id() + "').simulate('mouseout')");
        waitForCondition();
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void dragAndDrop(Component from, Component to) {
        evaluate("window.tQuery('#" + from.id() + "').simulate('dragTo', {'target': window.tQuery('#" + to.id() + "')})");
        waitForCondition();
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void focusOn(Component component) {
        click(component, Click.left);
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void type(String text) {
        String keyModifier = keyModifier();
        if (currentFocusedComponent != null) {
            for (byte charCode : text.getBytes()) {
                if (Boolean.valueOf(evaluate("window.tQuery.browser.msie"))) {
                    evaluate("window.tQuery('#" + currentFocusedComponent.id() + "')" +
                            ".val(window.tQuery('#" + currentFocusedComponent.id() + "').val() + String.fromCharCode(" + charCode + "));");
                }
                evaluate("window.tQuery('#" + currentFocusedComponent.id() + "').simulate('type', {charCode: " + charCode + keyModifier + "});");
            }
        } else {
            for (char charCode : text.toCharArray()) {
                evaluate("if (window.tQuery.browser.mozilla) {window.tQuery(window.document).simulate('type', {keyCode: " + keyboardLayout.convert(charCode) + keyModifier + "})}" +
                        "else {window.tQuery(window.document).simulate('type', {charCode: " + keyboardLayout.convert(charCode) + keyModifier + "})};");
            }
        }
        waitForCondition();
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void press(Key key) {
        typeKey(key.code());
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public void close(AbstractWindow window) {
        if (window instanceof AlertBox && selenium.isAlertPresent()) {
            selenium.getAlert();
        }
    }

    /**
     * @see org.testatoo.core.Evaluator
     */
    @Override
    public String reference(Link link) {
        if (attribute(link.id(), Attribute.href).equals(""))
            return "#";
        return attribute(link.id(), Attribute.href);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public void open(String url) {
        // Selenium issue !!!!!
        if (selenium.isAlertPresent()) {
            selenium.getAlert();
        }
        currentFocusedComponent = null;
        release();
        selenium.open(url);
        waitForCondition();
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public String attribute(String id, Attribute attribute) {
        String attributeValue = evaluate("window.tQuery('#" + id + "').attributeValue('" + attribute + "');");
        if (attributeValue.equals("null")) {
            return "";
        }
        return attributeValue;
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Boolean exist(String id, Attribute attribute) {
        return !evaluate("window.tQuery('#" + id + "[" + attribute + "]')").isEmpty();
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public String pageSource() {
        return selenium.getHtmlSource();
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<OptionGroup> optionGroups(Select select) {
        List<OptionGroup> optionGroups = new ArrayList<OptionGroup>();
        try {
            for (String id : By.jQuery("#" + select.id() + " optgroup").ids(this)) {
                optionGroups.add(new OptionGroup(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(optionGroups);
        }
        return ListSelection.from(optionGroups);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Option> options(Select select) {
        List<Option> options = new ArrayList<Option>();
        try {
            for (String id : By.jQuery("#" + select.id() + " option").ids(this)) {
                options.add(new Option(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(options);
        }
        return ListSelection.from(options);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Option> selectedOptions(Select select) {
        try {
            String[] selectedIds = elementsId("jquery:#" + select.id() + " option:selected");
            List<Option> options = new ArrayList<Option>();
            for (String id : selectedIds) {
                options.add(new Option(this, id));
            }
            return ListSelection.from(options);
        } catch (EvaluatorException se) {
            return ListSelection.empty();
        }
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Option> options(OptionGroup optionGroup) {
        List<Option> options = new ArrayList<Option>();
        try {
            for (String id : By.jQuery("#" + optionGroup.id() + " option").ids(this)) {
                options.add(new Option(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(options);
        }
        return ListSelection.from(options);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Param> params(Object object) {
        List<Param> params = new ArrayList<Param>();
        try {
            for (String id : By.xpath("//object[@id='" + object.id() + "']//param").ids(this)) {
                // TODO maybe bug in jQuery !
//            for (Component component : By.jQuery("#" + object.id() + " param").ids(this)) {
                params.add(new Param(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(params);
        }
        return ListSelection.from(params);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Area> areas(Map map) {
        List<Area> areas = new ArrayList<Area>();
        try {
            for (String id : By.jQuery("#" + map.id() + " area").ids(this)) {
                areas.add(new Area(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(areas);
        }
        return ListSelection.from(areas);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Col> cols(Colgroup colgroup) {
        List<Col> cols = new ArrayList<Col>();
        try {
            for (String id : By.jQuery("#" + colgroup.id() + " col").ids(this)) {
                cols.add(new Col(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(cols);
        }
        return ListSelection.from(cols);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Caption caption(Table table) {
        return new Caption(this, By.jQuery("#" + table.id() + " caption").id(this));
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public String content(Component component) {
        return nodeTextContent(component);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public THead thead(Table table) {
        return new THead(this, By.jQuery("#" + table.id() + " thead").id(this));
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public TBody tbody(Table table) {
        return new TBody(this, By.jQuery("#" + table.id() + " tbody").id(this));
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public TFoot tfoot(Table table) {
        return new TFoot(this, By.jQuery("#" + table.id() + " tfoot").id(this));
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Tr> tr(Component component) {
        List<Tr> tableRows = new ArrayList<Tr>();
        try {
            for (String id : By.jQuery("#" + component.id() + " tr").ids(this)) {
                tableRows.add(new Tr(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(tableRows);
        }
        return ListSelection.from(tableRows);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Td> td(Tr tr) {
        List<Td> td = new ArrayList<Td>();
        try {
            for (String id : By.jQuery("#" + tr.id() + " td").ids(this)) {
                td.add(new Td(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(td);
        }
        return ListSelection.from(td);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Th> th(Tr tr) {
        List<Th> th = new ArrayList<Th>();
        try {
            for (String id : By.jQuery("#" + tr.id() + " th").ids(this)) {
                th.add(new Th(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(th);
        }
        return ListSelection.from(th);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Col> cols(Table table) {
        List<Col> cols = new ArrayList<Col>();
        try {
            for (String id : By.jQuery("#" + table.id() + " col").ids(this)) {
                cols.add(new Col(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(cols);
        }
        return ListSelection.from(cols);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Selection<Colgroup> colgroups(Table table) {
        List<Colgroup> colgroups = new ArrayList<Colgroup>();
        try {
            for (String id : By.jQuery("#" + table.id() + " colgroup").ids(this)) {
                colgroups.add(new Colgroup(this, id));
            }
        } catch (EvaluatorException e) {
            return ListSelection.from(colgroups);
        }
        return ListSelection.from(colgroups);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public String content(Option option) {
        return nodeTextContent(option);
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public void submit(Form form) {
        evaluate("window.tQuery('#" + form.id() + "')[0].submit();");
        waitForCondition();
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public void reset(Form form) {
        click(getResetButton(form), Click.left);
        waitForCondition();
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public Boolean isReadOnly(Field field) {
        return !selenium.isEditable(field.id());
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public String elementId(String expression) {
        String id = null;

        if (expression.startsWith("xpath:")) {
            String xpath = expression.substring(6, expression.length());
            if (!selenium.isElementPresent(xpath)) {
                throw new EvaluatorException("Cannot find component defined by the xpath : " + xpath);
            }
            try {
                id = selenium.getAttribute(xpath + "@id");
            } catch (Exception e) {
                // Ok exists but without identifier so create one
                id = UUID.randomUUID().toString();
                selenium.assignId(xpath, id);
            }
        }

        if (expression.startsWith("jquery:")) {
            String query = expression.substring(7, expression.length());
            if (!Boolean.valueOf(evaluate("window.tQuery('" + query + "').length > 0"))) {
                throw new EvaluatorException("Cannot find component defined by the jquery expression : " + query);
            }

            id = evaluate("window.tQuery('" + query + "').attr('id')");
            if (id.isEmpty()) {
                // Ok exists but without identifier so create one
                id = UUID.randomUUID().toString();
                evaluate("window.tQuery('" + query + "').attr('id', '" + id + "')");
            }
        }
        return id;
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public String[] elementsId(String expression) {
        if (expression.startsWith("xpath:")) {
            String xpath = expression.substring(6, expression.length());
            if (!selenium.isElementPresent(xpath)) {
                throw new EvaluatorException("Cannot find component defined by the xpath : " + xpath);
            }

            String[] resultId = extractId(expression);
            for (int i = 0; i < resultId.length; i++) {
                String id = resultId[i];
                if (id.equals("")) {
                    id = UUID.randomUUID().toString();
                    selenium.assignId(xpath + "[" + (i + 1) + "]", id);
                    resultId[i] = id;
                }
            }
            return resultId;
        }

        if (expression.startsWith("jquery:")) {
            String query = expression.substring(7, expression.length());
            if (!Boolean.valueOf(evaluate("window.tQuery('" + query + "').length > 0"))) {
                throw new EvaluatorException("Cannot find component defined by the jquery expression : " + query);
            }

            String[] resultId = extractId(expression);
            for (int i = 0; i < resultId.length; i++) {
                String id = resultId[i];
                if (id.equals("")) {
                    id = UUID.randomUUID().toString();
                    evaluate("window.tQuery(window.tQuery('" + query + "')[" + i + "]).attr('id', '" + id + "')");
                    resultId[i] = id;
                }
            }
            return resultId;
        }

        throw new IllegalArgumentException("The expression format is not supported");
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public HtmlElementType htmlElementType(String id) {
        return HtmlElementType.valueOfIgnoreCase(evaluate("window.tQuery('#" + id + "').htmlType()"));
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public String nodeTextContent(Component component) {
        return selenium.getText(component.id());
    }

    /**
     * @see org.testatoo.cartridge.html4.HtmlEvaluator
     */
    @Override
    public String nodename(Component component) {
        return evaluate("window.tQuery('#" + component.id() + "').attr('nodeName');");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SeleniumHtmlEvaluator");
        sb.append("{location='").append(selenium.getLocation()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    // -------------- Private ----------------------

    private String[] extractId(String expression) {
        if (expression.startsWith("xpath:")) {
            String xpath = expression.substring(6, expression.length());
            return parseCSV(evaluate(
                    "var elements = eval_xpath(\"" + xpath + "\", window.document);" +
                            "var result = [];\n" +
                            "for (var i = 0; i < elements.length; i++) {\n" +
                            "  id = elements[i].id;\n" +
                            "  if (id == '') {id = 'undefined'}; \n" +
                            "  result.push(id);\n" +
                            "}\n" +
                            "result;"));
        }

        if (expression.startsWith("jquery:")) {
            expression = expression.substring(7, expression.length());
            return parseCSV(evaluate(
                    "var elements = window.tQuery(\"" + expression + "\");" +
                            "var result = [];\n" +
                            "for (var i = 0; i < elements.length; i++) {\n" +
                            "  id = elements[i].id;\n" +
                            "  if (id == '') {id = 'undefined'}; \n" +
                            "  result.push(id);\n" +
                            "}\n" +
                            "result;"));
        }
        return null;
    }

    private void setFocus(Component component) {
        if (component instanceof Link || component instanceof Area || component instanceof Button
                || component instanceof Object || component instanceof ListModel || component instanceof Field) {
            evaluate("window.tQuery('#" + component.id() + "').focus()");
            currentFocusedComponent = component;
        }
    }

    private void typeKey(int keyCode) {
        String keyModifier = keyModifier();
        evaluate("if(window.tQuery.browser.webkit) {window.tQuery(window.document).simulate('type', {charCode: " + keyCode + keyModifier + "});}" +
                "else {window.tQuery('body').simulate('type', {keyCode: " + keyCode + keyModifier + "});}");
    }

    private String keyModifier() {
        if (!pressedKeyModifier.isEmpty()) {
            List<String> options = new ArrayList<String>();
            if (pressedKeyModifier.contains(CONTROL)) {
                options.add("ctrlKey : true");
            }
            if (pressedKeyModifier.contains(SHIFT)) {
                options.add("shiftKey : true");
            }
            if (pressedKeyModifier.contains(ALT)) {
                options.add("altKey : true");
            }

            String result = "";
            for (String option : options) {
                result = result + ", " + option;
            }
            return result;
        } else {
            return "";
        }
    }

    private static String[] parseCSV(String input) {
        String[] splitedInput = input.split(",");
        for (int i = 0; i < splitedInput.length; i++) {
            if (splitedInput[i].equalsIgnoreCase("undefined"))
                splitedInput[i] = "";
        }
        return splitedInput;
    }

    private Button getResetButton(Form form) {
        return new Button(this, By.jQuery("#" + form.id() + " :reset").id(this));
    }

    private void waitForCondition() {
        new Wait() {
            public boolean until() {
                return getWaitingCondition().isReach();
            }
        }.wait("One of the waiting conditions has fail", 60000);
    }

    private Select findEmbeddedSelect(ListModel listModel) {
        try {
            if (listModel instanceof Select) {
                return (Select) listModel;
            } else {
                ListBox listBox = (ListBox) listModel;
                java.lang.reflect.Field fields[] = listBox.getClass().getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    field.setAccessible(true);
                    if (field.getName().equals("select")) {
                        return (Select) field.get(listBox);
                    }
                }
            }
        } catch (Exception e) {
            // Nop
        }
        throw new EvaluatorException("Unable to identify the type of ListModel");
    }

    private String evaluate(String expression) {

        try {
            selenium.getEval("window.tQuery().isTQueryAvailable()");
        } catch (Exception e) {
            selenium.runScript(loadUserExtensions());
        }

        return selenium.getEval(expression);
    }

    private String loadUserExtensions() {
        String script = "";
        script += addScript("tquery-1.4.2-min.js");
        script += addScript("tquery-simulate.js");
        script += addScript("tquery-util.js");
        return script;
    }

    private String addScript(String name) {
        try {
            Reader reader = new BufferedReader(new InputStreamReader(Bootstraper.class.getResourceAsStream(name)));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            return builder.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Internal error occured when trying to load custom scripts : " + e.getMessage(), e);
        }
    }

}
