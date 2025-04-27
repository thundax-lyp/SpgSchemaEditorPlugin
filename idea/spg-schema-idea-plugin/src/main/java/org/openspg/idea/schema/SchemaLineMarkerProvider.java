package org.openspg.idea.schema;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.grammar.psi.SchemaTypes;
import org.openspg.idea.lang.psi.SchemaEntityInfo;

import java.util.Collection;

public final class SchemaLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element.getNode().getElementType() == SchemaTypes.ENTITY_CLASS) {
            handleEntityType(element, result);
        }
    }

    private void handleEntityType(@NotNull PsiElement element,
                                  @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        String entityType = element.getText();
        if (entityType == null || entityType.isEmpty()) {
            return;
        }

        Collection<SchemaEntityInfo> infos = PsiTreeUtil.findChildrenOfType(element.getContainingFile(), SchemaEntityInfo.class);
        infos = infos.stream()
                .filter(info -> entityType.equals(info.getEntityName()))
                .toList();
        if (!infos.isEmpty()) {
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(SchemaIcons.FILE)
                            .setTargets(infos)
                            .setTooltipText("Navigate to Schema entity");
            result.add(builder.createLineMarkerInfo(element));
        }
    }

}
