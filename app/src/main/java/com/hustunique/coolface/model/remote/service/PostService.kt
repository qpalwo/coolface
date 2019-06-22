package com.hustunique.coolface.model.remote.service

import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.BmobQuery
import com.hustunique.coolface.bean.Post
import com.hustunique.coolface.bean.Resource
import com.hustunique.coolface.util.Callback
import io.reactivex.Observable

object PostService {

    /**
     * 获取所有的动态
     */
    fun getAllPost(callback: Callback<List<Post>>) {
//        val query = BmobQuery<Post>()
//        query.findObjects(object : FindListener<Post>() {
//            override fun done(p0: MutableList<Post>?, p1: BmobException?) {
//                if (p1 == null) {
//                    callback.onResponse(p0)
//                } else {
//                    callback.onResponse(null)
//                }
//            }
//        })

        // 测试数据
        callback.onResponse(
            listOf(
//                Post(
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561540728&di=185928ca86dc8b6c79ddf1398bd23d58&imgtype=jpg&er=1&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn08%2F234%2Fw535h499%2F20180905%2Ff548-hiqtcap4576873.jpg",
//                    "亲亲",
//                    "小岳岳",
//                    200,
//                    null
//                ),
//                Post(
//                    "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2281494139,1226120020&fm=26&gp=0.jpg",
//                    "我结婚了",
//                    "林志玲",
//                    2123,
//                    null
//                ),
//                Post(
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561540230&di=2b0d9fd7b48e062e6aa997469dd59c21&imgtype=jpg&er=1&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201511%2F18%2F20151118123829_VvPUR.jpeg",
//                    "帅不帅",
//                    "胡歌",
//                    22,
//                    null
//                ),
//                Post(
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561540261&di=b42c385054fba5b481ab79a4fe7527e8&imgtype=jpg&er=1&src=http%3A%2F%2Fnews.cnhubei.com%2Fnews%2Fylxw%2F201012%2FW020101217165663482448.jpg",
//                    "郭老师最帅",
//                    "郭德纲",
//                    340,
//                    null
//                ),
//                Post(
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561540299&di=252c0c9a563d35ef915195df91893e93&imgtype=jpg&er=1&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201607%2F24%2F20160724064954_uhKTj.thumb.700_0.jpeg",
//                    "哈哈哈",
//                    "岳云鹏",
//                    230,
//                    null
//                ),
//                Post(
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561540299&di=252c0c9a563d35ef915195df91893e93&imgtype=jpg&er=1&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201607%2F24%2F20160724064954_uhKTj.thumb.700_0.jpeg",
//                    "哈哈哈",
//                    "岳云鹏",
//                    230,
//                    null
//                ),
//                Post(
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561540299&di=252c0c9a563d35ef915195df91893e93&imgtype=jpg&er=1&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201607%2F24%2F20160724064954_uhKTj.thumb.700_0.jpeg",
//                    "哈哈哈",
//                    "岳云鹏",
//                    230,
//                    null
//                ),
//                Post(
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1561540299&di=252c0c9a563d35ef915195df91893e93&imgtype=jpg&er=1&src=http%3A%2F%2Fimg3.duitang.com%2Fuploads%2Fitem%2F201607%2F24%2F20160724064954_uhKTj.thumb.700_0.jpeg",
//                    "哈哈哈",
//                    "岳云鹏",
//                    230,
//                    null
//                )
            )
        )
    }



    fun getPostByRange(start: Int, end: Int) {

    }

    /**
     * 存动态
     * @param post 要上传的动态
     * @param callback onResponse中返回Post
     *                  成功 上传的Post
     *                  失败 返回null
     */
    fun save(post: Post, callback: MutableLiveData<Resource<Post>>) {

    }
}