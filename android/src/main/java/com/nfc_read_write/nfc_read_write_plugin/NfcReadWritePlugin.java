package com.nfc_read_write.nfc_read_write_plugin;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

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

    String data = "";
    Integer sectorIndex =1;
    Integer blockIndex =0;
    String cardPasswordA="";
    String cardPasswordB="";
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
        if (_events==null){
          result.error("0","请先监听插件!",null);
      }else{
          readerService = new ReaderService(cardPasswordA,cardPasswordB);
          byte[] uid = tag.getId();
          String cardId =  Tools.Bytes2HexString(uid, uid.length);
          readerService.setKV(cardId);
          result.success("初始化服务成功,请刷卡!");
        }
        break;
      case "readAll":
        if (_events==null){
          result.error("0","请先监听插件!",null);
        }if(readerService==null ){
        result.error("0","请先调用initServer初始化服务!",null);
      } else {
        readAll(decrypt );
      }
        break;
      case "writeBlock":
        if (_events==null){
          result.error("0","请先监听插件!",null);
        }if(readerService==null ){
        result.error("0","请先调用initServer初始化服务!",null);

      } else {
        writeBlock(blockIndex,data,encrypt );
        }
        break;
      case "readBlock":
        if (_events==null){
        result.error("0","请先监听插件!",null);
      }if(readerService==null ){
        result.error("0","请先调用initServer初始化服务!",null);
      } else {
        readBlock(blockIndex,decrypt);
      }
        break;
      case "readSector":
        if (_events==null){
        result.error("0","请先监听插件!",null);
      }if(readerService==null ){
        result.error("0","请先调用initServer初始化服务!",null);

      } else {
          readSector(sectorIndex,decrypt);
      }
        break;
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

    _events.success(ResultUtil.ok("读卡成功!",info.toString()));
  }
  private void writeBlock(Integer blockIndex, String data,boolean encrypt  )   {
    try {
      byte[] uid = tag.getId();
      String cardId =  Tools.Bytes2HexString(uid, uid.length);
      System.out.println("cardId======================"+cardId+"============================");
       readerService.writeBlock(MifareClassic.get(tag),blockIndex,data ,encrypt);
      _events.success(ResultUtil.ok("请求成功"));
    } catch (Exception e) {
      e.printStackTrace();
      _events.error("0",e.getMessage(),null);
    }
  }
  private void readSector(Integer sectorIndex, boolean decrypt  )   {
    SparseArray<SparseArray<String>>   data ;
    try {
      byte[] uid = tag.getId();
      String cardId =  Tools.Bytes2HexString(uid, uid.length);
      System.out.println("cardId======================"+cardId+"============================");
       data =readerService.readSector(MifareClassic.get(tag),sectorIndex ,decrypt);
      _events.success(ResultUtil.ok("请求成功",data.toString()));
    } catch (Exception e) {
      e.printStackTrace();
      _events.error("0","失败",e.getMessage());
    }
  }
  private void readBlock(Integer blockIndex, boolean decrypt)   {
     SparseArray<String>  data ;
    try {
      byte[] uid = tag.getId();
      String cardId =  Tools.Bytes2HexString(uid, uid.length);
      System.out.println("cardId======================"+cardId+"============================");
       data =readerService.readBlock(MifareClassic.get(tag),blockIndex ,decrypt);
      _events.success(ResultUtil.ok("请求成功",data.toString()));
    } catch (Exception e) {
      e.printStackTrace();
      _events.error("0","失败",e.getMessage());
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
     _events.success(ResultUtil.ok("读卡成功!",cardId));

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
