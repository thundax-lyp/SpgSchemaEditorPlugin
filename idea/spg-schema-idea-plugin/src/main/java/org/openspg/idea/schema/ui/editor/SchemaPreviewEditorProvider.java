package org.openspg.idea.schema.ui.editor;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.impl.DefaultPlatformFileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefApp;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SchemaPreviewEditorProvider implements DefaultPlatformFileEditorProvider, DumbAware {

    public static final ExtensionPointName<SchemaPreviewEditorProvider> EP_NAME =
            new ExtensionPointName<>("org.openspg.schema.previewEditorProvider");

    public static SchemaPreviewEditorProvider getInstance() {
        return EP_NAME.findExtension(SchemaPreviewEditorProvider.class);
    }

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return JBCefApp.isSupported();
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new SchemaPreviewEditor(project, file);
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return "schema-preview-editor";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
