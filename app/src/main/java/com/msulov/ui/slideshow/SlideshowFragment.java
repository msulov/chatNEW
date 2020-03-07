package com.msulov.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msulov.MainActivity;
import com.msulov.Models.ChatMessage;
import com.msulov.Models.User;
import com.msulov.R;

import br.com.instachat.emojilibrary.model.layout.WhatsAppPanel;
import br.com.instachat.emojilibrary.model.layout.WhatsAppPanelEventListener;

public class SlideshowFragment extends Fragment implements WhatsAppPanelEventListener {

    private FirebaseUser auth;
    private DatabaseReference databaseReference;

    public static String id, name;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // Получаем имя пользователя
        auth = FirebaseAuth.getInstance().getCurrentUser();
        id = auth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);

                if (name == null)
                    User.unistalling = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);

        // Отобразить сообщения
        ListView messages = root.findViewById(R.id.messages);

        messages.setAdapter(MainActivity.adapter);



        return root;
    }

    @Override
    public void onSendClicked() {
        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        if (User.unistalling)
            FirebaseDatabase.getInstance()
                    .getReference("Messages")
                    .push()
                    .setValue(new ChatMessage("Пользователь удален", "Admin")
                    );
        else
            FirebaseDatabase.getInstance()
                    .getReference("Messages")
                    .push()
                    .setValue(new ChatMessage(messageInput.getText().toString(), name)
                    );

        // Clear the input
        messageInput.setText("");
    }
}