package com.infernalbitsoft.guardianangel.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.infernalbitsoft.guardianangel.Model.GADatabase;
import com.infernalbitsoft.guardianangel.Model.MessageClass;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class MessageGrabService extends AccessibilityService {

    private final String[] packages = new String[]{"com.whatsapp", "com.facebook.orca"};


    private boolean isGroup = false;
    private String chatName = "";
    private String senderName;
    private String appName;
    private boolean isWorking = false;
    private boolean errorInMessage = false;

    private ArrayList<MessageClass> chatList;

    public static boolean isMessageGrabServiceRunning = true;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


        appName = "";

        if (isMessageGrabServiceRunning)
            if (event.getPackageName() != null && event.getPackageName().equals(packages[0])) {
                chatList = new ArrayList<>();
                senderName = "";
                appName = "WhatsApp";
                Log("EventOccured", AccessibilityEvent.eventTypeToString(event.getEventType()));
                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED && !isWorking) {
                    isWorking = true;
                    Log("NewEvent", "--------------------------------------------------------");
                    AccessibilityNodeInfo parentNode = event.getSource();
                    if (parentNode != null) {

                        if (getRootInActiveWindow() == null) {
                            isWorking = false;
                            return;
                        }

                        List<AccessibilityNodeInfo> isHome = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/home_tab_layout");
                        if (isHome.size() > 0)
                            Log("isHome", "True");
                        else
                            Log("isHome", "False");
                        //Check for group
                        List<AccessibilityNodeInfo> header = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_status");
                        if (header.size() > 0) {
                            String headerTitle = header.get(0).getText().toString();
                            isGroup = !headerTitle.equals("online") && !headerTitle.equals("typing…");
                        } else
                            isGroup = false;

                        List<AccessibilityNodeInfo> chatNode = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name");

                        if (chatNode.size() > 0) {
                            chatName = chatNode.get(0).getText().toString();
                            Log("ChatOpened", chatName);
                            Log("GroupInfo", "Group: " + isGroup);
                        } else {
                            isWorking = false;
                            return;
                        }

                        if (!chatName.equals("")) {
                            errorInMessage = false;
                            List<AccessibilityNodeInfo> messageList = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("android:id/list");
                            if (messageList.size() > 0) {
                                AccessibilityNodeInfo messageListNode = messageList.get(0);
                                int childCount = messageListNode.getChildCount();
                                Log("ChatChild", childCount + " ");
                                for (int i = 0; i < messageListNode.getChildCount(); i++) {
                                    processChild(chatName, messageListNode.getChild(i));
                                    childOfChild(messageListNode.getChild(i), 0);
                                }
                                if (!errorInMessage)
                                    filterNewMessages("WhatsApp");
                            }
                        }
                    }
                    isWorking = false;
                }

            }
