package edu.uw.tcss450.group6project.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;

import edu.uw.tcss450.group6project.AuthActivity;
import edu.uw.tcss450.group6project.R;
import edu.uw.tcss450.group6project.ui.chat.ChatMessage;
import me.pushy.sdk.Pushy;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class PushReceiver extends BroadcastReceiver {

    public static final String RECEIVED_NEW_MESSAGE = "new message from pushy";

    private static final String CHANNEL_ID = "1";

    @Override
    public void onReceive(Context context, Intent intent) {

        //the following variables are used to store the information sent from Pushy
        //In the WS, you define what gets sent. You can change it there to suit your needs
        //Then here on the Android side, decide what to do with the message you got

        //for the lab, the WS is only sending chat messages so the type will always be msg
        //for your project, the WS needs to send different types of push messages.
        //So perform logic/routing based on the "type"
        //feel free to change the key or type of values.
        String typeOfMessage = intent.getStringExtra("type");
        if (typeOfMessage.equals("msg")) {
            processMessage(context, intent);
        } else if (typeOfMessage.equals("newRoom")) {
            processNewRoom(context, intent);
        } else if (typeOfMessage.equals("newContact")) {
            processNewContactRequest(context, intent);
        } else if (typeOfMessage.equals("deleteContact")) {
            processDeleteContact(context, intent);
        } else if (typeOfMessage.equals("confirmContact")) {
            processConfirmContact(context, intent);
        } else if (typeOfMessage.equals("denyContact")) {
            processDenyContact(context, intent);
        }
    }

    /**
     * Process push notifications of receiving new message.
     * @param context context
     * @param intent intent
     */
    private void processMessage(Context context, Intent intent) {
        ChatMessage message = null;
        int chatId = -1;
        try{
            message = ChatMessage.createFromJsonString(intent.getStringExtra("message"));
            chatId = intent.getIntExtra("chatid", -1);
        } catch (JSONException e) {
            //Web service sent us something unexpected...I can't deal with this.
            throw new IllegalStateException("Error from Web Service. Contact Dev Support");
        }

        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            //app is in the foreground so send the message to the active Activities
            Log.d("PUSHY", "Message received in foreground: " + message);

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtra("chatMessage", message);
            i.putExtra("chatid", chatId);
            i.putExtras(intent.getExtras());

            context.sendBroadcast(i);
        } else {
            //app is in the background so create and post a notification
            Log.d("PUSHY", "Message received in background: " + message.getMessage());

            Intent i = new Intent(context, AuthActivity.class);
            i.putExtras(intent.getExtras());

            sendNotification(context, i, "Message from: " + message.getUsername(), message.getMessage());
        }
    }

    /**
     * Process push notifications of joining new chat room.
     * @param context context the current context
     * @param intent intent the intent sent by web services
     */
    private void processNewRoom(Context context, Intent intent) {
        String roomName = intent.getStringExtra("roomName");
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            // app is in the foreground so send the message to the active Activities
            Log.d("PUSHY", "New chat room created in foreground: " + roomName);

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtras(intent.getExtras());

            context.sendBroadcast(i);

        } else {
            // app is in the background so create and post a notification
            Log.d("PUSHY", "New chat room created in background: " + roomName);

            Intent i = new Intent(context, AuthActivity.class);
            i.putExtra("roomName", roomName);
            i.putExtras(intent.getExtras());

            sendNotification(context, i, "You were added in a new chat room: " + roomName, "");
        }
    }

    /** Process push notifications of receiving a new contact request.
     *
     * @param context context the current context
     * @param intent intent the intent sent by web services
     */
    private void processNewContactRequest(Context context, Intent intent) {
        String username = intent.getStringExtra("username"); // ^ this but username

        // Not sure what this does but it seems important because all of them do it
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        // app is in the foreground so send the message to the active Activities
        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            Log.d("PUSHY", "Contact request received in foreground from: " + username);

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtra("username",username);
            i.putExtras(intent.getExtras());

            context.sendBroadcast(i);
        }

        // app is in the background so create and post a notification
        else {
            Log.d("PUSHY", "Contact request received in background from: " + username);

            Intent i = new Intent(context, AuthActivity.class);
            i.putExtras(intent.getExtras());

            sendNotification(context, i, "New contact request from: " + username, "");
        }
    }

    /** Process push notifications of a contact request you sent being confirmed
     *
     * @param context context the current context
     * @param intent intent the intent sent by web services
     */
    private void processConfirmContact(Context context, Intent intent) {
        String username = intent.getStringExtra("username"); // ^ this but username

        // Not sure what this does but it seems important because all of them do it
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        // app is in the foreground so send the message to the active Activities
        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            Log.d("PUSHY", "Contact request confirmed in background from: " + username);

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtra("username",username);
            i.putExtras(intent.getExtras());

            context.sendBroadcast(i);
        }

        // app is in the background so create and post a notification
        else {
            Log.d("PUSHY", "Contact request confirmed in background from: " + username);

            Intent i = new Intent(context, AuthActivity.class);
            i.putExtras(intent.getExtras());

            sendNotification(context, i, "Your contact request was confirmed by: " + username, "");
        }
    }

    /** Process push notifications of one of your contacts being deleted. Not shown to user.
     *
     * @param context context the current context
     * @param intent intent the intent sent by web services
     */
    private void processDeleteContact(Context context, Intent intent) {
        String userId = intent.getStringExtra("userId");

        // Not sure what this does but it seems important because all of them do it
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        // app is in the foreground so send the message to the active Activities
        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            Log.d("PUSHY", "Contact deleted in background from ID: " + userId);

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtra("userId",userId);
            i.putExtras(intent.getExtras());

            context.sendBroadcast(i);
        }

        // app is in the background so create and post a notification
        else {
            Log.d("PUSHY", "Contact deleted in background from ID: " + userId);
        }
    }

    /** Process push notifications of one of your contact requests being denied. Not shown to user.
     *
     * @param context context the current context
     * @param intent intent the intent sent by web services
     */
    private void processDenyContact(Context context, Intent intent) {
        String userId = intent.getStringExtra("userId");

        // Not sure what this does but it seems important because all of them do it
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        // app is in the foreground so send the message to the active Activities
        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            Log.d("PUSHY", "Contact request denied in background fromID: " + userId);

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtra("userId",userId);
            i.putExtras(intent.getExtras());

            context.sendBroadcast(i);
        }

        // app is in the background so create and post a notification
        else {
            Log.d("PUSHY", "Contact request denied in background fromID: " + userId);
        }
    }

    /**
     * Send notification in background
     * @param context context
     * @param i intent
     * @param title notification title
     * @param text notification text
     */
    private void sendNotification(Context context, Intent i, String title, String text) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        //research more on notifications the how to display them
        //https://developer.android.com/guide/topics/ui/notifiers/notifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_chat_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        // Automatically configure a ChatMessageNotification Channel for devices running Android O+
        Pushy.setNotificationChannel(builder, context);

        // Get an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // Build the notification and display it
        notificationManager.notify(1, builder.build());
    }
}