package com.newmedia.erxeslibrary.ui.conversations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.newmedia.erxeslibrary.configuration.Config;
import com.newmedia.erxeslibrary.configuration.DB;
import com.newmedia.erxeslibrary.ui.message.MessageActivity;
import com.newmedia.erxeslibrary.model.Conversation;
import com.newmedia.erxeslibrary.model.ConversationMessage;
import com.newmedia.erxeslibrary.R;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationHolder> {
    private Context context;
    public RealmResults<Conversation> conversationList;
    private Realm realm;
    private Config config;
    public ConversationListAdapter(Context context) {
        this.context = context;
        Realm.init(context);
        realm = DB.getDB();
        config = Config.getInstance(context);
        this.conversationList = realm.where(Conversation.class)
                .equalTo("status","open")
                .equalTo("customerId",config.customerId)
                .equalTo("integrationId",config.integrationId)
                .findAll();

        this.conversationList.addChangeListener(new RealmChangeListener<RealmResults<Conversation>>() {
            @Override
            public void onChange(RealmResults<Conversation> conversations) {
                ConversationListAdapter.this.notifyDataSetChanged();
                Log.d("Listener","changed all");
            }
        });



    }
    public void update_position(String conversationId){
//        for(int i = 0 ; i< conversationList.size();i++){
//            if(conversationList.get(i)._id.equalsIgnoreCase(conversationId)){
//                this.notifyItemChanged(i);
//                return;
//            }
//        }
//        this.conversationList = realm.where(Conversation.class)
//                .equalTo("status","open")
//                .equalTo("customerId",config.customerId)
//                .equalTo("integrationId",config.integrationId)
//                .findAll();
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.chatlist_item, parent, false);
        view.setOnClickListener(onClickListener);
        return new ConversationHolder(view);
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent a = new Intent(context,MessageActivity.class);
            config.conversationId = conversationList.get((int)view.getTag())._id;
            context.startActivity(a);
        }
    };
    @Override
    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {

        if(conversationList.get(position).isread) {
            if(conversationList.get(position).content!=null)
            holder.content.setText(Html.fromHtml(conversationList.get(position).content));
            holder.content.setTypeface(holder.content.getTypeface(), Typeface.NORMAL);
            holder.name.setTypeface(holder.content.getTypeface(), Typeface.NORMAL);
            holder.content.setTextColor(Color.parseColor("#808080"));
        }
        else{
            holder.content.setText(Html.fromHtml(conversationList.get(position).content));
            holder.content.setTypeface(holder.content.getTypeface(), Typeface.BOLD);
            holder.name.setTypeface(holder.content.getTypeface(), Typeface.BOLD);
            holder.content.setTextColor(Color.BLACK);
        }

        ConversationMessage message = realm.where(ConversationMessage.class).equalTo("conversationId",conversationList.get(position)._id).isNotNull("user").sort("createdAt", Sort.DESCENDING).findFirst();

        holder.name.setText("");
        if(message!=null&&message.user !=null){
            String myString = message.user.fullName;
            String upperString = myString.substring(0,1).toUpperCase() + myString.substring(1);
            holder.name.setText(upperString);
            if(message.user.avatar!=null)
                Glide.with(context).load(message.user.avatar).placeholder(R.drawable.avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.circleImageView);

            Long createDate = Long.valueOf(message.createdAt);
            holder.date.setText(config.convert_datetime(createDate));
            holder.parent.setTag(position);
        }else {
            holder.circleImageView.setImageResource(R.drawable.avatar);
            Long createDate = Long.valueOf(conversationList.get(position).date);
            holder.date.setText(config.convert_datetime(createDate));
            holder.parent.setTag(position);
        }

    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }
}
