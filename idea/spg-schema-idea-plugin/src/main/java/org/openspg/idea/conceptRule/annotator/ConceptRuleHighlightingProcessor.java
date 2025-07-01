package org.openspg.idea.conceptRule.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.common.annotator.AnnotateProcessor;
import org.openspg.idea.conceptRule.highlighter.ConceptRuleHighlightingColors;
import org.openspg.idea.conceptRule.lang.psi.ConceptRuleLabelName;
import org.openspg.idea.conceptRule.lang.psi.ConceptRuleRuleWrapperPattern;

public class ConceptRuleHighlightingProcessor implements AnnotateProcessor {

    public boolean process(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        TextAttributesKey textAttributesKey = null;

        if (element instanceof ConceptRuleLabelName
                && PsiTreeUtil.getParentOfType(element, ConceptRuleRuleWrapperPattern.class) != null) {
            textAttributesKey = ConceptRuleHighlightingColors.WRAPPER_PATTERN;
        }

        if (textAttributesKey != null) {
            holder.newAnnotation(HighlightSeverity.TEXT_ATTRIBUTES, element.getText())
                    .range(element.getTextRange())
                    .textAttributes(textAttributesKey)
                    .create();
        }

        return true;
    }

}
