package xyz.donot.roselinx.model


class BaseListModel(){


}

data class CommonDataPack<out T>(val hasNext :Boolean, val data:List<T>)