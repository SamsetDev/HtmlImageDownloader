package com.samset.htmlimagedownloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.samset.htmlimagedownloader.downloader.HtmlParserClass;

public class MainActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textView = (TextView) findViewById(R.id.html_text);
            downloadHtmlImage();

    }

    private void downloadHtmlImage() {
        HtmlParserClass p = new HtmlParserClass(textView, this);
        Spanned htmlSpan = Html.fromHtml(Constants.text, p, null);
        textView.setText(htmlSpan);
    }
}
