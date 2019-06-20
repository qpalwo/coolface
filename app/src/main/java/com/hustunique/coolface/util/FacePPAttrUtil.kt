package com.hustunique.coolface.util

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/20/19
 */
class FacePPAttrUtil private constructor() {

    class Builder {
        private val instance = FacePPAttrUtil()

        fun addBeauty(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("beauty")
                this@Builder
            }

        fun addAge(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("age")
                this@Builder
            }

        fun addGender(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("gender")
                this@Builder
            }

        fun addSmiling(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("smiling")
                this@Builder
            }

        fun addHeadPose(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("headpose")
                this@Builder
            }

        fun addFaceQuality(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("facequality")
                this@Builder
            }

        fun addBlur(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("blur")
                this@Builder
            }

        fun addEyeStatus(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("eyestatus")
                this@Builder
            }

        fun addEthnicity(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("ethnicity")
                this@Builder
            }

        fun addMouthStatus(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("mouthstatus")
                this@Builder
            }

        fun addEyeGaze(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("eyegaze")
                this@Builder
            }

        fun addSkinStatus(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("skinstatus")
                this@Builder
            }

        fun addEmotion(): FacePPAttrUtil.Builder =
            instance.let {
                it.addAttr("emotion")
                this@Builder
            }

        fun build() = instance.build()

        fun default() =
            addBeauty()
                .addEmotion()
                .build()
    }

    val attrs: MutableList<String> = ArrayList()

    fun addAttr(attr: String) {
        attrs.add(attr)
    }

    fun build(): String {
        val builder = StringBuilder()
        for (i in 0 until attrs.size - 1) {
            builder.append("${attrs[i]},")
        }
        builder.append(attrs[attrs.size - 1])
        return builder.toString()
    }

}