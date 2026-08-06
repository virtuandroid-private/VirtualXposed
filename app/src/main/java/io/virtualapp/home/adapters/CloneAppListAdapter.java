package io.virtualapp.home.adapters;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import io.virtualapp.R;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.glide.GlideUtils;
import io.virtualapp.home.models.AppInfo;
import io.virtualapp.widgets.DragSelectRecyclerViewAdapter;
import io.virtualapp.widgets.LabelView;

/**
 * @author Lody
 */
public class CloneAppListAdapter extends DragSelectRecyclerViewAdapter<CloneAppListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<AppInfo> mAppList;
    private ItemEventListener mItemEventListener;

    private Context mContext;
    private File mFrom;


    public CloneAppListAdapter(Context context, @Nullable File from) {
        mContext = context;
        mFrom = from;
        this.mInflater = LayoutInflater.from(context);
        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, VUiKit.dpToPx(context, 60)
        );
        params.setFullSpan(true);

    }

    public void setOnItemClickListener(ItemEventListener mItemEventListener) {
        this.mItemEventListener = mItemEventListener;
    }

    public List<AppInfo> getList() {
        return mAppList;
    }

    public void setList(List<AppInfo> models) {
        this.mAppList = models;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_clone_app, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        AppInfo info = mAppList.get(position);

        if (mFrom == null) {
            GlideUtils.loadInstalledPackageIcon(mContext, info.packageName, holder.iconView, android.R.drawable.sym_def_app_icon);
        } else {
            GlideUtils.loadPackageIconFromApkFile(mContext, info.path, holder.iconView, android.R.drawable.sym_def_app_icon);
        }

        holder.nameView.setText(String.format("%s: %s%s", info.name, info.version, info.splitApk ? " [S]" : ""));
        if (isIndexSelected(position)) {
            holder.iconView.setAlpha(1f);
            holder.appCheckView.setImageResource(R.drawable.ic_check);
        } else {
            holder.iconView.setAlpha(0.65f);
            holder.appCheckView.setImageResource(R.drawable.ic_no_check);
        }
        if (info.cloneCount > 0) {
            holder.labelView.setVisibility(View.VISIBLE);
            holder.labelView.setText(info.cloneCount + 1 + "");
        } else {
            holder.labelView.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            mItemEventListener.onItemClick(info, position);
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    protected boolean isIndexSelectable(int index) {
        return mItemEventListener.isSelectable(index);
    }

    @Override
    public int getItemCount() {
        return mAppList == null ? 0 : mAppList.size();
    }

    public AppInfo getItem(int index) {
        return mAppList.get(index);
    }

    public interface ItemEventListener {

        void onItemClick(AppInfo appData, int position);

        boolean isSelectable(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView nameView;
        private ImageView appCheckView;
        private LabelView labelView;

        ViewHolder(View itemView) {
            super(itemView);
            iconView = (ImageView) itemView.findViewById(R.id.item_app_icon);
            nameView = (TextView) itemView.findViewById(R.id.item_app_name);
            appCheckView = (ImageView) itemView.findViewById(R.id.item_app_checked);
            labelView = (LabelView) itemView.findViewById(R.id.item_app_clone_count);
        }
    }
}
