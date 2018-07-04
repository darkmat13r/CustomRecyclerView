package `in`.darkmatter.recycler_view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * Created by gymat on 3/13/2018.
 */
class CustomRecyclerView : RecyclerView {

    companion object {
        private val TAG = CustomRecyclerView::class.java.simpleName
    }

    private var mEmptyView: View? = null
    private var mNetworkErrorView: View? = null
    private var mLoadingView: View? = null

    var isLoading = false

    enum class State {
        LOADING, ERROR, EMPTY, LIST
    }

    private var currentState = State.LOADING

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    private val mOnScrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = this@CustomRecyclerView.layoutManager as LinearLayoutManager
            if (dy > 0 && layoutManager.findLastVisibleItemPosition()
                    == this@CustomRecyclerView.adapter!!.itemCount - 1  && !isLoading) {
                isLoading = true
                Log.e(TAG, "On scrolled to bottom : ${layoutManager.findLastVisibleItemPosition()}")
                mListener?.onLoadMore()
            }
        }
    }
    private val mObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
           adapter?.let{
               if (it.itemCount > 0) {
                   showRecyclerView()
                   Log.e(TAG, "List have items")
               } else {
                   Log.e(TAG, "List is empty")
                   setEmptyState()
               }
           }
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            Log.e(TAG, "On item range changed")
            isLoading = false
        }
    }

    init {
        setLoadingState()
    }

    private fun showError(b: Boolean) {
        Log.e(TAG, "Show error : $b")
    }

    fun setEmptyState() {
        Log.e(TAG, "Empty state : ${mEmptyView}")
        switchState(State.EMPTY)
    }


    fun showRecyclerView() {
        Log.e(TAG, "List state")
        switchState(State.LIST)


    }

    fun setLoadingState() {
        Log.e(TAG, "Loading state")
        switchState(State.LOADING)

    }

    fun setNetworkErrorState() {
        Log.e(TAG, "Network state")
        switchState(State.ERROR)
    }


    /**
     * Switch state of recycler view
     */
    private fun switchState(state: State) {
        currentState = state
        when(state){
            State.EMPTY ->{
                mEmptyView?.visibility = View.VISIBLE
                mLoadingView?.visibility = View.GONE
                mNetworkErrorView?.visibility = View.GONE
                visibility = View.GONE
            }
            State.LOADING ->{
                mEmptyView?.visibility = View.GONE
                mLoadingView?.visibility = View.VISIBLE
                mNetworkErrorView?.visibility = View.GONE
                visibility = View.GONE
            }
            State.ERROR ->{
                mEmptyView?.visibility = View.GONE
                mLoadingView?.visibility = View.GONE
                mNetworkErrorView?.visibility = View.VISIBLE
                visibility = View.GONE
            }
            State.LIST ->{
                Log.e(TAG, "When List view ${mLoadingView}")
                mEmptyView?.visibility = View.GONE
                mLoadingView?.visibility = View.GONE
                mNetworkErrorView?.visibility = View.GONE
                visibility = View.VISIBLE
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        addOnScrollListener(mOnScrollListener)
        isLoading = false
        Log.e(TAG, "Register adapter : ${adapter}")
        getAdapter()?.registerAdapterDataObserver(mObserver)
        mObserver.onChanged()
    }


    /**
     * Set Empty View
     */
    fun setEmptyView(view: View) {
        this.mEmptyView = view

    }

    /**
     * Set Loading view
     */
    fun setLoadingView(view: View) {
        Log.e(TAG, " Setting Loading view....")
        this.mLoadingView = view
    }

    /**
     * Set network error view
     */
    fun setNetworkErrorView(view: View) {
        this.mNetworkErrorView = view
    }


    private var mListener: OnLoadMoreListener? = null

    fun setOnLoadMoreListener(mListener: OnLoadMoreListener) {
        Log.e(TAG, "Load more listener is set")
        this.mListener = mListener
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }
}


