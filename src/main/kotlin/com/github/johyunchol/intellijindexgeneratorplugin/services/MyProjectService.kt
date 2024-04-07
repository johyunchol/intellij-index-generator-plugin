package com.github.johyunchol.intellijindexgeneratorplugin.services

import com.github.johyunchol.intellijindexgeneratorplugin.MyBundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {
    init {
        // 메시지 로깅
        thisLogger().info(MyBundle.message("projectService", project.name))

        // 서비스 로딩을 위한 안전한 접근 예제
        val myService = project.getService(MyProjectService::class.java)
        if (myService == null) {
            thisLogger().error("MyProjectService is not available.")
        }
    }

    fun getRandomNumber() = (1..100).random()
}
