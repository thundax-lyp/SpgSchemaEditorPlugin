package org.openspg.idea.schema.codeStyle;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.schema.SchemaLanguage;
import org.openspg.idea.schema.demo.SchemaDemo;

public final class SchemaLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

    @NotNull
    @Override
    public Language getLanguage() {
        return SchemaLanguage.INSTANCE;
    }

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
            consumer.showStandardOptions("BLANK_LINES_AFTER_PACKAGE");
            consumer.renameStandardOption("BLANK_LINES_AFTER_PACKAGE", "After namespace");
            consumer.showStandardOptions("BLANK_LINES_AFTER_IMPORTS");
            consumer.renameStandardOption("BLANK_LINES_AFTER_IMPORTS", "After schema type");

        } else if (settingsType == SettingsType.SPACING_SETTINGS) {
            consumer.showStandardOptions("SPACE_AFTER_COMMA");
            consumer.showStandardOptions("SPACE_BEFORE_COMMA");
            consumer.showStandardOptions("SPACE_AFTER_COLON");
            consumer.showStandardOptions("SPACE_BEFORE_COLON");
            consumer.showStandardOptions("SPACE_WITHIN_BRACKETS");

        } else if (settingsType == SettingsType.INDENT_SETTINGS) {
            consumer.showStandardOptions("INDENT_SIZE");
        }
    }

    @Override
    public @NotNull IndentOptionsEditor getIndentOptionsEditor() {
        return new IndentOptionsEditor(this);
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return SchemaDemo.getCodeStyleText();
    }

}
