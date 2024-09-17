package com.oneline.gamecraft.modal

data class EarningwallModal(
    val offer_id: String,
    val offer_title: String,
    val offer_icon: String,
    val offer_coin: String,
    val offer_link: String,
    val offer_status: String,
    val offer_description: String
) {
    companion object {
        var selectedEarningwallModal: EarningwallModal? = null
    }
}