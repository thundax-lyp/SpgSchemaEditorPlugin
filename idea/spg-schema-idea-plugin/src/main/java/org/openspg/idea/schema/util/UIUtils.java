package org.openspg.idea.schema.util;

import com.intellij.openapi.editor.colors.EditorColorsManager;

public class UIUtils {

    public static boolean isDarkTheme() {
        return EditorColorsManager.getInstance().isDarkEditor();
    }

}
