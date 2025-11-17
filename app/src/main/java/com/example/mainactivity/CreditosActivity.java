package com.example.mainactivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreditosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);
        gitHubUsername();
    }
    public void gitHubUsername(){
        TextView textViewG = (TextView)findViewById(R.id.Gabriel);
        TextView textViewJH = (TextView)findViewById(R.id.JH);
        TextView textViewJW = (TextView)findViewById(R.id.JW);

        textViewG.setClickable(true);
        textViewG.setMovementMethod(LinkMovementMethod.getInstance());
        textViewJH.setClickable(true);
        textViewJH.setMovementMethod((LinkMovementMethod.getInstance()));
        textViewJW.setClickable(true);
        textViewJW.setMovementMethod((LinkMovementMethod.getInstance()));

        String textG = "<a href='https://github.com/Gab1203'>Gabriel Fraga</a>";
        String textJH = "<a href='https://github.com/HenrykMendes'>João Henrique Mendes</a>";
        String textJW = "<a href='https://github.com/JuanWillian'>Juan Willian</a>";

        textViewG.setText(Html.fromHtml(textG, Html.FROM_HTML_MODE_LEGACY));
        textViewJH.setText(Html.fromHtml(textJH, Html.FROM_HTML_MODE_LEGACY));
        textViewJW.setText(Html.fromHtml(textJW, Html.FROM_HTML_MODE_LEGACY));

    }
}