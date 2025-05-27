package org.openspg.idea.schema.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.openspg.idea.schema.grammar.psi.SchemaTypes.*;


public class SchemaBlock extends AbstractBlock {

    private static final Set<IElementType> NONBLOCK_ELEMENT_TYPES = Set.of(
            TokenType.WHITE_SPACE
    );

    private static final Set<IElementType> NONE_INDENT_BLOCK_ELEMENT_TYPES = Set.of(
            NAMESPACE, ENTITY
    );

    private static final Set<IElementType> NORMAL_INDENT_BLOCK_ELEMENT_TYPES = Set.of(
            ENTITY_META, PROPERTY, PROPERTY_META, SUB_PROPERTY, SUB_PROPERTY_META
    );

    private final Indent myIndent;
    private final SpacingBuilder mySpacingBuilder;

    protected SchemaBlock(
            @NotNull ASTNode node,
            @Nullable Wrap wrap,
            @Nullable Alignment alignment,
            @Nullable SpacingBuilder spacingBuilder
    ) {
        this(node, wrap, alignment, spacingBuilder, null);
    }

    protected SchemaBlock(
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
        if (NONE_INDENT_BLOCK_ELEMENT_TYPES.contains(type)) {
            indent = Indent.getNoneIndent();
        } else if (NORMAL_INDENT_BLOCK_ELEMENT_TYPES.contains(type)) {
            indent = Indent.getNormalIndent();
        }

        return new SchemaBlock(
                node,
                Wrap.createWrap(WrapType.NONE, true),
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
