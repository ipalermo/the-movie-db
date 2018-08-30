
package com.android.example.themoviedb.util

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

import com.android.example.themoviedb.TestApp

/**
 * Custom runner to disable dependency injection.
 */
class TmdbTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}
