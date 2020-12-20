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
      JSONArray productsArray = item.getJSONArray("products");
      if (ret == PRNSTS_OK) {
          printerManager.setupPage(384, -1);
          int fontSize = 20;
          int fontStyle = 0x0000;
          int height = 0;

          printerManager.drawBarcode(item.getString("oc"), 0, height, 20, 4, 130, 0);
          height += 170;
          height += printerManager.drawText("O. Compra N° "+item.getString("oc"), 0, height, fontName, 28, true, false, 0) + 15;
          height += printerManager.drawText("Ticket: "+item.getString("ticket"), 0, height, fontName, fontSize, false, false, 0);
          height += printerManager.drawText("Sucursal de despacho: ", 0, height, fontName, fontSize, false, false, 0);
          height += printerManager.drawText(item.getString("place"), 0, height, fontName, fontSize, false, false, 0);
          height += printerManager.drawText("F. Venta: "+item.getString("saleDate"), 0, height, fontName, fontSize, false, false, 0);
          height += printerManager.drawText("F. Despacho: "+item.getString("dispatchDate"), 0, height, fontName, fontSize, false, false, 0);
          height += printerManager.drawText("F. Picking: "+item.getString("pickingDate"), 0, height, fontName, fontSize, false, false, 0);
          height += printerManager.drawText("DNI del picker: "+item.getString("pickerDni"), 0, height, fontName, fontSize, false, false, 0) + 20;
          height += printerManager.drawText("Cliente", 0, height, fontName, 24, true, false, 0);
          height += printerManager.drawText(item.getString("clientName"), 0, height, fontName, fontSize, false, false, 0);
          height += printerManager.drawText("DNI: "+item.getString("clientDni"), 0, height, fontName, fontSize, false, false, 0) + 20;
          height += printerManager.drawText("Productos ("+productsArray.length()+")", 0, height, fontName, 24, true, false, 0);
          for(int i=0; i<productsArray.length(); i++) {
            JSONObject product = productsArray.getJSONObject(i);
            String cud = product.getString("cud");
            String quantity = product.getString("quantity");
            String sku = product.getString("sku");
            printerManager.drawText("CUD: "+cud, 0, height, fontName, fontSize, false, false, 0);
            height += printerManager.drawText("x"+quantity, 360, height, fontName, fontSize, true, false, 0);
            height += printerManager.drawText("SKU: "+sku, 0, height, fontName, fontSize, false, false, 0);
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
