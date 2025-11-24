package org.me.gcu.sardinia_shaira_s2264713.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.me.gcu.sardinia_shaira_s2264713.R;
import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyItem;

import java.util.ArrayList;

public class CurrencyAdapterExpandable extends BaseAdapter {

    private Context context;
    private ArrayList<CurrencyItem> currencyList;
    private LayoutInflater inflater;
    private int expandedPosition = -1;
    private OnCurrencyActionListener listener;
    private boolean isSavedPage = false;

    public interface OnCurrencyActionListener {
        void onSaveClicked(CurrencyItem item);
        void onConvertClicked(CurrencyItem item);
    }

    public CurrencyAdapterExpandable(Context context, ArrayList<CurrencyItem> currencyList,
                                     OnCurrencyActionListener listener) {
        this.context = context;
        this.currencyList = currencyList;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
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
     * View All list (expandable)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.currency_item_expandable, parent, false);

            holder = new ViewHolder();
            holder.currencyCard = convertView.findViewById(R.id.currencyCard);
            holder.currencyImage = convertView.findViewById(R.id.currencyImageView);
            holder.titleText = convertView.findViewById(R.id.titleTextView);
            holder.descriptionText = convertView.findViewById(R.id.descriptionTextView);
            holder.pubDateText = convertView.findViewById(R.id.pubDateTextView);
            holder.expandedSection = convertView.findViewById(R.id.expandedSection);
            holder.saveButton = convertView.findViewById(R.id.saveButton);
            holder.convertButton = convertView.findViewById(R.id.convertButton);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CurrencyItem currentItem = currencyList.get(position);

        /* Set currency data */
        holder.titleText.setText(currentItem.getFormattedTitle());
        holder.descriptionText.setText(currentItem.getFormattedDescription());
        holder.descriptionText.setTextColor(currentItem.getRateColor());
        holder.pubDateText.setText("Up to date as of: " + currentItem.getPubDate());

        /* Set flag image */
        int flagResourceId = currentItem.getFlagResourceId(context);
        if (flagResourceId != 0) {
            holder.currencyImage.setImageResource(flagResourceId);
        } else {
            holder.currencyImage.setImageResource(R.drawable.unknown);
        }

       /* Handle expansion */
        boolean isExpanded = position == expandedPosition;
        holder.expandedSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.currencyCard.setOnClickListener(v -> {
            if (expandedPosition == position) {
                expandedPosition = -1;
            } else {
                expandedPosition = position;
            }
            notifyDataSetChanged();
        });

        /* Save button click */
        if (isSavedPage) {
            holder.saveButton.setImageResource(R.drawable.btn_small_remove);
        } else {
            holder.saveButton.setImageResource(R.drawable.btn_small_save);
        }
        holder.saveButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSaveClicked(currentItem);
            }
        });

        /* Convert button click */
        holder.convertButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConvertClicked(currentItem);
            }
        });

        return convertView;
    }

    public void updateList(ArrayList<CurrencyItem> newList) {
        this.currencyList = newList;
        this.expandedPosition = -1;
        notifyDataSetChanged();
    }

    public void setIsSavedPage(boolean isSavedPage) {
        this.isSavedPage = isSavedPage;
    }

    static class ViewHolder {
        View currencyCard;
        ImageView currencyImage;
        TextView titleText;
        TextView descriptionText;
        TextView pubDateText;
        LinearLayout expandedSection;
        ImageView saveButton;
        ImageView convertButton;
    }
}