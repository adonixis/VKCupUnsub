package ru.adonixis.vkcupunsub.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.adonixis.vkcupunsub.models.VKGroup
import java.util.*

class VKGroupsRequest(): VKRequest<List<VKGroup>>("groups.get")  {
    init {
        addParam("extended", 1)
        addParam("fields", "description,members_count")
    }

    override fun parse(r: JSONObject): List<VKGroup> {
        val docs = r.getJSONObject("response").getJSONArray("items")
        val result = ArrayList<VKGroup>()
        for (i in 0 until docs.length()) {
            result.add(VKGroup.parse(docs.getJSONObject(i)))
        }
        return result
    }
}