package n7.ad2.utils;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.LinkedList;

import n7.ad2.R;
import n7.ad2.databinding.ItemListPlainBinding;

public class PlainAdapter extends RecyclerView.Adapter<PlainAdapter.Holder> {

    private LayoutInflater inflater;
    private LinkedList<String> list = new LinkedList<>();

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String name = list.get(position);
        holder.binding.tvItemListPlain.setText(name);
        float alpha = (1F - (getItemCount() - position) * 0.075F);//для 15 элементов
        holder.binding.tvItemListPlain.setAlpha(alpha);
    }

    public void add(String item) {
        list.add(item);
//        notifyItemInserted(list.size() - 1);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        ItemListPlainBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_list_plain, parent, false);
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

        ItemListPlainBinding binding;

        Holder(@NonNull ItemListPlainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
