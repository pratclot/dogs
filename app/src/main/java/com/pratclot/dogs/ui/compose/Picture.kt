package com.pratclot.dogs.ui.compose

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import com.pratclot.dogs.R
import com.pratclot.dogs.fragments.Picture.ClickListener

@Composable
fun Picture(
    imageUrl: String,
    clickListener: ClickListener,
    likedState: Boolean,
    onLoadingError: (e: Throwable?) -> Unit,
    onLoadingSuccess: (Bitmap) -> Unit
) {
    ConstraintLayout(modifier = Modifier.background(Color.White)) {
        val (image, fab) = createRefs()
        AsyncImage(
            model = imageUrl,
            contentDescription = "a dog picture",
            onError = { onLoadingError(it.result.throwable) },
            onSuccess = { onLoadingSuccess(it.result.drawable.toBitmap()) },
            placeholder = painterResource(id = R.drawable.ic_baseline_sync_24),
//        placeholder = painterResource(id = R.drawable.rotating_sync),
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(image) {},
        )
        FloatingActionButton(
            onClick = { clickListener.onClick(imageUrl) },
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(fab) {
                    bottom.linkTo(image.bottom, margin = 16.dp)
                    end.linkTo(image.end, margin = 16.dp)
                }
        ) {
            Image(
                painter = painterResource(id = if (likedState) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24),
                contentDescription = "like button"
            )
        }
    }
}

@Preview
@Composable
private fun PicturePreview() {
    Picture(
        imageUrl = "",
        clickListener = ClickListener({}),
        likedState = false,
        onLoadingError = {},
        onLoadingSuccess = {}
    )
}