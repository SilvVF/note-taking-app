package io.silv.jikannoto.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.silv.jikannoto.NavItem
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import io.silv.jikannoto.ui.theme.LocalSpacing

@Composable
fun NavigationItem(
    selected: Boolean,
    navItem: NavItem,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(6.dp))
            .background(
                color = if (!selected) LocalCustomTheme.current.drawer
                else LocalCustomTheme.current.primary,
            )
            .clickable {
                onClick()
            }
            .padding(start = LocalSpacing.current.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (navItem.painter != null) {
            Icon(
                painter = painterResource(id = navItem.painter),
                tint = if (selected) LocalCustomTheme.current.drawer
                else LocalCustomTheme.current.primary,
                contentDescription = navItem.name,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .size(40.dp)
            )
        } else {
            Icon(
                imageVector = navItem.icon,
                tint = if (selected) LocalCustomTheme.current.drawer
                else LocalCustomTheme.current.primary,
                contentDescription = navItem.name,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .size(40.dp)
            )
        }
        Text(
            text = navItem.name,
            color = if (selected) LocalCustomTheme.current.drawer
            else LocalCustomTheme.current.primary,
            modifier = Modifier.padding(
                start = LocalSpacing.current.l,
                top = LocalSpacing.current.s,
                bottom = LocalSpacing.current.s,
            ),
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
        )
    }
}