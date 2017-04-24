package rs.reviewer.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import model.Cinema;
import rs.reviewer.R;
import rs.reviewer.tools.Mokap;

/**
 * Created by milossimic on 3/21/16.
 */
public class CinemaAdapter extends BaseAdapter{
    private Activity activity;

    public CinemaAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return Mokap.getCinemas().size();
    }

    @Override
    public Object getItem(int position) {
        return Mokap.getCinemas().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        Cinema cinema = Mokap.getCinemas().get(position);

        if(convertView==null)
            vi = activity.getLayoutInflater().inflate(R.layout.cinema_list, null);

        TextView name = (TextView)vi.findViewById(R.id.name);
        TextView description = (TextView)vi.findViewById(R.id.description);
        ImageView image = (ImageView)vi.findViewById(R.id.item_icon);

        name.setText(cinema.getName());
        description.setText(cinema.getDescription());

        if (cinema.getAvatar() != -1){
            image.setImageResource(cinema.getAvatar());
        }

        return  vi;
    }
}
