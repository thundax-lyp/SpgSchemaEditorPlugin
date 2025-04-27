package org.openspg.idea.schema.codeStyle;


import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;


public class SchemaCodeStyleSettings extends CustomCodeStyleSettings {

    public String settingFileName;

    public SchemaCodeStyleSettings(CodeStyleSettings settings) {
        super("SchemaCodeStyleSettings", settings);
    }

}
