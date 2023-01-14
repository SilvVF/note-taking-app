package io.silv.jikannoto.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import io.silv.jikannoto.ui.theme.LocalSpacing

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
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = noto.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color.text,
                    modifier = Modifier.padding(end = LocalSpacing.current.m)
                )
                for (
                    category in runCatching {
                        noto.category.subList(
                            0,
                            2
                        )
                    }.getOrDefault(noto.category)
                ) {
                    SuggestionChip(
                        onClick = { },
                        modifier = Modifier
                            .height(20.dp)
                            .wrapContentWidth()
                            .padding(end = LocalSpacing.current.xs),
                        enabled = true,
                        colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                            containerColor = color.primary,
                        ),
                        border = androidx.compose.material3.SuggestionChipDefaults.suggestionChipBorder(
                            borderColor = color.primary,
                        ),
                        label = {
                            Text(text = category, color = color.notoListBackGround)
                        }
                    )
                }

                if (noto.category.size > 2)
                    Text("...", color = color.subtext)
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