package org.openspg.idea.schema.ui.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.TextEditorWithPreview;
import org.jetbrains.annotations.NotNull;

public class SchemaSplitEditor extends TextEditorWithPreview  {

    public SchemaSplitEditor(@NotNull TextEditor editor, @NotNull FileEditor preview) {
        super(editor, preview);
        this.setLayout(Layout.SHOW_EDITOR_AND_PREVIEW);
    }

}
