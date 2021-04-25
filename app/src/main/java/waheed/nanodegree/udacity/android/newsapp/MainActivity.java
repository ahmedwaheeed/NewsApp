package waheed.nanodegree.udacity.android.newsapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //q=debate&order-by=oldest&from-date=2014-01-01&api-key=test
    private static final String API_URL = "https://content.guardianapis.com/search?";
    CustomArrayAdapter adapter;
    ProgressBar progressBar ;
    TextView state;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView lisview = (ListView) findViewById(R.id.listView);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        state = (TextView) findViewById(R.id.textState);

        adapter = new CustomArrayAdapter(getBaseContext(),new ArrayList<Article>());

        lisview.setEmptyView(state);

        lisview.setAdapter(adapter);

        lisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Article article = adapter.getItem(i);
                assert article != null;
                String url = article.getArticleUrl();
                Intent ii = new Intent(Intent.ACTION_VIEW);
                ii.setData(Uri.parse(url));
                startActivity(ii);
            }
        });

        getLoaderManager().initLoader(2, null, new LoaderManager.LoaderCallbacks<ArrayList<Article>>() {
            @Override
            public Loader<ArrayList<Article>> onCreateLoader(int i, Bundle bundle) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key)
                ,getString(R.string.settings_order_by_default));

                String rating = sharedPreferences.getString(getString(R.string.settings_star_rating_key)
                ,getString(R.string.settings_star_rating_default));

                Uri bassUri = Uri.parse(API_URL);

                Uri.Builder builder = bassUri.buildUpon();

                builder.appendQueryParameter("q","debate");
                builder.appendQueryParameter("order-by",orderBy);
                builder.appendQueryParameter("star-rating",rating);
                builder.appendQueryParameter("show-tags","contributor");
                builder.appendQueryParameter("from-date","2014-01-01");
                builder.appendQueryParameter("api-key","test");

                return new ArticleAsyncTask(getBaseContext(),builder.toString());
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> articles) {
                adapter.clear();
                if(isOnline()) {
                    if (articles != null && !articles.isEmpty()) {
                        adapter.addAll(articles);
                        progressBar.setVisibility(View.GONE);
                    }else{
                        progressBar.setVisibility(View.GONE);
                        state.setText(R.string.noArticles);
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    state.setText(R.string.noWifi);
                }

            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Article>> loader) {
                adapter.clear();
            }
        }).forceLoad();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settings){
            //intent code here
            Intent i = new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private static class ArticleAsyncTask extends AsyncTaskLoader<ArrayList<Article>>{

        String mUrl;
        HttpURLConnection urlConnection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;
        String line;
        String jsonData;
        ArrayList<Article> articles;


        public ArticleAsyncTask(Context context, String url) {
            super(context);
            this.mUrl = url;
        }

        @Override
        public ArrayList<Article> loadInBackground() {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                URL mURL = new URL(mUrl);

                urlConnection = (HttpURLConnection) mURL.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if(urlConnection.getResponseCode() == 200){
                    inputStream = urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                    bufferedReader = new BufferedReader(inputStreamReader);
                    line = bufferedReader.readLine();
                    stringBuilder = new StringBuilder();
                    while(line != null){
                        stringBuilder.append(line);
                        line = bufferedReader.readLine();
                    }

                    jsonData = stringBuilder.toString();

                    articles = extractDataFromJson(jsonData);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return articles;
        }

        private ArrayList<Article> extractDataFromJson(String jsonData) {

            ArrayList<Article> arraylist = new ArrayList<>();

            try {
                JSONObject root = new JSONObject(jsonData);
                JSONObject response = root.getJSONObject("response");
                JSONArray results = response.getJSONArray("results");

                for(int i = 0; i< results.length(); i++){
                    JSONObject article = results.getJSONObject(i);
                    String articleTitle = article.getString("webTitle");
                    String articleType = article.getString("type");
                    String articleSection = article.getString("sectionName");
                    String articleUrl = article.getString("webUrl");
                    String articleTime = article.getString("webPublicationDate");
                    JSONArray tags = article.getJSONArray("tags");
                    JSONObject object = tags.getJSONObject(0);
                    String articleAuthor = object.getString("webTitle");

                    Article articleData = new Article(articleTitle,articleUrl,articleSection,articleType,articleTime,articleAuthor);
                    arraylist.add(articleData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arraylist;
        }
    }
}
