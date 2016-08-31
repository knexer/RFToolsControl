package mcjty.rftoolscontrol.logic.editors;

import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.rftoolscontrol.logic.registry.ParameterValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class AbstractParameterEditor implements ParameterEditor {

    public static final String PAGE_CONSTANT = "Constant";
    public static final String PAGE_VARIABLE = "Variable";
    public static final String PAGE_FUNCTION = "Function";

    private TextField variableIndex;
    private TabbedPanel tabbedPanel;
    private Panel buttonPanel;

    public static Integer parseIntSafe(String newText) {
        Integer f;
        try {
            f = Integer.parseInt(newText);
        } catch (NumberFormatException e) {
            f = 0;
        }
        return f;
    }

    protected abstract ParameterValue readConstantValue();

    protected abstract void writeConstantValue(ParameterValue value);

    @Override
    public ParameterValue readValue() {
        if (PAGE_CONSTANT.equals(tabbedPanel.getCurrentName())) {
            return readConstantValue();
        } else if (PAGE_VARIABLE.equals(tabbedPanel.getCurrentName())) {
            return ParameterValue.variable(parseIntSafe(variableIndex.getText()));
        } else if (PAGE_FUNCTION.equals(tabbedPanel.getCurrentName())) {
            // @todo
        }
        return null;
    }

    @Override
    public void writeValue(ParameterValue value) {
        if (value == null || value.isConstant()) {
            switchPage(PAGE_CONSTANT, null);
            writeConstantValue(value);
        } else if (value.isVariable()) {
            switchPage(PAGE_VARIABLE, null);
            variableIndex.setText(Integer.toString(value.getVariableIndex()));
        }  // @todo function
    }

    void createEditorPanel(Minecraft mc, Gui gui, Panel panel, ParameterEditorCallback callback, Panel constantPanel) {
        Panel variablePanel = new Panel(mc, gui).setLayout(new HorizontalLayout());
        variableIndex = new TextField(mc, gui)
            .addTextEvent((parent,newText) -> callback.valueChanged(readValue()));
        variablePanel.addChild(new Label(mc, gui).setText("Index:")).addChild(variableIndex);

        Panel functionPanel = new Panel(mc, gui).setLayout(new HorizontalLayout());

        tabbedPanel = new TabbedPanel(mc, gui)
                .addPage(PAGE_CONSTANT, constantPanel)
                .addPage(PAGE_VARIABLE, variablePanel)
                .addPage(PAGE_FUNCTION, functionPanel);

        buttonPanel = new Panel(mc, gui).setLayout(new HorizontalLayout());
        ToggleButton constantButton = new ToggleButton(mc, gui).setText(PAGE_CONSTANT)
                .addButtonEvent(w -> switchPage(PAGE_CONSTANT, callback));
        ToggleButton variableButton = new ToggleButton(mc, gui).setText(PAGE_VARIABLE)
                .addButtonEvent(w -> switchPage(PAGE_VARIABLE, callback));
        ToggleButton functionButton = new ToggleButton(mc, gui).setText(PAGE_FUNCTION)
                .addButtonEvent(w -> switchPage(PAGE_FUNCTION, callback));
        buttonPanel.addChild(constantButton).addChild(variableButton).addChild(functionButton);

        panel.addChild(buttonPanel).addChild(tabbedPanel);
    }

    private void switchPage(String page, ParameterEditorCallback callback) {
        for (int i = 0 ; i < buttonPanel.getChildCount() ; i++) {
            ToggleButton button = (ToggleButton) buttonPanel.getChild(i);
            if (!page.equals(button.getText())) {
                button.setPressed(false);
            } else {
                button.setPressed(true);
            }
            tabbedPanel.setCurrent(page);
            if (callback != null) {
                callback.valueChanged(readValue());
            }
        }

    }
}