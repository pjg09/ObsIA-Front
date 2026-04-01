package com.upb.obsia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Entry point de Hilt. Sin lógica adicional — el motor se inicializa de forma lazy la primera vez
 * que el usuario entra a ChatScreen.
 */
@HiltAndroidApp class ObsiaApplication : Application()
