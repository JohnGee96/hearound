package com.hearound.hearound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Button back = (Button) findViewById(R.id.submitButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);

                Bundle bundle = new Bundle();

                EditText userField = (EditText)findViewById(R.id.userID);
                EditText postField = (EditText)findViewById(R.id.postBody);
                EditText latField = (EditText)findViewById(R.id.latitude);
                EditText longField = (EditText)findViewById(R.id.longitude);

                try {
                    bundle.putString("userID", userField.getText().toString());
                    bundle.putString("postBody", postField.getText().toString());
                    bundle.putDouble("latitude", Double.parseDouble(latField.getText().toString()));
                    bundle.putDouble("longitude", Double.parseDouble(longField.getText().toString()));
                } catch (Exception e) {
                    Log.d("****** exception", "empty text box" + e);
                }

                intent.putExtras(bundle);

                startActivity(intent);
            }

        });
    }
}
