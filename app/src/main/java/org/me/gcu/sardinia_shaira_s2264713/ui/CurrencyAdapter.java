package org.me.gcu.sardinia_shaira_s2264713.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.me.gcu.sardinia_shaira_s2264713.R;
import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyItem;

import java.util.ArrayList;

public class CurrencyAdapter extends BaseAdapter {

    Context context;
    ArrayList<CurrencyItem> currencyList;
    LayoutInflater inflater;

    public CurrencyAdapter(Context context, ArrayList<CurrencyItem> currencyList) {
        this.context = context;
        this.currencyList = currencyList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return currencyList.size();
    }

    @Override
    public Object getItem(int position) {
        return currencyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Home screen preview list (non-expandable)
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        WidgetsHolder holder;

        if(view == null) {
            view = inflater.inflate(R.layout.currency_item_layout, viewGroup, false);

            holder = new WidgetsHolder();
            holder.titleText = view.findViewById(R.id.titleTextView);
            holder.descriptionText = view.findViewById(R.id.descriptionTextView);
            holder.currencyImage = view.findViewById(R.id.currencyImageView);

            view.setTag(holder);
        }
        else {
            holder = (WidgetsHolder) view.getTag();
        }

        CurrencyItem currentItem = currencyList.get(i);

        holder.titleText.setText(currentItem.getFormattedTitle());
        holder.descriptionText.setText(currentItem.getFormattedDescription());
        holder.descriptionText.setTextColor(currentItem.getRateColor());

        int flagResourceId = currentItem.getFlagResourceId(context);
        if (flagResourceId != 0) {
            holder.currencyImage.setImageResource(flagResourceId);
        } else {
            holder.currencyImage.setImageResource(R.drawable.unknown);
        }

        return view;
    }

    static class WidgetsHolder {
        TextView titleText;
        TextView descriptionText;
        ImageView currencyImage;
    }
}
