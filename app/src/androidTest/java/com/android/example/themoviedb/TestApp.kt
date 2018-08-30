
package com.android.example.themoviedb

import android.app.Application

/**
 * We use a separate App for tests to prevent initializing dependency injection.
 *
 * See [com.android.example.themoviedb.util.TmdbTestRunner].
 */
class TestApp : Application()
