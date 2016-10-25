/*
 * Copyright (C) 2008 ZXing authors
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

package com.google.zxing.client.android.result;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.zxing.Result;
import com.google.zxing.client.android.LocaleManager;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

/**
 * A base class for the Android-specific barcode handlers. These allow the app to polymorphically
 * suggest the appropriate actions for each data type.
 *
 * This class also contains a bunch of utility methods to take common actions like opening a URL.
 * They could easily be moved into a helper object, but it can't be static because the Activity
 * instance is needed to launch an intent.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public abstract class ResultHandler {

  private static final String TAG = ResultHandler.class.getSimpleName();

  private static final DateFormat DATE_FORMAT;
  static {
    DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    // For dates without a time, for purposes of interacting with Android, the resulting timestamp needs to
    // be midnight of that day in GMT (http://code.google.com/p/android/issues/detail?id=8330)
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
  }
  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

  public static final int MAX_BUTTON_COUNT = 4;

  private final ParsedResult result;
  private final Activity activity;
  private final Result rawResult;
  private final String customProductSearch;

  ResultHandler(Activity activity, ParsedResult result) {
    this(activity, result, null);
  }

  ResultHandler(Activity activity, ParsedResult result, Result rawResult) {
    this.result = result;
    this.activity = activity;
    this.rawResult = rawResult;
    this.customProductSearch = parseCustomSearchURL();
  }

  public ParsedResult getResult() {
    return result;
  }

  boolean hasCustomProductSearch() {
    return customProductSearch != null;
  }

  /**
   * Indicates how many buttons the derived class wants shown.
   *
   * @return The integer button count.
   */
  public abstract int getButtonCount();

  /**
   * The text of the nth action button.
   *
   * @param index From 0 to getButtonCount() - 1
   * @return The button text as a resource ID
   */
  public abstract int getButtonText(int index);


  /**
   * Execute the action which corresponds to the nth button.
   *
   * @param index The button that was clicked.
   */
  public abstract void handleButtonPress(int index);

  /**
   * Create a possibly styled string for the contents of the current barcode.
   *
   * @return The text to be displayed.
   */
  public CharSequence getDisplayContents() {
    String contents = result.getDisplayResult();
    return contents.replace("\r", "");
  }

  /**
   * A string describing the kind of barcode that was found, e.g. "Found contact info".
   *
   * @return The resource ID of the string.
   */
  public abstract int getDisplayTitle();

  /**
   * A convenience method to get the parsed type. Should not be overridden.
   *
   * @return The parsed type, e.g. URI or ISBN
   */
  public final ParsedResultType getType() {
    return result.getType();
  }

  /**
   * Sends an intent to create a new calendar event by prepopulating the Add Event UI. Older
   * versions of the system have a bug where the event title will not be filled out.
   *
   * @param summary A description of the event
   * @param start   The start time as yyyyMMdd or yyyyMMdd'T'HHmmss or yyyyMMdd'T'HHmmss'Z'
   * @param end     The end time as yyyyMMdd or yyyyMMdd'T'HHmmss or yyyyMMdd'T'HHmmss'Z'
   * @param location a text description of the event location
   * @param description a text description of the event itself
   */
  final void addCalendarEvent(String summary,
                              String start,
                              String end,
                              String location,
                              String description) {
    Intent intent = new Intent(Intent.ACTION_EDIT);
    intent.setType("vnd.android.cursor.item/event");
    intent.putExtra("beginTime", calculateMilliseconds(start));
    boolean allDay = start.length() == 8;
    if (allDay) {
      intent.putExtra("allDay", true);
    } else {
      if (end == null) {
        end = start;
      }
      long endMilliseconds = calculateMilliseconds(end);
      intent.putExtra("endTime", endMilliseconds);
    }
    intent.putExtra("title", summary);
    intent.putExtra("eventLocation", location);
    intent.putExtra("description", description);
    launchIntent(intent);
  }

  private static long calculateMilliseconds(String when) {
    if (when.length() == 8) {
      // Only contains year/month/day
      Date date;
      synchronized (DATE_FORMAT) {
        date = DATE_FORMAT.parse(when, new ParsePosition(0));
      }
      // Note this will be relative to GMT, not the local time zone
      return date.getTime();
    } else {
      // The when string can be local time, or UTC if it ends with a Z
      Date date;
      synchronized (DATE_TIME_FORMAT) {
       date = DATE_TIME_FORMAT.parse(when.substring(0, 15), new ParsePosition(0));
      }
      long milliseconds = date.getTime();
      if (when.length() == 16 && when.charAt(15) == 'Z') {
        Calendar calendar = new GregorianCalendar();
        int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        milliseconds += offset;
      }
      return milliseconds;
    }
  }

  /**
   * Do a geo search using the address as the query.
   *
   * @param address The address to find
   * @param title An optional title, e.g. the name of the business at this address
   */
  final void searchMap(String address, String title) {
    String query = address;
    if (title != null && title.length() > 0) {
      query = query + " (" + title + ')';
    }
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(query))));
  }

  final void getDirections(double latitude, double longitude) {
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google." +
        LocaleManager.getCountryTLD() + "/maps?f=d&daddr=" + latitude + ',' + longitude)));
  }

  // Uses the mobile-specific version of Product Search, which is formatted for small screens.
  final void openProductSearch(String upc) {
    Uri uri = Uri.parse("http://www.google." + LocaleManager.getProductSearchCountryTLD() +
        "/m/products?q=" + upc + "&source=zxing");
    launchIntent(new Intent(Intent.ACTION_VIEW, uri));
  }

  final void openBookSearch(String isbn) {
    Uri uri = Uri.parse("http://books.google." + LocaleManager.getBookSearchCountryTLD() +
        "/books?vid=isbn" + isbn);
    launchIntent(new Intent(Intent.ACTION_VIEW, uri));
  }

  final void openURL(String url) {
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
  }

  final void webSearch(String query) {
    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
    intent.putExtra("query", query);
    launchIntent(intent);
  }

  void launchIntent(Intent intent) {
    if (intent != null) {
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      Log.d(TAG, "Launching intent: " + intent + " with extras: " + intent.getExtras());
      try {
        activity.startActivity(intent);
      } catch (ActivityNotFoundException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("");
        builder.setPositiveButton("", null);
        builder.show();
      }
    }
  }

  @SuppressWarnings("unused")
private static void putExtra(Intent intent, String key, String value) {
    if (value != null && value.length() > 0) {
      intent.putExtra(key, value);
    }
  }

  protected void showNotOurResults(int index, AlertDialog.OnClickListener proceedListener) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    if (prefs.getBoolean(PreferencesActivity.KEY_NOT_OUR_RESULTS_SHOWN, false)) {
      // already seen it, just proceed
      proceedListener.onClick(null, index);
    } else {
      // note the user has seen it
      prefs.edit().putBoolean(PreferencesActivity.KEY_NOT_OUR_RESULTS_SHOWN, true).commit();
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setMessage("���ڵ� �ν��� �����Ͽ����ϴ�");
      builder.setPositiveButton("Ȯ��", proceedListener);
      builder.show();
    }
  }

  private String parseCustomSearchURL() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    String customProductSearch = prefs.getString(PreferencesActivity.KEY_CUSTOM_PRODUCT_SEARCH, null);
    if (customProductSearch != null && customProductSearch.trim().length() == 0) {
      return null;
    }
    return customProductSearch;
  }

  String fillInCustomSearchURL(String text) {
    String url = customProductSearch.replace("%s", text);
    if (rawResult != null) {
      url = url.replace("%f", rawResult.getBarcodeFormat().toString());
    }
    return url;
  }

}