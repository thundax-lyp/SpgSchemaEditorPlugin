package org.openspg.idea.schema.structureView.viewElement;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.lang.psi.SchemaProperty;
import org.openspg.idea.lang.psi.SchemaPropertyMeta;
import org.openspg.idea.schema.SchemaIcons;

import java.util.ArrayList;
import java.util.List;

public class SchemaPropertyStructureViewElement extends AbstractSchemaStructureViewElement<SchemaProperty> {

    public SchemaPropertyStructureViewElement(SchemaProperty element) {
        super(element);
    }

    @Override
    public String getNullableAlphaSortKey() {
        return myElement.getPropertyInfo().getPropertyName();
    }

    @Override
    protected PresentationData createPresentation(SchemaProperty element) {
        return new PresentationData(
                myElement.getPropertyInfo().getPropertyName(),
                myElement.getPropertyInfo().getPropertyAliasName(),
                SchemaIcons.Nodes.Property,
                null
        );
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<SchemaPropertyMeta> elements = PsiTreeUtil.getChildrenOfTypeAsList(myElement, SchemaPropertyMeta.class);

        List<TreeElement> treeElements = new ArrayList<>(elements.size());
        for (SchemaPropertyMeta element : elements) {
            treeElements.add(new SchemaPropertyMetaStructureViewElement(element));
        }
        return treeElements.toArray(new TreeElement[0]);
    }

}
