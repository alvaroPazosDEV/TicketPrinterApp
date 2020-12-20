package com.ticketprinter;

import android.widget.Toast;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.device.PrinterManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.facebook.react.bridge.Promise;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrinterModule extends ReactContextBaseJavaModule {
  private static ReactApplicationContext reactContext;

  PrinterManager mPrinterManager;
  private Handler mPrintHandler;
  private final static int PRNSTS_OK = 0;

  PrinterModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }

  private PrinterManager getPrinterManager() {
    if (mPrinterManager == null) {
      mPrinterManager = new PrinterManager();
      mPrinterManager.open();
    }
    return mPrinterManager;
  }

  @Override
  public String getName() {
    return "PrinterModule";
  }

  @ReactMethod
  public void getStatus(Promise promise) {
    try {
      PrinterManager printerManager = getPrinterManager();
      int ret = printerManager.getStatus();
      promise.resolve(ret);
    } catch(Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void print(String fontName, String data, Promise promise) {
    // new CustomThread().start();
    try {
      PrinterManager printerManager = getPrinterManager();
      int ret = printerManager.getStatus();   //Get printer status
      JSONObject item = new JSONObject(data);
      JSONArray productsArray = item.getJSONArray("skus");
      if (ret == PRNSTS_OK) {
          printerManager.setupPage(384, -1);
          int fontSize = 28;
          int fontStyle = 0x0000;
          int height = 0;


          height += printerManager.drawText("TICKET DE REGALO", 0, height, fontName, 32, false, false, 0) + 10;
          height += printerManager.drawText("Para cambio o servicio técnico,", 0, height, fontName, fontSize, false, false, 0) + 6;
          height += printerManager.drawText("debe presentar este documento", 0, height, fontName, fontSize, false, false, 0) + 20;
          height += printerManager.drawText("COMERCIO/SUCURSAL: "+item.getString("sucursalC"), 0, height, fontName, fontSize, false, false, 0) + 10;
          height += printerManager.drawText(item.getString("sucursalS")+"/"+item.getString("caja")+" "+item.getString("fecha")+" "+item.getString("transaccion"), 0, height, fontName, fontSize, false, false, 0) + 20;
          height += printerManager.drawText("BOLETA REFERENCIA: "+item.getString("boleta"), 0, height, fontName, fontSize, false, false, 0) + 10;
          printerManager.drawBarcode(item.getString("barcode"), 0, height, 20, 4, 130, 0);
          height += 170;
          height += printerManager.drawText("Ripley", 0, height, fontName, fontSize, false, false, 0) + 10;
          height += printerManager.drawText("NRO. DE UNIDADES: "+item.getString("unidades"), 0, height, fontName, fontSize, false, false, 0) + 20;
          
          for(int i=0; i<productsArray.length(); i++) {
            JSONObject product = productsArray.getJSONObject(i);
            String sku = product.getString("sku");
            height += printerManager.drawText(sku, 0, height, fontName, fontSize, false, false, 0) + 6;
          }
          height = 0;
          printerManager.printPage(0);  //Execution printing
          printerManager.paperFeed(16);  //paper feed
          promise.resolve(0);
      } else {
        promise.resolve(ret);
      }
    } catch(Exception e) {
      promise.reject(e);
    }
  }

}
