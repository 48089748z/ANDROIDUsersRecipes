package com.casino.uri.firebaseusers;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment
{
    FirebaseConfig config;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ListView regramsList = (ListView) view.findViewById(R.id.LVnotes);
        config  = (FirebaseConfig) getActivity().getApplication();
        Firebase loggedUserNotesReference = config.getLoggedUserReference().child("NotesList");
        FirebaseListAdapter<Note> adapter = new FirebaseListAdapter<Note>(getActivity(), Note.class, R.layout.listview_layout, loggedUserNotesReference) {
            @Override
            protected void populateView(View view, Note note, int position)
            {
                TextView title = (TextView) view.findViewById(R.id.TVtitle);
                TextView description = (TextView) view.findViewById(R.id.TVdescription);
                TextView latlng = (TextView) view.findViewById(R.id.TVlatlng);
                ImageView image = (ImageView) view.findViewById(R.id.IVimage);
                title.setText(note.getTitle());
                description.setText(note.getDescription());
                try
                {
                    File imagePath = new File(note.getImagePath());
                    if (imagePath.exists()) {Picasso.with(getContext()).load(imagePath).centerCrop().resize(185, 185).into(image);}
                    else{Picasso.with(getContext()).load(R.drawable.ic_alert).centerCrop().resize(185, 185).into(image);}
                    //image.setImageBitmap(decodeImage(note.getCodedImage()));  FULLY SAVE IMAGES IN FIREBASE MAKES YOU RUN OUT OF MEMORY
                }
                catch (Exception e)
                {
                    Picasso.with(getContext()).load(R.drawable.ic_alert).centerCrop().resize(185, 185).into(image);
                }
                if (config.getLanguage().equals("SPANISH")) {latlng.setText("Latitud: " + note.getLatitude() + "\nLongitud: " + note.getLongitude());}
                else {latlng.setText("Latitude: " + note.getLatitude() + "\nLongitude: " + note.getLongitude());}
            }
        };
        regramsList.setAdapter(adapter);
        regramsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                choiceWindow(position);
            }
        });
        return view;
    }
    public void choiceWindow(final int position)
    {
        new AlertDialog.Builder(getContext())
                .setTitle("   WHAT YO PLAY?")
                .setPositiveButton("Audio", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        openAudio();
                    }
                })
                .setNegativeButton("Video", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        openVideo(position);
                    }
                })
                .setIcon(R.drawable.ic_alert)
                .show();
    }
    public void openVideo(int position)
    {
        Intent videoPlayer = new Intent(getContext(), VideoPlayer.class);
        videoPlayer.putExtra("position", position);
        startActivity(videoPlayer);
    }
    public void openAudio()
    {
        Intent openRecorder = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
        startActivity(openRecorder);
    }
    public NotesFragment() {
    }
    public Bitmap decodeImage(String toDecode) //FULLY SAVE IMAGES IN FIREBASE MAKES YOU RUN OUT OF MEMORY
    {
        byte[] decodedString = Base64.decode(toDecode, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
