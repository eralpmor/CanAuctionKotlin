package app.ralpdevs.canauction.model

data class CarModel(
    var id: Int = 0,
    var title: String = "",
    var imgUrl: String = "",
    var bids: List<BidModel> = emptyList()
)
