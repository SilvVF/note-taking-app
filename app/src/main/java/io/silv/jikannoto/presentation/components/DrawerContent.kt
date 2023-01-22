package io.silv.jikannoto.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.silv.jikannoto.NavItem
import io.silv.jikannoto.presentation.navigation.Screens
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import io.silv.jikannoto.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContentNav(
    drawerState: DrawerState,
    navigationItems: List<NavItem>,
    selectedScreen: Screens,
    name: Pair<String, String>,
    imageUrl: String,
    darkTheme: Boolean,
    onImageUrlChange: (String) -> Unit,
    onToggleDarkTheme: () -> Unit,
    navigate: (Screens) -> Unit
) {

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .fillMaxHeight(),
        drawerContainerColor = LocalCustomTheme.current.drawer
    ) {
        val spacing = LocalSpacing.current
        Box {
            Box {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    item {
                        ProfileImage(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(
                                    bottom = LocalSpacing.current.xl,
                                    top = LocalSpacing.current.xl
                                )
                                .height(150.dp),
                            imageUrl = imageUrl,
                            onImageUrlChange = { onImageUrlChange(it) }
                        )
                    }
                    item {
                        ProfileName(
                            firstName = name.first,
                            lastName = name.second
                        )
                    }
                    items(navigationItems) { navItem ->
                        val selected = remember(selectedScreen) {
                            derivedStateOf { selectedScreen == navItem.route }
                        }.value
                        NavigationItem(
                            selected = selected,
                            navItem = navItem,
                            onClick = {
                                navigate(navItem.route)
                            }
                        )
                        Spacer(modifier = Modifier.height(spacing.m))
                    }
                }
            }
            ThemeToggle(
                darkTheme = darkTheme,
                modifier = Modifier
                    .size(90.dp)
                    .clickable(MutableInteractionSource(), null) {
                        onToggleDarkTheme()
                    }
                    .align(Alignment.TopEnd),
            )
        }
    }
}