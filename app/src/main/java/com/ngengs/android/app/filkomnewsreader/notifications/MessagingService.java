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

package com.ngengs.android.app.filkomnewsreader.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.data.enumeration.Types;
import com.ngengs.android.app.filkomnewsreader.data.model.News;
import com.ngengs.android.app.filkomnewsreader.ui.main.MainActivity;
import com.ngengs.android.app.filkomnewsreader.ui.newsdetail.NewsDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Timber.d("onMessageReceived() called with: remoteMessage to = [" + remoteMessage.getTo() +
                 "]");

        if (remoteMessage.getData().size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                buildStackNotification(remoteMessage.getData());
            } else {
                buildOldNotification(remoteMessage.getData());
            }
        }
    }

    private void buildStackNotification(Map<String, String> data) {
        Timber.d("buildStackNotification() called with: data = [" + data + "]");
        int type = Integer.parseInt(data.get("type"));
        List<String> notificationData = new ArrayList<>();
        List<String> notificationNewsId = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (data.containsKey("title_" + i)) {
                notificationData.add(data.get("title_" + i));
            }
            if (data.containsKey("id_" + i)) {
                notificationNewsId.add(data.get("id_" + i));
            }
        }
        String channelId;
        String notificationTitle;
        String notificationMessage = getString(R.string.notification_message_generic);
        int notificationPriority;
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        int notificationIcon;
        switch (type) {
            case Types.TYPE_NEWS:
                channelId = Types.NOTIFICATION_CHANNEL_NEWS;
                notificationTitle = getString(R.string.notification_title_news);
                notificationIcon = R.drawable.ic_notification_news;
                notificationPriority = NotificationCompat.PRIORITY_DEFAULT;
                break;
            case Types.TYPE_ANNOUNCEMNT:
                channelId = Types.NOTIFICATION_CHANNEL_ANNOUNCEMENT;
                notificationTitle = getString(R.string.notification_title_announcement);
                notificationIcon = R.drawable.ic_notification_announcement;
                notificationPriority = NotificationCompat.PRIORITY_HIGH;
                break;
            default:
                Timber.e("buildNotification: %s", "Notification type not handle");
                return;
        }

        NotificationCompat.InboxStyle mStyle = new NotificationCompat.InboxStyle();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Long timeStamp = System.currentTimeMillis() / 1000L;
        for (int i = 0; i < 5 && i < notificationData.size(); i++) {
            mStyle.addLine(notificationData.get(i));
            int notificationId = type + timeStamp.intValue() + i;
            Intent intent;
            switch (type) {
                case Types.TYPE_NEWS:
                    News mNews = new News(notificationNewsId.get(i));
                    mNews.setTitle(notificationData.get(i));
                    intent = new Intent(this, NewsDetailActivity.class);
                    intent.putExtra("DATA", mNews);
                    break;
                case Types.TYPE_ANNOUNCEMNT:
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra(MainActivity.INTENT_ARGS_TYPE, type);
                    break;
                default:
                    Timber.e("buildStackNotification: %s",
                             "Notification type not handle to create intent");
                    return;
            }
            TaskStackBuilder mStackBuilder = TaskStackBuilder.create(this);
            mStackBuilder.addParentStack(MainActivity.class);
            mStackBuilder.addNextIntent(intent);
            PendingIntent mPendingIntent = mStackBuilder.getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mNotificationBuilder = buildNotification(channelId,
                                                                                mPendingIntent,
                                                                                notificationTitle);
            mNotificationBuilder.setContentText(notificationData.get(i))
                                .setSound(soundUri)
                                .setPriority(notificationPriority)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                                .setSmallIcon(notificationIcon)
                                .setGroup(Types.NOTIFICATION_GROUP);


            mNotificationManager.notify(channelId, notificationId, mNotificationBuilder.build());
        }
        mStyle.setSummaryText(notificationMessage);
        mStyle.setBigContentTitle("");
        Intent intentSummary = new Intent(this, MainActivity.class);
        intentSummary.putExtra(MainActivity.INTENT_ARGS_TYPE, type);
        TaskStackBuilder mStackBuilderSummary = TaskStackBuilder.create(this);
        mStackBuilderSummary.addParentStack(MainActivity.class);
        mStackBuilderSummary.addNextIntent(intentSummary);
        PendingIntent mPendingIntentSummary = mStackBuilderSummary.getPendingIntent(0,
                                                                                    PendingIntent.FLAG_UPDATE_CURRENT);
        Notification mGroupSummary = buildNotification(channelId, mPendingIntentSummary,
                                                       getString(
                                                               R.string.notification_title_generic))
                .setGroup(Types.NOTIFICATION_GROUP)
                .setStyle(mStyle)
                .setSmallIcon(R.drawable.ic_notification_news)
                .setContentText(notificationMessage)
                .setGroupSummary(true)
                .build();
        mNotificationManager.notify(0, mGroupSummary);
    }

    private void buildOldNotification(Map<String, String> data) {
        Timber.d("buildOldNotification() called with: data = [" + data + "]");
        int type = Integer.parseInt(data.get("type"));
        int total = Integer.parseInt(data.get("total"));
        List<String> notificationData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (data.containsKey("title_" + i)) {
                notificationData.add(data.get("title_" + i));
            }
        }
        String channelId;
        String notificationTitle;
        String notificationMore;
        String notificationMessage;
        int notificationPriority;
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        int notificationIcon;
        switch (type) {
            case Types.TYPE_NEWS:
                channelId = Types.NOTIFICATION_CHANNEL_NEWS;
                notificationTitle = getString(R.string.notification_title_news);
                notificationIcon = R.drawable.ic_notification_news;
                notificationMessage = getString(R.string.notification_message_news, total);
                notificationPriority = NotificationCompat.PRIORITY_DEFAULT;
                break;
            case Types.TYPE_ANNOUNCEMNT:
                channelId = Types.NOTIFICATION_CHANNEL_ANNOUNCEMENT;
                notificationTitle = getString(R.string.notification_title_announcement);
                notificationIcon = R.drawable.ic_notification_announcement;
                notificationMessage = getString(R.string.notification_message_announcement, total);
                notificationPriority = NotificationCompat.PRIORITY_HIGH;
                break;
            default:
                Timber.e("buildNotification: %s", "Notification type not handle");
                return;
        }

        if (total > notificationData.size()) {
            notificationMore = getString(R.string.notification_message_more,
                                         (total - notificationData.size()));
        } else {
            notificationMore = notificationMessage;
        }

        NotificationCompat.InboxStyle mStyle = new NotificationCompat.InboxStyle();
        for (int i = 0; i < 5 && i < notificationData.size(); i++) {
            mStyle.addLine(notificationData.get(i));
        }
        mStyle.setSummaryText(notificationMore);
        mStyle.setBigContentTitle("");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_ARGS_TYPE, type);
        TaskStackBuilder mStackBuilder = TaskStackBuilder.create(this);
        mStackBuilder.addParentStack(MainActivity.class);
        mStackBuilder.addNextIntent(intent);
        PendingIntent mPendingIntent = mStackBuilder.getPendingIntent(0,
                                                                      PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification mNotification = buildNotification(channelId, mPendingIntent, notificationTitle)
                .setContentText(notificationMessage)
                .setSound(soundUri)
                .setPriority(notificationPriority)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setStyle(mStyle)
                .setNumber(total)
                .setSmallIcon(notificationIcon)
                .build();
        mNotificationManager.notify(channelId, type, mNotification);
    }

    private NotificationCompat.Builder buildNotification(String channelId,
                                                         PendingIntent pendingIntent,
                                                         String title) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId);
        mBuilder.setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(title);

        return mBuilder;

    }
}
