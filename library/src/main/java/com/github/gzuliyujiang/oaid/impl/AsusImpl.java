/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.github.gzuliyujiang.oaid.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDException;
import com.github.gzuliyujiang.oaid.OAIDLog;

import repeackage.com.asus.msa.SupplementaryDID.IDidAidlInterface;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class AsusImpl implements IOAID {
    private final Context context;

    public AsusImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.asus.msa.SupplementaryDID", 0);
            return pi != null;
        } catch (Exception e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        Intent intent = new Intent("com.asus.msa.action.ACCESS_DID");
        String pkg = "com.asus.msa.SupplementaryDID";
        ComponentName componentName = new ComponentName(pkg, pkg + ".SupplementaryDIDService");
        intent.setComponent(componentName);
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IDidAidlInterface anInterface = IDidAidlInterface.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new OAIDException("IDidAidlInterface is null");
                }
                if (!anInterface.isSupport()) {
                    throw new OAIDException("IDidAidlInterface#isSupport return false");
                }
                return anInterface.getOAID();
            }
        });
    }

}
