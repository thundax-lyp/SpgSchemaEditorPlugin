package org.openspg.idea.schema.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.common.annotator.AnnotateProcessor;
import org.openspg.idea.schema.annotator.processor.SchemaEntitySemanticNameProcessor;
import org.openspg.idea.schema.annotator.processor.SchemaHighlightingProcessor;

import java.util.HashSet;
import java.util.Set;

final class SchemaAnnotator implements Annotator {

    private final Set<AnnotateProcessor> myProcessors;

    public SchemaAnnotator() {
        myProcessors = new HashSet<>();
        myProcessors.add(new SchemaHighlightingProcessor());
        myProcessors.add(new SchemaEntitySemanticNameProcessor());
    }

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        for (AnnotateProcessor processor : myProcessors) {
            if (!processor.process(element, holder)) {
                return;
            }
        }
    }

}
