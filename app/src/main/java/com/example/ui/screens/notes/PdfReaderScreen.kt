package com.example.ui.screens.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PdfMaterial
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderScreen(
    pdf: PdfMaterial,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    var currentPage by remember { mutableIntStateOf(1) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var isBookmarked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) pdf.titleHi else pdf.titleEn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${pdf.subject} • ${pdf.chapter}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isBookmarked = !isBookmarked }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) SaffronGold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDownloadClick) {
                        Icon(
                            imageVector = if (pdf.isDownloaded) Icons.Default.CheckCircle else Icons.Default.FileDownload,
                            contentDescription = "Download",
                            tint = if (pdf.isDownloaded) EmeraldSuccess else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { if (currentPage > 1) currentPage-- },
                        enabled = currentPage > 1,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("◀ " + AppStrings.get("previous", currentLanguage), fontSize = 12.sp)
                    }

                    Text(
                        text = "${AppStrings.get("of", currentLanguage)} $currentPage / ${pdf.pageCount}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    FilledTonalButton(
                        onClick = { if (currentPage < pdf.pageCount) currentPage++ },
                        enabled = currentPage < pdf.pageCount,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(AppStrings.get("next", currentLanguage) + " ▶", fontSize = 12.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            // Document Viewport (Clean Digital Reader Layout)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header of the page
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "GYANVARDHAN 2.0 • STUDY NOTES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "PAGE $currentPage OF ${pdf.pageCount}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color.LightGray)

                    // Title on document
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) pdf.titleHi else pdf.titleEn,
                        fontSize = (16 * zoomLevel).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Subject: ${pdf.subject} | Chapter: ${pdf.chapter}",
                        fontSize = (12 * zoomLevel).sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated Page Content
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AmberLight.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "📌 मुख्य अवधारणा (Key Concept):" else "📌 Core Formula & High-Yield Summary:",
                                fontSize = (13 * zoomLevel).sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    pdf.previewSummaryHi.ifEmpty { "1. विभाज्यता के नियम एवं सूत्र\n2. चक्रवृद्धि ब्याज निकालने का ट्री मेथड\n3. महत्वपूर्ण परीक्षा उपयोगी तथ्य एवं ट्रिक्स" }
                                else
                                    pdf.previewSummaryEn.ifEmpty { "1. Master formula sheet and derivation steps.\n2. Step-by-step shortcuts verified by exam toppers.\n3. Previous year exam questions analysis." },
                                fontSize = (12 * zoomLevel).sp,
                                lineHeight = (18 * zoomLevel).sp,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "• परीक्षा दृष्टि से महत्वपूर्ण बिंदु:\nयह अध्याय पिछले 5 वर्षों में SSC GD एवं रेलवे परीक्षाओं में न्यूनतम 3 से 4 प्रश्नों के रूप में पूछा गया है। सभी सूत्रों को याद करें और प्रतिदिन अभ्यास करें।\n\n• अभ्यास प्रश्न:\n1. 1 से 100 तक सभी अभाज्य संख्याओं की कुल संख्या कितनी है? (उत्तर: 25)\n2. दो संख्याओं का ल.स. 180 तथा म.स. 6 है, यदि एक संख्या 30 है तो दूसरी क्या होगी? (उत्तर: 36)"
                        else
                            "• Exam Strategy & Analysis:\nThis chapter has contributed an average of 3-4 questions in recent examinations. Carefully review the derivations and shortcut methods before attempting the mock tests.\n\n• Practice Questions:\n1. How many total prime numbers exist between 1 and 100? (Answer: 25)\n2. LCM of two numbers is 180 and HCF is 6. If one number is 30, find the other. (Answer: 36)",
                        fontSize = (12 * zoomLevel).sp,
                        lineHeight = (18 * zoomLevel).sp,
                        color = Color.Black
                    )
                }

                // Floating Zoom In/Out pills
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel - 0.15f).coerceAtLeast(0.85f) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavyDark.copy(alpha = 0.8f))
                    ) {
                        Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White)
                    }
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel + 0.15f).coerceAtMost(1.5f) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavyDark.copy(alpha = 0.8f))
                    ) {
                        Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White)
                    }
                }
            }
        }
    }
}
