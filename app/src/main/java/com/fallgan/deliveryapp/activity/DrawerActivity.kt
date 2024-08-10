package com.fallgan.deliveryapp.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.activity.MainActivity
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session

open class DrawerActivity : AppCompatActivity() {
    lateinit var navigationView: NavigationView
    lateinit var drawer: DrawerLayout
    lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var tvMobile: TextView
    protected lateinit var frameLayout: FrameLayout
    lateinit var session: Session
    private lateinit var lytProfile: LinearLayout
    private lateinit var lytWallet: LinearLayout
    lateinit var activity: Activity

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_drawer)
        frameLayout = findViewById(R.id.content_frame)
        navigationView = findViewById(R.id.nav_view)
        drawer = findViewById(R.id.drawer_layout)
        val header = navigationView.getHeaderView(0)
        lytWallet = header.findViewById(R.id.lytWallet)
        tvWallet = header.findViewById(R.id.tvWallet)
        tvName = header.findViewById(R.id.header_name)
        tvMobile = header.findViewById(R.id.tvMobile)
        lytProfile = header.findViewById(R.id.lytProfile)
        activity = this@DrawerActivity
        session = Session(activity)
        if (session.isUserLoggedIn) {
            tvName.text = session.getData(Constant.NAME)
            tvMobile.text = session.getData(Constant.MOBILE)
            lytWallet.visibility = View.VISIBLE
            tvWallet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet_white, 0, 0, 0)
            tvWallet.text =
                getString(R.string.wallet_balance) + "\t:\t" + Session(activity).getData(
                    Constant.CURRENCY
                ) + Constant.formatter.format(
                    session.getData(Constant.BALANCE)!!.toDouble()
                )
        } else {
            lytWallet.visibility = View.GONE
            tvName.text = resources.getString(R.string.is_login)
        }
        lytProfile.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext, ProfileActivity::class.java
                )
            )
        }
        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawer.closeDrawers()
            when (menuItem.itemId) {
                R.id.menu_home -> startActivity(
                    Intent(
                        applicationContext,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                R.id.menu_notifications -> startActivity(
                    Intent(
                        applicationContext,
                        NotificationListActivity::class.java
                    )
                )
                R.id.menu_profile -> startActivity(
                    Intent(
                        applicationContext,
                        ProfileActivity::class.java
                    )
                )
                R.id.menu_wallet_history -> startActivity(
                    Intent(
                        applicationContext,
                        WalletHistoryActivity::class.java
                    )
                )
                R.id.menu_policy -> {
                    val policy = Intent(this@DrawerActivity, WebViewActivity::class.java)
                    policy.putExtra("title", getString(R.string.privacy_policy))
                    policy.putExtra("link", Constant.DELIVERY_BOY_POLICY)
                    startActivity(policy)
                }
                R.id.menu_terms -> {
                    val terms = Intent(this@DrawerActivity, WebViewActivity::class.java)
                    terms.putExtra("title", getString(R.string.terms_conditions))
                    terms.putExtra("link", Constant.DELIVERY_BOY_TERMS)
                    startActivity(terms)
                }
                R.id.menu_logout -> session.logoutUserConfirmation(activity)
            }
            true
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        //Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var tvName: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var tvWallet: TextView
    }
}