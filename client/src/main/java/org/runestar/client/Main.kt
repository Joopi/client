@file:JvmName("Main")

package org.runestar.client

import com.google.common.base.Throwables
import org.kxtra.slf4j.getLogger
import org.runestar.client.api.Application
import org.runestar.client.common.JAV_CONFIG
import org.runestar.client.common.MANIFEST_NAME
import org.runestar.client.common.lookupClassLoader
import java.util.Locale
import java.util.jar.JarInputStream
import java.util.jar.Manifest
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

fun main() {
    Thread.setDefaultUncaughtExceptionHandler(::handleUncaughtException)
    Locale.setDefault(Locale.ENGLISH)
    SwingUtilities.invokeLater((StarTheme)::install)

    checkUpToDate()

    Application.start()
}

private fun handleUncaughtException(t: Thread, e: Throwable) {
    getLogger().error("Uncaught exception", e)
    showErrorDialog(Throwables.getStackTraceAsString(e))
}

private fun checkUpToDate() {
    val serverManifest = JarInputStream(JAV_CONFIG.gamepackUrl.openStream()).use { it.manifest }
    val bundledManifest = lookupClassLoader.getResourceAsStream(MANIFEST_NAME).use { Manifest(it) }
    if (serverManifest != bundledManifest) {
        showErrorDialog("Client is out of date")
        exitProcess(1)
    }
}

private fun showErrorDialog(s: String) {
    val runnable = Runnable {
        val component = JScrollPane().apply {
            setViewportView(JTextArea(s, 5, 40).apply {
                isEditable = false
            })
        }
        JOptionPane.showMessageDialog(null, component, "Error", JOptionPane.ERROR_MESSAGE)
    }

    if (SwingUtilities.isEventDispatchThread()) {
        runnable.run()
    } else {
        SwingUtilities.invokeAndWait(runnable)
    }
}