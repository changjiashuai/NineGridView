package io.github.changjiashuai.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.changjiashuai.ImagePicker;
import io.github.changjiashuai.bean.ImageItem;
import io.github.changjiashuai.ninegridview.SortableNineGridView;
import io.github.changjiashuai.ninegridview.loader.ImageLoader;
import io.github.changjiashuai.widget.CropImageView;

import static io.github.changjiashuai.ImagePicker.REQUEST_CODE_PICK;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int IMAGE_PICKER = 0;
    private SortableNineGridView mSortableNineGridView;
    private ArrayList<String> urls = new ArrayList<>(Arrays.asList("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered11.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered12.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered13.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered14.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered15.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered16.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered17.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered18.png", "http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered19.png"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSortableNineGridView = (SortableNineGridView) findViewById(R.id.nine_photo_layout);
        mSortableNineGridView.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(ImageView imageView, String path, int width, int height) {
                Glide.with(imageView.getContext())
                        .load(path)
                        .into(imageView);
            }
        });
        mSortableNineGridView.setOnSortableNineGridViewListener(new SortableNineGridView.OnSortableNineGridViewListener() {
            @Override
            public void onAddNineGridViewItemClick(SortableNineGridView sortableNineGridView, View view, int position, ArrayList<String> models) {
                initMultiPicker();
            }

            @Override
            public void onDeleteNineGridViewItemClick(SortableNineGridView sortableNineGridView, View view, int position, String model, ArrayList<String> models) {
                sortableNineGridView.removeItem(position);
                Toast.makeText(MainActivity.this, "onDeleteNineGridViewItemClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNineGridViewItemClick(SortableNineGridView sortableNineGridView, View view, int position, String model, ArrayList<String> models) {
//                initMultiPickerWithAllConfig();
                initMultiPicker();
                Toast.makeText(MainActivity.this, "onNineGridViewItemClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNineGridViewItemSortFinished(SortableNineGridView sortableNineGridView,
                                                       int fromPosition, int toPosition) {
                Log.i(TAG, "onNineGridViewItemSortFinished: " + fromPosition + " --> " + toPosition);
            }
        });
        mSortableNineGridView.setData(urls);
    }

    private void initMultiPicker() {
        ImagePicker.Config config = new ImagePicker.Config(new GlideImageLoader());
        config.multiMode(true);
        config.selectLimit(9);
        config.showCamera(true);
        config.crop(true);
        config.cropStyle(CropImageView.RECTANGLE);
        config.saveRectangle(true);
        config.focusWidth(800);
        config.focusHeight(800);
        config.outPutX(1000);
        config.outPutY(1000);
        ImagePicker.getInstance().pickImageForResult(this, config);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK) {
            if (data != null) {
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    ArrayList<ImageItem> imageItems = ImagePicker.getInstance()
                            .getSelectedImages();
                    if (imageItems.size() > 0) {
                        ArrayList<String> paths = new ArrayList<>();
                        for (ImageItem imageItem1 : imageItems) {
                            paths.add(imageItem1.path);
                        }
                        mSortableNineGridView.addMoreData(paths);
                    }
                }
            }
        }
    }
}