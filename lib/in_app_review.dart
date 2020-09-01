import 'package:flutter/services.dart';

class InAppReview {
  static const MethodChannel _channel = const MethodChannel('in_app_review');

  static const String _init = 'init';
  static const String _preWarmReview = 'preWarmReview';
  static const String _launchReview = 'launchReview';

  static Future<void> init() => _channel.invokeMethod(_init);

  static Future<void> preWarmReview() => _channel.invokeMethod(_preWarmReview);

  static Future<void> launchReview() => _channel.invokeMethod(_launchReview);
}
