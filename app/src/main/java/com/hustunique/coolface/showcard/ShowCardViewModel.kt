package com.hustunique.coolface.showcard

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustunique.coolface.R
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.model.repo.PostRepo
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.ui.widget.DanmakuView

class ShowCardViewModel : ViewModel() {
    private val postRepo = PostRepo.getInstance()
    val postData: MutableLiveData<Resource<Post>> = MutableLiveData()
    val collectPostData: MutableLiveData<Resource<Post>> = MutableLiveData()


    fun init(post: Post?) {
        postData.value = Resource.success(post)
        collectPostData.value = Resource.success(post)
    }

    fun showDanmu(danmaku: BaseDanmaku?, dmView: DanmakuView) {
        dmView.addDanmaku(danmaku)
    }


    fun addDanmu(
        content: String,
        dmContext: DanmakuContext,
        dmView: DanmakuView
    ) {
        postData.value?.data?.let { post ->
            {
                if (post.comments == null)
                    post.comments = ArrayList()
                (post.comments as MutableList).add(content)
                postRepo.addComment(post.objectId!!, content) {
                    (post.comments as MutableList).removeAt(post.comments.indexOf(content))
                }
            }

        }

        if (dmView.isPaused)
            dmView.resume()
        showDanmu(createDanmu(dmContext, dmView.currentTime, content, Color.RED, Color.GREEN), dmView)
    }

    fun getDmContext(): DanmakuContext {
        val maxLines = HashMap<Int, Int>()
        maxLines[BaseDanmaku.TYPE_SCROLL_RL] = 4

        val overMap = HashMap<Int, Boolean>()
        overMap[BaseDanmaku.TYPE_SCROLL_RL] = true
        overMap[BaseDanmaku.TYPE_FIX_TOP] = true

        val context = DanmakuContext.create()
        context.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3f).setDuplicateMergingEnabled(true)
            .setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f).setMaximumLines(maxLines).preventOverlapping(overMap)
            .setDanmakuMargin(50)
        context.isDuplicateMergingEnabled = true
        return context
    }

    fun getParser() = object : BaseDanmakuParser() {
        override fun parse(): IDanmakus = Danmakus()
    }

    /**
     * 默认的创建弹幕
     */
    fun createDanmu(context: Context, dmContext: DanmakuContext, time: Long, content: String): BaseDanmaku? =
        createDanmu(
            dmContext,
            time,
            content,
            context.getColor(R.color.colorPrimaryDark),
            context.getColor(R.color.colorAccent)
        )

    /**
     * 可配置颜色的创建弹幕
     */
    fun createDanmu(
        dmContext: DanmakuContext,
        time: Long,
        content: String,
        textColor: Int,
        borderColor: Int
    ): BaseDanmaku? {
        val danmaku = dmContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL) ?: return null
        danmaku.text = content
        danmaku.padding = 5
        danmaku.priority = 1
        danmaku.isLive = true
        danmaku.time = time + 2000
        danmaku.textSize = 60f
        danmaku.textColor = textColor
        danmaku.borderColor = borderColor

        return danmaku
    }

    fun like(onError: ((String) -> Unit)?) {
        postData.value?.data?.let {
            postRepo.like(it.objectId!!, postData, onError)
        }
    }

    fun unLike(onError: ((String) -> Unit)?) {
        postData.value?.data?.let {
            postRepo.unLike(it.objectId!!, postData, onError)
        }
    }

    fun collect(onError: ((String) -> Unit)? = null) {
        postRepo.favourite(collectPostData.value?.data?.objectId!!, collectPostData, onError)
    }

    fun unCollect(onError: ((String) -> Unit)?) {
        postRepo.unFavourite(collectPostData.value?.data?.objectId!!, collectPostData, onError)
    }

}