package n7.ad2.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

import n7.ad2.R;
import n7.ad2.databinding.ItemListPlainAdapterBinding;

public class PlainTextAdapter extends RecyclerView.Adapter<PlainTextAdapter.Holder> {


    private LayoutInflater inflater;
    private LinkedList<String> list = new LinkedList<>();

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String name = list.get(position);
        if (name != null) {
            holder.bindTo(name, position);
        }
    }

    public void add(String item) {
        list.add(item);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        ItemListPlainAdapterBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_list_plain_adapter, parent, false);
        return new Holder(binding);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        ItemListPlainAdapterBinding binding;

        Holder(@NonNull ItemListPlainAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(String name, int position) {
            binding.tv.setText(String.format("...%s", name));
//        float alpha = (1F - (getItemCount() - position)* 0.1F);//для 10 элементов
            float alpha = (1F - (getItemCount() - position) * 0.075F);//для 15 элементов
            binding.getRoot().setAlpha(alpha);
        }
    }
}
