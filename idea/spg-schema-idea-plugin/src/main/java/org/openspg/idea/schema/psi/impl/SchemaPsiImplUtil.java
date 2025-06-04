package org.openspg.idea.schema.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.openspg.idea.schema.grammar.psi.SchemaTypes;
import org.openspg.idea.schema.lang.psi.*;
import org.openspg.idea.schema.reference.SchemaVariableStructureTypeReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SchemaPsiImplUtil {

    // ============================================
    // SchemaNamespace methods
    //
    public static String getNamespace(SchemaNamespace element) {
        ASTNode node = element.getNode().findChildByType(SchemaTypes.IDENTIFIER);
        if (node != null) {
            return node.getText();
        }
        return null;
    }

    public static Map<String, Object> toJson(SchemaNamespace element) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("value", getNamespace(element));
        return result;
    }

    // ============================================
    // SchemaBasicStructureDeclaration methods
    //
    public static String getName(SchemaBasicStructureDeclaration element) {
        return element.getStructureNameDeclaration().getText();
    }

    public static Map<String, Object> toJson(SchemaBasicStructureDeclaration element) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("name", getName(element));
        result.put("aliasName", unwrapText(element.getStructureAliasDeclaration().getText()));
        result.put("types", element.getStructureTypeDeclaration().getTypes());

        return result;
    }

    // ============================================
    // SchemaBasicStructureDeclaration methods
    //
    public static List<String> getTypes(SchemaStructureTypeDeclaration element) {
        List<String> types = new ArrayList<>();

        SchemaBasicStructureTypeDeclaration basicType = element.getBasicStructureTypeDeclaration();
        if (basicType != null) {
            SchemaBasicStructureTypeVariable variable = basicType.getBasicStructureTypeVariable();
            types.add(variable.getText());
        }

        SchemaInheritedStructureTypeDeclaration inheritedType = element.getInheritedStructureTypeDeclaration();
        if (inheritedType != null) {
            for (SchemaInheritedStructureTypeVariable variable : inheritedType.getInheritedStructureTypeVariableList()) {
                types.add(variable.getText());
            }
        }

        return types;
    }

    // ============================================
    // SchemaVariableStructureType methods
    //
    public static PsiReference getReference(SchemaVariableStructureType element) {
        return new SchemaVariableStructureTypeReference(element);
    }

    // ============================================
    // SchemaBasicPropertyDeclaration methods
    //
    public static String getName(SchemaBasicPropertyDeclaration element) {
        return element.getPropertyNameDeclaration().getPropertyNameVariable().getText();
    }

    public static String getValue(SchemaBasicPropertyDeclaration element) {
        PsiElement sibling = element.getPropertyNameDeclaration().getNextSibling();

        while (sibling != null && sibling.getNode().getElementType() != SchemaTypes.COLON) {
            sibling = sibling.getNextSibling();
        }

        if (sibling == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();

        sibling = sibling.getNextSibling();
        while (sibling != null) {
            value.append(sibling.getText());
            sibling = sibling.getNextSibling();
        }

        return value.toString().trim();
    }

    public static Map<String, Object> toJson(SchemaBasicPropertyDeclaration element) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("name", getName(element));
        result.put("value", unwrapText(getValue(element)));

        return result;
    }

    // ============================================
    // SchemaEntity methods
    //
    public static String getName(SchemaEntity element) {
        return element.getEntityHead().getName();
    }

    public static boolean isBodyEmpty(SchemaEntity element) {
        return element.getEntityBody() == null || element.getEntityBody().getEntityMetaList().isEmpty();
    }

    public static Map<String, Object> toJson(SchemaEntity element) {
        Map<String, Object> result = element.getEntityHead().getBasicStructureDeclaration().toJson();
        if (element.getEntityBody() != null) {
            result.put("properties", element.getEntityBody()
                    .getEntityMetaList()
                    .stream()
                    .map(SchemaPsiImplUtil::toJson)
                    .toList()
            );
        }
        return result;
    }

    // ============================================
    // SchemaEntityHead methods
    //
    public static String getName(SchemaEntityHead element) {
        return element.getBasicStructureDeclaration().getStructureNameDeclaration().getText();
    }

    public static PsiElement setName(SchemaEntityHead element, String newName) {
        throw new IllegalArgumentException("unsupported operation. setName(SchemaEntityHead element, String newName)");
    }

    public static PsiElement getNameIdentifier(SchemaEntityHead element) {
        return element.getBasicStructureDeclaration().getStructureNameDeclaration();
    }


    // ============================================
    // SchemaEntityMeta methods
    //
    public static boolean isBodyEmpty(SchemaEntityMeta element) {
        return element.getEntityMetaBody() == null || element.getEntityMetaBody().getPropertyList().isEmpty();
    }

    public static Map<String, Object> toJson(SchemaEntityMeta element) {
        Map<String, Object> result = element.getEntityMetaHead().getBasicPropertyDeclaration().toJson();
        if (element.getEntityMetaBody() != null) {
            result.put("children", element.getEntityMetaBody()
                    .getPropertyList()
                    .stream()
                    .map(SchemaPsiImplUtil::toJson)
                    .toList()
            );
        }
        return result;
    }

    // ============================================
    // SchemaProperty methods
    //
    public static boolean isBodyEmpty(SchemaProperty element) {
        return element.getPropertyBody() == null || element.getPropertyBody().getPropertyMetaList().isEmpty();
    }

    public static Map<String, Object> toJson(SchemaProperty element) {
        Map<String, Object> result = element.getPropertyHead().getBasicStructureDeclaration().toJson();
        if (element.getPropertyBody() != null) {
            result.put("properties", element.getPropertyBody()
                    .getPropertyMetaList()
                    .stream()
                    .map(SchemaPsiImplUtil::toJson)
                    .toList()
            );
        }
        return result;
    }

    // ============================================
    // SchemaPropertyMeta methods
    //
    public static Map<String, Object> toJson(SchemaPropertyMeta element) {
        Map<String, Object> result = element.getPropertyMetaHead().getBasicPropertyDeclaration().toJson();
        if (element.getPropertyMetaBody() != null) {
            result.put("children", element.getPropertyMetaBody()
                    .getSubPropertyList()
                    .stream()
                    .map(SchemaPsiImplUtil::toJson)
                    .toList()
            );
        }
        return result;
    }

    // ============================================
    // SchemaSubProperty methods
    //
    public static Map<String, Object> toJson(SchemaSubProperty element) {
        Map<String, Object> result = element.getSubPropertyHead().getBasicStructureDeclaration().toJson();
        if (element.getSubPropertyBody() != null) {
            result.put("properties", element.getSubPropertyBody()
                    .getSubPropertyMetaList()
                    .stream()
                    .map(SchemaPsiImplUtil::toJson)
                    .toList()
            );
        }
        return result;
    }

    // ============================================
    // SchemaSubPropertyMeta methods
    //
    public static Map<String, Object> toJson(SchemaSubPropertyMeta element) {
        return element.getBasicPropertyDeclaration().toJson();
    }

    // ============================================
    // SchemaPlainTextBlock methods
    //
//    public static TextRange getInjectTextRange(SchemaPlainTextBlock element) {
//        System.out.println(element.getFirstChild().getText());
//        System.out.println(element.getLastChild().getText());
//        int start = element.getFirstChild().getTextLength();
//        int end = element.getTextLength() - element.getLastChild().getTextLength();
//        System.out.println(element.getTextLength());
//        return TextRange.create(start, end);
//    }
    // ============================================

    public static String unwrapText(String text) {
        if (text == null || text.length() < 2) {
            return null;
        }

        text = text.trim();
        if (text.startsWith("'") && text.endsWith("'")) {
            return text.substring(1, text.length() - 1);
        }

        if (text.startsWith("\"") && text.endsWith("\"")) {
            return text.substring(1, text.length() - 1);
        }

        if (text.startsWith("`") && text.endsWith("`")) {
            return text.substring(1, text.length() - 1);
        }

        return text.isEmpty() ? null : text;
    }
}
