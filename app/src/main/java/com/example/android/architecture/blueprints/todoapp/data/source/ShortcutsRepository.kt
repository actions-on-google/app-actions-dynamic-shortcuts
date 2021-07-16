package com.example.android.architecture.blueprints.todoapp.data.source

import android.content.Context
import android.content.Intent
import androidx.annotation.WorkerThread
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity

private const val GET_THING_KEY = "q"

/**
 * ShortcutsRepository provides an interface for managing dynamic shortcuts.
 *
 * @property appContext Application Context
 */
class ShortcutsRepository(val context: Context) {

    private val appContext = context.applicationContext

    /**
     * Pushes a Dynamic Shortcut for the task. The task's ID is used as a shortcut ID.
     * The task's title and description are used as shortcut's short and long labels. The
     * created shortcut corresponds to GET_THING capability with task's title used as BII's "name"
     * argument.
     *
     * @param task Task object for which to create a shortcut.
     */
    @WorkerThread
    fun pushShortcut(task: Task) {
        ShortcutManagerCompat.pushDynamicShortcut(appContext, createShortcutCompat(task))
    }

    private fun createShortcutCompat(task: Task): ShortcutInfoCompat {
        val intent = Intent(appContext, TasksActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        // Filtering is set based on currentTitle.
        intent.putExtra(GET_THING_KEY, task.title)

        // Need to have unique ID in order to not overwrite exiting shortcut.
        return ShortcutInfoCompat.Builder(appContext, task.id)
                .setShortLabel(task.title)
                .setLongLabel(task.description)
                // User is able to invoke shortcut by its title.
                .addCapabilityBinding(
                        "actions.intent.GET_THING", "thing.name", listOf(task.title))
                .setIntent(intent)
                .setLongLived(false)
            .build()
    }

    /**
     *  Updates a Dynamic Shortcut for the task. If the shortcut associated with this task
     * doesn't exist, throws an error. This operation may take a few seconds to complete.
     *
     * @param tasks list of tasks to update.
     */
    @WorkerThread
    fun updateShortcuts(tasks: List<Task>) {
        val scs = tasks.map { createShortcutCompat(it) }
        ShortcutManagerCompat.updateShortcuts(appContext, scs)
    }

    /**
     * Removes shortcuts if IDs are known.
     * @param ids list of shortcut IDs
     */
    @WorkerThread
    fun removeShortcutsById(ids: List<String>) {
        ShortcutManagerCompat.removeDynamicShortcuts(appContext, ids)
    }

    /**
     * Removes shortcuts associated with the tasks.
     *
     * @param tasks list of tasks to remove.
     */
    @WorkerThread
    fun removeShortcuts(tasks: List<Task>) {
        ShortcutManagerCompat.removeDynamicShortcuts (appContext,
                tasks.map { it.id })
    }
}

