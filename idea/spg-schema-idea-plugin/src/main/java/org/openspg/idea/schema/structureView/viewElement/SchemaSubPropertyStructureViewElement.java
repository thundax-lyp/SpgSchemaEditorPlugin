package org.openspg.idea.schema.structureView.viewElement;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.lang.psi.SchemaSubProperty;
import org.openspg.idea.lang.psi.SchemaSubPropertyMeta;
import org.openspg.idea.schema.SchemaIcons;

import java.util.ArrayList;
import java.util.List;

public class SchemaSubPropertyStructureViewElement extends AbstractSchemaStructureViewElement<SchemaSubProperty> {

    public SchemaSubPropertyStructureViewElement(SchemaSubProperty element) {
        super(element);
    }

    @Override
    public String getNullableAlphaSortKey() {
        return myElement.getSubPropertyInfo().getPropertyName();
    }

    @Override
    protected PresentationData createPresentation(SchemaSubProperty element) {
        return new PresentationData(
                myElement.getSubPropertyInfo().getPropertyName(),
                myElement.getSubPropertyInfo().getPropertyAliasName(),
                SchemaIcons.Nodes.SubProperty,
                null
        );
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<SchemaSubPropertyMeta> elements = PsiTreeUtil.getChildrenOfTypeAsList(myElement, SchemaSubPropertyMeta.class);

        List<TreeElement> treeElements = new ArrayList<>(elements.size());

        for (SchemaSubPropertyMeta element : elements) {
            treeElements.add(new SchemaSubPropertyMetaStructureViewElement(element));
        }

        return treeElements.toArray(new TreeElement[0]);
    }

}
