package com.axecom.iweight.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.bean.WifiBean;
import com.axecom.iweight.ui.view.WifiPwdDialog;
import com.axecom.iweight.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class WifiSettingsActivity extends BaseActivity {

    public static final String KEY_SSID_WIFI_SAVED = "key_ssid_wifi_saved";
    private View rootView;
    private GridView wifiGridView;
    private WifiManager wifiManager;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;
    private WifiAdapter wifiAdapter;
    private int linkingID;
    private List<WifiBean> wifiSSIDList = new ArrayList<>();;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.wifi_settings_activity, null);
        wifiGridView = rootView.findViewById(R.id.wifi_settings_grid_view);
        wifiAdapter = new WifiAdapter(this, wifiSSIDList);
        wifiGridView.setAdapter(wifiAdapter);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }

        return rootView;
    }

    @Override
    public void initView() {
        startScan(this);
        WifiBean wifiBean;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        for (int i = 0; i < mWifiList.size(); i++) {
            wifiBean = new WifiBean();
            if(wifiInfo.getBSSID().equals(mWifiList.get(i).BSSID)){
                wifiBean.setConnected(true);
            }else {
                wifiBean.setConnected(false);
            }
            wifiBean.setSsid(mWifiList.get(i).SSID);
            wifiSSIDList.add(wifiBean);
        }

        wifiGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                // 连接到外网
                final WifiConfiguration[] mWifiConfiguration = new WifiConfiguration[1];
                //检测指定SSID的WifiConfiguration 是否存在
                WifiConfiguration tempConfig = IsExsits(mWifiList.get(position).SSID);
                if (tempConfig == null) {
                    WifiPwdDialog.Builder builder = new WifiPwdDialog.Builder(WifiSettingsActivity.this);
                    builder.create(new WifiPwdDialog.OnConfirmedListener() {
                        @Override
                        public void onConfirmed(String result) {
                            String pwd = result;
                            //创建一个新的WifiConfiguration ，CreateWifiInfo()需要自己实现
                            mWifiConfiguration[0] = createWifiInfo(mWifiList.get(position).SSID, pwd, 3);
                            int wcgID = wifiManager.addNetwork(mWifiConfiguration[0]);
                            boolean b = wifiManager.enableNetwork(wcgID, true);
                            updateListView(b, position, mWifiList.get(position).SSID);

                        }
                    }).show();
                } else {
                    //发现指定WiFi，并且这个WiFi以前连接成功过
                    mWifiConfiguration[0] = tempConfig;
                    boolean b = wifiManager.enableNetwork(mWifiConfiguration[0].networkId, true);
                    if(b){
                        SPUtils.putString(WifiSettingsActivity.this, KEY_SSID_WIFI_SAVED, mWifiList.get(position).SSID);
                    }
                    updateListView(b, position, mWifiList.get(position).SSID);
                }
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                wifiManager.startScan();
//                List<ScanResult> resultList = wifiManager.getScanResults();
//                StringBuilder stringBuilder = new StringBuilder();
//                for (int i = 0; i < resultList.size(); i++) {
//                    stringBuilder
//                            .append("Index_" + new Integer(i + 1).toString() + ":");
//                    // 将ScanResult信息转换成一个字符串包
//                    // 其中把包括：BSSID、SSID、capabilities、frequency、level
//                    stringBuilder.append((resultList.get(i)).toString());
//                    stringBuilder.append("/n");
//                }
//
//            }
//        }).run();
    }

    public void updateListView(boolean flag, int position, String SSID){
        WifiBean bean;
        for (int i = 0; i < wifiSSIDList.size(); i++) {
            bean = new WifiBean();
            bean.setSsid(wifiSSIDList.get(i).getSsid());
            if(position == i){
                bean.setConnected(true);
            }else {
                bean.setConnected(false);
            }
            wifiSSIDList.set(i, bean);
        }
        wifiAdapter.notifyDataSetChanged();
    }

    public WifiConfiguration createWifiInfo(String SSID, String Password,
                                            int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    // 添加一个网络并连接
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = wifiManager.addNetwork(wcg);
        boolean b = wifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
        if (b) {
            linkingID = wcgID;
        }
        return b;
    }

    private void Wificonnect(String SSID, String password) {
        // 连接到外网
        WifiConfiguration mWifiConfiguration;
        //检测指定SSID的WifiConfiguration 是否存在
        WifiConfiguration tempConfig = IsExsits(SSID);
        if (tempConfig == null) {
            //创建一个新的WifiConfiguration ，CreateWifiInfo()需要自己实现
            mWifiConfiguration = createWifiInfo(SSID, password, 3);
            int wcgID = wifiManager.addNetwork(mWifiConfiguration );
            boolean b = wifiManager.enableNetwork(wcgID, true);
        } else {
            //发现指定WiFi，并且这个WiFi以前连接成功过
            mWifiConfiguration = tempConfig;
            boolean b = wifiManager.enableNetwork(mWifiConfiguration.networkId, true);
        }

    }

    //判断曾经连接过得WiFi中是否存在指定SSID的WifiConfiguration
    public WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    @SuppressLint("WrongConstant")
    public void startScan(Context context) {
        wifiManager.startScan();
        //得到扫描结果
        List<ScanResult> results = wifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = wifiManager.getConfiguredNetworks();
        if (results == null) {
            if(wifiManager.getWifiState()== 3){
                Toast.makeText(context,"当前区域没有无线网络",Toast.LENGTH_SHORT).show();
            }else if(wifiManager.getWifiState() == 2){
                Toast.makeText(context,"wifi正在开启，请稍后扫描", Toast.LENGTH_SHORT).show();
            }else{Toast.makeText(context,"WiFi没有开启", Toast.LENGTH_SHORT).show();
            }
        } else {
            mWifiList = new ArrayList();
            for(ScanResult result : results){
                if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                boolean found = false;
                for(ScanResult item:mWifiList){
                    if(item.SSID.equals(result.SSID)&&item.capabilities.equals(result.capabilities)){
                        found = true;break;
                    }
                }
                if(!found){
                    mWifiList.add(result);
                }
            }
            wifiAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

    }

    class WifiAdapter extends BaseAdapter{
        private Context context;
        private List<WifiBean> ssidList;

        public WifiAdapter(Context context, List<WifiBean> ssidList){
            this.context = context;
            this.ssidList = ssidList;
        }

        @Override
        public int getCount() {
            return ssidList.size();
        }

        @Override
        public Object getItem(int position) {
            return ssidList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.wifi_settings_item, null);
                holder = new ViewHolder();
                holder.SSIDTv = convertView.findViewById(R.id.wifi_settings_ssid_tv);
                holder.connectedIv = convertView.findViewById(R.id.wifi_settings_connected_iv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            WifiBean item = ssidList.get(position);
            if(item.isConnected()){
                holder.connectedIv.setVisibility(View.VISIBLE);
            }else {
                holder.connectedIv.setVisibility(View.GONE);
            }
            holder.SSIDTv.setText(item.getSsid());

            return convertView;
        }

        class ViewHolder{
            TextView SSIDTv;
            ImageView connectedIv;
        }
    }
}
