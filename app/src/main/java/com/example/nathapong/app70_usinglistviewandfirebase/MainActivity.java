package com.example.nathapong.app70_usinglistviewandfirebase;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText edtName, edtPunchPower, edtPunchSpeed;
    Button btnSend;
    ListView listViewData;

    DatabaseReference databaseReference;

    FirebaseListAdapter<Boxer> firebaseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edtName = (EditText)findViewById(R.id.edtName);
        edtPunchPower = (EditText)findViewById(R.id.edtPunchPower);
        edtPunchSpeed = (EditText)findViewById(R.id.edtPunchSpeed);
        btnSend = (Button)findViewById(R.id.btnSend);
        listViewData = (ListView)findViewById(R.id.listViewData);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databaseReference.keepSynced(true);

        //databaseReference.removeValue();   // Delete All Child Node


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boxer boxer = new Boxer(edtName.getText().toString(),
                        Integer.parseInt(edtPunchPower.getText().toString()),
                        Integer.parseInt(edtPunchSpeed.getText().toString()));

                databaseReference.child(databaseReference.push().getKey()).setValue(boxer);

                edtName.setText("");
                edtPunchPower.setText("");
                edtPunchSpeed.setText("");

            }
        });


        FirebaseListOptions<Boxer> options = new FirebaseListOptions.Builder<Boxer>()
                .setLayout(android.R.layout.two_line_list_item)
                .setQuery(databaseReference, Boxer.class)
                .build();


        firebaseListAdapter = new FirebaseListAdapter<Boxer>(options) {
            @Override
            protected void populateView(View v, Boxer model, final int position) {

                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getBoxerName());
                ((TextView)v.findViewById(android.R.id.text2)).setText(
                        "Punch Power : " + model.getPunchPower()+"" + "\n" +
                        "Punch Speed : " + model.getPunchSpeed()+"");


                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setMessage("DID YOU WANT TO DELETE THIS ITEM ?");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Toast.makeText(MainActivity.this, "Deleted Item at Position : " +
                                        position, Toast.LENGTH_SHORT).show();

                                DatabaseReference myRef = firebaseListAdapter.getRef(position);
                                myRef.removeValue();

                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.dismiss();
                            }
                        });

                        builder.show();


                    }
                });

            }
        };

        listViewData.setAdapter(firebaseListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseListAdapter.stopListening();
    }
}
