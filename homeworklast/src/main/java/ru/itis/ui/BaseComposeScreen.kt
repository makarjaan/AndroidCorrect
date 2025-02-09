package ru.itis.ui


import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.itis.data.entities.BookEntity
import ru.itis.homeworklast.R




@Composable
fun SetToolbar(
    userName: String,
    onLogoutClick: () -> Unit
) {

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.widthIn(16.dp))

        Text(
            text = userName,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.weight(2f))

        IconButton(onClick = onLogoutClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = stringResource(id = R.string.logout),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.widthIn(8.dp))
    }
}


@Composable
fun ComposeListSample(
    listState: LazyGridState,
    items: List<BookEntity>,
    userName: String,
    onClick: (BookEntity) -> Unit,
    onLongClick: (BookEntity) -> Unit,
    onLogoutClick: () -> Unit
) {
    SetToolbar(userName = userName, onLogoutClick)

    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(2),
    ) {
        items(count = items.size) { position ->
            ListItemSample(items[position], onClick, onLongClick)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItemSample(
    model: BookEntity,
    onClick: (BookEntity) -> Unit,
    onLongClick: (BookEntity) -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(8.dp))
            .shadow(2.dp)
            .combinedClickable(
                onClick = { onClick(model) },
                onLongClick = { onLongClick(model) },
            )
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Log.d("TEST-TAG", "${model.image}")
        AsyncImage(
            model = model.image,
            contentDescription = stringResource(id = R.string.book_image),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit,
            error = painterResource(id = R.drawable.default_image)
        )

        Text(
            text = model.title,
            color = Color.Black,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

    }
}

@Composable
fun DetailsPage(
    userName: String,
    onLogoutClick: () -> Unit,
    book: BookEntity,
    onReviewUpdated: (String) -> Unit,
    onRatingUpdated: (Float) -> Unit,
    onImageUrlUpdated: (String) -> Unit,
    onSaveClick: () -> Unit
) {

    var imageUrl = remember { mutableStateOf(book.image) }

    Column {
        SetToolbar(
            userName = userName,
            onLogoutClick = onLogoutClick,
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = imageUrl.value,
                contentDescription = stringResource(id = R.string.book_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                error = painterResource(id = R.drawable.default_image)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = book.title,
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${stringResource(id = R.string.author)} ${book.author}",
                style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${stringResource(id = R.string.genre)} ${book.genre}",
                style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
            )


            Spacer(modifier = Modifier.height(8.dp))

            RatingBar(
                initialRating = book.rating,
                onRatingChanged = { newRating ->
                    onRatingUpdated(newRating)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.review),
                style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
            )

            book.review?.let {
                ReviewField(
                    initialReview = it,
                    onReviewChanged = { updatedReview ->
                        onReviewUpdated(updatedReview)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ImageUrlTextField(
                onImageUrlUpdated = onImageUrlUpdated,
                onSaveClick = onSaveClick
            )

        }
    }
}

@Composable
fun ImageUrlTextField(
    onImageUrlUpdated: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    val inputUrl = remember { mutableStateOf("") }
    val isModified = remember { mutableStateOf(false) }

    TextField(
        value = inputUrl.value,
        onValueChange = { newUrl ->
            inputUrl.value = newUrl
            isModified.value = newUrl.isNotEmpty() 
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        textStyle = TextStyle(color = Color.Black),
        placeholder = { Text(text = stringResource(id = R.string.bottom_url)) }
    )

    if (isModified.value) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(
                onClick = {
                    onImageUrlUpdated(inputUrl.value)
                    onSaveClick()
                }
            ) {
                Text(text = stringResource(id = R.string.save), fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun RatingBar(
    initialRating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val rating = remember { mutableStateOf(initialRating) }

    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating.value) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = stringResource(id = R.string.star),
                tint = if (i <= rating.value) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .clickable {
                        rating.value = i.toFloat()
                        onRatingChanged(i.toFloat())
                    }
            )
        }
    }
}

@Composable
fun ReviewField(
    initialReview: String,
    onReviewChanged: (String) -> Unit
) {
    val reviewText = remember { mutableStateOf(initialReview) }
    val isModified = remember { mutableStateOf(false) }

    Column {
        TextField(
            value = reviewText.value,
            onValueChange = { newReview ->
                reviewText.value = newReview
                isModified.value = newReview != initialReview
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            textStyle = TextStyle(fontSize = 20.sp),
            maxLines = 5,
            minLines = 2
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (isModified.value) {
            Button(
                onClick = {
                    onReviewChanged(reviewText.value)
                    isModified.value = false
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(id = R.string.save), fontSize = 18.sp)
            }
        }
    }
}
