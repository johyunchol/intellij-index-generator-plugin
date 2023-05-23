package com.github.johyunchol.intellijindexgeneratorplugin.actions

import com.github.johyunchol.intellijindexgeneratorplugin.constants.EnvType
import com.github.johyunchol.intellijindexgeneratorplugin.services.generateIndexFileContent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import java.io.File
import javax.swing.Icon

/**
 * Action class to demonstrate how to interact with the IntelliJ Platform.
 * The only action this class performs is to provide the user with a popup dialog as feedback.
 * Typically this class is instantiated by the IntelliJ Platform framework based on declarations
 * in the plugin.xml file. But when added at runtime this class is instantiated by an action group.
 * https://github.com/JetBrains/intellij-sdk-code-samples 자바로 돼있는 코드 참고하여 코틀린으로 변환후 플러그인에 필요한 메서드 구현함
 * 툴바 혹은 액션 클릭시 나오는 행위 구현
 * 해당 프로젝트에서는 메뉴 클릭시 환경파일 스위칭 되는 것을 구현
 */
class IndexGenerateAction : AnAction() {
    /**
     * (동적 작업 메뉴를 선택시 알림창 띄우기)
     * 간단한 메시지 대화 상자를 표시합니다.
     * @param event 연결된 메뉴 항목을 선택시 이벤트
     * Local | Pre | Live 중 한개 누른 경우
     */
    override fun actionPerformed(event: AnActionEvent) {
        val currentVirtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)

        val isDirectory = currentVirtualFile?.canonicalFile?.isDirectory == true
        val currentDirectory = if (isDirectory) {
            currentVirtualFile?.canonicalFile
        } else {
            currentVirtualFile?.parent
        }

        if (currentDirectory == null) return

        // 현재 디렉토리의 PsiDirectory 가져오기
        val psiManager = PsiManager.getInstance(event.project!!)
        val currentPsiDirectory = psiManager.findDirectory(currentDirectory)

        // 현재 디렉토리의 PsiDirectory의 파일 목록 가져오기
        val files = currentPsiDirectory?.files!!
        val indexText = generateIndexFileContent(files)

        WriteCommandAction.runWriteCommandAction(event.project) {
            try {
                val psiFileFactory: PsiFileFactory = PsiFileFactory.getInstance(event.project)
                val fileTypeManager: FileTypeManager = FileTypeManager.getInstance()

                val createdFile: PsiFile = psiFileFactory.createFileFromText(
                    "index.ts",
                    fileTypeManager.getFileTypeByExtension("ts"),
                    indexText
                )

                val indexFile = currentPsiDirectory.findFile("index.ts")
                if (indexFile != null) {
                    indexFile.virtualFile?.setBinaryContent(indexText.toByteArray())
                } else {
                    currentPsiDirectory.add(createdFile)
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    /**
     * Determines whether this menu item is available for the current context.
     * Requires a project to be open.
     *
     * @param e Event received when the associated group-id menu is chosen.
     */
    override fun update(e: AnActionEvent) {
        // Set the availability based on whether a project is open
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}
