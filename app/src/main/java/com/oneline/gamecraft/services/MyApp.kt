package com.oneline.gamecraft.services

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.oneline.gamecraft.TinyDB

class MyApp : Application() {

    private var isMusicOn: Boolean = true

    override fun onCreate() {
        super.onCreate()


        // Register the ActivityLifecycleCallbacks to monitor app state
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            private var activityReferences = 0
            private var isActivityChangingConfigurations = false

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                if (++activityReferences == 1 && !isActivityChangingConfigurations) {

                    isMusicOn = TinyDB.getBoolean(
                        this@MyApp, "isMusicOn",
                        true
                    )
                    // App enters foreground
                    if (isMusicOn) {
                        startService(Intent(this@MyApp, MusicService::class.java))
                    }
                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations
                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                    // App enters background
                    stopService(Intent(this@MyApp, MusicService::class.java))
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}