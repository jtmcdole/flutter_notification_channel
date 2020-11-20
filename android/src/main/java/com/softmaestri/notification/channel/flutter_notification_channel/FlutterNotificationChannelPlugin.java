package com.softmaestri.notification.channel.flutter_notification_channel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static androidx.core.content.ContextCompat.getSystemService;

/** FlutterNotificationChannelPlugin */
public class FlutterNotificationChannelPlugin implements FlutterPlugin, MethodCallHandler {

  static String TAG = "ChannelPlugin";
  private MethodChannel channel;
  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_notification_channel");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    String methodName = call.method;
    Log.i(TAG, methodName);
    if (methodName.equals("registerNotificationChannel")) {
      Log.i(TAG, "Version code is: " + Build.VERSION.SDK_INT);
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Log.i(TAG, "Version code is good, start registering...");
        try {
          String id = call.argument("id");
          String name = call.argument("name");
          String description = call.argument("description");
          int importance = (int)call.argument("importance");
          NotificationChannel notificationChannel =
                  new NotificationChannel(id, name, importance);
          notificationChannel.setDescription(description);

          NotificationManager notificationManager =
                  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

          notificationManager.createNotificationChannel(notificationChannel);
          result.success(
        "Notification channel with id: " + id +
              ", name: " + name +
              ", description: " + description +
              ", importance: " + importance + 
              " registered successfully!"
          );
        }
        catch (Exception e) {
          Log.e(TAG, e.getMessage());
          result.success("Could not register channel: " + e.getMessage());
        }
      } else {
        result.success("Android version code must be at least Oreo");
      }
    }
    else {
      Log.i(TAG, "Method " + methodName + " is not supported!");
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}