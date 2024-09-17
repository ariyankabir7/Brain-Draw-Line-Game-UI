package com.oneline.gamecraft.modal

data class ScreenshotModal(
    val task_id: String,
    val task_title: String,
    val task_icon: String,
    val task_coin: String,
    val task_link: String,
    val ocr_validation_text: String,
    val task_description: String,
    val howto: String
)