package de.jensklingenberg.ktorfit.requestData

class DataHolder(private val key: String, private val data: String, private val encoded: Boolean?) {
    override fun toString(): String {
        val encodedText = if(encoded!=null){
            ",$encoded"
        }else{
            ""
        }
        return "DH($key,$data$encodedText)"
    }
}