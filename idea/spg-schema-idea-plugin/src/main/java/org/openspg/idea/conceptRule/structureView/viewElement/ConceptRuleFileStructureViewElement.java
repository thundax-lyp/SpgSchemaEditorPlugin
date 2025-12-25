package org.openspg.idea.conceptRule.structureView.viewElement;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.openspg.idea.conceptRule.psi.ConceptRuleRuleWrapperDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ConceptRuleFileStructureViewElement extends AbstractConceptRuleStructureViewElement<PsiFile> {

    public ConceptRuleFileStructureViewElement(PsiFile element) {
        super(element);
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        return myElement.getName();
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<ConceptRuleRuleWrapperDeclaration> elements = PsiTreeUtil.getChildrenOfTypeAsList(myElement, ConceptRuleRuleWrapperDeclaration.class);

        List<TreeElement> treeElements = new ArrayList<>(elements.size());

        for (ConceptRuleRuleWrapperDeclaration element : elements) {
            treeElements.add(new ConceptRuleRuleWrapperDeclarationStructureViewElement(element));
        }

        return treeElements.toArray(new TreeElement[0]);
    }

}
