package com.github.yamanakahirofumi.siren.psi

import com.github.yamanakahirofumi.siren.lexer.MermaidLexer
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class MermaidParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = MermaidLexer()

    override fun createParser(project: Project?): PsiParser {
        return PsiParser { root, builder ->
            val rootMarker = builder.mark()
            while (!builder.eof()) {
                builder.advanceLexer()
            }
            rootMarker.done(root)
            builder.treeBuilt
        }
    }

    override fun getFileNodeType(): IFileElementType = MermaidTokenTypes.FILE

    override fun getCommentTokens(): TokenSet = MermaidTokenTypes.COMMENTS

    override fun getWhitespaceTokens(): TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode?): PsiElement {
        return com.intellij.extapi.psi.ASTWrapperPsiElement(node!!)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile = MermaidFile(viewProvider)
}
