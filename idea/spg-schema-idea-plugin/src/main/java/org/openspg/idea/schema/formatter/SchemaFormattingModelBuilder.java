package org.openspg.idea.schema.formatter;

import com.intellij.formatting.*;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.schema.SchemaLanguage;

import static org.openspg.idea.schema.grammar.psi.SchemaTypes.*;

public final class SchemaFormattingModelBuilder implements FormattingModelBuilder {

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        final CodeStyleSettings codeStyleSettings = formattingContext.getCodeStyleSettings();
        return FormattingModelProvider.createFormattingModelForPsiFile(
                formattingContext.getContainingFile(),
                new SchemaBlock(
                        formattingContext.getNode(),
                        Wrap.createWrap(WrapType.NONE, false),
                        Alignment.createAlignment(),
                        createSpaceBuilder(codeStyleSettings)
                ),
                codeStyleSettings
        );
    }

    private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
        CommonCodeStyleSettings commonSetting = settings.getCommonSettings(SchemaLanguage.INSTANCE.getID());

        SpacingBuilder builder = new SpacingBuilder(settings, SchemaLanguage.INSTANCE);
        builder = builder.after(NAMESPACE_KEYWORD).spaces(1);

        int blankLinesAfterNamespace = Math.max(0, commonSetting.BLANK_LINES_AFTER_PACKAGE) + 1;
        builder = builder.after(TokenSet.create(NAMESPACE))
                .spacing(0, 0, blankLinesAfterNamespace, false, 0);

        int blankLinesAfterEntity = Math.max(0, commonSetting.BLANK_LINES_AFTER_IMPORTS) + 1;
        builder = builder.after(TokenSet.create(ENTITY))
                .spacing(0, 0, blankLinesAfterEntity, false, 0);

        builder = builder
                .before(TokenSet.create(DOUBLE_LBRACKET))
                .spacing(1, 1, 0, false, 0)
                .after(TokenSet.create(DOUBLE_LBRACKET))
                .spacing(0, 0, 1, false, 0)
                .around(TokenSet.create(DOUBLE_RBRACKET))
                .spacing(0, 0, 1, false, 0);

        builder = initSpaceBuilderByComma(builder, commonSetting);
        builder = initSpaceBuilderByColon(builder, commonSetting);

        // non-blank tokens
        TokenSet nonBlankLineTokens = TokenSet.create(
                ENTITY_HEAD, ENTITY_META, ENTITY_META_HEAD, PROPERTY, PROPERTY_HEAD, PROPERTY_META, PROPERTY_META_HEAD, SUB_PROPERTY
        );
        builder = builder
                .after(nonBlankLineTokens)
                .spacing(0, 0, 1, false, 0);

        return builder;
    }

    private static SpacingBuilder initSpaceBuilderByComma(SpacingBuilder builder, CommonCodeStyleSettings settings) {
        boolean spaceAfterComma = settings.SPACE_AFTER_COMMA;
        boolean spaceBeforeComma = settings.SPACE_BEFORE_COMMA;

        if (spaceBeforeComma && !spaceAfterComma) {
            builder = builder.before(COMMA).spaces(1);
        } else if (spaceBeforeComma) {
            builder = builder.around(COMMA).spaces(1);
        } else if (spaceAfterComma) {
            builder = builder.after(COMMA).spaces(1);
        } else {
            builder = builder.before(COMMA).spaces(0);
        }
        return builder;
    }

    private static SpacingBuilder initSpaceBuilderByColon(SpacingBuilder builder, CommonCodeStyleSettings settings) {
        boolean spaceAfterColon = settings.SPACE_AFTER_COLON;
        boolean spaceBeforeColon = settings.SPACE_BEFORE_COLON;

        TokenSet tokenSet = TokenSet.create(COLON, RIGHT_ARROW);
        if (spaceBeforeColon && !spaceAfterColon) {
            builder = builder.before(tokenSet).spaces(1);
        } else if (spaceBeforeColon) {
            builder = builder.around(tokenSet).spaces(1);
        } else if (spaceAfterColon) {
            builder = builder.after(tokenSet).spaces(1);
        } else {
            builder = builder.before(tokenSet).spaces(0);
        }
        return builder;
    }

}
