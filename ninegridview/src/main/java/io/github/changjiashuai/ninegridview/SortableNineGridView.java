package io.github.changjiashuai.ninegridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import io.github.changjiashuai.ninegridview.adapter.NineGridViewAdapter;
import io.github.changjiashuai.ninegridview.loader.ImageLoader;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

/**
 * 网格图片视图：
 * 1、可排序
 * 2、可移除
 * 3、可添加
 *
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/3/8 10:09.
 */

public class SortableNineGridView extends RecyclerView implements NineGridViewAdapter.OnItemClickListener {

    private static final String TAG = "SortableNineGridView";
    private NineGridViewAdapter mPhotoAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private OnSortableNineGridViewListener mOnSortableNineGridViewListener;
    private GridLayoutManager mGridLayoutManager;

    private boolean mPlusEnable;
    private boolean mSortable;
    private int mDeleteDrawableResId;
    private int mMaxItemCount;
    private int mItemSpanCount;
    private int mPlusDrawableResId;
    private int mItemWhiteSpacing;
    private int mOtherWhiteSpacing;
    private boolean mRemovable;

    private int mItemWidth;
    private ImageLoader mImageLoader;

    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mPhotoAdapter = new NineGridViewAdapter(getContext(), mImageLoader, mItemSpanCount,
                mPlusDrawableResId, mDeleteDrawableResId, mRemovable,
                mPlusEnable, mMaxItemCount);
        mPhotoAdapter.setOnItemClickListener(this);
        setAdapter(mPhotoAdapter);
    }

    public SortableNineGridView(Context context) {
        this(context, null);
    }

    public SortableNineGridView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortableNineGridView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDefaultAttrs(context);
        initCustomAttrs(context, attrs);
        afterInitDefaultAndCustomAttrs(context);
    }

    private void initDefaultAttrs(Context context) {
        mPlusEnable = true;
        mSortable = true;
        mRemovable = true;
        mDeleteDrawableResId = R.drawable.bga_pp_ic_delete;
        mMaxItemCount = 9;
        mItemSpanCount = 3;
        mItemWidth = 0;
        mPlusDrawableResId = R.drawable.bga_pp_ic_plus;
        mItemWhiteSpacing = Utils.dp2px(context, 4);
        mOtherWhiteSpacing = Utils.dp2px(context, 100);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SortableNineGridView);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.SortableNineGridView_sngvPlusEnable) {
            mPlusEnable = typedArray.getBoolean(attr, mPlusEnable);
        } else if (attr == R.styleable.SortableNineGridView_sngvSortable) {
            mSortable = typedArray.getBoolean(attr, mSortable);
        } else if (attr == R.styleable.SortableNineGridView_sngvDeleteDrawable) {
            mDeleteDrawableResId = typedArray.getResourceId(attr, mDeleteDrawableResId);
        } else if (attr == R.styleable.SortableNineGridView_sngvMaxItemCount) {
            mMaxItemCount = typedArray.getInteger(attr, mMaxItemCount);
        } else if (attr == R.styleable.SortableNineGridView_sngvItemSpanCount) {
            mItemSpanCount = typedArray.getInteger(attr, mItemSpanCount);
        } else if (attr == R.styleable.SortableNineGridView_sngvPlusDrawable) {
            mPlusDrawableResId = typedArray.getResourceId(attr, mPlusDrawableResId);
        } else if (attr == R.styleable.SortableNineGridView_sngvItemWhiteSpacing) {
            mItemWhiteSpacing = typedArray.getDimensionPixelSize(attr, mItemWhiteSpacing);
        } else if (attr == R.styleable.SortableNineGridView_sngvOtherWhiteSpacing) {
            mOtherWhiteSpacing = typedArray.getDimensionPixelOffset(attr, mOtherWhiteSpacing);
        } else if (attr == R.styleable.SortableNineGridView_sngvRemovable) {
            mRemovable = typedArray.getBoolean(attr, mRemovable);
        } else if (attr == R.styleable.SortableNineGridView_sngvItemWidth) {
            mItemWidth = typedArray.getDimensionPixelSize(attr, mItemWidth);
        }
    }

    private void afterInitDefaultAndCustomAttrs(Context context) {
        if (mItemWidth == 0) {
            mItemWidth = (Utils.getScreenWidth(context) - mOtherWhiteSpacing) / mItemSpanCount;
        } else {
            mItemWidth += mItemWhiteSpacing;
        }

        setOverScrollMode(OVER_SCROLL_NEVER);
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(this);

        mGridLayoutManager = new GridLayoutManager(context, mItemSpanCount);
        setLayoutManager(mGridLayoutManager);
        addItemDecoration(new SpaceItemDecoration(mItemWhiteSpacing / 2));
    }

    /**
     * 设置是否可拖拽排序，默认值为 true
     */
    public void setSortable(boolean sortable) {
        mSortable = sortable;
    }

    /**
     * 获取是否可拖拽排序
     */
    public boolean isSortable() {
        return mSortable;
    }

    /**
     * 设置是否可编辑，默认值为 true
     */
    public void setRemovable(boolean removable) {
        mRemovable = removable;
        mPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 获取是否可编辑
     */
    public boolean isRemovable() {
        return mRemovable;
    }

    /**
     * 设置删除按钮图片资源id，默认值为
     */
    public void setDeleteDrawableResId(@DrawableRes int deleteDrawableResId) {
        mDeleteDrawableResId = deleteDrawableResId;
    }

    /**
     * 设置可选择图片的总张数,默认值为 9
     */
    public void setMaxItemCount(int maxItemCount) {
        mMaxItemCount = maxItemCount;
    }

    /**
     * 获取选择的图片的最大张数
     */
    public int getMaxItemCount() {
        return mMaxItemCount;
    }

    /**
     * 设置列数,默认值为 3
     */
    public void setItemSpanCount(int itemSpanCount) {
        mItemSpanCount = itemSpanCount;
        mGridLayoutManager.setSpanCount(mItemSpanCount);
    }

    /**
     * 设置添加按钮图片，默认值为 R.mipmap.bga_pp_ic_plus
     */
    public void setPlusDrawableResId(@DrawableRes int plusDrawableResId) {
        mPlusDrawableResId = plusDrawableResId;
    }

    /**
     * 设置图片路径数据集合
     */
    public void setData(ArrayList<String> paths) {
        mPhotoAdapter.setData(paths);
    }

    /**
     * 在集合尾部添加更多数据集合
     */
    public void addMoreData(ArrayList<String> paths) {
        if (paths != null) {
            mPhotoAdapter.getData().addAll(paths);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 在集合的尾部添加一条数据
     */
    public void addLastItem(String path) {
        if (!TextUtils.isEmpty(path)) {
            mPhotoAdapter.getData().add(path);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spanCount = mItemSpanCount;
        int itemCount = mPhotoAdapter.getItemCount();
        if (itemCount > 0 && itemCount < mItemSpanCount) {
            spanCount = itemCount;
        }
        mGridLayoutManager.setSpanCount(spanCount);

        int expectWidth = mItemWidth * spanCount;
        int expectHeight = 0;
        if (itemCount > 0) {
            int rowCount = (itemCount - 1) / spanCount + 1;
            expectHeight = mItemWidth * rowCount;
        }

        int width = resolveSize(expectWidth, widthMeasureSpec);
        int height = resolveSize(expectHeight, heightMeasureSpec);
        width = Math.min(width, expectWidth);
        height = Math.min(height, expectHeight);

        setMeasuredDimension(width, height);
    }

    /**
     * 获取图片路径数据集合
     */
    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getData();
    }

    /**
     * 删除指定索引位置的图片
     */
    public void removeItem(int position) {
        mPhotoAdapter.removeItem(position);
    }

    /**
     * 获取图片总数
     */
    public int getItemCount() {
        return mPhotoAdapter.getData().size();
    }

    @Override
    public void onDeleteClick(View childView, int position) {
        if (mOnSortableNineGridViewListener != null) {
            mOnSortableNineGridViewListener.onDeleteNineGridViewItemClick(this, childView, position,
                    mPhotoAdapter.getItem(position), (ArrayList<String>) mPhotoAdapter.getData());
        }
    }

    @Override
    public void onItemClick(View itemView, int position) {
        if (mPhotoAdapter.isPlusItem(position)) {
            if (mOnSortableNineGridViewListener != null) {
                mOnSortableNineGridViewListener.onAddNineGridViewItemClick(this, itemView, position, (ArrayList<String>) mPhotoAdapter.getData());
            }
        } else {
            if (mOnSortableNineGridViewListener != null && ViewCompat.getScaleX(itemView) <= 1.0f) {
                mOnSortableNineGridViewListener.onNineGridViewItemClick(this, itemView, position, mPhotoAdapter.getItem(position), (ArrayList<String>) mPhotoAdapter.getData());
            }
        }
    }

    /**
     * 设置是否显示加号
     */
    public void setPlusEnable(boolean plusEnable) {
        mPlusEnable = plusEnable;
        mPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 是否显示加号按钮
     */
    public boolean isPlusEnable() {
        return mPlusEnable;
    }

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return mSortable && mPhotoAdapter.getData().size() > 1;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            if (mPhotoAdapter.isPlusItem(viewHolder.getAdapterPosition())) {
                dragFlags = ACTION_STATE_IDLE;
            }
            int swipeFlags = ACTION_STATE_IDLE;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()
                    || mPhotoAdapter.isPlusItem(target.getAdapterPosition())) {
                return false;
            }
            mPhotoAdapter.moveItem(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ACTION_STATE_IDLE) {
                ViewCompat.setScaleX(viewHolder.itemView, 1.2f);
                ViewCompat.setScaleY(viewHolder.itemView, 1.2f);
                ImageView imageView = (ImageView) viewHolder.itemView
                        .findViewById(R.id.iv_square_image);
                imageView.setColorFilter(getResources().getColor(R.color.bga_pp_photo_selected_mask));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            ViewCompat.setScaleX(viewHolder.itemView, 1.0f);
            ViewCompat.setScaleY(viewHolder.itemView, 1.0f);
            ImageView imageView = (ImageView) viewHolder.itemView
                    .findViewById(R.id.iv_square_image);
            imageView.setColorFilter(null);
            super.clearView(recyclerView, viewHolder);
        }
    }

    public interface OnSortableNineGridViewListener {
        void onAddNineGridViewItemClick(SortableNineGridView sortableNineGridView, View view,
                                        int position, ArrayList<String> models);

        void onDeleteNineGridViewItemClick(SortableNineGridView sortableNineGridView, View view,
                                           int position, String model, ArrayList<String> models);

        void onNineGridViewItemClick(SortableNineGridView sortableNineGridView, View view,
                                     int position, String model, ArrayList<String> models);
    }

    public void setOnSortableNineGridViewListener(OnSortableNineGridViewListener onSortableNineGridViewListener) {
        mOnSortableNineGridViewListener = onSortableNineGridViewListener;
    }
}