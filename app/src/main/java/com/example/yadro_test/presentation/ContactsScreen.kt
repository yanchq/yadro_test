package com.example.yadro_test.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yadro_test.domain.Contact
import kotlin.collections.forEach

private fun formatContactsList(contacts: List<Contact>): List<Any> {
    val list = mutableListOf<Any>()
    var prev: Char? = null
    contacts.forEach { contact ->
        if (contact.name[0] != prev) {
            prev = contact.name[0]
            list.add(prev)
        }
        list.add(contact)
    }
    return list
}

@Composable
fun ContactsScreen(viewModel: MainViewModel) {

    val contacts = viewModel.contacts.collectAsState()
    val buttonState = viewModel.buttonState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(9f)
        ) {
            items(formatContactsList(contacts.value)) { contact ->
                if (contact is Contact) {
                    ContactCard(contact)
                } else {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.Blue, RoundedCornerShape(4.dp))
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = contact.toString().uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { viewModel.deleteDuplicates() },
                enabled = buttonState.value,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "Delete duplicates",
                    fontSize = 18.sp
                )
            }
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