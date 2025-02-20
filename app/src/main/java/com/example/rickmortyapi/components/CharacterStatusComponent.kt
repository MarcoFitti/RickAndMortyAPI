package com.example.rickmortyapi.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.models.domain.CharacterStatus
import com.example.rickmortyapi.ui.theme.RickTextPrimary


//@Composable
//fun CharacterStatusComponent(characterStatus: CharacterStatus) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .border(
//                width = 1.dp,
//                color = characterStatus.color,
//                shape = RoundedCornerShape(12.dp)
//            )
//            .padding(horizontal = 12.dp, vertical = 4.dp)
//    ) {
//        Text(
//            text = "Status: ${characterStatus.displayName}",
//            fontSize = 20.sp,
//            color = RickTextPrimary
//        )
//    }
//}



@Composable
fun CharacterStatusComponent(characterStatus: CharacterStatus) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = characterStatus.color,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                top = 12.dp,
                bottom = 12.dp,
                start = 12.dp,
                end = 48.dp
            )
    ) {
        Text(
            text  ="Status",
            fontSize = 14.sp
        )

        Text(
            text = characterStatus.displayName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewAlive(){
    CharacterStatusComponent(CharacterStatus.Alive)
}

@Preview
@Composable
fun CharacterStatusComponentPreviewDead(){
    CharacterStatusComponent(CharacterStatus.Dead)
}
@Preview
@Composable
fun CharacterStatusComponentPreviewUnknown(){
    CharacterStatusComponent(CharacterStatus.Unknown)
}



