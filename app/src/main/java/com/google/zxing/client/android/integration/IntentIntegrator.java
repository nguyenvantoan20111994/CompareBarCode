/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.integration;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
public final class IntentIntegrator {

  public static final String DEFAULT_TITLE = "Install Barcode Scanner?";
  public static final String DEFAULT_MESSAGE =
      "This application requires Barcode Scanner. Would you like to install it?";
  public static final String DEFAULT_YES = "Yes";
  public static final String DEFAULT_NO = "No";

  // supported barcode formats
  public static final String PRODUCT_CODE_TYPES = "UPC_A,UPC_E,EAN_8,EAN_13";
  public static final String ONE_D_CODE_TYPES = PRODUCT_CODE_TYPES + ",CODE_39,CODE_93,CODE_128";
  public static final String QR_CODE_TYPES = "QR_CODE";
  public static final String ALL_CODE_TYPES = null;

  public IntentIntegrator() {
  }

  public static AlertDialog initiateScan(Activity activity,int requestcode) {
    return initiateScan(activity, DEFAULT_TITLE, DEFAULT_MESSAGE, DEFAULT_YES, DEFAULT_NO,requestcode);
  }

  public static AlertDialog initiateScan(Activity activity,
                                         CharSequence stringTitle,
                                         CharSequence stringMessage,
                                         CharSequence stringButtonYes,
                                         CharSequence stringButtonNo,
                                         int requestcode) {

    return initiateScan(activity,
                        stringTitle,
                        stringMessage,
                        stringButtonYes,
                        stringButtonNo,
                        ALL_CODE_TYPES,
            requestcode
            );
  }

  /**
   * Invokes scanning.
   *
   * @param stringTitle title of dialog prompting user to download Barcode Scanner
   * @param stringMessage text of dialog prompting user to download Barcode Scanner
   * @param stringButtonYes text of button user clicks when agreeing to download
   *  Barcode Scanner (e.g. "Yes")
   * @param stringButtonNo text of button user clicks when declining to download
   *  Barcode Scanner (e.g. "No")
   * @param stringDesiredBarcodeFormats a comma separated list of codes you would
   *  like to scan for.
   * @return an {@link AlertDialog} if the user was prompted to download the app,
   *  null otherwise
   * @throws InterruptedException if timeout expires before a scan completes
   */
  public static AlertDialog initiateScan(Activity activity,
                                         CharSequence stringTitle,
                                         CharSequence stringMessage,
                                         CharSequence stringButtonYes,
                                         CharSequence stringButtonNo,
                                         CharSequence stringDesiredBarcodeFormats,
                                         int requestcode) {
    Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");
    intentScan.addCategory(Intent.CATEGORY_DEFAULT);

    // check which types of codes to scan for
    if (stringDesiredBarcodeFormats != null) {
      // set the desired barcode types
      intentScan.putExtra("SCAN_FORMATS", stringDesiredBarcodeFormats);
    }

    try {
      activity.startActivityForResult(intentScan, requestcode);
      return null;
    } catch (ActivityNotFoundException e) {
      return showDownloadDialog(activity, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
    }
  }

  private static AlertDialog showDownloadDialog(final Activity activity,
                                                CharSequence stringTitle,
                                                CharSequence stringMessage,
                                                CharSequence stringButtonYes,
                                                CharSequence stringButtonNo) {
    AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
    downloadDialog.setTitle(stringTitle);
    downloadDialog.setMessage(stringMessage);
    downloadDialog.setPositiveButton(stringButtonYes, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {
        Uri uri = Uri.parse("market://search?q=pname:com.google.zxing.client.android");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
      }
    });
    downloadDialog.setNegativeButton(stringButtonNo, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {}
    });
    return downloadDialog.show();
  }


  /**
   * <p>Call this from your {@link Activity}'s
   * {@link Activity#onActivityResult(int, int, Intent)} method.</p>
   *
   * @return null if the event handled here was not related to {@link IntentIntegrator}, or
   *  else an {@link IntentResult} containing the result of the scan. If the user cancelled scanning,
   *  the fields will be null.
   */
  public static IntentResult parseActivityResult(int requestCode, int resultCode, Intent intent) {
      if (resultCode == Activity.RESULT_OK) {
        String contents = intent.getStringExtra("SCAN_RESULT");
        String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
        return new IntentResult(contents, formatName);
      } else {
        return new IntentResult(null, null);
      }
  }

  /**
   * See {@link #shareText(Activity, CharSequence, CharSequence, CharSequence, CharSequence, CharSequence)} --
   * same, but uses default English labels.
   */
  public static void shareText(Activity activity, CharSequence text) {
    shareText(activity, text, DEFAULT_TITLE, DEFAULT_MESSAGE, DEFAULT_YES, DEFAULT_NO);
  }

  /**
   * See {@link #shareText(Activity, CharSequence, CharSequence, CharSequence, CharSequence, CharSequence)} --
   * same, but takes string IDs which refer to the {@link Activity}'s resource bundle entries.
   */
  public static void shareText(Activity activity,
                               CharSequence text,
                               int stringTitle,
                               int stringMessage,
                               int stringButtonYes,
                               int stringButtonNo) {
    shareText(activity,
              text,
              activity.getString(stringTitle),
              activity.getString(stringMessage),
              activity.getString(stringButtonYes),
              activity.getString(stringButtonNo));
  }

  /**
   * Shares the given text by encoding it as a barcode, such that another user can
   * scan the text off the screen of the device.
   *
   * @param text the text string to encode as a barcode
   * @param stringTitle title of dialog prompting user to download Barcode Scanner
   * @param stringMessage text of dialog prompting user to download Barcode Scanner
   * @param stringButtonYes text of button user clicks when agreeing to download
   *  Barcode Scanner (e.g. "Yes")
   * @param stringButtonNo text of button user clicks when declining to download
   *  Barcode Scanner (e.g. "No")
   */
  public static void shareText(Activity activity,
                               CharSequence text,
                               CharSequence stringTitle,
                               CharSequence stringMessage,
                               CharSequence stringButtonYes,
                               CharSequence stringButtonNo) {

    Intent intent = new Intent();
    intent.setAction("com.google.zxing.client.android.ENCODE");
    intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
    intent.putExtra("ENCODE_DATA", text);
    try {
      activity.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      showDownloadDialog(activity, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
    }
  }

}
