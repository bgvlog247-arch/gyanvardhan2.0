package com.example.ui.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PdfMaterial
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@Composable
fun PdfNotesScreen(
    currentLanguage: AppLanguage,
    pdfMaterials: List<PdfMaterial>,
    onPdfClick: (PdfMaterial) -> Unit,
    onDownloadClick: (PdfMaterial) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf("ALL") }
    val subjects = listOf("ALL", "Static GK", "Mathematics", "Science")

    val filteredPdfs = pdfMaterials.filter {
        selectedSubject == "ALL" || it.subject.contains(selectedSubject, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("pdf_notes_screen")
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = AppStrings.get("pdf_notes", currentLanguage),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "हस्तलिखित नोट्स, फॉर्मूला हैंडबुक एवं पिछले वर्षों के प्रश्न संकलन" else "Hand-written study materials, formula sheets & NCERT compendiums",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Subject Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subjects) { subj ->
                FilterChip(
                    selected = selectedSubject == subj,
                    onClick = { selectedSubject = subj },
                    label = {
                        Text(
                            text = if (subj == "ALL") AppStrings.get("all", currentLanguage) else subj,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanAccent,
                        selectedLabelColor = NavyDark
                    )
                )
            }
        }

        // PDF List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredPdfs) { pdf ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPdfClick(pdf) }
                        .testTag("pdf_item_${pdf.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (pdf.isFree) EmeraldSuccess else SaffronGold
                                ) {
                                    Text(
                                        text = if (pdf.isFree) AppStrings.get("free", currentLanguage) else "PREMIUM",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (pdf.isFree) Color.White else NavyDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = pdf.subject,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) pdf.titleHi else pdf.titleEn,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "📄 ${pdf.pageCount} ${if (currentLanguage == AppLanguage.HINDI) "पृष्ठ" else "Pages"} • ${pdf.fileSizeMb} MB",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                if (pdf.isDownloaded) onPdfClick(pdf) else onDownloadClick(pdf)
                            }
                        ) {
                            Icon(
                                imageVector = if (pdf.isDownloaded) Icons.Default.CheckCircle else Icons.Default.FileDownload,
                                contentDescription = "Download",
                                tint = if (pdf.isDownloaded) EmeraldSuccess else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
