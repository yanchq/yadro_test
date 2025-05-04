package com.example.yadro_test.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.yadro_test.IContactDuplicateService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactDuplicateServiceConnector @Inject constructor(
    @ApplicationContext private val context: Context
) {

    var service: IContactDuplicateService? = null
        private set
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            service = IContactDuplicateService.Stub.asInterface(p1)
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            service = null
            bound = false
        }
    }

    init {
        bindService()
    }

    private fun bindService() {
        val intent = Intent("com.example.yadro_test.DELETE_DUPLICATE_SERVICE").apply {
            setPackage(context.packageName)
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        context.unbindService(connection)
    }
}