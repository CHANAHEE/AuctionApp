package com.cha.auctionapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.model.MessageItem
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(var context: Context, var items: MutableList<MessageItem>) : Adapter<MessageAdapter.VH>(){

    private var TYPE_MY_MESSAGE = 0
    private var TYPE_OTHER_MESSAGE = 1
    private var TYPE_MY_PICTURE_MESSAGE = 2
    private var TYPE_OTHER_PICTURE_MESSAGE = 3

    inner class VH(itemView: View) : ViewHolder(itemView) {

        var profile: CircleImageView
        var nickName: TextView
        //var message: TextView
        var time: TextView
        var picture: RecyclerView

        init {
            Log.i("zxcvasdf","$itemViewType")
            picture = itemView.findViewById(R.id.recycler_picture_message)
            //message = itemView.findViewById(R.id.tv_msg)
            profile = itemView.findViewById<CircleImageView>(R.id.civ_profile)
            nickName = itemView.findViewById(R.id.tv_name)
            time = itemView.findViewById(R.id.tv_time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        /*
        *       회원가입 시 만들었던 닉네임과 채팅 메시지의 닉네임 값을 비교해서 내꺼면 TYPE MY MESSAGE 를 뷰타입으로 리턴.
        * */
        Log.i("messageCheck","${items.get(position).id} : ${G.userAccount.id} : ${items[position].image.size}")
        if(items[position].id == G.userAccount.id && items[position].image.size == 0) return TYPE_MY_MESSAGE
        else if (items[position].id == G.userAccount.id && items[position].image.size != 0) {
            Log.i("messageCheck","${items.get(position).id} : ${G.userAccount.id} : ${items[position].image.size}")
            return TYPE_MY_PICTURE_MESSAGE
        }
        else if (items[position].id != G.userAccount.id && items[position].image.size == 0) return TYPE_OTHER_MESSAGE
        else return TYPE_OTHER_PICTURE_MESSAGE
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        Log.i("zxcvasdf","$viewType")
        var view: VH? = null
        if(viewType == TYPE_MY_MESSAGE) view = VH(LayoutInflater.from(context).inflate(R.layout.recycler_message_me,parent,false))
        else if(viewType == TYPE_OTHER_MESSAGE) view = VH(LayoutInflater.from(context).inflate(R.layout.recycler_message_other,parent,false))
        else if(viewType == TYPE_MY_PICTURE_MESSAGE) {
            Log.i("messageCheck","onCreateViewHOlder")
            view = VH(LayoutInflater.from(context).inflate(R.layout.recycler_message_me_picture,parent,false))
        }
        else if(viewType == TYPE_OTHER_PICTURE_MESSAGE) view = VH(LayoutInflater.from(context).inflate(R.layout.recycler_message_other_picture,parent,false))
        return view!!
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var item = items[position]

        if(holder.itemViewType == TYPE_MY_MESSAGE || holder.itemViewType == TYPE_OTHER_MESSAGE) {
            Log.i("messageCheck","onBindViewHolder : TYPE MESSAGE")
            holder.nickName.text = item.nickname
            //holder.message.text = item.message
            holder.time.text = item.time
            Glide.with(context).load(item.profileImage).error(R.drawable.default_profile)
                .into(holder.profile)
        }else {
            Log.i("messageCheck","onBindViewHolder : TYPE PICTURE")
            holder.nickName.text = item.nickname
            holder.picture.adapter = PictureMessageAdapter(context, item.image)
            holder.time.text = item.time
            Glide.with(context).load(item.profileImage).error(R.drawable.default_profile)
                .into(holder.profile)
        }
    }
}