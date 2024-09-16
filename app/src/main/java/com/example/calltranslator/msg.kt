package com.example.calltranslator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.PointerIconCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.example.calltranslator.databinding.MessageItemBinding

data class Message(
    val id: Int,
    @Suppress("PropertyName")
    val icon_id: Int = 0,
    val header: String = "",
    val name: String = "",
    val text: String = "",
    val data: String = "",
    /////////////////////////
    val full: String = "",
    val number: String = "",
    val track: String = ""
)

val adapter = MsgAdapter();
var listener: EndlessRecyclerViewScrollListener? = null;

class MsgAdapter() : RecyclerView.Adapter<MsgAdapter.MsgHolder>() {

    //этой функции тут не должно быть
    fun setIconById(image: ImageView, id:Int)
    {
        val loId:Int = id % 10
        when(loId)
        {
            0 -> image.visibility = View.INVISIBLE
            1 -> image.setImageResource(R.drawable.ic_call_received)
            2 -> image.setImageResource(R.drawable.ic_call_made)
            3 -> image.setImageResource(R.drawable.ic_call_missed)
            4 -> image.setImageResource(R.drawable.ic_call_missed_out)
            5 -> image.setImageResource(R.drawable.ic_call_uncknown)
            6 -> image.setImageResource(R.drawable.ic_call_uncknown)
            7 -> image.setImageResource(R.drawable.ic_call_uncknown)
            8 -> image.setImageResource(R.drawable.ic_call_uncknown)
            9 -> image.setImageResource(R.drawable.ic_call_uncknown)
            else -> throw (Exception("unknown icon low id"))
        }
    }

    val sortedList = SortedList(Message::class.java, object : SortedListAdapterCallback<Message>(this) {
        public override fun compare(o1: Message, o2: Message): Int {
            return o2.id.compareTo(o1.id)
        }
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(item1: Message, item2: Message): Boolean {
            return item1.id == item2.id
        }
    })
    fun binarySearch(id: Int): Int {
        var low = 0
        var high = sortedList.size() - 1
        while (low <= high) {
            val mid = (low + high) / 2
            val midId = sortedList.get(mid).id
            when {
                midId > id -> low = mid + 1
                midId < id -> high = mid - 1
                else -> return mid
            }
        }
        return -1 // Не найдено
    }

    companion object {
        private const val TYPE_NO_DATA    = 2
        private const val TYPE_FIRST_ITEM = 1
        private const val TYPE_CARD = 0
        private const val TYPE_LAST_ITEM = -1
        private const val TYPE_END_DATA  = -2
    }


    class MsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = MessageItemBinding.bind(itemView)
        fun bind(msg: Message) = with(binding) {
            cl.tag = msg.id
            textViewName.text = msg.name
            textVievSmalltext.text = msg.text
            textViewData.text = msg.data
            textViewHeader.text = msg.header
            adapter.setIconById(imageInfo, msg.icon_id)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MsgHolder(view)
    }

    override fun getItemCount(): Int {
        return sortedList.size()
    }

    override fun onBindViewHolder(holder: MsgHolder, position: Int) {
        holder.bind(sortedList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_CARD
//        when(position)
//        {
//            0 -> return TYPE_NO_DATA
//            1 -> return TYPE_FIRST_ITEM
//            sortedList.size() - 2 -> return if ( sortedList[position].id == 0 ){ TYPE_LAST_ITEM } else { TYPE_CARD }
//            else -> return TYPE_CARD
//        }
    }

    fun addItem(item: Message) {
        sortedList.add(item)
        listener!!.resetState()
    }

    fun removeItem(item: Message) {
        sortedList.remove(item)
        listener!!.resetState()
    }
    fun updateItem(item: Message) {
        val index = binarySearch(item.id)
        sortedList.updateItemAt(index, item)
        listener!!.resetState()
    }
}

abstract class EndlessRecyclerViewScrollListener(private val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 5

    // The current offset index of data you have loaded
    private var currentPage = 0

    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0

    // True if we are still waiting for the last set of data to load.
    private var loading = false

    // Sets the starting page index
    private val startingPageIndex = 0

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(view, dx, dy)

        val visibleItemCount = view.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

        //layoutManager.scrollToPosition( firstVisibleItem + 2 )

//        if (!loading)
//            if (dy < 0)
//            {
//                //скролим в выше
//                if(firstVisibleItem == 0)
//                {
//                    //на верху и надо обновить
//                    loading = true
//                    onLoadMore(0, totalItemCount, view)
//                }
//            }
        if (!loading)
            if (dy > 0)
            {
                //скролим ниже
                if(lastVisibleItem == totalItemCount - 1)
                {

                    val lastId = adapter.sortedList[lastVisibleItem].id
                    if (lastId == 0)
                        return
                    //в низу и надо обновить
                    loading = true
                    onLoadMore( lastId - 1 , totalItemCount, view)
                }
            }

    }

    // Must be implemented by subclasses
    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)

    fun resetState() {
        //this.currentPage = this.startingPageIndex
        //this.previousTotalItemCount = 0
        this.loading = false
    }
}


