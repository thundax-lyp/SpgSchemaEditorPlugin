package org.openspg.idea.schema;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openspg.idea.grammar.psi.SchemaTypes;
import org.openspg.idea.lang.psi.SchemaEntityInfo;
import org.openspg.idea.schema.lexer.SchemaLexerAdapter;

public class SchemaFindUsagesProvider implements FindUsagesProvider {

    TokenSet comments = TokenSet.create(
            SchemaTypes.COMMENT, SchemaTypes.LINE_COMMENT
    );

    TokenSet identifiers = TokenSet.create(
            SchemaTypes.ENTITY_NAME, SchemaTypes.ENTITY_CLASS, SchemaTypes.PROPERTY_CLASS
    );

    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new SchemaLexerAdapter(), identifiers, comments, TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return "getHelpId: reference.dialogs.schema";
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof SchemaEntityInfo) {
            return "Schema Entity";
        }
        return "";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof SchemaEntityInfo info) {
            return info.getEntityName();
        }
        return "";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        //System.out.println("getNodeText: " + element + " " + useFullName);
        //if (element instanceof SchemaEntityInfo info) {
        //    String nodeText = info.getEntityName() + ":" + info.getEntityClassList();
        //    System.out.println("   nodeText: " + nodeText);
        //    return nodeText;
        //}
        return "";
    }
}
