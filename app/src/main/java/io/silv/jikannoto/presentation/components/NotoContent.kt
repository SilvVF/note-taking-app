package io.silv.jikannoto.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.silv.jikannoto.domain.models.NotoItem
import io.silv.jikannoto.ui.theme.LocalCustomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotoContent(noto: NotoItem) {

    val color = LocalCustomTheme.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.notoListBackGround)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(4.dp)) {
                Text(
                    text = noto.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color.text,
                    modifier = Modifier.weight(1f, true)
                )
                LazyRow(modifier = Modifier.weight(0.25f)) {
                    items(noto.category) {
                        SuggestionChip(
                            onClick = { },
                            modifier = Modifier
                                .height(20.dp)
                                .wrapContentWidth(),
                            enabled = true,
                            colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                                containerColor = color.primary,
                            ),
                            border = androidx.compose.material3.SuggestionChipDefaults.suggestionChipBorder(
                                borderColor = color.primary,
                            ),
                            label = {
                                Text(text = it, color = color.notoListBackGround)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = noto.content,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                color = color.subtext
            )
        }
    }
}