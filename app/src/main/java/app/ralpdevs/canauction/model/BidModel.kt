package app.ralpdevs.canauction.model

data class BidModel(
    var email: String = "",
    var bidValue: String = "",
    var timestamp: Long = 0L
)
