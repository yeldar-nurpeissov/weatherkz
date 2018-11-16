package com.example.weatherkz.ui;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weatherkz.R;
import com.example.weatherkz.pojo.Weather;

import java.util.List;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherVH> {
    private List<Weather> items;
    private int dataVersion = 0;

    WeatherAdapter() {
    }

    @NonNull
    @Override
    public WeatherVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new WeatherVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherVH holder, int position) {
        holder.city.setText(items.get(position).getCityName());
        holder.temperature.setText(holder.city.getContext().getString(R.string.temperature, items.get(position).getTemperature()));
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }

        return items.size();
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    void replace(final List<Weather> update) {
        dataVersion++;
        if (items == null) {
            if (update == null) {
                return;
            }
            items = update;
            notifyDataSetChanged();
        } else if (update == null) {
            int oldSize = items.size();
            items = null;
            notifyItemRangeRemoved(0, oldSize);
        } else {
            final int startVersion = dataVersion;
            final List<Weather> oldItems = items;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            Weather oldItem = oldItems.get(oldItemPosition);
                            Weather newItem = update.get(newItemPosition);
                            return oldItem.getTemperature() == newItem.getTemperature() && newItem.getCityName().compareTo(oldItem.getCityName()) == 0;
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            Weather oldItem = oldItems.get(oldItemPosition);
                            Weather newItem = update.get(newItemPosition);
                            return oldItem.getTemperature() == newItem.getTemperature() && newItem.getCityName().compareTo(oldItem.getCityName()) == 0;
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != dataVersion) {
                        return;
                    }
                    items = update;
                    diffResult.dispatchUpdatesTo(WeatherAdapter.this);

                }
            }.execute();
        }
    }

    class WeatherVH extends RecyclerView.ViewHolder {
        TextView city;
        TextView temperature;

        public WeatherVH(View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.text_city);
            temperature = itemView.findViewById(R.id.text_temperature);
        }
    }
}
