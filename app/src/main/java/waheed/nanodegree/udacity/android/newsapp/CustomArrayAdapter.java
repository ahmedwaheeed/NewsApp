package waheed.nanodegree.udacity.android.newsapp;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by waheed on 3/20/2018.
 */

public class CustomArrayAdapter extends ArrayAdapter<Article> {
    public CustomArrayAdapter(Context context, List<Article> objects) {
        super(context,0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Article article = getItem(position);

        TextView textTitle = (TextView) convertView.findViewById(R.id.textTitle);
        assert article != null;
        textTitle.setText(article.getArticleTitle());

        TextView textSection = (TextView) convertView.findViewById(R.id.textSection);
        textSection.setText(article.getArticleSecton());

        TextView textType = (TextView) convertView.findViewById(R.id.textType);
        textType.setText(article.getArticleType());

        TextView textDate = (TextView) convertView.findViewById(R.id.textDate);

        TextView textAuthor = (TextView) convertView.findViewById(R.id.textAuthor);
        textAuthor.setText(article.getArticleAuthor());

        String data = article.getArticleTime();
        String[] finalData = data.split("T");
        String date = finalData[0];
        textDate.setText(date);

        return convertView;
    }
}
