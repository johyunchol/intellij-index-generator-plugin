package com.github.johyunchol.intellijindexgeneratorplugin.toolWindow

import com.github.johyunchol.intellijindexgeneratorplugin.MyBundle
import com.github.johyunchol.intellijindexgeneratorplugin.services.MyProjectService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import javax.swing.JButton

class MyToolWindowFactory : ToolWindowFactory {
    init {
        Logger.getInstance(MyToolWindowFactory::class.java).warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow, project)
        myToolWindow.initContent()
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(private val toolWindow: ToolWindow, private val project: Project) {
        private val service = getServiceOrLogError()

        fun initContent() {
            if (service == null) {
                // If service is not available, do not proceed to initialize the content.
                return
            }

            val contentFactory = ApplicationManager.getApplication().getService(ContentFactory::class.java)
            val content = contentFactory.createContent(createContentPanel(), null, false)
            toolWindow.contentManager.apply {
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
                        label.text = MyBundle.message("randomLabel", service?.getRandomNumber() ?: "?")
                    }
                })
            }
            return panel
        }

        private fun getServiceOrLogError(): MyProjectService? {
            val service = project.getService(MyProjectService::class.java)
            if (service == null) {
                Logger.getInstance(MyToolWindow::class.java).error("MyProjectService is not available.")
            }
            return service
        }
    }
}
