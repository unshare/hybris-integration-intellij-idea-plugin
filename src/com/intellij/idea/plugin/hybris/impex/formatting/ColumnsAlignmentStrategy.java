package com.intellij.idea.plugin.hybris.impex.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexFile;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexTypes;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexValueGroup;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 12:55 01 January 2015
 *
 * @author Alexander Bartash <AlexanderBartash@gmail.com>
 */
public class ColumnsAlignmentStrategy implements AlignmentStrategy {

    private int columnNumber = 0;
    private final List<Alignment> alignments = new ArrayList<Alignment>();

    public ColumnsAlignmentStrategy() {
    }

    @Override
    public Alignment getAlignment(@NotNull final ASTNode currentNode) {

        final Alignment alignment;

        if (isNewColumn(currentNode)) {
            if (columnNumber >= alignments.size()) {
                alignment = Alignment.createAlignment(true, Alignment.Anchor.LEFT);
                alignments.add(alignment);
            } else {
                alignment = alignments.get(columnNumber);
            }
            columnNumber++;
        } else {
            alignment = Alignment.createAlignment();
        }

        return alignment;
    }

    @Override
    public void processNode(@NotNull final ASTNode currentNode) {
        if (isStartOfTheFile(currentNode)) {
            columnNumber = 0;
            alignments.clear();
        } else {
            if (isNewLine(currentNode)) {
                columnNumber = 0;
            } else if (isHeaderLine(currentNode)) {
                alignments.clear();
            }
        }
    }

    private boolean isStartOfTheFile(final ASTNode currentNode) {
        return currentNode.getPsi() instanceof ImpexFile;
    }

    private boolean isNewLine(final ASTNode currentNode) {
        return ImpexTypes.VALUE_GROUP == currentNode.getElementType()
               && isStartOfValueLine(currentNode);
    }

    private boolean isStartOfValueLine(final ASTNode currentNode) {
        return PsiTreeUtil.findChildOfType(currentNode.getTreeParent().getPsi(), ImpexValueGroup.class) == currentNode.getPsi();
    }

    private boolean isNewColumn(@NotNull final ASTNode currentNode) {
        return ImpexTypes.VALUE_GROUP == currentNode.getElementType();
    }

    private boolean isHeaderLine(@NotNull final ASTNode currentNode) {
        return ImpexTypes.HEADER_LINE == currentNode.getElementType();
    }
}
