package ru.adonixis.vkcupunsub.requests

import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKGroupsFriendsCountRequest(groupId: String): VKRequest<Int>("groups.getMembers")  {
    init {
        addParam("group_id", groupId)
        addParam("filter", "friends")
    }

    override fun parse(r: JSONObject): Int {
        return r.getJSONObject("response").getInt("count")
    }
}