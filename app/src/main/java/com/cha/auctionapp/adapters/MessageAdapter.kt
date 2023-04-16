package com.cha.auctionapp.adapters

import android.content.Context
import android.net.Uri
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
import com.cha.auctionapp.model.PictureItem
import com.cha.auctionapp.model.PictureMessageItem
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(var context: Context, var items: MutableList<MessageItem>) : Adapter<ViewHolder>(){

    private var TYPE_MY_MESSAGE = 0
    private var TYPE_OTHER_MESSAGE = 1
    private var TYPE_MY_PICTURE_MESSAGE = 2
    private var TYPE_OTHER_PICTURE_MESSAGE = 3
    private var pictureItem: MutableList<Uri> = mutableListOf()

    inner class MessageVH(itemView: View) : ViewHolder(itemView) {

        var profile: CircleImageView
        var nickName: TextView
        var message: TextView
        var time: TextView

        init {
            message = itemView.findViewById(R.id.tv_msg)
            profile = itemView.findViewById<CircleImageView>(R.id.civ_profile)
            nickName = itemView.findViewById(R.id.tv_name)
            time = itemView.findViewById(R.id.tv_time)
        }
    }

    inner class PictureVH(itemView: View) : ViewHolder(itemView) {

        var profile: CircleImageView
        var nickName: TextView
        var time: TextView
        var picture: RecyclerView

        init {
            picture = itemView.findViewById(R.id.recycler_picture_message)
            profile = itemView.findViewById<CircleImageView>(R.id.civ_profile)
            nickName = itemView.findViewById(R.id.tv_name)
            time = itemView.findViewById(R.id.tv_time)
        }
    }

    override fun getItemViewType(position: Int): Int {
        /*
        *       회원가입 시 만들었던 닉네임과 채팅 메시지의 닉네임 값을 비교해서 내꺼면 TYPE MY MESSAGE 를 뷰타입으로 리턴.
        * */
//        Log.i("messagePosition","$position")
//        Log.i("messageCheck","${items.get(position).id} : ${G.userAccount.id} : ${items[position].image.size}")
//        Log.i("messageCheck","${items.get(position).image.size} : ${position}")
        if(items[position].id
            == G.userAccount.id
            && items[position].message != "") return TYPE_MY_MESSAGE
        else if (items[position].id
            == G.userAccount.id
            && items[position].message == "") return TYPE_MY_PICTURE_MESSAGE
        else if (items[position].id
            != G.userAccount.id
            && items[position].message != "") return TYPE_OTHER_MESSAGE
        else return TYPE_OTHER_PICTURE_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if(viewType == TYPE_MY_MESSAGE) return MessageVH(LayoutInflater.from(context).inflate(R.layout.recycler_message_me,parent,false))
        else if(viewType == TYPE_OTHER_MESSAGE) return MessageVH(LayoutInflater.from(context).inflate(R.layout.recycler_message_other,parent,false))
        else if(viewType == TYPE_MY_PICTURE_MESSAGE) return PictureVH(LayoutInflater.from(context).inflate(R.layout.recycler_message_me_picture,parent,false))
        else return PictureVH(LayoutInflater.from(context).inflate(R.layout.recycler_message_other_picture,parent,false))

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = items[position]

        if(holder is MessageVH){
            holder.nickName.text = item.nickname
            holder.message.text = item.message
            holder.time.text = item.time
            Glide.with(context).load(item.profileImage).error(R.drawable.default_profile)
                .into(holder.profile)
        }else if(holder is PictureVH){
            Log.i("15ee","Picture VH")
            holder.nickName.text = item.nickname
            //Log.i("pictureIssue","Message 어댑터 onBind Items[position] -> 맵: ${items[position].image.size}")
            pictureItem = items[position].image
            var newPictureItem: MutableList<Uri> = mutableListOf()
            var copyPictureItem = pictureItem.toMutableList()
            //Log.i("pictureIssue","Message 어댑터 onBind 에서의 size : ${items[position].imageSize} ==== ${items[position].image.size}")
            for(i in 0 until items[position].imageSize){
                Log.i("pictureIssue","Message 어댑터 onBind 에서의 내가 저장한 이미지 사이즈정보 : ${items[position].imageSize}")
                Log.i("pictureIssue","Message 어댑터 onBind 에서의 실제 저장된 이미지 사이즈정보 : ${pictureItem.size}")


                newPictureItem.add(pictureItem.get(i))
                //if(pictureItem.size == i)break
            }
            Log.i("pictureIssue","Message 어댑터 onBind 에서의 내가 저장한 이미지 사이즈정보 : ${items[position].imageSize}")
            //if(items[position].imageSize != pictureItem.size) return
//            var copyPictureItem = pictureItem.toMutableList()
//            for(i in items[position].imageSize-1 downTo 0){
//                Log.i("pictureIssue","Message 어댑터 onBind 에서의 내가 저장한 이미지 사이즈정보2 : ${items[position].imageSize-1}")
//                Log.i("pictureIssue","copy 사이즈 와 픽쳐 사이즈 : ${copyPictureItem.size} : ${pictureItem.size}")
//                newPictureItem.add(copyPictureItem.get(i))
//                copyPictureItem.removeAt(i)
//
//            }
//            for(i in copyPictureItem.size-1 downTo copyPictureItem.size-items[position].imageSize){
//                Log.i("pictureIssue","Message 어댑터 onBind 에서의 내가 저장한 이미지 사이즈정보2 : ${items[position].imageSize-1}")
//                //Log.i("pictureIssue","copy 사이즈 와 픽쳐 사이즈 : ${copyPictureItem.size} : ${pictureItem.size}")
//                newPictureItem.add(copyPictureItem.get(i))
//                copyPictureItem.removeAt(i)
//
//            }
            holder.picture.adapter = PictureMessageAdapter(context, items[position].id,newPictureItem)

            holder.time.text = item.time
            Glide.with(context).load(item.profileImage).error(R.drawable.default_profile)
                .into(holder.profile)

        }
    }



}