package org.openspg.idea.schema.psi;

import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.conceptRule.ConceptRuleLanguage;
import org.openspg.idea.schema.lang.psi.SchemaPlainTextBlock;

public final class SchemaLanguageInjector implements LanguageInjector {

    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (host instanceof SchemaPlainTextBlock block) {
            injectionPlacesRegistrar.addPlace(
                    ConceptRuleLanguage.INSTANCE,
                    block.getInjectTextRange(),
                    "",
                    ""
            );
        }
    }
}
