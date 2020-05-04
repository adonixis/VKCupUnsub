package ru.adonixis.vkcupunsub.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKGroupsLastPostRequest(ownerId: String): VKRequest<Int>("wall.get")  {
    init {
        addParam("owner_id", ownerId)
        addParam("count", 1)
    }

    override fun parse(r: JSONObject): Int {
        return r.getJSONObject("response").getJSONArray("items").getJSONObject(0).getInt("date")
    }
}