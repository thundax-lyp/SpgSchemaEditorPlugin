package org.openspg.idea.schema.structureView.viewElement;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.lang.psi.SchemaPropertyMeta;
import org.openspg.idea.lang.psi.SchemaSubProperty;
import org.openspg.idea.schema.SchemaIcons;

import java.util.ArrayList;
import java.util.List;

public class SchemaPropertyMetaStructureViewElement extends AbstractSchemaStructureViewElement<SchemaPropertyMeta> {

    public SchemaPropertyMetaStructureViewElement(SchemaPropertyMeta element) {
        super(element);
    }

    @Override
    public String getNullableAlphaSortKey() {
        return myElement.getPropertyMetaInfo().getName();
    }

    @Override
    protected PresentationData createPresentation(SchemaPropertyMeta element) {
        return new PresentationData(
                element.getPropertyMetaInfo().getName(),
                element.getPropertyMetaInfo().getValue(),
                element.getSubPropertyList().isEmpty() ? SchemaIcons.Nodes.EmptyMeta: SchemaIcons.Nodes.PropertyMeta,
                null
        );
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<SchemaSubProperty> elements = PsiTreeUtil.getChildrenOfTypeAsList(myElement, SchemaSubProperty.class);

        List<TreeElement> treeElements = new ArrayList<>(elements.size());
        for (SchemaSubProperty element : elements) {
            treeElements.add(new SchemaSubPropertyStructureViewElement(element));
        }
        return treeElements.toArray(new TreeElement[0]);
    }

}
