package adapter;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.iscod.myapplication.R;

import java.util.List;

import model.RecorderData;

/**
 * Created by iscod on 2016/3/8.
 */
public class RecorderAdapter extends ArrayAdapter<RecorderData> {
    private int mMinItemWidth;
    private int mMaxItemWidth;
    private LayoutInflater inflater;

    public RecorderAdapter(Context context, List<RecorderData> datas) {
        super(context, -1, datas);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrice = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrice);
        mMaxItemWidth = (int) (outMetrice.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrice.widthPixels * 0.15f);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_recorder, parent, false);
            holder = new ViewHolder();
            holder.seconds = (TextView) convertView.findViewById(R.id.id_recorder_time);
            holder.length = convertView.findViewById(R.id.id_record_length);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.seconds.setText(Math.round(getItem(position).time) + "\"");
        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60 * getItem(position).time));

        return convertView;
    }

    public class ViewHolder {
        TextView seconds;
        View length;
    }
}

