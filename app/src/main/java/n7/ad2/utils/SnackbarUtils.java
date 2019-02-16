/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package n7.ad2.utils;

import android.support.design.widget.Snackbar;
import android.view.View;


/**
 * Provides a method to show a Snackbar.
 */
public class SnackbarUtils {

    public static void showSnackbar(View v, String snackbarText) {
        if (v == null || snackbarText == null) {
            return;
        }
        Snackbar.make(v, snackbarText, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackbarWithAction(View v, String snackbarText, String actionText, View.OnClickListener actionClick) {
        if (v == null || snackbarText == null) {
            return;
        }
        Snackbar snackbar = Snackbar.make(v, snackbarText, Snackbar.LENGTH_SHORT).setAction(actionText, actionClick);
        snackbar.show();
    }

}
