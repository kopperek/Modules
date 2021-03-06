package app.android.kopper.selectmaparea.util;

import android.util.Log;

/**
 * Created by kopper on 2015-05-16.
 * (C) Copyright 2015 kopperek@gmail.com
 * <p/>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
public class LogUtil {
    private static final String LOG_TAG="SelectMapArea";

    public static void e(Exception e) {
        Log.e(LOG_TAG,"",e);
    }

    public static void i(String s) {
        Log.i(LOG_TAG,s);
    }
}
