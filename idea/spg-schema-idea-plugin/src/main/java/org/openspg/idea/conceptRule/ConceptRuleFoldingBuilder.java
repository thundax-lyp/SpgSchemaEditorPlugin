package org.openspg.idea.conceptRule;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openspg.idea.conceptRule.lang.psi.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public class ConceptRuleFoldingBuilder extends FoldingBuilderEx implements DumbAware {

    private final Set<FoldingAdapter<? extends PsiElement>> adapters = Set.of(
            new ConceptRuleCommentFoldingAdapter(),
            new ConceptRuleRuleWrapperFoldingAdapter(),
            new ConceptRuleTheDefineStructureFoldingAdapter(),
            new ConceptRuleTheGraphStructureFoldingAdapter(),
            new ConceptRuleTheRuleFoldingAdapter(),
            new ConceptRuleCreateActionFoldingAdapter()
    );

    @Override
    @SuppressWarnings("unchecked")
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        Class<? extends PsiElement>[] classes = adapters
                .stream()
                .map(FoldingAdapter::getType)
                .toArray(Class[]::new);

        return PsiTreeUtil.findChildrenOfAnyType(root, classes)
                .stream()
                .map(element -> new FoldingDescriptor(element, element.getTextRange()))
                .toArray(FoldingDescriptor[]::new);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        PsiElement element = node.getPsi();
        for (FoldingAdapter<? extends PsiElement> adapter : adapters) {
            if (adapter.getType().isInstance(element)) {
                return adapter.getPlaceholderText(node, element);
            }
        }
        throw new IllegalArgumentException("Unknown PsiElement type: " + element.getClass());
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return node.getPsi() instanceof PsiComment;
    }

    public abstract static class FoldingAdapter<T extends PsiElement> {

        @SuppressWarnings("unchecked")
        public Class<T> getType() {
            Type superClass = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) superClass).getActualTypeArguments();
            return (Class<T>) params[0];
        }

        @SuppressWarnings("unchecked")
        public String getPlaceholderText(@NotNull ASTNode node, @NotNull PsiElement element) {
            assert getType().isInstance(element);
            return getPlaceholderText((T) element);
        }

        protected abstract String getPlaceholderText(T element);
    }

    public static class ConceptRuleCommentFoldingAdapter extends FoldingAdapter<PsiComment> {
        @Override
        protected String getPlaceholderText(@NotNull PsiComment element) {
            return "#...";
        }
    }

    public static class ConceptRuleRuleWrapperFoldingAdapter extends FoldingAdapter<ConceptRuleRuleWrapper> {
        @Override
        protected String getPlaceholderText(@NotNull ConceptRuleRuleWrapper element) {
            String placeHolder = element.getRuleWrapperHead().getText();
            if (element.getRuleWrapperBody() != null) {
                placeHolder += " ...";
            }
            return placeHolder;
        }
    }

    public static class ConceptRuleTheDefineStructureFoldingAdapter extends FoldingAdapter<ConceptRuleTheDefineStructure> {
        @Override
        protected String getPlaceholderText(@NotNull ConceptRuleTheDefineStructure element) {
            return "Define " + element.getPredicatedDefine().getText() + " {...}";
        }
    }

    public static class ConceptRuleTheGraphStructureFoldingAdapter extends FoldingAdapter<ConceptRuleTheGraphStructure> {
        @Override
        protected String getPlaceholderText(@NotNull ConceptRuleTheGraphStructure element) {
            return element.getGraphStructureHead().getText() + " {...}";
        }
    }

    public static class ConceptRuleTheRuleFoldingAdapter extends FoldingAdapter<ConceptRuleTheRule> {
        @Override
        protected String getPlaceholderText(@NotNull ConceptRuleTheRule element) {
            return element.getRuleHead().getText() + " {...}";
        }
    }

    public static class ConceptRuleCreateActionFoldingAdapter extends FoldingAdapter<ConceptRuleCreateAction> {
        @Override
        protected String getPlaceholderText(@NotNull ConceptRuleCreateAction element) {
            return element.getCreateActionSymbol().getText() + " {...}";
        }
    }

}

