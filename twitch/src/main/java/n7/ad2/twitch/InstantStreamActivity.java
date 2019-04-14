package n7.ad2.twitch;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class InstantStreamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
//        intent.addCategory(Intent.CATEGORY_BROWSABLE);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.setPackage(getPackageName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayer.create(this,R.raw.waking_up).start();
    }
}
