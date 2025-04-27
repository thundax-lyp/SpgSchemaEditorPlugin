package org.openspg.idea.schema.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openspg.idea.schema.SchemaIcons;
import org.openspg.idea.schema.demo.SchemaDemo;

import javax.swing.*;
import java.util.Map;

final class SchemaColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Base//Keywords", SchemaHighlightingColors.KEYWORD),
            new AttributesDescriptor("Base//Operation", SchemaHighlightingColors.OPERATION_SIGN),
            new AttributesDescriptor("Schema//Name", SchemaHighlightingColors.ENTITY_NAME),
            new AttributesDescriptor("Schema//Alias name", SchemaHighlightingColors.ENTITY_ALIAS),
            new AttributesDescriptor("Schema//Type", SchemaHighlightingColors.ENTITY_REFERENCE),
            new AttributesDescriptor("Others//Comment", SchemaHighlightingColors.COMMENT),
            new AttributesDescriptor("Others//Error", SchemaHighlightingColors.ERROR),
    };

    @Override
    public Icon getIcon() {
        return SchemaIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new SchemaSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return SchemaDemo.getHighlighterText();
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "OpenSPG Schema";
    }

}
