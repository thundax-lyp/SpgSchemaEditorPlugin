package org.openspg.idea.conceptRule.structureView;

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension;
import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openspg.idea.conceptRule.ConceptRuleLanguage;
import org.openspg.idea.conceptRule.lang.psi.ConceptRuleNamespace;
import org.openspg.idea.conceptRule.lang.psi.ConceptRuleRuleWrapper;
import org.openspg.idea.conceptRule.psi.ConceptRuleFile;
import org.openspg.idea.schema.SchemaIcons;

import javax.swing.*;

public class ConceptRuleStructureAwareNavbar extends StructureAwareNavBarModelExtension {

    @NotNull
    @Override
    protected Language getLanguage() {
        return ConceptRuleLanguage.INSTANCE;
    }

    @Override
    public @Nullable String getPresentableText(Object object) {
        if (object instanceof ConceptRuleFile file) {
            return file.getName();
        }

        if (object instanceof ConceptRuleNamespace namespace) {
            return namespace
                    .getNamespaceValue()
                    .getText();
        }

        if (object instanceof ConceptRuleRuleWrapper ruleWrapper) {
            //TODO
            return ruleWrapper
                    .getRuleWrapperHead()
                    .getRuleWrapperPattern()
                    .getText();
        }

        return null;
    }

    @Override
    @Nullable
    public Icon getIcon(Object object) {
        if (object instanceof ConceptRuleRuleWrapper) {
            return SchemaIcons.Nodes.Entity;
        }

        return null;
    }

}
