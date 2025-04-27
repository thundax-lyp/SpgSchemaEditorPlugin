package org.openspg.idea.schema.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.grammar.psi.SchemaTypes;
import org.openspg.idea.lang.psi.SchemaEntityInfo;

import java.util.Collection;

final class SchemaCompletionContributor extends CompletionContributor {

    SchemaCompletionContributor() {
        this.extendCompletionForEntityClass();
        this.extendCompletionForPropertyClass();
        this.extendCompletionForBuiltinType();
    }

    private void extendCompletionForEntityClass() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SchemaTypes.ENTITY_CLASS),
                new CompletionProvider<>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("EntityType"));
                        resultSet.addElement(LookupElementBuilder.create("ConceptType"));
                        resultSet.addElement(LookupElementBuilder.create("EventType"));
                        resultSet.addElement(LookupElementBuilder.create("StandardType"));
                        resultSet.addElement(LookupElementBuilder.create("BasicType"));

                        PsiElement inheritedElement = PsiTreeUtil.findSiblingBackward(parameters.getPosition(), SchemaTypes.INHERITED, null);
                        if (inheritedElement != null) {
                            SchemaEntityInfo currentInfo = (SchemaEntityInfo) PsiTreeUtil.findFirstParent(parameters.getPosition(), SchemaEntityInfo.class::isInstance);
                            String currentEntityName = currentInfo == null ? null : currentInfo.getEntityName();
                            Collection<SchemaEntityInfo> infos = PsiTreeUtil.findChildrenOfType(parameters.getOriginalFile(), SchemaEntityInfo.class);
                            for (SchemaEntityInfo info : infos) {
                                if (info.getEntityName() != null && !info.getEntityName().equals(currentEntityName)) {
                                    resultSet.addElement(LookupElementBuilder.create(info.getEntityName()));
                                }
                            }
                        }
                    }
                }
        );
    }

    private void extendCompletionForPropertyClass() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(SchemaTypes.PROPERTY_CLASS), new CompletionProvider<>() {
            public void addCompletions(@NotNull CompletionParameters parameters,
                                       @NotNull ProcessingContext context,
                                       @NotNull CompletionResultSet resultSet) {
                resultSet.addElement(LookupElementBuilder.create("Text"));
                resultSet.addElement(LookupElementBuilder.create("Float"));
                resultSet.addElement(LookupElementBuilder.create("Integer"));
            }
        });
    }

    private void extendCompletionForBuiltinType() {
        extend(CompletionType.SMART, PlatformPatterns.psiElement(SchemaTypes.BUILTIN_TYPE), new CompletionProvider<>() {
            public void addCompletions(@NotNull CompletionParameters parameters,
                                       @NotNull ProcessingContext context,
                                       @NotNull CompletionResultSet resultSet) {
                //PsiElement parentElement = parameters.getPosition().getParent();

                //resultSet.addElement(LookupElementBuilder.create("Text"));
                //resultSet.addElement(LookupElementBuilder.create("Float"));
                //resultSet.addElement(LookupElementBuilder.create("Integer"));
            }
        });
    }

}
