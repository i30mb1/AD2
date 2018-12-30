package n7.ad2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import n7.ad2.R;

public class PlainTextAdapter extends RecyclerView.Adapter<PlainTextAdapter.Holder> {

    public PlainTextAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        list = new LinkedList<>();
    }

    private final LayoutInflater inflater;
    private LinkedList<String> list;

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String name = list.get(position);
        holder.textView.setText(String.format("...%s", name));
//        float alpha = (1F - (getItemCount() - position)* 0.1F);//для 10 элементов
        float alpha = (1F - (getItemCount() - position) * 0.075F);//для 15 элементов
        holder.view.setAlpha(alpha);
    }

    public void add(String item) {
        list.add(item);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_plain_text, parent, false);
        return new Holder(view);
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView textView;
        private LinearLayout view;

        Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_item_name);
            view = itemView.findViewById(R.id.view);
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }
}
