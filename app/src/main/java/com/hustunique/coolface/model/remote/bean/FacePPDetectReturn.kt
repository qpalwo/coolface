package com.hustunique.coolface.model.remote.bean

/**
 * @author  : Xiao Yuxuan
 * @date    : 6/18/19
 */
data class FacePPDetectReturn(
    val faces: List<Face>,
    val image_id: String,
    val request_id: String,
    val time_used: Int
)

data class Face(
    val attributes: Attributes,
    val error_message: String,
    val face_rectangle: FaceRectangle,
    val face_token: String,
    val landmark: Landmark
)

data class Attributes(
    val age: Age,
    val beauty: Beauty,
    val emotion: Emotion,
    val gender: Gender,
    val glass: Glass,
    val headpose: Headpose,
    val smile: Smile
)

data class Glass(
    val value: String
)

data class Gender(
    val value: String
)

data class Age(
    val value: Int
)

data class Headpose(
    val pitch_angle: Double,
    val roll_angle: Double,
    val yaw_angle: Double
)

data class Beauty(
    val female_score: Double,
    val male_score: Double
)

data class Smile(
    val threshold: Double,
    val value: Double
)

data class Emotion(
    val anger: Double,
    val disgust: Double,
    val fear: Double,
    val happiness: Double,
    val neutral: Double,
    val sadness: Double,
    val surprise: Double
)

data class Landmark(
    val contour_chin: ContourChin,
    val mouth_upper_lip_bottom: MouthUpperLipBottom,
    val mouth_upper_lip_left_contour2: MouthUpperLipLeftContour2,
    val right_eye_pupil: RightEyePupil
)

data class MouthUpperLipLeftContour2(
    val x: Int,
    val y: Int
)

data class ContourChin(
    val x: Int,
    val y: Int
)

data class RightEyePupil(
    val x: Int,
    val y: Int
)

data class MouthUpperLipBottom(
    val x: Int,
    val y: Int
)

data class FaceRectangle(
    val height: Int,
    val left: Int,
    val top: Int,
    val width: Int
)