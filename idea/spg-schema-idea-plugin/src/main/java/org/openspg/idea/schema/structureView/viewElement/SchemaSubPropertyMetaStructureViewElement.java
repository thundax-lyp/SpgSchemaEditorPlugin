package org.openspg.idea.schema.structureView.viewElement;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.lang.psi.SchemaSubPropertyMeta;
import org.openspg.idea.schema.SchemaIcons;

public class SchemaSubPropertyMetaStructureViewElement extends AbstractSchemaStructureViewElement<SchemaSubPropertyMeta> {

    public SchemaSubPropertyMetaStructureViewElement(SchemaSubPropertyMeta element) {
        super(element);
    }

    @Override
    public String getNullableAlphaSortKey() {
        return myElement.getName();
    }

    @Override
    protected PresentationData createPresentation(SchemaSubPropertyMeta element) {
        return new PresentationData(
                myElement.getName(),
                myElement.getValue(),
                SchemaIcons.Nodes.SubPropertyMeta,
                null);
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        return TreeElement.EMPTY_ARRAY;
    }

}
