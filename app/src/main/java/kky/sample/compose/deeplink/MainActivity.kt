package kky.sample.compose.deeplink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kky.sample.compose.deeplink.route.Route
import kky.sample.compose.deeplink.screen.DeepLinkScreen
import kky.sample.compose.deeplink.screen.HomeScreen
import kky.sample.compose.deeplink.ui.theme.DeepLinkSampleTheme

val LocalActivity = staticCompositionLocalOf<ComponentActivity> { error("no activity") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DeepLinkSample", "Activity onCreate")
        enableEdgeToEdge()
        setContent {
            DeepLinkSampleTheme {
                CompositionLocalProvider(
                    LocalActivity provides this
                ) {
                    Scaffold { innerPadding ->
                        DeepLinkSampleApp(
                            scaffoldPadding = innerPadding
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("DeepLinkSample", "Activity onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("DeepLinkSample", "Activity onResume")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("DeepLinkSample", "Activity onNewIntent :: ${intent.toUri(Intent.URI_INTENT_SCHEME)}")
    }
}


@Composable
fun DeepLinkSampleApp(
    scaffoldPadding: PaddingValues,
) {
    val navController = rememberNavController()
    val activity = LocalActivity.current

    DisposableEffect(Unit) {
        val onNewIntentConsumer = Consumer<Intent> {
            val number = it.data?.getQueryParameter("number")?.toInt() ?: 0
            navController.navigate(
                Route.DeepLink(
                    number = number
                )
            )
        }

        activity.addOnNewIntentListener(onNewIntentConsumer)

        onDispose {
            activity.removeOnNewIntentListener(onNewIntentConsumer)
        }
    }
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding),
    ) {
        composable<Route.Home> {
            HomeScreen {
                navController.navigate(Route.DeepLink(number = 2))
            }
        }

        composable<Route.DeepLink>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "deeplink://sample?number={number}"
                }
            )
        ) { navBackStackEntry ->
            val number = navBackStackEntry.toRoute<Route.DeepLink>().number
            DeepLinkScreen(
                number = number
            )
        }
    }
}

