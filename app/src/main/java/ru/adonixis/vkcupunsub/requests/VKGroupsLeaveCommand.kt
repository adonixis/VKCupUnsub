package ru.adonixis.vkcupunsub.requests

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class VKGroupsLeaveCommand(private val groups: Set<Int>): ApiCommand<Int>() {
    override fun onExecute(manager: VKApiManager): Int {
        var result: Int = 1
        for (groupId in groups) {
            val call = VKMethodCall.Builder()
                    .method("groups.leave")
                    .args("group_id", groupId)
                    .version(manager.config.version)
                    .build()
            result *= manager.execute(call, ResponseApiParser())
        }
        return result
    }

    private class ResponseApiParser : VKApiResponseParser<Int> {
        override fun parse(response: String): Int{
            try {
                return JSONObject(response).getInt("response")
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}