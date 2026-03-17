package com.fontakip.presentation.screens.backup

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fontakip.presentation.theme.Background
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.getThemeBackgroundColor
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }

    // File picker for import
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isImporting = true
                try {
                    val success = importDatabase(context, it)
                    if (success) {
                        Toast.makeText(context, "Veritabanı içe aktarıldı! Uygulama yeniden başlatılıyor...", Toast.LENGTH_LONG).show()
                        // Restart app after successful import
                        restartApp(context)
                    } else {
                        Toast.makeText(context, "İçe aktarma başarısız!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    isImporting = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = getPrimaryColor()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "YEDEKLE",
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getThemeBackgroundColor())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Veritabanı Yedekleme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Veritabanınızı yedekleyebilir veya daha önce aldığınız bir yedeği ger yükleyebilirsiniz.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Export Card
            BackupCard(
                title = "Veritabanını Dışa Aktar",
                description = "Mevcut verilerinizi SQL formatında yedekleyin",
                icon = Icons.Default.Folder,
                isLoading = isExporting,
                onClick = {
                    scope.launch {
                        // Check storage permission for Android 11+
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()) {
                                Toast.makeText(context, "Lutfen 'Tum dosyalara erisim' iznini verin", Toast.LENGTH_LONG).show()
                                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                                isExporting = false
                                return@launch
                            }
                        }
                        
                        isExporting = true
                        try {
                            val file = exportDatabase(context)
                            Toast.makeText(context, "Yedek başarıyla Downloads klasörüne kaydedildi: ${file.name}", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isExporting = false
                        }
                    }
                }
            )

            // Import Card
            BackupCard(
                title = "Veritabanını İçe Aktar",
                description = "Daha önce aldığınız bir yedeği geri yükleyin",
                icon = Icons.Default.CloudUpload,
                isLoading = isImporting,
                onClick = {
                    // Filter for database files only
                    importLauncher.launch(arrayOf("application/octet-stream", "application/x-sqlite3", "application/sql"))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = getPrimaryColor().copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Backup,
                        contentDescription = null,
                        tint = getPrimaryColor(),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Yedekleme Önerisi",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = getPrimaryColor()
                        )
                        Text(
                            text = "Düzenli olarak verilerinizi yedeklemenizi öneririz. Bu sayede veri kaybı durumunda verilerinizi geri getirebilirsiniz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BackupCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = getPrimaryColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = getPrimaryColor()
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = getPrimaryColor(),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private suspend fun exportDatabase(context: android.content.Context): File = withContext(Dispatchers.IO) {
    val dbFile = context.getDatabasePath("fontakip_database")
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale("tr", "TR"))
    val fileName = "Fontakip_${dateFormat.format(Date())}.db"
    
    // Get the Downloads directory
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    
    if (!downloadsDir.exists()) {
        downloadsDir.mkdirs()
    }
    
    val exportFile = File(downloadsDir, fileName)
    
    // Read database and create export
    FileInputStream(dbFile).use { input ->
        FileOutputStream(exportFile).use { output ->
            input.copyTo(output)
        }
    }
    
    exportFile
}

private suspend fun importDatabase(context: android.content.Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
    val dbName = "fontakip_database"
    val dbFile = context.getDatabasePath(dbName)
    
    // Delete existing database and WAL files
    context.deleteDatabase(dbName)
    
    // Also delete WAL (Write-Ahead Log) files if they exist
    val walFile = File(dbFile.path + "-wal")
    val shmFile = File(dbFile.path + "-shm")
    if (walFile.exists()) walFile.delete()
    if (shmFile.exists()) shmFile.delete()
    
    // Ensure parent directory exists
    dbFile.parentFile?.mkdirs()
    
    // Copy new database from URI
    var success = false
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(dbFile).use { output ->
            input.copyTo(output)
            success = true
        }
    }
    
    success
}

private fun restartApp(context: android.content.Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    android.os.Process.killProcess(android.os.Process.myPid())
}
