package com.eleks.mowid.ui.feature.widget.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.eleks.mowid.ui.feature.widget.LeftArrowClickAction
import com.eleks.mowid.R
import com.eleks.mowid.ui.feature.widget.QuotesWidget
import com.eleks.mowid.ui.feature.widget.RightArrowClickAction

@Composable
fun QuotesWidgetContent(
    modifier: GlanceModifier,
) {

    val context = LocalContext.current

    val prefs = currentState<Preferences>()
    // TODO remove predefined strings
    val quote = prefs[QuotesWidget.quotePreference] ?: "Quote"
    val author = prefs[QuotesWidget.authorPreference] ?: "Author"

    WidgetContent(
        modifier = modifier,
        quote = quote,
        author = author
    )
}

@Composable
fun WidgetContent(
    modifier: GlanceModifier,
    quote: String,
    author: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Horizontal.Start
    ) {
        Text(
            modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
            style = TextStyle(
                textAlign = TextAlign.Center,
                color = ColorProvider(color = Color.White),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            text = quote,
        )
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
        author = "Author"
    )
}