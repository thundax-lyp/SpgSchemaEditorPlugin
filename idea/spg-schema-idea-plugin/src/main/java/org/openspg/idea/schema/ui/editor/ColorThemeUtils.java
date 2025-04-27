package org.openspg.idea.schema.ui.editor;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.schema.highlighter.SchemaHighlightingColors;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Map;

public class ColorThemeUtils {

    public static String getCssStyle() {
        try {
            EditorColorsScheme editorColorsScheme = EditorColorsManager.getInstance().getGlobalScheme();

            Map<String, String> stylesheet = new LinkedHashMap<>();

            stylesheet.put("--bg-color", toCssColor(editorColorsScheme.getDefaultBackground()));
            stylesheet.put("--text-color", toCssColor(editorColorsScheme.getDefaultForeground()));

            stylesheet.put("--keyword-color", readForegroundColorAsCss(editorColorsScheme, SchemaHighlightingColors.KEYWORD));
            stylesheet.put("--entity-name-color", readForegroundColorAsCss(editorColorsScheme, SchemaHighlightingColors.ENTITY_NAME));
            stylesheet.put("--entity-alias-color", readForegroundColorAsCss(editorColorsScheme, SchemaHighlightingColors.ENTITY_ALIAS));
            stylesheet.put("--entity-reference-color", readForegroundColorAsCss(editorColorsScheme, SchemaHighlightingColors.ENTITY_REFERENCE));

            StringBuilder sb = new StringBuilder(":root {\n");
            for (Map.Entry<String, String> entry : stylesheet.entrySet()) {
                if (entry.getValue() != null) {
                    sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(";\n");
                }
            }
            sb.append("}\n");

            return sb.toString();

        } catch (Exception e) {
            return "";
        }

    }

    private static String readForegroundColorAsCss(EditorColorsScheme editorColorsScheme, TextAttributesKey attributesKey) {
        TextAttributes textAttributes = editorColorsScheme.getAttributes(attributesKey);
        if (textAttributes != null) {
            Color color = textAttributes.getForegroundColor();
            return toCssColor(color);
        }
        return null;
    }

    private static String toCssColor(@NotNull Color color) {
        if (color.getAlpha() == 255) {
            return String.format("rgb(%s,%s,%s)", color.getRed(), color.getGreen(), color.getBlue());
        }

        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return String.format("rgba(%s,%s,%s,%s)",
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                df.format(color.getAlpha() / (float) 255)
        );
    }

}
