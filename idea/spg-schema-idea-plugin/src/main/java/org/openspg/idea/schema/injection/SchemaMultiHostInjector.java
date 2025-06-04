package org.openspg.idea.schema.injection;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.conceptRule.ConceptRuleLanguage;
import org.openspg.idea.schema.lang.psi.SchemaPlainTextContent;

import java.util.List;

public final class SchemaMultiHostInjector implements MultiHostInjector {

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (context instanceof SchemaPlainTextContent content) {
            registrar.startInjecting(ConceptRuleLanguage.INSTANCE)
                    .addPlace(
                            "",
                            "",
                            content,
                            TextRange.from(0, content.getTextLength())
                    )
                    .doneInjecting();
        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return List.of(SchemaPlainTextContent.class);
    }
}
