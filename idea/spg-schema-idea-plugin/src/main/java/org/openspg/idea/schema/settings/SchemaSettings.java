package org.openspg.idea.schema.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;


@Service(Service.Level.PROJECT)
@State(name = "SchemaSettings", storages = {@Storage("schema.xml")})
public final class SchemaSettings implements PersistentStateComponent<SchemaSettings> {

    private static final Logger logger = Logger.getInstance(SchemaSettings.class);

    private boolean verticalSplit = false;

    public static SchemaSettings getInstance(@NotNull Project project) {
        return project.getService(SchemaSettings.class);
    }

    public boolean isVerticalSplit() {
        return this.verticalSplit;
    }

    public void setVerticalSplit(boolean verticalSplit) {
        this.verticalSplit = verticalSplit;
    }

    @Override
    public @NotNull SchemaSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SchemaSettings state) {
        XmlSerializerUtil.copyBean(state, this);
        ApplicationManager.getApplication().getMessageBus().syncPublisher(SchemaSettings.SettingsChangedListener.TOPIC).onSettingsChange(this);
    }


    public interface SettingsChangedListener {
        Topic<SettingsChangedListener> TOPIC = Topic.create("SchemaApplicationSettingsChanged", SettingsChangedListener.class);

        void onSettingsChange(@NotNull SchemaSettings settings);
    }
}
