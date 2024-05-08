package com.app.athkar.export.presentation.composables

import android.graphics.Picture
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.athkar.R
import com.app.athkar.core.util.Constants
import com.app.athkar.ui.theme.ExportText

@Composable
fun CanvasImage(text: String, picture: Picture) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val exportedImageWidth = screenWidth - 16.dp
    val exportedImageHeight = (exportedImageWidth - 16.dp) * Constants.IMAGE_ASPECT_RATIO


    Column(
        modifier = Modifier
            .width(exportedImageWidth)
            .height(exportedImageHeight)
            .drawWithCache {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()
                onDrawWithContent {
                    val pictureCanvas =
                        Canvas(
                            picture.beginRecording(
                                width,
                                height
                            )
                        )
                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }
                    picture.endRecording()

                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawPicture(
                            picture
                        )
                    }
                }
            }
    ) {
        Box(
            Modifier
                .width(exportedImageWidth)
                .height(exportedImageHeight),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_export_template),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(exportedImageWidth)
                    .height(exportedImageHeight)
            )

            Text(
                text = text,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .align(Alignment.Center),
                color = ExportText,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewImageCanvas() {
    CanvasImage(
        text = "سبحان الله",
        picture = Picture()
    )
}