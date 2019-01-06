package n7.ad2.items.db;

import android.app.Activity;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import n7.ad2.BR;
import n7.ad2.items.full.ItemFullActivity;

import static n7.ad2.items.full.ItemFullActivity.ITEM_CODE_NAME;
import static n7.ad2.items.full.ItemFullActivity.ITEM_NAME;

@Entity
public class ItemModel  {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String codeName;
    @NonNull
    private String name;

    public ItemModel(@NonNull String codeName, @NonNull String name) {
        this.codeName = codeName;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(@NonNull String codeName) {
        this.codeName = codeName;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void startItemFull(View view) {
        Intent intent = new Intent(view.getContext(), ItemFullActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), view, "iv");
        intent.putExtra(ITEM_CODE_NAME, codeName);
        intent.putExtra(ITEM_NAME, name);
        view.getContext().startActivity(intent, options.toBundle());
    }
}
