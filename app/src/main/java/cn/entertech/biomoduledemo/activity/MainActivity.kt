package cn.entertech.biomoduledemo.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.entertech.affective.sdk.api.IAffectiveDataAnalysisService
import cn.entertech.affective.sdk.api.IConnectionServiceListener
import cn.entertech.affective.sdk.api.IFinishAffectiveServiceListener
import cn.entertech.affective.sdk.api.IGetReportListener
import cn.entertech.affective.sdk.api.IStartAffectiveServiceLister
import cn.entertech.affective.sdk.bean.AffectiveDataCategory
import cn.entertech.affective.sdk.bean.AffectiveServiceWay
import cn.entertech.affective.sdk.bean.BioDataCategory
import cn.entertech.affective.sdk.bean.EnterAffectiveConfigProxy
import cn.entertech.affective.sdk.bean.Error
import cn.entertech.affective.sdk.bean.RealtimeAffectiveData
import cn.entertech.affective.sdk.bean.RealtimeBioData
import cn.entertech.affective.sdk.bean.UploadReportEntity
import cn.entertech.biomoduledemo.R
import cn.entertech.biomoduledemo.fragment.MessageReceiveFragment
import cn.entertech.biomoduledemo.fragment.MessageSendFragment
import cn.entertech.biomoduledemo.utils.*
import cn.entertech.ble.single.BiomoduleBleManager
import com.orhanobut.logger.Logger
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var appSecret: String? = null
    private var appKey: String? = null
    private lateinit var biomoduleBleManager: BiomoduleBleManager
    private lateinit var messageReceiveFragment: MessageReceiveFragment
    private lateinit var messageSendFragment: MessageSendFragment
    private lateinit var vpContainer: ViewPager
    private lateinit var pagerSlidingTabStrip: PagerSlidingTabStrip
    private val affectiveService by lazy {
        IAffectiveDataAnalysisService.getService(AffectiveServiceWay.AffectiveLocalService)
//        IAffectiveDataAnalysisService.getService(AffectiveServiceWay.AffectiveCloudService)
    }

    //    var websocketAddress = "wss://server.affectivecloud.cn/ws/algorithm/v2/"
    var websocketAddress = "wss://server-test.affectivecloud.cn/ws/algorithm/v2/"

    companion object{
        private const val TAG="MainActivity"
    }
    private var availableAffectiveDataCategories =
        listOf(
            AffectiveDataCategory.ATTENTION,
            AffectiveDataCategory.PRESSURE,
            AffectiveDataCategory.AROUSAL,
            AffectiveDataCategory.RELAXATION,
            AffectiveDataCategory.PLEASURE,
            AffectiveDataCategory.SLEEP,
            AffectiveDataCategory.COHERENCE,
            AffectiveDataCategory.FLOW
        )
    private var availableBioDataCategories = listOf(BioDataCategory.EEG, BioDataCategory.HR)

    private val connectionListener by lazy {
        {
            val i = Log.d(TAG,"connectionListener")
        }
    }

    private val disconnectionListener by lazy {
        { errorMsg: String ->
            val i = Log.d(TAG, "disconnect:$errorMsg")
        }
    }

    private val startAffectiveServiceLister by lazy {
        object : IStartAffectiveServiceLister {
            override fun startSuccess() {
                Log.d(TAG, "startAffectiveServiceLister:startSuccess")
            }

            override fun startBioFail(error: Error?) {
                Log.d(TAG, "startAffectiveServiceLister:startBioFail $error")
            }

            override fun startAffectionFail(error: Error?) {
                Log.d(TAG, "startAffectiveServiceLister:startAffectionFail $error")
            }

            override fun startFail(error: Error?) {
                Log.d(TAG, "startAffectiveServiceLister:startFail $error")
            }
        }
    }

    private val authenticationInputStream: InputStream? by lazy {
        TODO("添加鉴权文件流")
    }

    private val connectionServiceListener by lazy {
        object : IConnectionServiceListener {
            override fun connectionSuccess(sessionId: String?) {
                Log.d(TAG, "connectionSuccess: $sessionId")
                affectiveService?.startAffectiveService(
                    authenticationInputStream,
                    this@MainActivity, startAffectiveServiceLister
                )
            }

            override fun connectionError(error: Error?) {
                Log.d(TAG, "connectionError: $error")
            }
        }
    }

    private val bdListener by lazy {
        { data: RealtimeBioData? ->
            Log.d(TAG, "bdListener: $data")
            messageReceiveFragment.appendMessageToScreen(getString(R.string.main_realtime_biodata) + data.toString())
        }
    }
    private val affectiveListener by lazy {
        { data: RealtimeAffectiveData? ->
            Log.d(TAG, "affectiveListener: $data")
            messageReceiveFragment.appendMessageToScreen(getString(R.string.main_realtime_affective_data) + data.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        biomoduleBleManager = BiomoduleBleManager.getInstance(applicationContext)
        biomoduleBleManager.addRawDataListener(rawListener)
        biomoduleBleManager.addHeartRateListener(heartRateListener)
        initView()
        initPermission()
    }


    private fun initEnterAffectiveCloudManager() {
        val proxy = EnterAffectiveConfigProxy(
            availableBioDataCategories,
            availableAffectiveDataCategories,
            userId = -1,
            appSecret = appSecret!!,
            appKey = appKey!!
        )

        affectiveService?.addServiceConnectStatueListener(
            connectionListener,
            disconnectionListener
        )?: kotlin.run {
            Log.d(TAG,"affectiveService is null")
        }
        affectiveService?.connectAffectiveServiceConnection(
            configProxy = proxy,
            listener = connectionServiceListener
        )
        affectiveService?.subscribeData(bdListener, affectiveListener)
    }

    private fun initView() {
        vpContainer = findViewById(R.id.vp_contain)
        pagerSlidingTabStrip = findViewById(R.id.message_tabs)
        val listFragment = mutableListOf<Fragment>()
        messageReceiveFragment = MessageReceiveFragment()
        messageSendFragment = MessageSendFragment()
        listFragment.add(messageReceiveFragment)
        listFragment.add(messageSendFragment)
        val listTitles = listOf(getString(R.string.main_receive), getString(R.string.main_send))
        val adapter = MessageAdapter(
            supportFragmentManager,
            listFragment,
            listTitles
        )
        vpContainer.adapter = adapter
        pagerSlidingTabStrip.setViewPager(vpContainer)
    }

    class MessageAdapter(
        fragmentManager: FragmentManager,
        fragments: List<Fragment>,
        titles: List<String>
    ) :
        FragmentStatePagerAdapter(fragmentManager) {
        private var fragments: List<Fragment> = listOf()
        private var titles: List<String> = listOf()

        init {
            this.fragments = fragments
            this.titles = titles
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initPermissionS() {
        val needPermission = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
        )
        val needRequestPermissions = ArrayList<String>()
        for (i in needPermission.indices) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    needPermission[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needRequestPermissions.add(needPermission[i])
            }
        }
        if (needRequestPermissions.size != 0) {
            val permissions = arrayOfNulls<String>(needRequestPermissions.size)
            for (i in needRequestPermissions.indices) {
                permissions[i] = needRequestPermissions[i]
            }
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)
        }
    }


    /**
     * Android6.0 auth
     */
    private fun initPermission() {
        val needPermission = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val needRequestPermissions = ArrayList<String>()
        for (i in needPermission.indices) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    needPermission[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needRequestPermissions.add(needPermission[i])
            }
        }
        if (needRequestPermissions.size != 0) {
            val permissions = arrayOfNulls<String>(needRequestPermissions.size)
            for (i in needRequestPermissions.indices) {
                permissions[i] = needRequestPermissions[i]
            }
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            initPermissionS()
        }
    }


    fun onConnectDevice(@Suppress("UNUSED_PARAMETER") view: View) {
        messageReceiveFragment.appendMessageToScreen(getString(R.string.main_ble_scaning))
        biomoduleBleManager.scanNearDeviceAndConnect(fun() {
            messageReceiveFragment.appendMessageToScreen(getString(R.string.main_scan_success))
            Logger.d("扫描设备成功")
        }, fun(_: Exception) {

        }, fun(mac: String) {
            messageReceiveFragment.appendMessageToScreen(getString(R.string.main_connect_to_ble_success))
            Logger.d("连接成功$mac")
            runOnUiThread {
                Toast.makeText(this@MainActivity, "设备连接成功", Toast.LENGTH_SHORT).show()
            }
        },{msg->
            Logger.d("连接失败")
            messageReceiveFragment.appendMessageToScreen(getString(R.string.main_ble_connect_failed) + msg)
            runOnUiThread {
                Toast.makeText(this@MainActivity, "设备连接失败：$msg", Toast.LENGTH_SHORT).show()
            }
        },0,)
    }

    fun onDisconnectDevice(@Suppress("UNUSED_PARAMETER") view: View) {
        messageReceiveFragment.appendMessageToScreen(getString(R.string.main_ble_connect_failed))
        biomoduleBleManager.disConnect()
    }

    fun onClear(@Suppress("UNUSED_PARAMETER") view: View) {
        messageSendFragment.clearScreen()
        messageReceiveFragment.clearScreen()
    }

    fun onPause(@Suppress("UNUSED_PARAMETER") view: View) {
        biomoduleBleManager.stopHeartAndBrainCollection()
    }

    private var rawListener = fun(bytes: ByteArray) {
        affectiveService?.appendEEGData(bytes)
    }

    private var heartRateListener = fun(heartRate: Int) {
        affectiveService?.appendHeartRateData(heartRate)
    }

    fun onInit(@Suppress("UNUSED_PARAMETER") view: View) {
        initEnterAffectiveCloudManager()
    }

    fun onStartUpload(@Suppress("UNUSED_PARAMETER") view: View) {
        biomoduleBleManager.startHeartAndBrainCollection()
        messageReceiveFragment.appendMessageToScreen(getString(R.string.main_start_uploading))
    }

    fun onReport(@Suppress("UNUSED_PARAMETER") view: View) {
        affectiveService?.getReport(object : IGetReportListener {
            override fun onError(error: Error?) {
                Log.d(TAG, "getReport: onError $error")
                messageReceiveFragment.appendMessageToScreen("getReport onError" + error.toString())
            }

            override fun onSuccess(entity: UploadReportEntity?) {
                Log.d(TAG, "getReport: onSuccess $entity")
                messageReceiveFragment.appendMessageToScreen("getReport onSuccess" + entity.toString())
            }

            override fun getBioReportError(error: Error?) {
                Log.d(TAG, "getReport: getBioReportError $error")
                messageReceiveFragment.appendMessageToScreen("getReport getBioReportError" + error.toString())
            }

            override fun getAffectiveReportError(error: Error?) {
                Log.d(TAG, "getReport: getAffectiveReportError $error")
                messageReceiveFragment.appendMessageToScreen("getReport getAffectiveReportError" + error.toString())
            }
        }, false)

    }

    fun onFinish(@Suppress("UNUSED_PARAMETER") view: View) {
        affectiveService?.finishAffectiveService(object : IFinishAffectiveServiceListener {
            override fun finishBioFail(error: Error?) {
                Log.d(TAG, "onFinish: finishBioFail $error")
            }

            override fun finishAffectiveFail(error: Error?) {
                Log.d(TAG, "onFinish: finishAffectiveFail $error")
            }

            override fun finishError(error: Error?) {
                Log.d(TAG, "onFinish: finishError $error")
                affectiveService?.closeAffectiveServiceConnection()
            }

            override fun finishSuccess() {
                Log.d(TAG, "onFinish: finishSuccess ")
                affectiveService?.closeAffectiveServiceConnection()
            }
        })
    }

}