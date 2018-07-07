package com.javaone.onepet.myAlarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.javaone.onepet.R;

import java.util.Calendar;

public class AlarmView extends LinearLayout{
    private AlarmManager alarmManager; //系统的闹钟服务
    private static  final  String KEY_ALARM_LIST="alarmList";
    private Button btnAddAlarm;
    private ListView lvAlarmList;
    private ArrayAdapter<AlarmData> adapter;

    public AlarmView(Context context) {
        super(context);
        init();
    }

    public AlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlarmView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        // 实例化按钮和列表
        btnAddAlarm = (Button)findViewById(R.id.btnAddAlarm);
        lvAlarmList = (ListView)findViewById(R.id.lvAlarmList);

        // 实例化适配器
        adapter = new ArrayAdapter<AlarmData>(getContext(),
                android.R.layout.simple_list_item_1);

        // 为列表添加适配器
        lvAlarmList.setAdapter(adapter);

        // 保存闹钟列表
        readSavedAlarmList();

        // 添加闹钟的监听事件
        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 添加闹钟
                addAlarm();
            }
        });

        // 闹钟列表的监听事件
        lvAlarmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                // 单击其中一项会调出对话框，其中有“删除”和“全部删除”两项可以选择
                new AlertDialog.Builder(getContext()).setTitle("操作选项").setItems(
                        new CharSequence[]{"删除", "清空"}, new DialogInterface.
                                OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // 对话框中的项索引从0开始，删除选中项
                                        deleteAlarm(position);
                                        break;
                                    case 1:
                                        // 删除所有项，实际App中并不常用，只是因为测试过程中添加了太多闹钟。
                                        deleteAllAlarm();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                ).setNegativeButton("取消", null).show();  // 为对话框添加一个取消的按钮
                return true;
            }
        });
    }

    // 添加闹钟
    private  void addAlarm(){
        // 实例化一个Calendar类，并取当前时间
        Calendar c = Calendar.getInstance();

        // 弹出时间选择对话框
        new TimePickerDialog(getContext(),0,
            new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND,0);

                    // 当前时间
                    Calendar currentTime = Calendar.getInstance();

                    // 如果设置的闹钟时间比当前时间还小，你不能将闹钟定在过去咯？所以令其往前加一天
                    if(calendar.getTimeInMillis() <= currentTime.getTimeInMillis()){
                        calendar.setTimeInMillis(calendar.getTimeInMillis() + 24*60*60*1000);
                    }

                    // 实例化一个AlarmData类
                    AlarmData ad = new AlarmData(calendar.getTimeInMillis());
                    // 为适配器添加数据
                    adapter.add(ad);
                    // 将数据设置到闹钟管理中
                    /*  第一个参数  RTC操作系统时间为启动时间点，WAKEUP系统休眠时同样执行
                        第二个参数  什么时候启动
                        第三个参数  启动之后什么时间再次启动
                        第四个参数  Pendingintent挂起的Intent,不立即执行
                    */
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            ad.getTime(),
                            5*60*1000,
                            PendingIntent.getBroadcast(getContext(),
                                    ad.getId(),
                                    new Intent(getContext(),
                                            AlarmReceiver.class),
                                    0));
                    Toast.makeText(getContext(), "闹铃设置成功啦"
                            , Toast.LENGTH_SHORT).show();
                    // 添加之后记得保存闹钟列表
                    saveAlarmList();
                }
            },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), true).show();
    }

    // 删除闹钟，传入一个position位置，删除指定项
    private void deleteAlarm(int position){
        // 根据传入的位置参数实例化一个AlarmData
        AlarmData ad = adapter.getItem(position);
        // 从适配器中移除
        adapter.remove(ad);
        // 删除后记得再次保存列表
        saveAlarmList();
        // 记得在闹钟管理中将其取消掉，否则删除后闹钟依旧会激活
        alarmManager.cancel(PendingIntent.getBroadcast(getContext(),ad.getId(),
                new Intent(getContext(), AlarmReceiver.class),0));
    }

    // 删除所有闹钟
    private void deleteAllAlarm(){
        // 获取适配器中的闹钟数量
        int adapterCount =adapter.getCount();   // 为adapter的个数进行计数
        AlarmData ad;
        // 因为每次删除后适配器的数量都会改变，所以需要在上面一次性计算好，不能将其放到for循环中计算
        for(int i = 0; i < adapterCount; i++){
            // 此处括号中不能填i，因为每次移除后第二项又变成第一项
            ad = adapter.getItem(0);       // 每次从第1个开始移除
            adapter.remove(ad);

            saveAlarmList();       // 移除后重新保存列表

            alarmManager.cancel(PendingIntent.getBroadcast(getContext(),ad.getId(),
                    new Intent(getContext(),AlarmReceiver.class),0));   // 取消闹钟的广播
        }
    }

    // 保存闹钟列表
    private void saveAlarmList(){
        Editor editor = getContext().getSharedPreferences(
                AlarmView.class.getName(),
                Context.MODE_PRIVATE).edit();

        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < adapter.getCount(); i++){
            sb.append(adapter.getItem(i).getTime()).append(",");
        }

        if(sb.length() > 1){
            String content = sb.toString().substring(0,sb.length()-1);  // 去掉最后一个逗号

            editor.putString(KEY_ALARM_LIST, content);

            System.out.println(content);  // 调试使用
        }else{  // 如果长度为空，也要提交一个空的上去
            editor.putString(KEY_ALARM_LIST, null);
        }

        // 记得提交
        editor.commit();
    }

    // 读取保存的闹钟列表，在每次加载应用的时候会调用它
    private void readSavedAlarmList(){
        // 实例化共享首选项
        SharedPreferences sp = getContext().getSharedPreferences(
                AlarmView.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST, null);

        if(content != null){
            String[] timeStrings = content.split(",");
            // 遍历每一个字符串，并将其添加到适配器中
            for(String str : timeStrings){
                adapter.add(new AlarmData(Long.parseLong(str)));
            }
        }
    }

    private static class AlarmData{
        //获取时间的标签
        private String timeLable = "";
        private long time = 0;
        private Calendar date;

        //闹钟所要响起的时间
        public AlarmData(long time){
            this.time=time;

            date= Calendar.getInstance();
            date.setTimeInMillis(time);

            timeLable=String.format("%d月%d日 %d:%d",
                    date.get(Calendar.MONTH)+1,//getMonth的返回值是从0开始的
                    date.get(Calendar.DAY_OF_MONTH),
                    date.get(Calendar.HOUR_OF_DAY),
                    date.get(Calendar.MINUTE));
        }
        public long getTime(){
            return time;
        }
        public String getTimeLable(){
            return  timeLable;
        }

        @Override
        public String toString() {
            return getTimeLable();
        }
        public int getId(){
            return (int)(getTime()/1000/60); //精确到分钟，getTime为毫秒
        }
    }
}
