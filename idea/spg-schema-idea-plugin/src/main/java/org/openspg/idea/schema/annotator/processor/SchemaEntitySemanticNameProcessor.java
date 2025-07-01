package org.openspg.idea.schema.annotator.processor;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.common.annotator.AnnotateProcessor;
import org.openspg.idea.schema.SchemaBundle;
import org.openspg.idea.schema.lang.psi.SchemaStructureSemanticName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * validate semantic name
 */
public class SchemaEntitySemanticNameProcessor implements AnnotateProcessor {

    private static final Set<String> SEMANTIC_NAMES = new HashSet<>(Arrays.asList(
            "SYNANT", "CAU", "SEQ", "IND", "INC", "USE"
    ));

    public boolean process(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof SchemaStructureSemanticName) {
            String semanticName = element.getText().toUpperCase();
            if (!SEMANTIC_NAMES.contains(semanticName)) {
                holder.newAnnotation(HighlightSeverity.ERROR, element.getText())
                        .range(element.getTextRange())
                        .tooltip(SchemaBundle.message(
                                "SchemaAnnotator.error.semantic.name.must.be.one.of",
                                StringUtils.join(SEMANTIC_NAMES, ", ")))
                        .create();
                return false;
            }
        }
        return true;
    }


}
