package org.openspg.idea.schema.util;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.schema.lang.psi.SchemaEntity;
import org.openspg.idea.schema.lang.psi.SchemaEntityHead;
import org.openspg.idea.schema.lang.psi.SchemaNamespace;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PsiUtils {

    public static SchemaEntity findEntityByName(@NotNull PsiFile file, @NotNull String entityName) {
        Collection<SchemaEntity> entities = PsiTreeUtil.findChildrenOfType(file, SchemaEntity.class);
        for (SchemaEntity entity : entities) {
            SchemaEntityHead head = entity.getEntityHead();
            if (entityName.equals(head.getBasicStructureDeclaration().getStructureNameDeclaration().getText())) {
                return entity;
            }
        }
        return null;
    }

    public static Map<String, Object> toJson(@NotNull PsiFile file) {
        Map<String, Object> result = new LinkedHashMap<>();

        SchemaNamespace namespace = PsiTreeUtil.getChildOfType(file, SchemaNamespace.class);
        if (namespace != null) {
            result.put("namespace", namespace.toJson());
        }

        result.put("entities", PsiTreeUtil.getChildrenOfTypeAsList(file, SchemaEntity.class)
                .stream()
                .map(SchemaEntity::toJson)
                .collect(Collectors.toList()));
        return result;
    }

}
