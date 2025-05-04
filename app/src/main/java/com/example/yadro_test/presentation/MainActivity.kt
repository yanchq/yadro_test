package com.example.yadro_test.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.yadro_test.domain.Contact
import com.example.yadro_test.domain.DeleteDuplicatesResult
import com.example.yadro_test.ui.theme.Yadro_testTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactsScreen(viewModel)
        }
        checkPermissionsAndGetContacts()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serviceResult.collect {
                    when (it) {
                        DeleteDuplicatesResult.Success ->
                            Toast.makeText(
                                this@MainActivity,
                                "Дублирующиеся контакты успешно удалены",
                                Toast.LENGTH_SHORT
                            ).show()
                        DeleteDuplicatesResult.NothingFound ->
                            Toast.makeText(
                                this@MainActivity,
                                "Дублирующиеся контакты не найдены",
                                Toast.LENGTH_SHORT
                            ).show()
                        is DeleteDuplicatesResult.Error ->
                            Toast.makeText(
                                this@MainActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()

                        null -> {}
                    }
                }
            }
        }
    }

    private fun checkPermissionsAndGetContacts() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_REQUEST
            )
        } else {
            viewModel.getContacts()
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_CONTACTS),
                CONTACTS_PERMISSION_REQUEST
            )
        }
    }

    companion object {
        private const val CONTACTS_PERMISSION_REQUEST = 101
    }
}

@Composable
fun ContactsScreen(viewModel: MainViewModel) {

    val contacts = viewModel.contacts.collectAsState()
    val buttonState = viewModel.buttonState.collectAsState()

    Column {
        LazyColumn(
            modifier = Modifier
                .weight(9f)
        ) {
            items(contacts.value) { contact ->
                ContactCard(contact)
            }
        }
        Button(
            onClick = { viewModel.deleteDuplicates() },
            enabled = buttonState.value,
            modifier = Modifier
                .weight(1f)
        ) {
            Text(text = "Delete duplicates")
        }
    }
}

@Composable
private fun ContactCard(contact: Contact) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Blue, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        ContactAvatar(contact)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = contact.name,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = contact.phoneNumber,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun Prev() {
    ContactCard(Contact(1, "yan", "89146536625", null))
}

@Composable
private fun ContactAvatar(contact: Contact) {
    if (contact.photo == null) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Blue),
            contentAlignment = Alignment.Center
        ) {
            Text(text = contact.name[0].toString().uppercase())
        }
    } else {
        Image(
            bitmap = contact.photo.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
    }
}
