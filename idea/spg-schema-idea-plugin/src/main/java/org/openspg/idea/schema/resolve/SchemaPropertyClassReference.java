package org.openspg.idea.schema.resolve;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openspg.idea.grammar.psi.SchemaTypes;
import org.openspg.idea.lang.psi.SchemaEntity;
import org.openspg.idea.lang.psi.SchemaReferencablePropertyClass;
import org.openspg.idea.schema.util.PsiUtils;

public class SchemaPropertyClassReference extends PsiReferenceBase<SchemaReferencablePropertyClass> {

    private final String entityName;

    public SchemaPropertyClassReference(@NotNull SchemaReferencablePropertyClass element) {
        super(element, new TextRange(0, element.getTextLength()));
        this.entityName = element.getText();
    }

    @Override
    public @Nullable PsiElement resolve() {
        final SchemaEntity entity = PsiUtils.findEntityByName(myElement.getContainingFile(), entityName);
        if (entity != null) {
            ASTNode node = entity.getEntityInfo().getReferencableEntityName().getNode().findChildByType(SchemaTypes.ENTITY_NAME);
            assert node != null;
            return node.getPsi();
        }
        return null;
    }

}
