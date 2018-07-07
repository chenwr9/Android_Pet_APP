package com.javaone.onepet;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlueToothService extends Activity implements AdapterView.OnItemClickListener
{
    private Button search_button;
    private EditText editText;
    //用来保存搜索到的设备信息
    private List<String> bluetoothDevices = new ArrayList<String>();
    //获取到蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //字符串数组适配器
    private ArrayAdapter<String> mArrayAdapter;
    //  private List<BluetoothDevic> mDeviceList = new ArrayList<>();
    private static final UUID SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String NAME = "ding";
    //用来显示信息的列表组件
    private ListView list;
    //服务端利用线程不断接收客户端信息
    private AcceptThread thread;
    // 选中发送数据的蓝牙设备，全局变量，否则连接在方法执行完就结束了
    private BluetoothDevice selectDevice;
    // 获取到选中设备的客户端串口，全局变量，否则连接在方法执行完就结束了
    private BluetoothSocket clientSocket;
    // 获取到向设备写的输出流，全局变量，否则连接在方法执行完就结束了
    private OutputStream os;
    //dialog输入
   // private final EditText edit = new EditText(BlueToothService.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_layout);
        search_button = (Button) findViewById(R.id.search);
        //为listview设置字符串转数组适配器
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, bluetoothDevices);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                // 为listView设置item点击事件侦听
                if (mBluetoothAdapter == null)//不存在蓝牙
                {
                    Toast.makeText(BlueToothService.this, "未安装蓝牙硬件或驱动，请重试！", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled())//蓝牙未开启
                {
                    Toast.makeText(BlueToothService.this, "检测到蓝牙未开启，请开启蓝牙!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                }
                //请求可被搜索
                Intent discoverableIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(mBluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);

                if(mBluetoothAdapter.isDiscovering()){
                    //如果正在搜索，要停止。因为startDiscovery()不能重复调用
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();

                list = (ListView) findViewById(R.id.paired_devices_listview);
                //为listview绑定适配器
                list.setAdapter(mArrayAdapter);
                //用Set集合保存已配对设备
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) //已配对过的设备
                {
                    for (BluetoothDevice device : pairedDevices) {
                        bluetoothDevices.add(device.getName() + ":" + device.getAddress());
                    }
                }
                initBlue();
            }
        });
    }

    private void initBlue()
    {
        //对listview列表设置监听
        list.setOnItemClickListener(this);
        //每搜索到一个设备就会发送一个该广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mBluetoothReceiver, filter);
        //当全部搜索完后发送该广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mBluetoothReceiver, filter);

        //实例接收客户端传过来的数据线程
        thread = new AcceptThread();
        thread.start();
    };

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取到广播的action
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(BlueToothService.this, "搜索到：" + device.getName() + ":" + device.getAddress(), Toast.LENGTH_SHORT).show();
                //判断设备是否是之前已经绑定过的
                if(device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    bluetoothDevices.add(device.getName() + ":" + device.getAddress());
                    // 更新字符串数组适配器，将内容显示在listView中
                    mArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //ShowDialog();
        //获取这个设备的信息
        String s = mArrayAdapter.getItem(position);
        String address = s.substring(s.indexOf(":")+1).trim();
        if(mBluetoothAdapter.isDiscovering())
        {
            mBluetoothAdapter.cancelDiscovery();
        }

        //通过地址获取到该设备
        selectDevice = mBluetoothAdapter.getRemoteDevice(address);
        try {
            // 判断客户端接口是否为空
            if (clientSocket == null) {
                // 获取到客户端接口
                clientSocket = selectDevice.createRfcommSocketToServiceRecord(SERVICE_UUID);
                // 向服务端发送连接
                clientSocket.connect();
                // 获取到输出流，向外写数据
                os = clientSocket.getOutputStream();
            }
            // 判断是否拿到输出流
            if (os != null) {
                // 需要发送的信息
                String text ="";
                editText = (EditText) findViewById(R.id.input);
                text = editText.getText().toString();
                // 以utf-8的格式发送出去
                os.write(text.getBytes("UTF-8"));
                // 告诉用户发送成功
                Toast.makeText(getApplicationContext(), "消息已发送给对方！", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 如果发生异常则告诉用户发送失败
            Toast.makeText(getApplicationContext(), "发送信息失败 ！", Toast.LENGTH_LONG).show();
        }
    }

  /*  private void ShowDialog() {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(BlueToothService.this);
        inputDialog.setTitle("请输入要发送的内容").setView(edit);
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(BlueToothService.this,
                        edit.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }).show();
    }*/

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(BlueToothService.this, (String) msg.obj, Toast.LENGTH_LONG).show();
        }
    };

    private class AcceptThread extends Thread
    {
        private BluetoothServerSocket serverSocket;//服务器接口
        private BluetoothSocket socket;//获取到客户端的接口
        private InputStream is;//获取到输入流
        private OutputStream os;//获取到输出流

        public AcceptThread() {
            try {
                //通过UUID监听请求，然后获取到对应的服务端接口
                serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, SERVICE_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void run()
        {
            try{
                //接收其用户端的接口
                socket = serverSocket.accept();
                //获取到输入流与输出流
                is = socket.getInputStream();
                os = socket.getOutputStream();

                while(true)
                {
                    //创建一个128字节的缓冲
                    byte[] buffer = new byte[128];
                    //每次读取128字节并保存其读取的角标,并发送数据
                    int count = is.read(buffer);
                    Message msg = new Message();
                    msg.obj = new String(buffer,0,count,"utf-8");
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}