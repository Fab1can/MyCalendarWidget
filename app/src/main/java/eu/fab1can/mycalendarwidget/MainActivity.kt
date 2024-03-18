package eu.fab1can.mycalendarwidget

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import eu.fab1can.mycalendarwidget.calendar.Calendar
import eu.fab1can.mycalendarwidget.calendar.ContactEvent
import eu.fab1can.mycalendarwidget.calendar.Event
import eu.fab1can.mycalendarwidget.databinding.ActivityMainBinding
import eu.fab1can.mycalendarwidget.tasks.GoogleTasksManager
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val R_CODE_CALENDAR = 100
        const val R_CODE_CONTACTS = 101
        const val R_CODE_NOTIFICATIONS = 102

        lateinit var googleTasksManager : GoogleTasksManager
        lateinit var myNotificationManager: MyNotificationManager
    }


    private val POWERMANAGER_INTENTS = arrayOf(
        Intent().setComponent(
            ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.startupapp.StartupAppListActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.oppo.safe",
                "com.oppo.safe.permission.startup.StartupAppListActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.samsung.android.lool",
                "com.samsung.android.sm.ui.battery.BatteryActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.htc.pitroad",
                "com.htc.pitroad.landingpage.activity.LandingPageActivity"
            )
        ),
        Intent().setComponent(
            ComponentName(
                "com.asus.mobilemanager",
                "com.asus.mobilemanager.MainActivity"
            )
        )
    )

    fun robeBatteria(){
        val pref = getSharedPreferences("allow_notify", MODE_PRIVATE).edit()
        pref.apply()
        val sp = getSharedPreferences("allow_notify", MODE_PRIVATE)
        if (!sp.getBoolean("protected", false)) {
            for (intent in POWERMANAGER_INTENTS) {
                if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    startActivity(intent)
                    sp.edit().putBoolean("protected", true).apply()
                    break
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        robeBatteria()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener { view ->
            

            val calendars = Calendar.retreiveAll(contentResolver)
            for (calendar in calendars){
                Log.d("ev1", calendar.ID.toString()+":"+calendar.AccName)
            }
            val events=Event.retrieveFutureEvents(contentResolver, arrayOf(1,2,3,5,6,7,8,9,11,13,14,15,16))
            for (event in events){
            }
            val birthdays = ContactEvent.retrieveEvents(contentResolver)
            for(birthday in birthdays){
                Log.d("bb", birthday.Name+":"+birthday.Label+":"+birthday.Date)
            }
        }

        if(!EasyPermissions.hasPermissions(this, Manifest.permission.READ_CALENDAR, Manifest.permission.READ_CALENDAR, Manifest.permission.READ_CONTACTS, Manifest.permission.POST_NOTIFICATIONS)){
            EasyPermissions.requestPermissions(this, "", MainActivity.R_CODE_CALENDAR, Manifest.permission.READ_CALENDAR, Manifest.permission.READ_CONTACTS, Manifest.permission.POST_NOTIFICATIONS)
        }

        googleTasksManager.login(this)

        MyService.start(this)



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GoogleTasksManager.REQUEST_CODE_SIGN_IN -> {
                googleTasksManager.handleSignInResult(data)
            }

        }
    }






}