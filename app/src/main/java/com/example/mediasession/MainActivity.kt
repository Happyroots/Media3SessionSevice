package com.example.mediasession

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C.TRACK_TYPE_TEXT
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION
import androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED
import androidx.media3.common.Player.EVENT_TIMELINE_CHANGED
import androidx.media3.common.Player.EVENT_TRACKS_CHANGED
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController

import com.example.mediasession.ui.theme.MediaSessionTheme
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import androidx.media3.session.MediaBrowser
import androidx.media3.session.legacy.MediaBrowserCompat
import androidx.media3.session.legacy.MediaControllerCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.awaitAll
import kotlin.math.log


class MainActivity : ComponentActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: Player
    private lateinit var mediaController: MediaController

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        player = ExoPlayer.Builder(this).build()

        setContent {
            MediaSessionTheme {

                app(context = this, player = player as ExoPlayer)

            }
        }
    }



}




@Composable
fun app(context: Context, player: ExoPlayer, modifier: Modifier = Modifier){
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        screen(context, player)

    }
}

var mediaController : MediaController? = null
@Composable
fun screen(context: Context, player: ExoPlayer){
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    ExoPlayer(player)
        Column (

        ) {

            OutlinedButton(
                onClick = {


                    if(!player.isPlaying){
                        // in ui
                        var uri = Uri.parse("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
//                        var uri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
                        var mediaItem_ui = MediaItem.fromUri(uri)
                        player.setMediaItem(mediaItem_ui)
                        player.prepare()
//                         player.playWhenReady = true // 设置为true以便在准备好后自动播放
                        player.play()



/*****************************  Start playback using the MediaController  ********************************************************************************/

                        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))

                        var factory = MediaController.Builder(context, sessionToken).buildAsync()
//                        factory?.apply {
//                            addListener(Runnable {
//                                mediaController = get()
//                            }, MoreExecutors.directExecutor())
//                        }

                        factory?.addListener(
                            {
                                // MediaController is available here with controllerFuture.get()
                                mediaController = factory?.let {
                                    if (it.isDone)
                                        it.get()
                                    else
                                        null
                                }
                            },
                            MoreExecutors.directExecutor()
                        )

                        var mediaUri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
                        // Create a MediaItem
                        val mediaItem =
                            MediaItem.Builder()
                                // Set a unique media ID for the media item
                                .setMediaId("media-1")
                                // Set the URI (Uniform Resource Identifier) for the media content
                                .setUri(mediaUri)
                                // Set the media metadata, which includes details about the media content
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        // Set the artist's name for the media content
                                        .setArtist("David Bowie")
                                        // Set the title of the media content
                                        .setTitle("Heroes")
                                        // Set the URI for the artwork or album cover associated with the media content
                //                                    .setArtworkUri(artworkUri)
                                        .build()
                                )
                                .build()

                        // Set the created MediaItem on a MediaController
                //        mediaController?.setMediaItem(mediaItem)
                        mediaController?.addMediaItem(mediaItem)

                        // Prepare the MediaController for playback (loading media resources, etc.)
                        mediaController?.prepare()

                        // Start playback using the MediaController
                        mediaController?.play()


                   }
                    else{
                        player.stop()
                        player.release()
                    }







                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vector_music),
                    contentDescription = "play" // 提供内容描述，对于无障碍功能很重要
                )
            }
        }

    }

}



/**
 * A Composable function that provides a managed MediaController instance.
 *
 * @param lifecycle The lifecycle of the owner of this MediaController. Defaults to the lifecycle of the LocalLifecycleOwner.
 * @return A State object containing the MediaController instance. The Composable will automatically re-compose whenever the state changes.
// */
//@Composable
//fun rememberManagedMediaController(
//    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle
//): State<MediaController?> {
//    // Application context is used to prevent memory leaks
//    val appContext = LocalContext.current.applicationContext
//    val controllerManager = remember { MediaControllerManager.getInstance(appContext) }
//
//    // Observe the lifecycle to initialize and release the MediaController at the appropriate times.
//    DisposableEffect(lifecycle) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_START -> controllerManager.initialize()
//                Lifecycle.Event.ON_STOP -> controllerManager.release()
//                else -> {}
//            }
//        }
//        lifecycle.addObserver(observer)
//        onDispose { lifecycle.removeObserver(observer) }
//    }
//
//    return controllerManager.controller
//}


