/*******************************************************************************
 * Copyright (c) 2017 Rizky Kharisma (@ngengs)
 *
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
 ******************************************************************************/

package com.ngengs.android.app.filkomnewsreader.data.enumeration;

public final class Types {
    public static final int TYPE_NEWS = 10;
    public static final int TYPE_ANNOUNCEMENT = 11;
    public static final int TYPE_UPDATE_VERSION = 20;

    public static final int TYPE_INDICATOR_EMPTY = 20;
    public static final int TYPE_INDICATOR_ERROR = 21;
    public static final int TYPE_INDICATOR_NO_NETWORK = 22;

    public static final String NOTIFICATION_CHANNEL_NEWS = "news_notification";
    public static final String NOTIFICATION_CHANNEL_ANNOUNCEMENT = "announcement_notification";
    public static final String NOTIFICATION_CHANNEL_UPDATE_VERSION = "update_version_notification";
    public static final String NOTIFICATION_GROUP = "notification_group";
}
