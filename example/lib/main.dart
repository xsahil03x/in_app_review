import 'dart:async';

import 'package:flutter/material.dart';
import 'package:in_app_review/in_app_review.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  InAppReview.init();
  runApp(MyApp());

}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    InAppReview.preWarmReview();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Running on: $_platformVersion\n'),
              RaisedButton(
                onPressed: () async {
                  await InAppReview.launchReview();
                  print("Completed");
                },
                child: Text('Launch Review'),
              )
            ],
          ),
        ),
      ),
    );
  }
}
