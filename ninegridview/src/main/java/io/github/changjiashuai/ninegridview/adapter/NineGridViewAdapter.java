package io.github.changjiashuai.ninegridview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import io.github.changjiashuai.ninegridview.R;
import io.github.changjiashuai.ninegridview.SquareImageView;
import io.github.changjiashuai.ninegridview.Utils;
import io.github.changjiashuai.ninegridview.loader.ImageLoader;


public class NineGridViewAdapter extends RecyclerView.Adapter<NineGridViewAdapter.ImageViewHolder> {

    private static final String TAG = "NineGridViewAdapter";
    private Context mContext;
    private int mImageSize;
    private int mItemSpanCount;
    private int mPlusDrawableResId;
    private int mDeleteDrawableResId;
    private boolean mRemovable;
    private boolean mPlusEnable;
    private int mMaxItemCount;
    private List<String> mData;
    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;

    public NineGridViewAdapter(Context context, ImageLoader imageLoader, int itemSpanCount,
                               int plusDrawableResId, int deleteDrawableResId,
                               boolean removable, boolean plusEnable,
                               int maxItemCount) {
        mContext = context;
        mImageLoader = imageLoader;
        mItemSpanCount = itemSpanCount;
        mPlusDrawableResId = plusDrawableResId;
        mDeleteDrawableResId = deleteDrawableResId;
        mRemovable = removable;
        mPlusEnable = plusEnable;
        mMaxItemCount = maxItemCount;
        mImageSize = Utils.getScreenWidth(context) / (mItemSpanCount > 3 ? 8 : 6);
        mData = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onDeleteClick(View childView, int position);
        void onItemClick(View itemView, int position);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater
                .from(mContext).inflate(R.layout.list_item_sortable_nine_grid_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        if (isPlusItem(position)) {
            holder.ivDelete.setVisibility(View.GONE);
            holder.imageView.setImageResource(mPlusDrawableResId);
        } else {
            if (mRemovable) {
                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.ivDelete.setImageResource(mDeleteDrawableResId);
            } else {
                holder.ivDelete.setVisibility(View.GONE);
            }
            String path =  mData.get(position);
            mImageLoader.displayImage(holder.imageView, path, mImageSize, mImageSize);
        }
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onDeleteClick(holder.ivDelete, holder.getAdapterPosition());
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                }
            }
        });
    }

    public void setData(List<String> data) {
        if (data != null) {
            mData = data;
        } else {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    public List<String> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        int size = mData.size();
        if (mPlusEnable && size < mMaxItemCount) {
            return size + 1;
        }
        return size;
    }

    public String getItem(int position) {
        if (isPlusItem(position)) {
            return null;
        }
        return mData.get(position);
    }

    /**
     * 移动数据条目的位置
     */
    public void moveItem(int fromPosition, int toPosition) {
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
        // 要先执行上面的 notifyItemChanged,然后再执行下面的 moveItem 操作

        mData.add(toPosition, mData.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 删除指定索引数据条目
     */
    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public boolean isPlusItem(int position) {
        return mPlusEnable && mData.size() < mMaxItemCount
                && position == getItemCount() - 1;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        SquareImageView imageView;
        ImageView ivDelete;

        public ImageViewHolder(View view) {
            super(view);
            imageView = (SquareImageView) view.findViewById(R.id.iv_square_image);
            ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
        }
    }
}