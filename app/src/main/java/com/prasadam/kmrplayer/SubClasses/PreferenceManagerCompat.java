/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.prasadam.kmrplayer.SubClasses;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class PreferenceManagerCompat {
	
	private static final String TAG = PreferenceManagerCompat.class.getSimpleName();

    interface OnPreferenceTreeClickListener {
        /**
         * Called when a preference in the tree rooted at this
         * {@link PreferenceScreen} has been clicked.
         * 
         * @param preferenceScreen The {@link PreferenceScreen} that the
         *        preference is located in.
         * @param preference The preference that was clicked.
         * @return Whether the click was handled.
         */
        boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference);
    }
    
	static PreferenceManager newInstance(Activity activity, int firstRequestCode) {
		try {
			Constructor<PreferenceManager> c = PreferenceManager.class.getDeclaredConstructor(Activity.class, int.class);
			c.setAccessible(true);
			return c.newInstance(activity, firstRequestCode);
		} catch (Exception e) {
			Log.w(TAG, "Couldn't call constructor PreferenceManager by reflection", e);
		}
		return null;
	}

    static void setFragment(PreferenceManager manager, PreferenceFragment fragment) {
    	// stub
    }

	static void setOnPreferenceTreeClickListener(PreferenceManager manager, final OnPreferenceTreeClickListener listener) {
		try {
			Field onPreferenceTreeClickListener = PreferenceManager.class.getDeclaredField("mOnPreferenceTreeClickListener");
			onPreferenceTreeClickListener.setAccessible(true);
			if (listener != null) {
				Object proxy = Proxy.newProxyInstance(
						onPreferenceTreeClickListener.getType().getClassLoader(),
						new Class[] { onPreferenceTreeClickListener.getType() },
						new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) {
						if (method.getName().equals("onPreferenceTreeClick")) {
							return Boolean.valueOf(listener.onPreferenceTreeClick((PreferenceScreen) args[0], (Preference) args[1]));
						} else {
							return null;
						}
					}
				});
				onPreferenceTreeClickListener.set(manager, proxy);
			} else {
				onPreferenceTreeClickListener.set(manager, null);
			}
		} catch (Exception e) {
			Log.w(TAG, "Couldn't set PreferenceManager.mOnPreferenceTreeClickListener by reflection", e);
		}
	}
	static PreferenceScreen inflateFromIntent(PreferenceManager manager, Intent intent, PreferenceScreen screen) {
		try {
            Method m = PreferenceManager.class.getDeclaredMethod("inflateFromIntent", Intent.class, PreferenceScreen.class);
            m.setAccessible(true);
            PreferenceScreen prefScreen = (PreferenceScreen) m.invoke(manager, intent, screen);
            return prefScreen;
        } catch (Exception e) {
			Log.w(TAG, "Couldn't call PreferenceManager.inflateFromIntent by reflection", e);
		}
		return null;
	}
	static PreferenceScreen inflateFromResource(PreferenceManager manager, Activity activity, int resId, PreferenceScreen screen) {
		try {
            Method m = PreferenceManager.class.getDeclaredMethod("inflateFromResource", Context.class, int.class, PreferenceScreen.class);
            m.setAccessible(true);
            PreferenceScreen prefScreen = (PreferenceScreen) m.invoke(manager, activity, resId, screen);
            return prefScreen;
        } catch (Exception e) {
			Log.w(TAG, "Couldn't call PreferenceManager.inflateFromResource by reflection", e);
		}
		return null;
	}
	static PreferenceScreen getPreferenceScreen(PreferenceManager manager) {
		try {
            Method m = PreferenceManager.class.getDeclaredMethod("getPreferenceScreen");
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(manager);
        } catch (Exception e) {
			Log.w(TAG, "Couldn't call PreferenceManager.getPreferenceScreen by reflection", e);
		}
		return null;
	}
	static void dispatchActivityResult(PreferenceManager manager, int requestCode, int resultCode, Intent data) {
		try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityResult", int.class, int.class, Intent.class);
            m.setAccessible(true);
            m.invoke(manager, requestCode, resultCode, data);
        } catch (Exception e) {
			Log.w(TAG, "Couldn't call PreferenceManager.dispatchActivityResult by reflection", e);
		}
	}
	static void dispatchActivityStop(PreferenceManager manager) {
		try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityStop");
            m.setAccessible(true);
            m.invoke(manager);
        } catch (Exception e) {
			Log.w(TAG, "Couldn't call PreferenceManager.dispatchActivityStop by reflection", e);
		}
	}
	static void dispatchActivityDestroy(PreferenceManager manager) {
		try {
			Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityDestroy");
			m.setAccessible(true);
			m.invoke(manager);
		} catch (Exception e) {
			Log.w(TAG, "Couldn't call PreferenceManager.dispatchActivityDestroy by reflection", e);
		}
	}
	static boolean setPreferences(PreferenceManager manager, PreferenceScreen screen) {
		try {
			Method m = PreferenceManager.class.getDeclaredMethod("setPreferences", PreferenceScreen.class);
			m.setAccessible(true);
			return ((Boolean) m.invoke(manager, screen));
		} catch (Exception e) {
			Log.w(TAG, "Couldn't call PreferenceManager.setPreferences by reflection", e);
		}
		return false;
	}
	
}