/**
 * A Singleton class that manages a MediaController instance.
 *
 * This class observes the Remember lifecycle to release the MediaController when it's no longer needed.
 */
@Stable
internal class MediaControllerManager private constructor(context: Context) : RememberObserver {
    private val appContext = context.applicationContext
    private var factory: ListenableFuture<MediaController>? = null
    var controller = mutableStateOf<MediaController?>(null)
        private set

    init { initialize() }

    /**
     * Initializes the MediaController.
     *
     * If the MediaController has not been built or has been released, this method will build a new one.
     */
    @OptIn(UnstableApi::class)
    internal fun initialize() {
        if (factory == null || factory?.isDone == true) {
            factory = MediaController.Builder(
                appContext,
                SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
            ).buildAsync()
        }
        factory?.addListener(
            {
                // MediaController is available here with controllerFuture.get()
                controller.value = factory?.let {
                    if (it.isDone)
                        it.get()
                    else
                        null
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    /**
     * Releases the MediaController.
     *
     * This method will release the MediaController and set the controller state to null.
     */
    internal fun release() {
        factory?.let {
            MediaController.releaseFuture(it)
            controller.value = null
        }
        factory = null
    }

    // Lifecycle methods for the RememberObserver interface.
    override fun onAbandoned() { release() }
    override fun onForgotten() { release() }
    override fun onRemembered() {}

    companion object {
        @Volatile
        private var instance: MediaControllerManager? = null

        /**
         * Returns the Singleton instance of the MediaControllerManager.
         *
         * @param context The context to use when creating the MediaControllerManager.
         * @return The Singleton instance of the MediaControllerManager.
         */
        fun getInstance(context: Context): MediaControllerManager {
            return instance ?: synchronized(this) {
                instance ?: MediaControllerManager(context).also { instance = it }
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun ExoPlayer(player: ExoPlayer, modifier: Modifier = Modifier) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            AndroidView(
                factory = {
                    PlayerView(it).apply {
//                        val player = ExoPlayer.Builder(context).build()
                        this.player = player//this@ExoPlayerScreen.player
                        useController = true  // 控制器是否可见
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    }

                },
                modifier = modifier.fillMaxSize(),
                update = { view ->
                    // 当player对象发生变化时更新PlayerView
                    view.player = player
                }
            )

    }
}

private fun initMediaController(context: Context) {
    val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
//    var mediaControllerFuture: MediaController? = null
    var  mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
//    mediaControllerFuture?.apply {
//        addListener(Runnable {
//            val player = mediaControllerFuture?.get()
//            player?.let {
//                PlayServiceModule.setPlayer(it)
//            }
//            mCurrPlayer = get()
//            playMedia()
//        }, MoreExecutors.directExecutor())
//    }
}

private fun playMedia() {
    val mediaItem = MediaItem.Builder()
        .setMediaId("www.soundhelix.com/examples/mp…")
        .setUri("www.soundhelix.com/examples/mp…")
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle("")
                .setArtist("")
                .setAlbumTitle("")
                .setAlbumArtist("")
                .setArtworkUri(Uri.parse("i.pinimg.com/736x/4b/02/…"))
//                .setBaseCover("i.pinimg.com/736x/4b/02/…")
//                .setDuration(1000L)
//                .setFilePath("i.pinimg.com/736x/4b/02/…")
//                .setFileName("")
//                .setFileSize(20L)
                .build()
        )
        .build()
//        mCurrPlayer.setMediaItem(mediaItem)
//        mCurrPlayer.prepare()
//        mCurrPlayer.play()

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MediaSessionTheme {

    }
}