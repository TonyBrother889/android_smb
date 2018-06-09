package com.cifs.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cifs.R;
import com.cifs.smbutils.SmbFileModel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SmbFileListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<SmbFileModel> smbFileModelArrayList;
    private final LayoutInflater layoutInflater;
    private ViewHolder viewHolder;
    private OnItemClickListener onItemClickListener;

    public SmbFileListAdapter(Activity activity, ArrayList<SmbFileModel> smbFileModelArrayList, OnItemClickListener onItemClickListener) {
        super();
        this.activity = activity;
        this.smbFileModelArrayList = smbFileModelArrayList;
        this.onItemClickListener = onItemClickListener;
        layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return smbFileModelArrayList == null ? 0 : smbFileModelArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_smb_file_info, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        final SmbFileModel smbFileModel = smbFileModelArrayList.get(i);
        viewHolder.tvFileName.setText(smbFileModel.getFileName());
        viewHolder.tvFileSize.setText(smbFileModel.getSize());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onFileItemClick(i, smbFileModel);
            }
        });
        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_file_name)
        TextView tvFileName;
        @InjectView(R.id.tv_file_size)
        TextView tvFileSize;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public interface OnItemClickListener {

        void onFileItemClick(int position, SmbFileModel smbFileModel);
    }

}
