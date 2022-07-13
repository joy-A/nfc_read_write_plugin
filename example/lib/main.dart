import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:nfc_read_write_plugin/nfc_read_write_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  dynamic result;
  @override
  void initState() {
    super.initState();
    initPlatformState();
    initEvent();
  }

  initEvent() async {
    await NfcReadWritePlugin.eventListen(onEvent: (message) {
      setState(() {
        result = message;
      });
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await NfcReadWritePlugin.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: SingleChildScrollView(
          child: Column(
            children: [
              Text('Running on: $_platformVersion\n'),
              Text('事件监听：${result?.toString()}'),
              ElevatedButton(
                onPressed: () async {
                  var data = await NfcReadWritePlugin.initService(
                      "42374D4C3546", "B10A23D18F20");
                  debugPrint("dart...$data");
                },
                child: const Text("initService"),
              ),
              ElevatedButton(
                onPressed: () async {
                  var data = await NfcReadWritePlugin.readAll(decrypt: true);

                  debugPrint("dart...$data");
                },
                child: const Text("readAll decrypt: true"),
              ),
              ElevatedButton(
                onPressed: () async {
                  var data = await NfcReadWritePlugin.readAll();
                  debugPrint("dart...$data");
                },
                child: const Text("readAll"),
              ),
              ElevatedButton(
                onPressed: () async {
                  var data = await NfcReadWritePlugin.writeBlock(40, "你好测试一下");
                  debugPrint("dart...$data");
                },
                child: const Text("writeBlock"),
              ),
              ElevatedButton(
                onPressed: () async {
                  var data = await NfcReadWritePlugin.readSector(10);
                  debugPrint("dart...$data");
                },
                child: const Text("readSector"),
              ),
              ElevatedButton(
                onPressed: () async {
                  var data =
                      await NfcReadWritePlugin.readSector(10, decrypt: true);
                  debugPrint("dart...$data");
                },
                child: const Text("readSector decrypt: true"),
              ),
              ElevatedButton(
                onPressed: () async {
                  var data = await NfcReadWritePlugin.writeBlock(40, "你好测试一下",
                      encrypt: true);
                  debugPrint("dart...$data");
                },
                child: const Text("writeBlock encrypt: true"),
              ),
              ElevatedButton(
                onPressed: () async {
                  var data =
                      await NfcReadWritePlugin.readBlock(40, decrypt: true);
                  debugPrint("dart...$data");
                },
                child: const Text("readBlock decrypt: true"),
              ),
              ElevatedButton(
                onPressed: () async {
                  var data = await NfcReadWritePlugin.readBlock(
                    40,
                  );
                  debugPrint("dart...$data");
                },
                child: const Text("readBlock  "),
              )
            ],
          ),
        ),
      ),
    );
  }
}