//        } else if (event.getPackageName() != null && event.getPackageName().equals(packages[1])) {
//            Log("FacebookEvent", "Works");
//            Log("EventOccured", AccessibilityEvent.eventTypeToString(event.getEventType()));
//            appName = "Messenger";
//            chatList = new ArrayList<>();
//            senderName = "";
//            if (!isWorking) {
//                isWorking = true;
//                Log("NewEvent", "--------------------------------------------------------");
//                AccessibilityNodeInfo parentNode = event.getSource();
//
//                AccessibilityNodeInfo root = getRootInActiveWindow();
//                if (root == null) {
//                    isWorking = false;
//                    return;
//                }
//
//                childOfChild(root, 0);
//
//                if(root.findAccessibilityNodeInfosByViewId("com.facebook.orca:id/message_list_container").size() > 0)
//                    Log("FacebookMessage","MessageListFound");
//
//                if (parentNode != null) {
//
//
////                    List<AccessibilityNodeInfo> isHome = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/home_tab_layout");
////                    if (isHome.size() > 0)
////                        Log("isHome", "True");
////                    else
////                        Log("isHome", "False");
////                    //Check for group
////                    List<AccessibilityNodeInfo> header = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_status");
////                    if (header.size() > 0) {
////                        String headerTitle = header.get(0).getText().toString();
////                        isGroup = !headerTitle.equals("online") && !headerTitle.equals("typing…");
////                    } else
////                        isGroup = false;
////
////                    List<AccessibilityNodeInfo> chatNode = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name");
////
////                    if (chatNode.size() > 0) {
////                        chatName = chatNode.get(0).getText().toString();
////                        Log("ChatOpened", chatName);
////                        Log("GroupInfo", "Group: " + isGroup);
////                    } else {
////                        isWorking = false;
////                        return;
////                    }
////
////                    if (!chatName.equals("")) {
////                        errorInMessage = false;
////                        List<AccessibilityNodeInfo> messageList = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("android:id/list");
////                        if (messageList.size() > 0) {
////                            AccessibilityNodeInfo messageListNode = messageList.get(0);
////                            int childCount = messageListNode.getChildCount();
////                            Log("ChatChild", childCount + " ");
////                            for (int i = 0; i < messageListNode.getChildCount(); i++) {
////                                processChild(chatName, messageListNode.getChild(i));
////                                childOfChild(messageListNode.getChild(i), 0);
////                            }
////                            if (!errorInMessage)
////                                filterNewMessages("WhatsApp");
////                        }
////                    }
//                }
//                isWorking = false;
//            }
//
//        }


    }

    private void childOfChild(AccessibilityNodeInfo node, int depth) {

        if (node == null)
            return;

        if (node.getClassName() == null)
            Log("ParentNode", " " + node.getViewIdResourceName());
        else
            Log("ParentNode", node.getClassName().toString() + " " + node.getViewIdResourceName());

        Log("ParentInfo", node.toString());

        int children = node.getChildCount();
        if (children > 0) {
            Log("ChildCountForViews", children + " ");
            for (int i = 0; i < children; i++)
                childOfChild(node.getChild(i), depth++);
        } else {
            CharSequence text = node.getText();
            if (text != null) {
                Log("BaseViews", text.toString() + " <- " + depth);
            }
        }

//        List<AccessibilityNodeInfo> messages = root.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text");
//        for (AccessibilityNodeInfo node:messages) {
//            Log("MessageNode",node.getText().toString());
//        }

    }

    private void processChild(String chatName, AccessibilityNodeInfo child) {
        if (child == null)
            return;

        child.refresh();

        int childCount = child.getChildCount();
        if (childCount > 0) {
            Log("MessageChild", childCount + " ");
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo messageNode = child.getChild(i);
                if (messageNode != null) {

                    Log("MessageChildClass", child.getChild(i).getClassName().toString() + " " + i);

                    if (messageNode.getClassName().toString().equals("android.widget.TextView")) {
                        if (messageNode.getText() != null)
                            Log("->", messageNode.getText().toString());
                    }
                }
            }

            try {

                String timestamp = "";
                String message = "";


                if (!isGroup) {

                    try {
                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/status").size() > 0)
                            senderName = "";
                        else
                            senderName = chatName;

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/date").size() > 0)
                            timestamp = child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/date").get(0).getText().toString();

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/sticker_image").size() > 0)
                            message += "<<Sticker>>";

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/image").size() > 0)
                            message += "<<Image>>";

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/caption").size() > 0)
                            message += child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/caption").get(0).getText().toString();

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text").size() > 0)
                            message += child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text").get(0).getText().toString();

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/thumb").size() > 0)
                            message += "<<Video>>";

                        if (message.isEmpty())
                            return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


//                    if (childCount == 2) {
//                        if (child.getChild(0).getClassName().equals("android.widget.ImageView") && child.getChild(1).getClassName().equals("android.widget.TextView")) {
//                            senderName = chatName;
//                            message = "<<" + child.getChild(0).getContentDescription().toString() + ">>";
//                            timestamp = child.getChild(1).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.LinearLayout") && child.getChild(1).getClassName().equals("android.widget.ImageView")) {
//
//                        } else {
//                            Log(chatName + " Sent", child.getChild(0).getText().toString() + " at: " + child.getChild(1).getText().toString());
//                            if (child.getChild(0).getText().toString().equals(" You deleted this message ")) {
//                                Log("DeletedMessage", "This message was deleted");
//                                message = "<<Deleted>>";
//                                senderName = "";
//                                timestamp = child.getChild(1).getText().toString();
//                            } else if (child.getChild(0).getText().toString().equals(" This message was deleted ")) {
//                                Log("DeletedMessage", "This message was deleted");
//                                message = "<<Deleted>>";
//                                senderName = chatName;
//                                timestamp = child.getChild(1).getText().toString();
//                            } else {
//                                message = child.getChild(0).getText().toString();
//                                senderName = chatName;
//                                timestamp = child.getChild(1).getText().toString();
//                            }
//                        }
//                    } else if (childCount == 3) {
//                        if (child.getChild(0).getClassName().equals("android.widget.FrameLayout")) {
//                            Log(chatName + " Sent", child.getChild(1).getText().toString() + " at: " + child.getChild(2).getText().toString());
//                            senderName = chatName;
//                            message = child.getChild(1).getText().toString();
//                            timestamp = child.getChild(2).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.ImageView") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.ImageView")) {
//                            senderName = "";
//                            message = "<<" + child.getChild(0).getContentDescription().toString() + ">>";
//                            timestamp = child.getChild(1).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.TextView") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.TextView")) {
//                            Log(chatName + " Sent", child.getChild(1).getText().toString() + " at: " + child.getChild(2).getText().toString());
//                            senderName = chatName;
//                            message = child.getChild(1).getText().toString();
//                            timestamp = child.getChild(2).getText().toString();
//                        } else {
//                            Log("You Sent", child.getChild(0).getText().toString() + " at: " + child.getChild(1).getText().toString());
//                            senderName = "";
//                            message = child.getChild(0).getText().toString();
//                            timestamp = child.getChild(1).getText().toString();
//                        }
//                    } else if (childCount == 4) {
//                        if (child.getChild(0).getClassName().equals("android.widget.ImageView") && child.getChild(1).getClassName().equals("android.widget.ImageView") && child.getChild(2).getClassName().equals("android.widget.TextView") && child.getChild(3).getClassName().equals("android.widget.ImageView")) {
//                            Log("You Sent", "<<" + child.getChild(1).getContentDescription() + ">>" + " at: " + child.getChild(2).getText().toString());
//                            senderName = "";
//                            message = "<<" + child.getChild(1).getContentDescription() + ">>";
//                            timestamp = child.getChild(2).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.TextView") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.TextView") && child.getChild(3).getClassName().equals("android.widget.ImageView")) {
//                            Log("You Sent", child.getChild(1).getText().toString() + " at: " + child.getChild(2).getText().toString());
//                            senderName = "";
//                            message = child.getChild(1).getText().toString();
//                            timestamp = child.getChild(2).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.TextView") && child.getChild(1).getClassName().equals("android.widget.ImageView") && child.getChild(2).getClassName().equals("android.widget.TextView") && child.getChild(3).getClassName().equals("android.widget.ImageView")) {
//                            Log(chatName + " Sent", "<<" + child.getChild(1).getContentDescription() + ">>" + " at: " + child.getChild(2).getText().toString());
//                            senderName = chatName;
//                            message = "<<" + child.getChild(1).getContentDescription() + ">>";
//                            timestamp = child.getChild(2).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.ImageView") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.TextView") && child.getChild(3).getClassName().equals("android.widget.ImageView")) {
//                            Log(chatName + " Sent", child.getChild(1).getText().toString() + " at: " + child.getChild(2).getText().toString());
//                            senderName = chatName;
//                            message = "<<" + child.getChild(0).getContentDescription() + ">>" + child.getChild(1).getText().toString();
//                            timestamp = child.getChild(2).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.TextView") && child.getChild(1).getClassName().equals("android.widget.LinearLayout") && child.getChild(2).getClassName().equals("android.widget.TextView") && child.getChild(3).getClassName().equals("android.widget.TextView")) {
//                            Log(chatName + " Sent", child.getChild(2).getText().toString() + " at: " + child.getChild(3).getText().toString());
//                            senderName = chatName;
//                            message = child.getChild(2).getText().toString();
//                            timestamp = child.getChild(3).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.FrameLayout") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.TextView") && child.getChild(3).getClassName().equals("android.widget.ImageView")) {
//                            Log("You Sent", child.getChild(1).getText().toString() + " at: " + child.getChild(2).getText().toString());
//                            senderName = "";
//                            message = child.getChild(1).getText().toString();
//                            timestamp = child.getChild(2).getText().toString();
//                        } else {
//                            Log(chatName + " Sent", child.getChild(1).getText().toString() + " at: " + child.getChild(2).getText().toString());
//                            senderName = chatName;
//                            message = child.getChild(1).getText().toString();
//                            timestamp = child.getChild(2).getText().toString();
//                        }
//                    }
                } else {

                    try {
                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/name_in_group_tv").size() > 0)
                            senderName = child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/name_in_group_tv").get(0).getText().toString();
                        else if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/status").size() > 0)
                            senderName = "";

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/date").size() > 0)
                            timestamp = child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/date").get(0).getText().toString();

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/sticker_image").size() > 0)
                            message += "<<Sticker>>";

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/image").size() > 0)
                            message += "<<Image>>";

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/caption").size() > 0)
                            message += child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/caption").get(0).getText().toString();

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text").size() > 0)
                            message += child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text").get(0).getText().toString();

                        if (child.findAccessibilityNodeInfosByViewId("com.whatsapp:id/thumb").size() > 0)
                            message += "<<Video>>";

                        if (message.isEmpty())
                            return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    if (childCount == 2) {
//                        if (child.getChild(0).getClassName().equals("android.widget.TextView") && child.getChild(1).getClassName().equals("android.widget.TextView") && !senderName.equals("")) {
//                            Log(senderName + " Sent", child.getChild(0).getText().toString() + " at: " + child.getChild(1).getText().toString());
//                            senderName = senderName;
//                            message = child.getChild(0).getText().toString();
//                            timestamp = child.getChild(1).getText().toString();
//                        }
//                    } else if (childCount == 3) {
//                        if (child.getChild(0).getClassName().equals("android.widget.LinearLayout") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.TextView")) {
//                            senderName = child.getChild(0).getChild(0).getText().toString();
//                            message = child.getChild(1).getText().toString();
//                            timestamp = child.getChild(2).getText().toString();
//                            Log(senderName + " Sent", child.getChild(1).getText().toString() + " at: " + child.getChild(2).getText().toString());
//                        } else if (child.getChild(0).getClassName().equals("android.widget.LinearLayout") && child.getChild(1).getClassName().equals("android.widget.ImageView") && child.getChild(2).getClassName().equals("android.widget.TextView")) {
//                            senderName = child.getChild(0).getChild(0).getText().toString();
//                            message = "<<" + child.getChild(1).getContentDescription() + ">>";
//                            timestamp = child.getChild(2).getText().toString();
//                            Log(senderName + " Sent", "<<" + child.getChild(1).getContentDescription() + ">>" + " at: " + child.getChild(2).getText().toString());
//                        } else if (child.getChild(0).getClassName().equals("android.widget.TextView") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.ImageView")) {
//                            Log("You Sent", child.getChild(0).getText().toString() + " at: " + child.getChild(1).getText().toString());
//                            message = child.getChild(0).getText().toString();
//                            timestamp = child.getChild(1).getText().toString();
//                            senderName = "";
//                        } else if (child.getChild(0).getClassName().equals("android.widget.ImageView") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.ImageView")) {
//                            Log("You Sent", child.getChild(0).getContentDescription() + " at: " + child.getChild(1).getText().toString());
//                            message = "<<" + child.getChild(0).getContentDescription() + ">>";
//                            timestamp = child.getChild(1).getText().toString();
//                            senderName = "";
//                        }
//                    } else if (childCount == 4) {
//                        if (child.getChild(0).getClassName().equals("android.widget.FrameLayout") && child.getChild(1).getClassName().equals("android.widget.TextView") && child.getChild(2).getClassName().equals("android.widget.TextView") && child.getChild(3).getClassName().equals("android.widget.ImageView")) {
//                            Log("You Sent", child.getChild(1).getText().toString() + " at: " + child.getChild(2).getText().toString());
//                            senderName = "";
//                            message = child.getChild(1).getText().toString();
//                            timestamp = child.getChild(2).getText().toString();
//                        } else if (child.getChild(0).getClassName().equals("android.widget.LinearLayout") && child.getChild(1).getClassName().equals("android.widget.FrameLayout") && child.getChild(2).getClassName().equals("android.widget.TextView") && child.getChild(3).getClassName().equals("android.widget.TextView")) {
//                            senderName = child.getChild(0).getChild(0).getText().toString();
//                            Log(senderName + " Sent", child.getChild(2).getText().toString() + " at: " + child.getChild(3).getText().toString());
//                            message = child.getChild(2).getText().toString();
//                            timestamp = child.getChild(3).getText().toString();
//                        }
//                    } else if (childCount == 6) {
//                        if (child.getChild(0).getClassName().equals("android.widget.LinearLayout") && child.getChild(1).getClassName().equals("android.widget.ImageView") && child.getChild(2).getClassName().equals("android.widget.FrameLayout") && child.getChild(3).getClassName().equals("android.widget.TextView") && child.getChild(4).getClassName().equals("android.widget.TextView") && child.getChild(5).getClassName().equals("android.widget.ImageView")) {
//                            senderName = child.getChild(0).getChild(0).getText().toString();
//                            Log(senderName + " Sent a ", child.getChild(3).getText().toString() + " min video at: " + child.getChild(4).getText().toString());
//                            message = child.getChild(3).getText().toString();
//                            timestamp = child.getChild(4).getText().toString();
//                        }
//                    }
                }

                chatList.add(new MessageClass(appName, chatName, senderName, timestamp, isGroup, message));
            } catch (NullPointerException e) {
                e.printStackTrace();
                errorInMessage = true;
            }
        }
    }

    private void filterNewMessages(String app) {
        GADatabase db = GADatabase.getInstance(this);
        List<MessageClass> lastMessages = db.messageDAO().getLastAppMessages(app, chatName);

        if (lastMessages.size() == 0 && chatList.size() > 1) {
            for (MessageClass chat : chatList) {
                chat.setDateTime(System.currentTimeMillis());
                db.messageDAO().insertMessage(chat);
                Log("AddedMessageNew", chat.message);
            }
            for (MessageClass chat : chatList)
                System.out.print(chat.message + " ");
            for (MessageClass chat : lastMessages)
                System.out.print(chat.message + " ");
        } else {

            boolean lastMatched = false;
            boolean secondLastMatched = false;

            int lastIndex = lastMessages.size() - 1;
            int secondLastIndex = lastIndex - 1;

            int newMessageIndex = -1;

            while (secondLastIndex > -1) {
                for (int i = chatList.size() - 1; i >= 1; i--) {

                    if (chatList.get(i).message.equals("<<Deleted>>"))
                        continue;

                    lastMatched = chatList.get(i).composite_id.equals(lastMessages.get(lastIndex).composite_id);
                    secondLastMatched = chatList.get(i - 1).composite_id.equals(lastMessages.get(secondLastIndex).composite_id);

                    newMessageIndex = i;
                    if (lastMatched && secondLastMatched)
                        break;
                    else if (lastMatched && !secondLastMatched)
                        if (chatList.get(i - 1).message.equals("<<Deleted>>")) {
                            int tempI = i - 2;
                            int tempSecondLast = secondLastIndex - 1;
                            while (tempI > 0 && tempSecondLast > 0) {
                                secondLastMatched = chatList.get(tempI).composite_id.equals(lastMessages.get(tempSecondLast).composite_id);
                                if (secondLastMatched || !chatList.get(tempI).message.equals("<<Deleted>>")) {
                                    break;
                                }
                                tempI--;
                                tempSecondLast--;
                            }
                            if (lastMatched && secondLastMatched)
                                break;
                        }
                }
                if (lastMatched && secondLastMatched) {
                    for (int i = newMessageIndex + 1; i < chatList.size(); i++) {
                        MessageClass chat = chatList.get(i);
                        chat.setDateTime(System.currentTimeMillis());
                        if (chat.getMessage().equals("<<Deleted>>"))
                            continue;
                        db.messageDAO().insertMessage(chat);
                        Log("AddedMessage", chat.message);
                    }
                    for (MessageClass chat : chatList)
                        Log("ChatList", chat.message + " ");
                    for (MessageClass chat : lastMessages)
                        Log("LastMessages", chat.message + " ");
                    return;
                } else {
                    lastMatched = false;
                    secondLastMatched = false;
                }

                lastIndex--;
                secondLastIndex = lastIndex - 1;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void checkData(AccessibilityEvent event, String packageName) {
//            Toast.makeText(this, "This is Messenger", Toast.LENGTH_SHORT).show();
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
//            Log.d("Grabbi_"+packageName, "Node info" + " " + event.getContentDescription());
//            Log.d("Grabbi_"+packageName, "Childs: " + nodeInfo.getChildCount());
            if (nodeInfo.getChildCount() > 0) {
                for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                    processChild(nodeInfo.getChild(i), packageName);
                }
            }
        }

    }

    private void processChild(AccessibilityNodeInfo node, String packageName) {
//        Log.d("Grabbi_node_"+packageName, node.getClassName() + " ");
        if (node == null) return;

        for (AccessibilityNodeInfo header : node.findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name"))
            Log.d("Grabbi_window_top_" + packageName, header.getText() + "");

        for (AccessibilityNodeInfo list : node.findAccessibilityNodeInfosByViewId("android:id/list"))
            checkList(list, 0);

        String viewId = node.getViewIdResourceName();
        if (viewId != null && !viewId.isEmpty())
//            Log.d("Grabbi_window_view_id_"+packageName,   viewId);

            if (node.getChildCount() > 0)
                for (int i = 0; i < node.getChildCount(); i++) {
                    processChild(node.getChild(i), packageName);
                }
//        else {
////            Log.d("Grabbi_node_id_" + packageName, node.getViewIdResourceName() + " ");
//            if(node.getClassName().equals("android.widget.TextView")) {
//                Log.d("Grabbi_text_" + packageName, node.getText() + "");
//                String id = node.getViewIdResourceName();
//                Log.d("Grabbi_text_id" + packageName, id + " ");
////                Log.d("Grabbi_string" + packageName, "--> " + node.toString());
//            }
//        }
    }

    private void checkList(AccessibilityNodeInfo list, int count) {
        if (list == null) return;

        if (list.getChildCount() > 0) {
            if (list.getChildCount() == 2) {
                Log.d("List_2_child", list.getClassName() + "");
                for (AccessibilityNodeInfo listNode : list.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text"))
                    Log.d("List_node_message", listNode.getText() + " ");

                for (AccessibilityNodeInfo listNode : list.findAccessibilityNodeInfosByViewId("com.whatsapp:id/date"))
                    Log.d("List_node_date", listNode.getText() + " ");

            } else if (list.getChildCount() == 3) {
                Log.d("List_3_child", list.getClassName() + "");
                for (AccessibilityNodeInfo listNode : list.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text"))
                    Log.d("List_node_message", listNode.getText() + " ");

                for (AccessibilityNodeInfo listNode : list.findAccessibilityNodeInfosByViewId("com.whatsapp:id/date"))
                    Log.d("List_node_date", listNode.getText() + " ");
            } else
                for (int i = 0; i < list.getChildCount(); i++) {
                    checkList(list.getChild(i), count + 1);
                }
        }
    }

    private void notificationReceived(AccessibilityEvent event, String name) {

        if (event == null) return;

        Parcelable data = event.getParcelableData();

        if (data instanceof Notification) {
            Notification notification = (Notification) data;
            RemoteViews remoteViews = notification.bigContentView;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup localView = (ViewGroup) inflater.inflate(remoteViews.getLayoutId(), null);
            remoteViews.reapply(getApplicationContext(), localView);
            Resources resources = null;
            PackageManager pkm = getPackageManager();

            try {
                resources = pkm.getResourcesForApplication("com.user.package");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            int TITLE = resources.getIdentifier("android:id/title", null, null);
            int INBOX = resources.getIdentifier("android:id/big_text", null, null);
            int TEXT = resources.getIdentifier("android:id/text", null, null);


            String packagename = String.valueOf(event.getPackageName());

            TextView title = (TextView) localView.findViewById(TITLE);

            TextView inbox = (TextView) localView.findViewById(INBOX);

            TextView text = (TextView) localView.findViewById(TEXT);

            Log.d("NOTIFICATION Package : ", packagename);

            Log.d("NOTIFICATION Title : ", title.getText().toString());

            Log.d("NOTIFICATION You have got x messages : ", text.getText().toString());

            Log.d("NOTIFICATION inbox : ", inbox.getText().toString());
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
