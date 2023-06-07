package com.eleks.mowid.ui.feature.widget.composable

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.datastore.preferences.core.Preferences
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.eleks.mowid.ui.feature.widget.LeftArrowClickAction
import com.eleks.mowid.R
import com.eleks.mowid.ui.feature.widget.QuotesWidget
import com.eleks.mowid.ui.feature.widget.RightArrowClickAction

@Composable
fun QuotesWidgetContent(
    modifier: GlanceModifier,
) {

    val prefs = currentState<Preferences>()

    val quote = prefs[QuotesWidget.quotePreference]
    val author = prefs[QuotesWidget.authorPreference]
    val memeUrl = prefs[QuotesWidget.memePreference]

    WidgetContent(
        modifier = modifier,
        quote = if (quote.isNullOrEmpty()) null else quote,
        author = if (author.isNullOrEmpty()) null else author,
        memeUrl =  if (memeUrl.isNullOrEmpty()) null else memeUrl,
    )
}

@Composable
fun WidgetContent(
    modifier: GlanceModifier,
    memeUrl: String?,
    quote: String?,
    author: String?
) {
    val context = LocalContext.current

    var randomImage by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(memeUrl) {
        Log.d("QuotesWidget", "randomImage = context.getRandomImage(memeUrl)")
        memeUrl?.let { randomImage = context.getRandomImage(it) }
        Log.d("QuotesWidget", "randomImage = $randomImage")
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Horizontal.Start
    ) {
        randomImage?.let {
            Log.d("QuotesWidget", "randomImage != null")
            Image(
                provider = ImageProvider(it),
                contentDescription = "",
                modifier = GlanceModifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
                contentScale = ContentScale.FillBounds,
            )
        }
        quote?.let {
            randomImage = null
            Text(
                modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = ColorProvider(color = Color.White),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                text = it,
            )
        }
        Row(
            modifier = GlanceModifier.padding(top = 6.dp).fillMaxWidth(),
        ) {
            Row(
                modifier = GlanceModifier.fillMaxHeight(),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(30.dp, 30.dp)
                        .background(ImageProvider(R.drawable.widget_button_background))
                        .clickable(onClick = actionRunCallback<LeftArrowClickAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = GlanceModifier.clickable(onClick = actionRunCallback<LeftArrowClickAction>()),
                        provider = ImageProvider(
                            resId = R.drawable.ic_left_arrow
                        ),
                        contentDescription = null
                    )
                }
                Spacer(modifier = GlanceModifier.width(8.dp))
                Box(
                    modifier = GlanceModifier
                        .size(30.dp, 30.dp)
                        .background(ImageProvider(R.drawable.widget_button_background))
                        .clickable(onClick = actionRunCallback<RightArrowClickAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = GlanceModifier.clickable(onClick = actionRunCallback<RightArrowClickAction>()),
                        provider = ImageProvider(
                            resId = R.drawable.ic_right_arrow
                        ),
                        contentDescription = null
                    )
                }
            }
            author?.let {
                Box(
                    modifier = GlanceModifier.fillMaxSize().defaultWeight(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        style = TextStyle(
                            color = ColorProvider(color = Color.White),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        text = author,
                    )
                }
            }
        }
    }
}

private suspend fun Context.getRandomImage(url: String, force: Boolean = false): Bitmap? {
    Log.d("QuotesWidget", "getRandomImage")
    val request = ImageRequest.Builder(this).data(url).apply {
        if (force) {
            memoryCachePolicy(CachePolicy.DISABLED)
            diskCachePolicy(CachePolicy.DISABLED)
        }
    }.build()

    return when (val result = imageLoader.execute(request)) {
        is ErrorResult -> {
            Log.d("QuotesWidget", "ErrorResult = ${result.throwable}")
            throw result.throwable
        }

        is SuccessResult -> {
            Log.d("QuotesWidget", "SuccessResult = ${result.drawable.toBitmapOrNull()}")
            result.drawable.toBitmapOrNull()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuotesWidgetContentPreview() {
    WidgetContent(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_background))
            .appWidgetBackground()
            .padding(8.dp),
        quote = "Empty quote",
        author = "Author",
        memeUrl = ""
    )
}