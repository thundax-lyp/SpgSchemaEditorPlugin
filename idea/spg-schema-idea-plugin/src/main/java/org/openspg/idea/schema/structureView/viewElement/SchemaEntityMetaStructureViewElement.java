package org.openspg.idea.schema.structureView.viewElement;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.lang.psi.SchemaEntityMeta;
import org.openspg.idea.lang.psi.SchemaProperty;
import org.openspg.idea.schema.SchemaIcons;

import java.util.ArrayList;
import java.util.List;

public class SchemaEntityMetaStructureViewElement extends AbstractSchemaStructureViewElement<SchemaEntityMeta> {

    public SchemaEntityMetaStructureViewElement(SchemaEntityMeta element) {
        super(element);
    }

    @Override
    public String getNullableAlphaSortKey() {
        return myElement.getEntityMetaInfo().getName();
    }

    @Override
    protected PresentationData createPresentation(SchemaEntityMeta element) {
        return new PresentationData(
                myElement.getEntityMetaInfo().getName(),
                myElement.getEntityMetaInfo().getValue(),
                element.getPropertyList().isEmpty() ? SchemaIcons.Nodes.EmptyMeta : SchemaIcons.Nodes.EntityMeta,
                null
        );
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<SchemaProperty> elements = PsiTreeUtil.getChildrenOfTypeAsList(myElement, SchemaProperty.class);

        List<TreeElement> treeElements = new ArrayList<>(elements.size());
        for (SchemaProperty element : elements) {
            treeElements.add(new SchemaPropertyStructureViewElement(element));
        }
        return treeElements.toArray(new TreeElement[0]);
    }

}
