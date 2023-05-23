package com.github.johyunchol.intellijindexgeneratorplugin.toolWindow

import com.github.johyunchol.intellijindexgeneratorplugin.MyBundle
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.github.johyunchol.intellijindexgeneratorplugin.services.MyProjectService
import javax.swing.JButton

class MyToolWindowFactory : ToolWindowFactory {
    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow, project)
        myToolWindow.initContent()
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(private val toolWindow: ToolWindow, private val project: Project) {
        private val service = project.service<MyProjectService>()

        fun initContent() {
            val contentFactory = ContentFactory.SERVICE.getInstance()
            val content = contentFactory.createContent(createContentPanel(), null, false)
            toolWindow.contentManager?.apply {
                removeAllContents(true)
                addContent(content)
            }
        }

        private fun createContentPanel(): JBPanel<*> {
            val panel = JBPanel<JBPanel<*>>().apply {
                val label = JBLabel(MyBundle.message("randomLabel", "?"))

                add(label)
                add(JButton(MyBundle.message("shuffle")).apply {
                    addActionListener {
                        label.text = MyBundle.message("randomLabel", service.getRandomNumber())
                    }
                })
            }
            return panel
        }
    }
}
