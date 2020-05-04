package ru.adonixis.vkcupunsub.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.adonixis.vkcupunsub.models.VKGroup
import ru.adonixis.vkcupunsub.requests.VKGroupsFriendsCountRequest
import ru.adonixis.vkcupunsub.requests.VKGroupsLastPostRequest
import ru.adonixis.vkcupunsub.requests.VKGroupsLeaveCommand
import ru.adonixis.vkcupunsub.requests.VKGroupsRequest

class UnsubViewModel : ViewModel() {
    companion object {
        private const val TAG = "UnsubViewModel"
    }
    private var groupsLiveData: MutableLiveData<List<VKGroup>>? = null
    private var groupsFriendsCountLiveData: MutableLiveData<Int>? = null
    private var groupsLastPostLiveData: MutableLiveData<Int>? = null
    private var unsubLiveData: MutableLiveData<Int>? = null
    private var errorMessageLiveData: MutableLiveData<String>? = null

    fun getGroupsLiveData(): LiveData<List<VKGroup>> {
        groupsLiveData = MutableLiveData()
        return groupsLiveData as LiveData<List<VKGroup>>
    }

    fun getGroupsFriendsCountLiveData(): LiveData<Int> {
        groupsFriendsCountLiveData = MutableLiveData()
        return groupsFriendsCountLiveData as LiveData<Int>
    }

    fun getGroupsLastPostLiveData(): LiveData<Int> {
        groupsLastPostLiveData = MutableLiveData()
        return groupsLastPostLiveData as LiveData<Int>
    }

    fun getUnsubLiveData(): LiveData<Int> {
        unsubLiveData = MutableLiveData()
        return unsubLiveData as LiveData<Int>
    }

    fun getErrorMessageLiveData(): LiveData<String> {
        errorMessageLiveData = MutableLiveData()
        return errorMessageLiveData as LiveData<String>
    }

    fun getGroups() {
        VK.execute(VKGroupsRequest(), object: VKApiCallback<List<VKGroup>> {
            override fun success(result: List<VKGroup>) {
                groupsLiveData?.value = result
            }

            override fun fail(error: Exception) {
                Log.e(TAG, error.toString())
                errorMessageLiveData!!.value = error.toString()
            }
        })
    }

    fun getGroupsFriendsCount(groupId: String) {
        VK.execute(VKGroupsFriendsCountRequest(groupId), object: VKApiCallback<Int> {
            override fun success(result: Int) {
                groupsFriendsCountLiveData?.value = result
            }

            override fun fail(error: Exception) {
                Log.e(TAG, error.toString())
                errorMessageLiveData!!.value = error.toString()
            }
        })
    }

    fun getGroupsLastPost(ownerId: String) {
        VK.execute(VKGroupsLastPostRequest(ownerId), object: VKApiCallback<Int> {
            override fun success(result: Int) {
                groupsLastPostLiveData?.value = result
            }

            override fun fail(error: Exception) {
                Log.e(TAG, error.toString())
                errorMessageLiveData!!.value = error.toString()
            }
        })
    }

    fun unsub(checkedGroups: Set<Int>) {
        VK.execute(VKGroupsLeaveCommand(checkedGroups), object: VKApiCallback<Int> {
            override fun success(result: Int) {
                if (result == 1) {
                    unsubLiveData?.value = result
                } else {
                    errorMessageLiveData!!.value = result.toString()
                }
            }
            override fun fail(error: Exception) {
                Log.e(TAG, error.toString())
                errorMessageLiveData!!.value = error.toString()
            }
        })
    }

}