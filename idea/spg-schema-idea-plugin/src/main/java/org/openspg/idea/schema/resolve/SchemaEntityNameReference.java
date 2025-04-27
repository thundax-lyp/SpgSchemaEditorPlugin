package org.openspg.idea.schema.resolve;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.grammar.psi.SchemaTypes;
import org.openspg.idea.lang.psi.SchemaEntity;
import org.openspg.idea.lang.psi.SchemaReferencableEntityName;
import org.openspg.idea.schema.SchemaIcons;
import org.openspg.idea.schema.util.PsiUtils;

import java.util.ArrayList;
import java.util.List;

public class SchemaEntityNameReference extends PsiPolyVariantReferenceBase<SchemaReferencableEntityName> {

    private final String entityName;

    public SchemaEntityNameReference(@NotNull SchemaReferencableEntityName element) {
        super(element, new TextRange(0, element.getTextLength()));
        this.entityName = element.getText();
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();

        List<PsiElement> elements = PsiUtils.searchChildrenOfAnyType(
                myElement.getContainingFile(),
                true,
                SchemaTypes.ENTITY_CLASS,
                SchemaTypes.PROPERTY_CLASS
        );
        for (PsiElement element : elements) {
            if (entityName.equals(element.getText())) {
                results.add(new PsiElementResolveResult(element));
            }
        }

        return results.toArray(new ResolveResult[0]);
    }

    @Override
    public Object @NotNull [] getVariants() {
        List<LookupElement> variants = new ArrayList<>();

        List<SchemaEntity> entities = PsiTreeUtil.getChildrenOfTypeAsList(myElement.getContainingFile(), SchemaEntity.class);
        for (SchemaEntity entity : entities) {
            variants.add(LookupElementBuilder
                    .create(entity)
                    .withIcon(SchemaIcons.FILE)
                    .withTypeText(entity.getContainingFile().getName())
            );
        }
        return variants.toArray();
    }

}
