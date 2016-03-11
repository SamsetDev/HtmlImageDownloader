package com.samset.htmlimagedownloader.downloader;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by samset on 11/03/16.
 */
public class HtmlParserClass implements Html.ImageGetter {

    Context c;
    //View container;
    TextView container;
    String s;
    WindowManager wm;
    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     *
     * @param t
     * @param c
     */
    public HtmlParserClass(TextView t, Context c) {
        this.c = c;
        this.container = t;
        wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
    }

    public Drawable getDrawable(String source) {
        URLBitmap urlDrawable = new URLBitmap();

        // get the actual source
        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLBitmap urlDrawable;

        public ImageGetterAsyncTask(URLBitmap d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // set the correct bound according to the result from HTTP call

            Log.d("height", "" + result.getIntrinsicHeight());
            Log.d("width",""+result.getIntrinsicWidth());
            urlDrawable.setBounds(50, 50, 0+result.getIntrinsicWidth(), 0+result.getIntrinsicHeight());

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;

            // redraw the image by invalidating the container
            HtmlParserClass.this.container.invalidate();

            // For ICS
            HtmlParserClass.this.container.setHeight((HtmlParserClass.this.container.getHeight()+ result.getIntrinsicHeight()));

            // Pre ICS
            /*HtmlParserClass.this.container.setEllipsize(null);
            urlDrawable.setBounds(-50, 10, 50 + result.getIntrinsicWidth(), -50
                    + result.getIntrinsicHeight());
            urlDrawable.drawable = result;
            // redraw the image by invalidating the container
            HtmlParserClass.this.container.invalidate();*/
        }

        /***
         * Get the Drawable from URL
         *
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                //Here i am get device width autometically
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int widthf = size.x;
                int heightf = size.y;





                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");

               // here i am risize image height and width

                float multiplier = (float)widthf / (float)drawable.getIntrinsicWidth();
                //Here you want to resize height then change size
                float mult_height = (float)150 / (float)drawable.getIntrinsicHeight();

                int width = (int)(drawable.getIntrinsicWidth() * multiplier);
                int height = (int)(drawable.getIntrinsicHeight()*mult_height);

                drawable.setBounds(0, 0, width,height);
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
            /*OkHttpClient client = new OkHttpClient();
            Request requestes = new Request.Builder().url(urlString).build();
            Response response = client.newCall(requestes).execute();
*/
          DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);
            HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
        }
    }
}