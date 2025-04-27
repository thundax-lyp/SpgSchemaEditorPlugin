package org.openspg.idea.schema.ui.editor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class SchemaPreviewEditor extends UserDataHolderBase implements FileEditor, DocumentListener {

    private static final Logger logger = Logger.getInstance(SchemaPreviewEditor.class);

    private final Project myProject;
    private final VirtualFile myFile;
    private final Document myDocument;

    private BorderLayoutPanel myHtmlPanelWrapper;
    private SchemaHtmlPanel myPanel;

    public SchemaPreviewEditor(Project project, VirtualFile file) {
        this.myProject = project;
        this.myFile = file;

        this.myDocument = FileDocumentManager.getInstance().getDocument(myFile);
        assert this.myDocument != null;

        this.myDocument.addDocumentListener(this);

        ApplicationManager.getApplication()
                .getMessageBus()
                .connect(this)
                .subscribe(EditorColorsManager.TOPIC, (EditorColorsListener) scheme -> {
                    if (myPanel != null) {
                        myPanel.updateStyle();
                    }
                });

        logger.info("Schema preview editor initialized");
    }

    @Override
    public @NotNull JComponent getComponent() {
        if (myHtmlPanelWrapper == null) {
            myHtmlPanelWrapper = JBUI.Panels.simplePanel();

            JBLabel loadingLabel = new JBLabel("Loading......");
            myHtmlPanelWrapper.addToCenter(loadingLabel);

            SchemaHtmlPanel tempPanel = null;
            try {
                tempPanel = new SchemaHtmlPanel(myProject, myFile);
                myHtmlPanelWrapper.addToCenter(tempPanel.getComponent());

            } catch (Throwable e) {
                myHtmlPanelWrapper.addToCenter(new JBLabel("<html><body>Your environment does not support JCEF.<br>Check the Registry 'ide.browser.jcef.enabled'.<br>" + e.getMessage() + "<body></html>"));

            } finally {
                myPanel = tempPanel;
                myHtmlPanelWrapper.remove(loadingLabel);
                myHtmlPanelWrapper.repaint();
            }
        }

        return myHtmlPanelWrapper;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "SchemaPreview";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void dispose() {
        if (myDocument != null) {
            myDocument.removeDocumentListener(this);
        }

        if (myPanel != null) {
            Disposer.dispose(myPanel);
        }
    }

    @Override
    public @Nullable VirtualFile getFile() {
        return myFile;
    }


    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        if (myPanel != null) {
            PsiDocumentManager.getInstance(myProject).commitDocument(event.getDocument());
            myPanel.updateSchema();
        }
    }

    public void activateEntity(@NotNull String name) {
        if (myPanel != null) {
            myPanel.activateEntity(name);
        }
    }

}
