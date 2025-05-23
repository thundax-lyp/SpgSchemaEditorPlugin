package org.openspg.idea.schema;

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
import org.openspg.idea.schema.lang.psi.SchemaEntity;
import org.openspg.idea.schema.lang.psi.SchemaEntityMeta;
import org.openspg.idea.schema.lang.psi.SchemaPlainTextBlock;

public class SchemaFoldingBuilder extends FoldingBuilderEx implements DumbAware {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        return PsiTreeUtil.findChildrenOfAnyType(root,
                        PsiComment.class,
                        SchemaEntity.class,
                        SchemaEntityMeta.class,
                        SchemaPlainTextBlock.class
                )
                .stream()
                .map(element -> new FoldingDescriptor(element, element.getTextRange()))
                .toArray(FoldingDescriptor[]::new);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        PsiElement element = node.getPsi();
        if (element instanceof PsiComment) {
            return "#...";

        } else if (element instanceof SchemaEntity entity) {
            String placeHolder = entity.getEntityHead().getText();
            if (!entity.isBodyEmpty()) {
                placeHolder += " {...}";
            }
            return placeHolder;

        } else if (element instanceof SchemaEntityMeta entityMeta) {
            String placeHolder = entityMeta.getEntityMetaHead().getText();
            if (!entityMeta.isBodyEmpty()) {
                placeHolder += " {...}";
            }
            return placeHolder;

        } else if (element instanceof SchemaPlainTextBlock) {
            return "[[...]]";
        }

        throw new IllegalArgumentException("Unknown PsiElement type: " + element.getClass());
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        PsiElement element = node.getPsi();
        return element instanceof PsiComment;
    }
}

