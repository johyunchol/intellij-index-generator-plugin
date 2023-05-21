package com.github.johyunchol.intellijindexgeneratorplugin.services

import com.github.johyunchol.intellijindexgeneratorplugin.constants.DefaultEnvExample
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import java.io.File
import java.io.IOException


/**
 * @param files Array<PsiFile>
 */
fun generateIndexFileContent(files: Array<PsiFile>): String {
    val exportStatements = mutableListOf<String>()

    files.sortedBy { file -> file.name }.map { file ->
        println("file : ${file.name}")
        if (file.name == "index.ts") return@map

        if (file.name.contains("scss")) return@map

        val lines = file.text.split("\n")
        for (line in lines) {
            val regex = Regex("""export\s+default\s+(\w+)""")
            val matchResult = regex.find(line)
            if (matchResult != null) {
                val className = matchResult.groupValues[1]
                val fileNameWithoutExtension = File(file.name).nameWithoutExtension
                val exportStatement = "export { default as $className } from './${fileNameWithoutExtension}'"
                println(exportStatement)
                exportStatements.add(exportStatement)
            }
        }
    }

    return exportStatements.joinToString("\n")
}
