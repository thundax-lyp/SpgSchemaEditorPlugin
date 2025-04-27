package org.openspg.idea.schema.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import org.openspg.idea.grammar.psi.SchemaTypes;
import org.openspg.idea.lang.psi.*;
import org.openspg.idea.schema.resolve.SchemaEntityClassReference;
import org.openspg.idea.schema.resolve.SchemaEntityNameReference;
import org.openspg.idea.schema.resolve.SchemaPropertyClassReference;
import org.openspg.idea.schema.util.PsiUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SchemaPsiImplUtil {

    // ============================================
    // SchemaNamespace methods
    //
    public static String getValue(SchemaNamespace element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.TEXT);
        if (node != null) {
            return node.getText();
        } else {
            return null;
        }
    }

    public static Map<String, Object> toJson(SchemaNamespace element) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("value", getValue(element));
        return result;
    }

    // ============================================
    // SchemaEntity methods
    //
    public static String getName(SchemaEntity element) {
        return element.getEntityInfo().getEntityName();
    }

    public static Map<String, Object> toJson(SchemaEntity element) {
        Map<String, Object> result = new LinkedHashMap<>();

        SchemaEntityInfo info = element.getEntityInfo();
        result.put("name", getEntityName(info));
        result.put("aliasName", getEntityAliasName(info));
        result.put("types", getEntityClassList(info));

        result.put("properties", element.getEntityMetaList().stream()
                .map(SchemaPsiImplUtil::toJson).collect(Collectors.toList()));
        return result;
    }

    // ============================================
    // SchemaEntityInfo methods
    //
    public static String getEntityName(SchemaEntityInfo element) {
        ASTNode node = element.getReferencableEntityName().getNode().findChildByType(SchemaTypes.ENTITY_NAME);
        if (node != null) {
            return unwrapText(node.getText());
        } else {
            return null;
        }
    }

    public static String getEntityAliasName(SchemaEntityInfo element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.ENTITY_ALIAS_NAME);
        if (node != null) {
            return unwrapText(node.getText());
        } else {
            return null;
        }
    }

    public static List<String> getEntityClassList(SchemaEntityInfo element) {
        List<PsiElement> elements = PsiUtils.searchChildrenOfAnyType(element, true, SchemaTypes.ENTITY_CLASS, SchemaTypes.ENTITY_BUILTIN_CLASS);
        return elements.stream().map(e -> unwrapText(e.getText())).collect(Collectors.toList());
    }

    public static String getName(SchemaEntityInfo element) {
        return getEntityName(element);
    }

    public static PsiElement setName(SchemaEntityInfo element, String newName) {
        throw new IllegalArgumentException("unsupported operation. setName(SchemaEntityInfo element, String newName)");
        //System.out.println("TODO: SchemaPsiImplUtil.setName()");
        //ASTNode node = element.getNode().findChildByType(SchemaTypes.ENTITY_NAME);
        //if (node != null) {
        //    System.out.println("TODO: SchemaPsiImplUtil.setName(SchemaEntityInfo element, String newName)");
        //    //element.getNode().replaceChild(node, newKeyNode);
        //}
        //return element;
    }

    public static PsiElement getNameIdentifier(SchemaEntityInfo element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.ENTITY_NAME);
        return node != null ? node.getPsi(): null;
    }

    // ============================================
    // SchemaEntityNameReference methods
    //
    public static PsiReference getReference(SchemaReferencableEntityName element) {
        return new SchemaEntityNameReference(element);
    }

    // ============================================
    // SchemaReferencableEntityClass methods
    //
    public static PsiReference getReference(SchemaReferencableEntityClass element) {
        return new SchemaEntityClassReference(element);
    }

    // ============================================
    // SchemaEntityMeta methods
    //
    public static Map<String, Object> toJson(SchemaEntityMeta element) {
        Map<String, Object> result = new LinkedHashMap<>();

        SchemaEntityMetaInfo info = element.getEntityMetaInfo();
        result.put("name", getName(info));
        result.put("value", getValue(info));

        result.put("children", element.getPropertyList().stream()
                .map(SchemaPsiImplUtil::toJson).collect(Collectors.toList()));
        return result;
    }

    // ============================================
    // SchemaEntityMetaInfo methods
    //
    public static String getName(SchemaEntityMetaInfo element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.META_TYPE);
        return node != null ? node.getText(): null;
    }

    public static String getValue(SchemaEntityMetaInfo element) {
        return unwrapText(PsiUtils.readPlainTextAfterNode(element, SchemaTypes.COLON));
    }

    // ============================================
    // SchemaProperty methods
    //
    public static Map<String, Object> toJson(SchemaProperty element) {
        Map<String, Object> result = new LinkedHashMap<>();

        SchemaPropertyInfo info = element.getPropertyInfo();
        result.put("name", getPropertyName(info));
        result.put("aliasName", getPropertyAliasName(info));
        result.put("types", getPropertyClassList(info));

        result.put("properties", element.getPropertyMetaList().stream()
                .map(SchemaPsiImplUtil::toJson).collect(Collectors.toList()));
        return result;
    }

    // ============================================
    // SchemaPropertyInfo methods
    //
    public static String getPropertyName(SchemaPropertyInfo element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.PROPERTY_NAME);
        return node != null ? node.getText(): null;
    }

    public static String getPropertyAliasName(SchemaPropertyInfo element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.PROPERTY_ALIAS_NAME);
        return node != null ? node.getText(): null;
    }

    public static List<String> getPropertyClassList(SchemaPropertyInfo element) {
        List<PsiElement> elements = PsiUtils.searchChildrenOfAnyType(element, true, SchemaTypes.PROPERTY_CLASS, SchemaTypes.BUILTIN_TYPE);
        return elements.stream().map(e -> unwrapText(e.getText())).collect(Collectors.toList());
    }

    // ============================================
    // SchemaReferencablePropertyClass methods
    //
    public static PsiReference getReference(SchemaReferencablePropertyClass element) {
        return new SchemaPropertyClassReference(element);
    }

    // ============================================
    // SchemaPropertyMeta methods
    //
    public static Map<String, Object> toJson(SchemaPropertyMeta element) {
        Map<String, Object> result = new LinkedHashMap<>();

        SchemaPropertyMetaInfo info = element.getPropertyMetaInfo();
        result.put("name", getName(info));
        result.put("value", getValue(info));

        result.put("children", element.getSubPropertyList().stream()
                .map(SchemaPsiImplUtil::toJson).collect(Collectors.toList()));
        return result;
    }

    // ============================================
    // SchemaPropertyInfoMeta methods
    //
    public static String getName(SchemaPropertyMetaInfo element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.META_TYPE);
        return node != null ? node.getText(): null;
    }

    public static String getValue(SchemaPropertyMetaInfo element) {
        return unwrapText(PsiUtils.readPlainTextAfterNode(element, SchemaTypes.COLON));
    }

    // ============================================
    // SchemaSubProperty methods
    //
    public static Map<String, Object> toJson(SchemaSubProperty element) {
        Map<String, Object> result = new LinkedHashMap<>();

        SchemaSubPropertyInfo info = element.getSubPropertyInfo();
        result.put("name", getPropertyName(info));
        result.put("alaisName", getPropertyAliasName(info));
        result.put("types", getPropertyClassList(info));

        result.put("properties", element.getSubPropertyMetaList().stream()
                .map(SchemaPsiImplUtil::toJson).collect(Collectors.toList()));
        return result;
    }

    // ============================================
    // SchemaSubPropertyInfo methods
    //
    public static String getPropertyName(SchemaSubPropertyInfo element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.PROPERTY_NAME);
        return node != null ? node.getText(): null;
    }

    public static String getPropertyAliasName(SchemaSubPropertyInfo element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.PROPERTY_ALIAS_NAME);
        return node != null ? node.getText(): null;
    }

    public static List<String> getPropertyClassList(SchemaSubPropertyInfo element) {
        List<String> classList = new ArrayList<>();
        for (PsiElement child : element.getChildren()) {
            IElementType type = child.getNode().getElementType();
            if (type == SchemaTypes.BUILTIN_TYPE || type == SchemaTypes.PROPERTY_CLASS) {
                classList.add(unwrapText(child.getText()));
            }
        }
        return classList;
    }

    // ============================================
    // SchemaSubPropertyMeta methods
    //
    public static String getName(SchemaSubPropertyMeta element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.META_TYPE);
        return node != null ? node.getText(): null;
    }

    public static String getValue(SchemaSubPropertyMeta element) {
        return unwrapText(PsiUtils.readPlainTextAfterNode(element, SchemaTypes.COLON));
    }

    public static Map<String, Object> toJson(SchemaSubPropertyMeta element) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", getName(element));
        result.put("value", getValue(element));
        return result;
    }
    // ============================================

    public static String unwrapText(String text) {
        if (text == null) {
            return null;
        }

        text = text.trim();
        if (text.startsWith("'") && text.endsWith("'")) {
            return text.substring(1, text.length() - 1);
        }

        if (text.startsWith("\"") && text.endsWith("\"")) {
            return text.substring(1, text.length() - 1);
        }

        return text.isEmpty() ? null: text;
    }
}
