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
import cn.entertech.affective.sdk.api.Callback
import cn.entertech.affective.sdk.api.IAffectiveDataAnalysisService
import cn.entertech.affective.sdk.api.IConnectionServiceListener
import cn.entertech.affective.sdk.api.IFinishAffectiveServiceListener
import cn.entertech.affective.sdk.api.IGetReportListener
import cn.entertech.affective.sdk.api.IStartAffectiveServiceLister
import cn.entertech.affective.sdk.bean.AffectiveServiceWay
import cn.entertech.affective.sdk.bean.EnterAffectiveConfigProxy
import cn.entertech.affective.sdk.bean.Error
import cn.entertech.affective.sdk.bean.RealtimeAffectiveData
import cn.entertech.affective.sdk.bean.RealtimeBioData
import cn.entertech.affective.sdk.bean.UploadReportEntity
import cn.entertech.affectivesdk.manager.EnterAffectiveLocalService
import cn.entertech.biomoduledemo.R
import cn.entertech.biomoduledemo.fragment.MessageReceiveFragment
import cn.entertech.biomoduledemo.fragment.MessageSendFragment
import cn.entertech.biomoduledemo.utils.*
import cn.entertech.ble.single.BiomoduleBleManager
import java.io.*
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var biomoduleBleManager: BiomoduleBleManager
    private lateinit var messageReceiveFragment: MessageReceiveFragment
    private lateinit var messageSendFragment: MessageSendFragment
    private lateinit var vpContainer: ViewPager
    private lateinit var pagerSlidingTabStrip: PagerSlidingTabStrip
    private val affectiveService by lazy {
        IAffectiveDataAnalysisService.getService(AffectiveServiceWay.AffectiveLocalService)
//        EnterAffectiveLocalService()
//        IAffectiveDataAnalysisService.getService(AffectiveServiceWay.AffectiveCloudService)

    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private val connectionListener by lazy {
        {
            val i = appendLog("connectionListener")
        }
    }

    private val disconnectionListener by lazy {
        { errorMsg: String ->
            val i = appendLog("disconnect:$errorMsg")
        }
    }

    private val startAffectiveServiceLister by lazy {
        object : IStartAffectiveServiceLister {
            override fun startSuccess() {
                appendLog("startAffectiveServiceLister:startSuccess")
            }

            override fun startBioFail(error: Error?) {
                appendLog("startAffectiveServiceLister:startBioFail $error")
            }

            override fun startAffectionFail(error: Error?) {
                appendLog("startAffectiveServiceLister:startAffectionFail $error")
            }

            override fun startFail(error: Error?) {
                appendLog("startAffectiveServiceLister:startFail $error")
            }
        }
    }

    private val authenticationInputStream: InputStream? by lazy {
        resources.openRawResource(R.raw.check)
    }

    private val connectionServiceListener by lazy {
        object : IConnectionServiceListener {
            override fun connectionSuccess(sessionId: String?) {
                appendLog("connectionSuccess: $sessionId")
                affectiveService?.subscribeData(bdListener, affectiveListener)
                affectiveService?.startAffectiveService(
                    authenticationInputStream,
                    this@MainActivity, startAffectiveServiceLister
                )
            }

            override fun connectionError(error: Error?) {
                appendLog("connectionError: $error")
            }
        }
    }

    private val bdListener by lazy {
        { data: RealtimeBioData? ->
            appendLog("bdListener data: $data")
        }
    }
    private val affectiveListener by lazy {
        { data: RealtimeAffectiveData? ->
            appendLog("affectiveListener: $data")
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
        affectiveService?.addServiceConnectStatueListener(
            connectionListener,
            disconnectionListener
        ) ?: kotlin.run {
            appendLog("affectiveService is null")
        }
        affectiveService?.connectAffectiveServiceConnection(
            configProxy = EnterAffectiveConfigProxy(),
            listener = connectionServiceListener
        )

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

    private fun appendLog(msg: String) {
        Log.d(TAG, msg)
        messageReceiveFragment.appendMessageToScreen(msg)
    }

    private fun startAffectiveService(startCollection: (IAffectiveDataAnalysisService?) -> Unit) {
        affectiveService?.apply {
            if(hasConnectAffectiveService()){
                if(hasStartAffectiveService()){
                    startCollection(this@apply)
                    appendLog("开始采集头环数据...")
                }else{
                    startAffectiveService(authenticationInputStream,
                        this@MainActivity.applicationContext,
                        object : IStartAffectiveServiceLister {
                            override fun startAffectionFail(error: Error?) {
                                appendLog("Affection算法初始化失败：${error}")
                            }

                            override fun startBioFail(error: Error?) {
                                appendLog("Bio算法初始化失败：${error}")
                            }

                            override fun startFail(error: Error?) {
                                appendLog("算法初始化失败：${error}")
                            }

                            override fun startSuccess() {
                                appendLog("算法初始化成功")
                                startCollection(this@apply)
                                appendLog("开始采集数据...")
                            }
                        })
                }
            }else{
                connectAffectiveServiceConnection(object : IConnectionServiceListener {
                    override fun connectionError(error: Error?) {
                        appendLog("服务连接失败...")
                    }

                    override fun connectionSuccess(sessionId: String?) {
                        if (hasStartAffectiveService()) {
                            startCollection(this@apply)
                            appendLog("开始采集头环数据...")
                        } else {

                        }
                    }
                }, EnterAffectiveConfigProxy())
            }


        }
    }

    fun onAnalysisSceegData(view: View) {
        startAffectiveService {
            it?.subscribeData(bdListener, affectiveListener)
            thread {
                var inputStream = resources.openRawResource(R.raw.sceeg)
                it?.apply {
                    readFileAnalysisData(inputStream, { singleData ->
                        appendSCEEGData(singleData)
                        true
                    }, { allData ->
                        if (allData.isNotEmpty()) {
                            appendSCEEGData(allData)
                        }
                    }, {
                        it.toInt()
                    }, object : Callback {
                        override fun onError(error: Error?) {
                            appendLog("解析文件失败：${error}")
                        }

                        override fun onSuccess() {
                            it?.unSubscribeData(bdListener, affectiveListener)
                            it?.getReport(object : IGetReportListener {
                                override fun getAffectiveReportError(error: Error?) {

                                }

                                override fun getBioReportError(error: Error?) {
                                }

                                override fun onError(error: Error?) {
                                    appendLog("生成报表数据失败：${error}")
                                }

                                override fun onSuccess(entity: UploadReportEntity?) {
                                    appendLog("生成报表数据：")

                                    entity?.data?.affective?.sleep?.apply {
                                        //睡眠曲线
                                        appendLog(
                                            "睡眠曲线： $sleepCurve 入睡点: $sleepPoint " +
                                                    "入睡用时 : $sleepLatency s 清醒时长 $awakeDuration s" +
                                                    "浅睡时长: $lightDuration s 深睡时长 $deepDuration s"
                                        )
                                    }
                                }
                            }, true)
                        }
                    })
                }
            }
        }
    }


    fun onConnectDevice(@Suppress("UNUSED_PARAMETER") view: View) {
        messageReceiveFragment.appendMessageToScreen(getString(R.string.main_ble_scaning))
        biomoduleBleManager.scanNearDeviceAndConnect(
            fun() {
                appendLog("扫描设备成功")
            },
            fun(_: Exception) {

            },
            fun(mac: String) {
                appendLog("连接成功$mac")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "设备连接成功", Toast.LENGTH_SHORT).show()
                }
            },
            { msg ->
                appendLog("连接失败")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "设备连接失败：$msg", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            0,
        )
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
        appendLog(getString(R.string.main_start_uploading))
    }

    fun onReport(@Suppress("UNUSED_PARAMETER") view: View) {
        affectiveService?.getReport(object : IGetReportListener {
            override fun onError(error: Error?) {
                appendLog("getReport: onError $error")
            }

            override fun onSuccess(entity: UploadReportEntity?) {
                appendLog("getReport: onSuccess $entity")
            }

            override fun getBioReportError(error: Error?) {
                appendLog("getReport: getBioReportError $error")
            }

            override fun getAffectiveReportError(error: Error?) {
                appendLog("getReport: getAffectiveReportError $error")
            }
        }, false)

    }

    fun onFinish(@Suppress("UNUSED_PARAMETER") view: View) {
        affectiveService?.finishAffectiveService(object : IFinishAffectiveServiceListener {
            override fun finishBioFail(error: Error?) {
                appendLog("onFinish: finishBioFail $error")
            }

            override fun finishAffectiveFail(error: Error?) {
                appendLog("onFinish: finishAffectiveFail $error")
            }

            override fun finishError(error: Error?) {
                appendLog("onFinish: finishError $error")
                affectiveService?.closeAffectiveServiceConnection()
            }

            override fun finishSuccess() {
                appendLog("onFinish: finishSuccess ")
                affectiveService?.closeAffectiveServiceConnection()
            }
        })
    }

}
