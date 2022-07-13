package com.nfc_read_write.nfc_read_write_plugin;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import kotlin.UByteArray;

/** NfcReadWritePlugin */
public class NfcReadWritePlugin implements FlutterPlugin, EventChannel.StreamHandler , PluginRegistry.NewIntentListener, MethodCallHandler , ActivityAware {

  private MethodChannel channel;

  private Activity activity;
  ///事件
  public static EventChannel.EventSink _events;
  ///要和Dart层对应
  private static final String METHOD_CHANNEL = "nfc_read_write";
  private static final String EVENT_CHANNEL = "nfc_read_write/event";
  private Tag tag;
  private MifareClassic mfc;
  protected NfcAdapter nfcAdapter;
  private ActivityPluginBinding binding = null;
  protected ReaderService readerService;
  private LifeCycleObserver observer;

  // This is null when not using v2 embedding;
  private Lifecycle lifecycle;
  private class LifeCycleObserver
          implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private final Activity thisActivity;

    LifeCycleObserver(Activity activity) {
      this.thisActivity = activity;
    }
    protected String[][] techList;
    protected IntentFilter[] intentFilters;
    protected PendingIntent pendingIntent;

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {}

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {}

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
      techList = new String[][] { new String[] { NfcV.class.getName() },
              new String[] { NfcA.class.getName() } };
      intentFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED), };
      // 创建一个 PendingIntent 对象, 这样Android系统就能在一个tag被检测到时定位到这个对象
      pendingIntent = PendingIntent.getActivity(thisActivity, 0,
              new Intent(thisActivity, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
      NfcAdapter.getDefaultAdapter(thisActivity).enableForegroundDispatch(thisActivity, pendingIntent, intentFilters, techList);

    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
      NfcAdapter.getDefaultAdapter(thisActivity).enableForegroundDispatch(thisActivity, pendingIntent, intentFilters, techList);

    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
      onActivityStopped(thisActivity);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
      onActivityDestroyed(thisActivity);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
      if (thisActivity == activity && activity.getApplicationContext() != null) {
        ((Application) activity.getApplicationContext())
                .unregisterActivityLifecycleCallbacks(
                        this); // Use getApplicationContext() to avoid casting failures
      }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    nfcAdapter  =NfcAdapter.getDefaultAdapter(flutterPluginBinding.getApplicationContext());
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), METHOD_CHANNEL);
    //事件注册
    EventChannel eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(),EVENT_CHANNEL);
    eventChannel.setStreamHandler(this);
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

    Object data ;
    Integer sectorIndex =1;
    Integer blockIndex =0;
    String cardPasswordA="";
    String cardPasswordB="";
    byte [] bytes;
      boolean decrypt = false;
      boolean encrypt =false;
    if(call.argument("passwordA")!=null){
      cardPasswordA =call.argument("passwordA");
    } if(call.argument("passwordB")!=null){
      cardPasswordB =call.argument("passwordB");
    }
    if(call.argument("decrypt")!=null){
      decrypt =call.argument("decrypt");
      System.out.println("decrypt:::"+decrypt);

    }
    if(call.argument("encrypt")!=null){
      encrypt =call.argument("encrypt");
      System.out.println("encrypt:::"+encrypt);

    }
    if (call .argument("sectorIndex")!=null){
      sectorIndex = Integer.parseInt(call.argument("sectorIndex").toString());
      System.out.println("sectorIndex:::"+sectorIndex);

    }
    if (call.argument("blockIndex")!=null){
      blockIndex = Integer.parseInt(call.argument("blockIndex").toString());
      System.out.println("bolckIndex:::"+blockIndex);
    }
    data = call.argument("data");
    if (activity == null) {
      result.error("500", "Cannot call method when not attached to activity", null);
      return;
    }

    nfcAdapter = NfcAdapter.getDefaultAdapter(this.activity);

    if (nfcAdapter.isEnabled() != true  ) {
      _events.error("404", "NFC not available", null);
      return;
    }
    switch (call.method){
      case  "getPlatformVersion":
        result.success("当前Android " + Build.VERSION.RELEASE);
        break;
      case "initService":
         readerService = new ReaderService(cardPasswordA,cardPasswordB);

        break;
      case "readAll":
        if(readerService==null ){
        result.error("0","请先调用initServer初始化服务!",null);
      } else {
        readAll(decrypt );
      }
        break;
      case "writeBlock":
        if(readerService==null ){
        result.error("0","请先调用initServer初始化服务!",null);

      } else {
        if (writeBlock(blockIndex,data,encrypt,result )){

        }
        }
        break;
      case "readBlock":
        if(readerService==null ){
        result.error("0","请先调用initServer初始化服务!",null);
      } else {
        readBlock(blockIndex,decrypt,result);
      }
        break;
      case "readSector":
       if(readerService==null ){
        result.error("0","请先调用initServer初始化服务!",null);

      } else {
          readSector(sectorIndex,decrypt,result);
      }
        break;
      case "writeBlockByBytes":
        if(readerService==null ){
          result.error("0","请先调用initServer初始化服务!",null);

        }else
        {

          writeBlockByBytes(blockIndex, (byte[]) data,result);

        }break;
      case "readBlockBytes":
        if(readerService==null ){
          result.error("0","请先调用initServer初始化服务!",null);

        }else
        {

          readBlockBytes(blockIndex, result);

        }
      default:
        result.notImplemented();

    }
  }
  private void readAll( boolean decrypt  ){
    byte[] uid = tag.getId();
    String cardId =  Tools.Bytes2HexString(uid, uid.length);
    System.out.println("cardId======================"+cardId+"============================");

    mfc = MifareClassic.get(tag);
    System.out.println("tag================"+tag+"================");

    System.out.println("mfc================"+mfc+"================");

    SparseArray<SparseArray<String>>   info =new SparseArray<>();
    try {
      info= readerService.readAll(mfc,decrypt) ;
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("================"+info+"================");
    _events.success(ResultUtil.ok(3000,"读卡成功",info.toString()));

  }
  private boolean writeBlock(Integer blockIndex, Object data,boolean encrypt  ,Result result)   {
    boolean re = false;
    try {
      byte[] uid = tag.getId();
      String cardId =  Tools.Bytes2HexString(uid, uid.length);
      System.out.println("cardId======================"+cardId+"============================");
        re = readerService.writeBlock(MifareClassic.get(tag),blockIndex,data.toString() ,encrypt);
        if (re){
          result.success(ResultUtil.ok(2000,"写块成功"));

        }else {
          result.success(ResultUtil.fail(2001,"写失败"));

        }
     } catch (Exception e) {
      e.printStackTrace();
      re= false;
      result.success(ResultUtil.fail(2001,e.getMessage()));

    }
    return  re;
  }
  private boolean writeBlockByBytes(Integer blockIndex, byte[] data ,Result result)   {
    boolean re = false;
    try {
      byte[] uid = tag.getId();
      String cardId =  Tools.Bytes2HexString(uid, uid.length);
      System.out.println("cardId======================"+cardId+"============================");
      re = readerService.writeBlockByBytes(MifareClassic.get(tag),blockIndex,data );
      if (re){
        result.success(ResultUtil.ok(2000,"写块成功"));

      }else {
        result.success(ResultUtil.fail(2001,"写失败"));

      }
    } catch (Exception e) {
      e.printStackTrace();
      re= false;
      result.success(ResultUtil.fail(2001,e.getMessage()));

    }
    return  re;
  }
  private void readSector(Integer sectorIndex, boolean decrypt ,Result result )   {
    SparseArray<SparseArray<String>>   data ;
    try {
      byte[] uid = tag.getId();
      String cardId =  Tools.Bytes2HexString(uid, uid.length);
      System.out.println("cardId======================"+cardId+"============================");
       data =readerService.readSector(MifareClassic.get(tag),sectorIndex ,decrypt);
      _events.success(ResultUtil.ok(3000,"读卡成功",data.toString()));
    } catch (Exception e) {
      e.printStackTrace();
      _events.success(ResultUtil.fail(3001,"读卡失败"+e.getMessage() ));
    }
  }
  private void readBlock(Integer blockIndex, boolean decrypt,Result result)   {
    Map<Integer,String> data ;
    try {
      byte[] uid = tag.getId();
      String cardId =  Tools.Bytes2HexString(uid, uid.length);
      System.out.println("cardId======================"+cardId+"============================");
       data =readerService.readBlock(MifareClassic.get(tag),blockIndex ,decrypt);
      result.success(ResultUtil.ok(3000,"请求成功",data ));
    } catch (Exception e) {
      e.printStackTrace();
      result.success(ResultUtil.fail(3001,"读卡失败"+e.getMessage() ));
    }
  }
  private void readBlockBytes(Integer blockIndex,  Result result)   {
     byte[] data ;
    try {
      byte[] uid = tag.getId();
      String cardId =  Tools.Bytes2HexString(uid, uid.length);
      System.out.println("cardId======================"+cardId+"============================");
      data =readerService.readBlockBytes(MifareClassic.get(tag),blockIndex  );
      result.success(ResultUtil.ok(3000,"读卡成功",data));
    } catch (Exception e) {
      e.printStackTrace();
      result.success(ResultUtil.fail(3001,"读卡失败"+e.getMessage() ));
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);

    this.activity = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull @NotNull ActivityPluginBinding activityPluginBinding) {
    if (this.activity != null) return;
    this.binding = activityPluginBinding;
    this.activity = activityPluginBinding.getActivity();
    observer = new LifeCycleObserver(this.activity);
    lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(this.binding);
    lifecycle.addObserver(observer);
    ///监听Intent
    activityPluginBinding.addOnNewIntentListener(this);

  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull @NotNull ActivityPluginBinding activityPluginBinding) {
    onAttachedToActivity(activityPluginBinding);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
    if(binding != null) {
      binding.removeOnNewIntentListener(this);
    }
  }
  @Override
  public boolean onNewIntent(Intent intent) {
    if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
      this. tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
      System.out.println("================"+tag.toString()+"================");
      if (tag != null) {
        Log.i("TAG", Arrays.toString(tag.getTechList()));
        findCardSuccess();
      }
    }
    return false;
  }


  public void findCardSuccess( ) {
if (readerService==null){
  _events.error("0","请先调用initSevice",null);
  return;
}
    byte[] uid = tag.getId();
    String cardId =  Tools.Bytes2HexString(uid, uid.length);
    readerService.setKV(cardId);
  if(_events!=null){
    _events.success(ResultUtil.ok("读卡成功!",cardId));

  }
  }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink eventSink) {
    eventSink.success(String.format("插件启动监听成功, 当前SDK版本: %s", "version"));
    if( _events == null ){
      _events = eventSink;
    }
  }

  @Override
  public void onCancel(Object arguments) {
    if( _events != null){
      _events = null;
    }
  }


}
