package n7.ad2;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;


public class LocalImageHolderView extends Holder<Commercial> {

    ImageView imageView;
    TextView textView;

    public LocalImageHolderView(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
        imageView = itemView.findViewById(R.id.imageView);
        textView = itemView.findViewById(R.id.textView);
    }

    @Override
    public void updateUI(Commercial data) {
        imageView.setImageResource(data.image);
        textView.setText(data.description);
    }

}
