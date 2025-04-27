package org.openspg.idea.schema.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.grammar.psi.SchemaTypes;
import org.openspg.idea.lang.psi.SchemaEntityInfo;
import org.openspg.idea.schema.highlighter.SchemaHighlightingColors;

import java.util.Collection;

final class SchemaAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element.getNode().getElementType() == SchemaTypes.ENTITY_CLASS
                || element.getNode().getElementType() == SchemaTypes.PROPERTY_CLASS) {
            this.handleEntityClass(element, holder);
        }
    }

    private void handleEntityClass(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        String entityClass = element.getText();

        SchemaEntityInfo target = null;
        Collection<SchemaEntityInfo> infos = PsiTreeUtil.findChildrenOfType(element.getContainingFile(), SchemaEntityInfo.class);
        for (SchemaEntityInfo info : infos) {
            if (entityClass.equals(info.getEntityName())) {
                target = info;
                break;
            }
        }

        if (target == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved schema")
                    .range(element.getTextRange())
                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                    //.withFix(new SchemaCreatePropertyQuickFix(entityType))
                    .create();
        } else {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.getTextRange())
                    .textAttributes(SchemaHighlightingColors.ENTITY_REFERENCE)
                    .create();
        }
    }

}
