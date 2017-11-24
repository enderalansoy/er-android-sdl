package com.easyroute.ui.adapter;

import android.content.Context;

import com.easyroute.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import antistatic.spinnerwheel.adapters.AbstractWheelTextAdapter;
/**
 * Created by imenekse on 07/02/17.
 */

public class TimeWheelAdapter extends AbstractWheelTextAdapter {

    private static final int RESOURCE = R.layout.item_time_wheel_adapter;

    private Context mContext;
    private SimpleDateFormat mDateFormat;

    public TimeWheelAdapter(Context context) {
        super(context, RESOURCE, R.id.text);
        mContext = context;
        mDateFormat = new SimpleDateFormat("HH:mm");
    }

    @Override
    protected CharSequence getItemText(int index) {
        if (index == 0) {
            return mContext.getString(R.string.main_fragment_time_wheel_now_label);
        } else {
            Calendar calendar = getItemAsCalendar(index);
            return mDateFormat.format(calendar.getTime());
        }
    }

    @Override
    public int getItemsCount() {
        return 96;
    }

    public Date getItemAsDate(int index) {
        return getItemAsCalendar(index).getTime();
    }

    public Calendar getItemAsCalendar(int index) {
        if (index == 0) {
            return Calendar.getInstance();
        }else {
            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);
            int diff = minute % 15;
            calendar.add(Calendar.MINUTE, -diff + index * 15);
            return calendar;
        }
    }
}
