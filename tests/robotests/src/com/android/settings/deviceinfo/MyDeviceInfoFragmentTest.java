/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.settings.deviceinfo;

import static com.android.settings.SettingsActivity.EXTRA_FRAGMENT_ARG_KEY;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.TelephonyManager;

import com.android.settings.deviceinfo.aboutphone.MyDeviceInfoFragment;
import com.android.settings.TestConfig;
import com.android.settings.testutils.FakeFeatureFactory;
import com.android.settings.testutils.SettingsRobolectricTestRunner;
import com.android.settings.testutils.shadow.SettingsShadowResources;
import com.android.settings.testutils.shadow.SettingsShadowSystemProperties;
import com.android.settings.testutils.shadow.ShadowConnectivityManager;
import com.android.settings.testutils.shadow.ShadowUserManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

@RunWith(SettingsRobolectricTestRunner.class)
@Config(
        manifest = TestConfig.MANIFEST_PATH,
        sdk = TestConfig.SDK_VERSION,
        shadows = {ShadowConnectivityManager.class, ShadowUserManager.class}
)
public class MyDeviceInfoFragmentTest {
    @Mock
    private Activity mActivity;
    @Mock
    private PreferenceScreen mScreen;
    @Mock
    private TelephonyManager mTelephonyManager;

    private Context mContext;
    private MyDeviceInfoFragment mSettings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FakeFeatureFactory.setupForTest();
        mContext = RuntimeEnvironment.application;
        mSettings = spy(new MyDeviceInfoFragment());

        when(mSettings.getActivity()).thenReturn(mActivity);
        when(mSettings.getContext()).thenReturn(mContext);
        when(mActivity.getTheme()).thenReturn(mContext.getTheme());
        when(mActivity.getResources()).thenReturn(mContext.getResources());
        doNothing().when(mSettings).onCreatePreferences(any(), any());

        doReturn(mScreen).when(mSettings).getPreferenceScreen();
        when(mSettings.getPreferenceScreen()).thenReturn(mScreen);
        ShadowApplication.getInstance().setSystemService(Context.TELEPHONY_SERVICE,
                mTelephonyManager);
    }

    @Test
    @Config(shadows = {SettingsShadowResources.SettingsShadowTheme.class,
            SettingsShadowSystemProperties.class})
    public void onCreate_fromSearch_shouldNotOverrideInitialExpandedCount() {
        final Bundle args = new Bundle();
        args.putString(EXTRA_FRAGMENT_ARG_KEY, "search_key");
        mSettings.setArguments(args);

        mSettings.onCreate(null /* icicle */);

        verify(mScreen).setInitialExpandedChildrenCount(Integer.MAX_VALUE);
    }
}
