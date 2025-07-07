package org.openspg.idea.conceptRule.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openspg.idea.conceptRule.lang.psi.ConceptRuleRuleWrapperBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.openspg.idea.conceptRule.grammar.psi.ConceptRuleTypes.*;


public class ConceptRuleBlock extends AbstractBlock {

    private static final Set<IElementType> NONBLOCK_ELEMENT_TYPES = Set.of(
            TokenType.WHITE_SPACE, EOL
    );

    private static final Set<IElementType> NORMAL_INDENT_BLOCK_ELEMENT_TYPES = Set.of(
            RULE_WRAPPER_BODY, BASE_RULE_DEFINE, RULE_EXPRESSION_BODY, PATH_PATTERN_LIST, CREATE_ACTION_BODY,
            ADD_EDGE_PARAM, ADD_TYPE, ADD_PROPS
    );

    private final Indent myIndent;
    private final SpacingBuilder mySpacingBuilder;

    protected ConceptRuleBlock(
            @NotNull ASTNode node,
            @Nullable Wrap wrap,
            @Nullable Alignment alignment,
            @Nullable SpacingBuilder spacingBuilder
    ) {
        this(node, wrap, alignment, spacingBuilder, null);
    }

    protected ConceptRuleBlock(
            @NotNull ASTNode node,
            @Nullable Wrap wrap,
            @Nullable Alignment alignment,
            @Nullable SpacingBuilder spacingBuilder,
            @Nullable Indent indent
    ) {
        super(node, wrap, alignment);
        mySpacingBuilder = spacingBuilder;
        myIndent = indent;
        this.setBuildIndentsOnly(true);
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();

        ASTNode[] children = myNode.getChildren(null);
        for (ASTNode child : children) {
            Block childBlock = buildBlock(child);
            if (childBlock != null) {
                blocks.add(childBlock);
            }
        }

        return blocks;
    }

    private Block buildBlock(ASTNode node) {
        IElementType type = node.getElementType();
        if (NONBLOCK_ELEMENT_TYPES.contains(type)) {
            return null;
        }

        Indent indent = null;
        if (NORMAL_INDENT_BLOCK_ELEMENT_TYPES.contains(type)) {
            indent = Indent.getNormalIndent();

        } else if (type == THE_DEFINE_STRUCTURE) {
            PsiElement parent = node.getPsi().getParent();
            if (parent instanceof ConceptRuleRuleWrapperBody) {
                indent = Indent.getNormalIndent();
            }
        }

        return new ConceptRuleBlock(
                node,
                Wrap.createWrap(WrapType.NONE, false),
                Alignment.createAlignment(),
                mySpacingBuilder,
                indent
        );
    }

    @Override
    public Indent getIndent() {
        return myIndent == null ? Indent.getNoneIndent() : myIndent;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block firstChild, @NotNull Block secondChild) {
        return mySpacingBuilder == null ? null : mySpacingBuilder.getSpacing(this, firstChild, secondChild);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

}
