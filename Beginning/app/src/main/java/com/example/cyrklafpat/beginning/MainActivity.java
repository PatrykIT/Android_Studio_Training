package com.example.cyrklafpat.beginning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.cyrklafpat.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view){
        /*
        The Intent constructor takes two parameters:
        A Context as its first parameter (this is used because the Activity class is a subclass of Context)
        The Class of the app component to which the system should deliver the Intent
        (in this case, the activity that should be started).
        */

        Intent my_intent = new Intent(this, Display_Activity_Message.class);
        EditText text_edit_handle = (EditText) findViewById(R.id.editText);
        String final_message = text_edit_handle.getText().toString();

        /* The putExtra() method adds the EditText's value to the intent. An Intent can carry data
        types as key-value pairs called extras. Your key is a public constant EXTRA_MESSAGE because
        the next activity uses the key to retrieve the text value. */
       my_intent.putExtra(EXTRA_MESSAGE, final_message);

        /* The startActivity() method starts an instance of the Display_Activity_Message
        specified by the Intent */
        startActivity(my_intent);
    }
}
