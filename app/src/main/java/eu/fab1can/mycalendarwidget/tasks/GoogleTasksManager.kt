package eu.fab1can.mycalendarwidget.tasks

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import com.google.api.services.tasks.TasksScopes
import com.google.api.services.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GoogleTasksManager(private val activity: Activity) {
    companion object {
        const val REQUEST_CODE_SIGN_IN = 400
        const val TASKLIST_ID = 0
    }

    private var initialized : Boolean = false
    private lateinit var tasks: Tasks

    var taskList : List<String> = listOf<String>()

    init {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(TasksScopes.TASKS_READONLY))
            .build()

        val signInClient = GoogleSignIn.getClient(activity, signInOptions)
        val signInIntent = signInClient.signInIntent
        activity.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)

        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            val credentials = GoogleAccountCredential.usingOAuth2(
                activity.applicationContext,
                listOf(TasksScopes.TASKS_READONLY)
            )
            credentials.selectedAccount = account.account

            tasks = Tasks.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credentials
            )
                .setApplicationName("MyCalendarWidget") // Replace with your app name
                .build()
            initialized=true
        }
    }

    fun handleSignInResult(data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener { account ->
                Log.d("GoogleTasksManager", "Email: ${account.email}")

                updateTaskList()


            }
            .addOnFailureListener { e ->
                Log.e("GoogleTasksManager", "Failed to connect to Google Tasks: ${e.message}")
            }
    }

    fun updateTaskList(){
        if(initialized){
            CoroutineScope(Dispatchers.IO).launch {
                val tasklistId=tasks.tasklists().list().execute().items[TASKLIST_ID].id
                val tasks =tasks.tasks().list(tasklistId).execute().items
                taskList = tasks.filter { it.completed==null }.map { it.title }
            }
        }

    }
}