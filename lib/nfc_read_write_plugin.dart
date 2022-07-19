import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class NfcReadWritePlugin {
  ///方法通道
  static const MethodChannel _channel = MethodChannel("nfc_read_write");

  /// 声明监听回调通道
  static const EventChannel _eventChannel =
      EventChannel("nfc_read_write/event");

  /// 监听器
  static late Stream<dynamic> _onListener;

  /// 初始化监听
  static Stream<dynamic> onChange({bool type = true}) {
    _onListener = _eventChannel.receiveBroadcastStream(type);
    return _onListener;
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<dynamic> initService(String passwordA, String passwordB) async {
    Map<String, String> map = {};
    map["passwordA"] = passwordA;
    map["passwordB"] = passwordB;
    dynamic re = await _channel.invokeMethod('initService', map);
    return re;
  }

  static Future<dynamic> writeBlock(int blockIndex, String data,
      {bool encrypt = false}) async {
    Map<String, dynamic> map = {};
    map["blockIndex"] = blockIndex;
    map["data"] = data;
    map["encrypt"] = encrypt;
    final dynamic version = await _channel.invokeMethod('writeBlock', map);
    return version;
  }

  static Future<dynamic> writeBlockByBytes(
    int blockIndex,
    List<int> data,
  ) async {
    Map<String, dynamic> map = {};
    map["blockIndex"] = blockIndex;
    map["data"] = data;
    final dynamic version =
        await _channel.invokeMethod('writeBlockByBytes', map);
    return version;
  }

  static Future<dynamic> readAll({bool decrypt = false}) async {
    final dynamic data =
        await _channel.invokeMethod('readAll', {"decrypt": decrypt});
    return data;
  }

  static Future<dynamic> readSector(int sectorIndex,
      {bool decrypt = false}) async {
    Map<String, dynamic> map = {};
    map["sectorIndex"] = sectorIndex;
    map["decrypt"] = decrypt;
    final dynamic version = await _channel.invokeMethod('readSector', map);
    return version;
  }

  static Future<dynamic> readBlock(int blockIndex,
      {bool decrypt = false}) async {
    Map<String, dynamic> map = {};
    map["blockIndex"] = blockIndex;
    map["decrypt"] = decrypt;
    final dynamic version = await _channel.invokeMethod('readBlock', map);
    return version;
  }

  static Future<dynamic> readBlockBytes(
    int blockIndex,
  ) async {
    Map<String, dynamic> map = {};
    map["blockIndex"] = blockIndex;
    final dynamic version = await _channel.invokeMethod('readBlockBytes', map);
    return version;
  }

  static Future<dynamic> decrypt(
      String message, Uint8List arrKey, Uint8List arrIV) async {
    Map<String, dynamic> map = {};
    map["message"] = message;
    map["arrKey"] = arrKey;
    map["arrIV"] = arrIV;
    final dynamic version =
        await _channel.invokeMethod('decrypt', map);
    return version;
  }

  static Future<dynamic> encrypt(
      String message, Uint8List arrKey, Uint8List arrIV) async {
    Map<String, dynamic> map = {};
    map["message"] = message;
    map["arrKey"] = arrKey;
    map["arrIV"] = arrIV;
    final dynamic version =
        await _channel.invokeMethod('encrypt', map);
    return version;
  }

  /// 数据监听
  static eventListen(
      {bool type = true, required Function onEvent, Function? onError}) async {
    onChange(type: type).listen(onEvent as void Function(dynamic),
        onError: onError, onDone: null, cancelOnError: null);
  }
}
