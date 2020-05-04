package ru.adonixis.vkcupunsub.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vk.api.sdk.VK
import kotlinx.android.synthetic.main.activity_unsub.*
import ru.adonixis.vkcupunsub.R
import ru.adonixis.vkcupunsub.adapter.GroupsAdapter
import ru.adonixis.vkcupunsub.models.VKGroup
import ru.adonixis.vkcupunsub.util.OnItemClickListener
import ru.adonixis.vkcupunsub.util.RecyclerItemDecoration
import ru.adonixis.vkcupunsub.util.RecyclerTouchListener
import ru.adonixis.vkcupunsub.util.Utils.showSnackbar
import ru.adonixis.vkcupunsub.viewmodel.UnsubViewModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UnsubActivity: AppCompatActivity() {

    private lateinit var viewModel: UnsubViewModel
    private val groups: ArrayList<VKGroup> = ArrayList()
    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private var currentPosition: Int = 0
    private lateinit var tvFollowers: TextView
    private lateinit var tvNewsFeed: TextView
    private var checkedGroups: MutableSet<Int> = mutableSetOf()
    private lateinit var mProgressDialog: ProgressDialog

    companion object {
        private const val TAG = "UnsubActivity"
        private const val NUMBER_OF_COLUMNS = 3

        fun startFrom(context: Context) {
            val intent = Intent(context, UnsubActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val onItemClickListener = object : OnItemClickListener {
        override fun onClick(view: View, position: Int) {
            currentPosition = position
            val group = groups[currentPosition]
            group.isChecked = !group.isChecked
            groupsAdapter.notifyItemChanged(position)

            if (group.isChecked) {
                checkedGroups.add(group.id)
            } else {
                checkedGroups.remove(group.id)
            }

            if (checkedGroups.isEmpty()) {
                btnUnsub.visibility = View.GONE
                tvCheckedGroupsCount.visibility = View.GONE
            } else if (checkedGroups.size == 1) {
                btnUnsub.visibility = View.VISIBLE
                tvCheckedGroupsCount.visibility = View.VISIBLE
            }

            tvCheckedGroupsCount.text = checkedGroups.size.toString()
        }
        override fun onLongClick(view: View, position: Int) {
            currentPosition = position
            val group = groups[position]

            val bottomSheetDialog = BottomSheetDialog(this@UnsubActivity)
            bottomSheetDialog.setContentView(R.layout.dialog_group_info)
            bottomSheetDialog.setOnShowListener { dialog ->
                Handler().postDelayed({
                    val d = dialog as BottomSheetDialog
                    val bottomSheet = d.findViewById<FrameLayout>(R.id.design_bottom_sheet)
                    val bottomSheetBehavior: BottomSheetBehavior<*> =
                        BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }, 0)
            }
            bottomSheetDialog.show()

            viewModel.getGroupsFriendsCount(group.id.toString())
            viewModel.getGroupsLastPost("-" + group.id.toString())

            val tvGroupName = bottomSheetDialog.findViewById<TextView>(R.id.tvGroupName)
            tvGroupName?.text = group.name
            tvFollowers = bottomSheetDialog.findViewById(R.id.tvFollowers)!!
            val tvArticle = bottomSheetDialog.findViewById<TextView>(R.id.tvArticle)
            if (group.description.isEmpty()) {
                tvArticle?.text = group.name
            } else {
                tvArticle?.text = group.description
            }
            tvNewsFeed = bottomSheetDialog.findViewById(R.id.tvNewsFeed)!!
            val icDismiss = bottomSheetDialog.findViewById<ImageView>(R.id.icDismiss)
            icDismiss?.setOnClickListener { bottomSheetDialog.hide() }
            val btnOpen = bottomSheetDialog.findViewById<Button>(R.id.btnOpen)
            btnOpen?.setOnClickListener {
                bottomSheetDialog.hide()
                val url = "https://vk.com/${group.screenName}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsub)

        viewModel = ViewModelProvider(this@UnsubActivity).get(UnsubViewModel::class.java)
        viewModel.getGroupsLiveData().observe(this, Observer {
            swipeRefresh.isRefreshing = false
            groups.addAll(it.reversed())
            groupsAdapter.notifyDataSetChanged()
        })

        viewModel.getGroupsFriendsCountLiveData().observe(this, Observer {
            val group = groups[currentPosition]
            val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
            val symbols = formatter.decimalFormatSymbols
            symbols.groupingSeparator = ' '
            formatter.decimalFormatSymbols = symbols
            val followersText = resources.getQuantityString(R.plurals.format_followers, group.membersCount, formatter.format(group.membersCount)) + " · " +
                    resources.getQuantityString(R.plurals.format_friends, it, it)
            tvFollowers.text = followersText
        })

        viewModel.getGroupsLastPostLiveData().observe(this, Observer {
            val date = Date(it.toLong() * 1000)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dateFormat: SimpleDateFormat
            val formattedDate: String
            val calendarYesterday = Calendar.getInstance()
            calendarYesterday.add(Calendar.DAY_OF_YEAR, -1)
            if (calendar[Calendar.YEAR] == Calendar.getInstance().get(Calendar.YEAR)) {
                if (calendar[Calendar.MONTH] == Calendar.getInstance().get(Calendar.MONTH) &&
                    calendar[Calendar.DAY_OF_MONTH] == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    formattedDate = getString(R.string.title_today)
                } else if (calendarYesterday.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                    formattedDate = getString(R.string.title_yesterday)
                } else {
                    dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
                    formattedDate = dateFormat.format(date)
                }
            } else {
                dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                formattedDate = dateFormat.format(date)
            }

            tvNewsFeed.text = getString(R.string.format_last_post, formattedDate)
        })

        viewModel.getUnsubLiveData().observe(this, Observer {
            mProgressDialog.dismiss()
            refreshGroups()
        })
        viewModel.getErrorMessageLiveData().observe(this, Observer {
            mProgressDialog.dismiss()
            swipeRefresh.isRefreshing = false
            showErrorMessage(it)
        })

        gridLayoutManager = GridLayoutManager(this, NUMBER_OF_COLUMNS)

        recyclerDocs.layoutManager = gridLayoutManager
        recyclerDocs.setHasFixedSize(true)
        groupsAdapter = GroupsAdapter(groups, this)
        recyclerDocs.adapter = groupsAdapter
        val recyclerItemDecoration = RecyclerItemDecoration(this)
        recyclerDocs.addItemDecoration(recyclerItemDecoration)
        (recyclerDocs.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerDocs.addOnItemTouchListener(RecyclerTouchListener(this@UnsubActivity, recyclerDocs, onItemClickListener))

        refreshGroups()

        swipeRefresh.setOnRefreshListener { refreshGroups() }

        mProgressDialog = ProgressDialog(this, R.style.AppTheme_Light_Dialog)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage(getString(R.string.progress_message_unsubscribing))

        btnUnsub.setOnClickListener {
            viewModel.unsub(checkedGroups)
            mProgressDialog.show()
        }
    }

    private fun refreshGroups() {
        swipeRefresh.isRefreshing = true
        groups.clear()
        groupsAdapter.notifyDataSetChanged()
        viewModel.getGroups()
        checkedGroups.clear()
        btnUnsub.visibility = View.GONE
        tvCheckedGroupsCount.visibility = View.GONE
    }

    private fun showErrorMessage(errorMessage: String) {
        showSnackbar(
            layoutRoot, Snackbar.Callback(),
            ContextCompat.getColor(this, R.color.red),
            Color.WHITE,
            errorMessage,
            Color.WHITE,
            getString(R.string.snackbar_action_hide), null
        )
    }

    private fun showSuccessMessage(successMessage: String) {
        showSnackbar(
            layoutRoot, Snackbar.Callback(),
            ContextCompat.getColor(this, R.color.green),
            Color.WHITE,
            successMessage,
            Color.WHITE,
            getString(R.string.snackbar_action_hide), null
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        VK.logout()
        WelcomeActivity.startFrom(this)
        finish()
    }

    override fun onBackPressed() {
        if (checkedGroups.isNotEmpty()) {
            for (group in groups) {
                group.isChecked = false
            }
            groupsAdapter.notifyDataSetChanged()
            checkedGroups.clear()
            btnUnsub.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

}