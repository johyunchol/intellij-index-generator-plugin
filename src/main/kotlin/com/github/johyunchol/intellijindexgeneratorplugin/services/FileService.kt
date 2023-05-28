package com.github.johyunchol.intellijindexgeneratorplugin.services

import com.intellij.psi.PsiFile
import java.io.File


/**
 * @param files Array<PsiFile>
 */
fun generateIndexFileContent(files: Array<PsiFile>): String {
    val exportStatements = mutableListOf<String>()

    files.sortedBy { file -> file.name }.map { file ->
        if (file.name == "index.ts") return@map

        if (file.name.contains("scss")) return@map

        val lines = file.text.split("\n")
        for (line in lines) {
            val regex = Regex("""export\s+default\s+(?:\b\w+\b\s+)?(\w+)""")
            val matchResult = regex.find(line)
            if (matchResult != null) {
                val className = matchResult.groupValues[1]
                val fileNameWithoutExtension = File(file.name).nameWithoutExtension
                val exportStatement = "export { default as $className } from './${fileNameWithoutExtension}'"
                exportStatements.add(exportStatement)
            }
        }
    }

    return exportStatements.joinToString("\n")
}
