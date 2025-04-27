package org.openspg.idea.schema.structureView.viewElement;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.lang.psi.SchemaEntity;
import org.openspg.idea.lang.psi.SchemaEntityInfo;
import org.openspg.idea.lang.psi.SchemaEntityMeta;
import org.openspg.idea.schema.SchemaIcons;

import java.util.ArrayList;
import java.util.List;

public class SchemaEntityStructureViewElement extends AbstractSchemaStructureViewElement<SchemaEntity> {

    public SchemaEntityStructureViewElement(SchemaEntity element) {
        super(element);
    }

    @Override
    public String getNullableAlphaSortKey() {
        return myElement.getName();
    }

    @Override
    protected PresentationData createPresentation(SchemaEntity element) {
        SchemaEntityInfo info = element.getEntityInfo();
        return new PresentationData(
                info.getEntityName(),
                info.getEntityAliasName(),
                SchemaIcons.Nodes.Entity,
                null
        );
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<SchemaEntityMeta> elements = PsiTreeUtil.getChildrenOfTypeAsList(myElement, SchemaEntityMeta.class);

        List<TreeElement> treeElements = new ArrayList<>(elements.size());

        for (SchemaEntityMeta element : elements) {
            treeElements.add(new SchemaEntityMetaStructureViewElement(element));
        }

        return treeElements.toArray(new TreeElement[0]);
    }

}
