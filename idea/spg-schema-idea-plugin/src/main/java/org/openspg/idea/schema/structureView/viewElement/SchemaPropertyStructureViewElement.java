package org.openspg.idea.schema.structureView.viewElement;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.schema.SchemaIcons;
import org.openspg.idea.schema.psi.SchemaEntity;
import org.openspg.idea.schema.psi.SchemaProperty;
import org.openspg.idea.schema.psi.SchemaPropertyHead;

import java.util.List;

public class SchemaPropertyStructureViewElement extends AbstractSchemaStructureViewElement<SchemaProperty> {

    public SchemaPropertyStructureViewElement(SchemaProperty element) {
        super(element);
    }

    @Override
    public String getNullableAlphaSortKey() {
        return myElement.getPropertyHead().getBasicPropertyDeclaration().getText();
    }

    @Override
    protected PresentationData createPresentation(SchemaProperty element) {
        SchemaPropertyHead head = element.getPropertyHead();
        return new PresentationData(
                head.getBasicPropertyDeclaration().getText(),
                head.getBasicPropertyDeclaration().getText(),
                SchemaIcons.Nodes.Entity,
                null
        );
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<SchemaEntity> elements = PsiTreeUtil.getChildrenOfTypeAsList(myElement, SchemaEntity.class);
        return this.buildEntityTreeElements(elements);
    }

}
