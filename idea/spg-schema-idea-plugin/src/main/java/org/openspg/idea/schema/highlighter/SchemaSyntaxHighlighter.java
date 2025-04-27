package org.openspg.idea.schema.highlighter;


import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.grammar.psi.SchemaTypes;
import org.openspg.idea.schema.lexer.SchemaLexerAdapter;


public class SchemaSyntaxHighlighter extends SyntaxHighlighterBase {

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new SchemaLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
//        IElementType iteratorType = tokenType;
//        while (iteratorType instanceof ParentProviderElementType parentProviderElementType) {
//            if (parentProviderElementType.getParents().size() == 1) {
//                iteratorType = parentProviderElementType.getParents().iterator().next();
//            }
//        }
//        System.out.println(iteratorType.getDebugName());

        if (tokenType.equals(SchemaTypes.NAMESPACE_MARKER)
                || tokenType.equals(SchemaTypes.ENTITY_BUILTIN_CLASS)
                || tokenType.equals(SchemaTypes.BUILTIN_TYPE)
                || tokenType.equals(SchemaTypes.META_TYPE)
        ) {
            return pack(SchemaHighlightingColors.KEYWORD);
        }

        if (tokenType.equals(SchemaTypes.COMMENT) || tokenType.equals(SchemaTypes.LINE_COMMENT)) {
            return pack(SchemaHighlightingColors.COMMENT);
        }

        if (tokenType.equals(SchemaTypes.COLON)
                || tokenType.equals(SchemaTypes.INHERITED)
                || tokenType.equals(SchemaTypes.COMMA)
                || tokenType.equals(SchemaTypes.OPEN_BRACKET)
                || tokenType.equals(SchemaTypes.CLOSE_BRACKET)
                || tokenType.equals(SchemaTypes.OPEN_PLAIN_BLOCK)
                || tokenType.equals(SchemaTypes.CLOSE_PLAIN_BLOCK)
        ) {
            return pack(SchemaHighlightingColors.OPERATION_SIGN);
        }

        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return pack(SchemaHighlightingColors.ERROR);
        }

        if (tokenType.equals(SchemaTypes.ENTITY_NAME)) {
            return pack(SchemaHighlightingColors.ENTITY_NAME);
        }

        if (tokenType.equals(SchemaTypes.ENTITY_ALIAS_NAME)
                || tokenType.equals(SchemaTypes.PROPERTY_ALIAS_NAME)
        ) {
            return pack(SchemaHighlightingColors.ENTITY_ALIAS);
        }

        if (tokenType.equals(SchemaTypes.PROPERTY_CLASS)
                || tokenType.equals(SchemaTypes.ENTITY_CLASS)
        ) {
            return pack(SchemaHighlightingColors.ENTITY_REFERENCE);
        }

        return pack(null);
    }

}
