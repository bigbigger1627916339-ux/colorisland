package io.github.colorisland.island;

import io.github.colorisland.util.StringUtils;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import io.github.colorisland.island.dispatch.IslandRequest;
import android.app.Notification;
import android.os.Parcelable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import io.github.colorisland.util.KResult;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

/* compiled from: IslandNotification.kt */
/* loaded from: classes3.dex */
public final /* data */ class IslandNotification {
    private static Method msgSenderMethod;
    private static Method msgTextMethod;
    private final List<IslandAction> actions;
    private final Drawable appIcon;
    private final String appLabel;
    private final PendingIntent contentIntent;
    private final int groupCount;
    private final String key;
    private final List<String> messages;
    private final String pkg;
    private final long postTime;
    private final int progress;
    private final boolean progressIndeterminate;
    private final String subText;
    private final String text;
    private final String title;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    public static final int $stable = 8;
    private static final Regex DIGITS = new Regex("\\d+");

    /* compiled from: IslandNotification.kt */
    /* loaded from: classes3.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private final boolean isMessagingStyle(Bundle extras) {
            String string = extras.getString(NotificationCompat.EXTRA_TEMPLATE);
            return string != null && StringUtils.contains(string, "MessagingStyle");
        }

        private final Method senderMethod(Object m) {
            Method method = IslandNotification.msgSenderMethod;
            if (method != null) {
                return method;
            }
            Method method2;
            try {
                method2 = m.getClass().getMethod("getSender", new Class[0]);
            } catch (NoSuchMethodException e) {
                return null;
            }
            IslandNotification.msgSenderMethod = method2;
            return method2;
        }

        private final Method textMethod(Object m) {
            Method method = IslandNotification.msgTextMethod;
            if (method != null) {
                return method;
            }
            Method method2;
            try {
                method2 = m.getClass().getMethod("getText", new Class[0]);
            } catch (NoSuchMethodException e) {
                return null;
            }
            IslandNotification.msgTextMethod = method2;
            return method2;
        }

        /* JADX WARN: Code restructure failed: missing block: B:14:0x0045, code lost:
        
            if (r6 != null) goto L229;
         */
        /* JADX WARN: Removed duplicated region for block: B:111:0x01e7  */
        /* JADX WARN: Removed duplicated region for block: B:116:0x01ff A[Catch: all -> 0x0208, TryCatch #2 {all -> 0x0208, blocks: (B:114:0x01eb, B:116:0x01ff, B:117:0x0203), top: B:113:0x01eb, outer: #3 }] */
        /* JADX WARN: Removed duplicated region for block: B:120:0x0219  */
        /* JADX WARN: Removed duplicated region for block: B:123:0x021e  */
        /* JADX WARN: Removed duplicated region for block: B:125:0x0255 A[Catch: all -> 0x029a, TryCatch #3 {all -> 0x029a, blocks: (B:96:0x0193, B:98:0x01a1, B:100:0x01ad, B:109:0x01e1, B:112:0x01e8, B:118:0x0213, B:121:0x021a, B:125:0x0255, B:127:0x0258, B:130:0x0222, B:132:0x0228, B:136:0x0237, B:137:0x024f, B:142:0x0209, B:146:0x01d7, B:151:0x0264, B:152:0x0273, B:154:0x0279, B:157:0x028a, B:162:0x028e, B:163:0x0295, B:169:0x0291, B:114:0x01eb, B:116:0x01ff, B:117:0x0203), top: B:95:0x0193, inners: #2 }] */
        /* JADX WARN: Removed duplicated region for block: B:128:0x0258 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:129:0x0220  */
        /* JADX WARN: Removed duplicated region for block: B:139:0x0202  */
        /* JADX WARN: Removed duplicated region for block: B:62:0x0141 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:66:0x010e A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
                /**
         * 将系统通知解析为岛通知数据（v1.0 smali Companion.from 逐条翻译）。
         *
         * 解析顺序：标题（title→conversationTitle→appLabel）→
         * 正文（bigText→textLines 拼接→text→空串）→ 操作按钮（最多 3 个）→
         * subText → 进度 → MessagingStyle 消息列表（反射取 sender/text）。
         *
         * groupCount 使用默认值 1，由调用方（IslandController.handlePostedWithMeta）
         * 依据同包跟踪计数通过 copy 更新。
         */
        public final IslandNotification from(StatusBarNotification sbn, String appLabel, Drawable appIcon) {
            Intrinsics.checkNotNullParameter(sbn, "sbn");
            Intrinsics.checkNotNullParameter(appLabel, "appLabel");
            Notification notification = sbn.getNotification();
            if (notification == null) {
                return null;
            }
            Bundle extras = notification.extras;
            // ---- 标题：android.title → android.conversationTitle → appLabel ----
            CharSequence titleCs = extras.getCharSequence("android.title");
            String title = titleCs != null ? titleCs.toString() : null;
            if (title == null) {
                CharSequence conversationTitle = extras.getCharSequence("android.conversationTitle");
                title = conversationTitle != null ? conversationTitle.toString() : null;
            }
            if (title == null || StringsKt.isBlank(title)) {
                title = appLabel;
            }
            // ---- 正文：bigText → textLines 以换行拼接 → text → 空串 ----
            CharSequence bigTextCs = extras.getCharSequence("android.bigText");
            String bigText = bigTextCs != null ? bigTextCs.toString() : null;
            CharSequence textCs = extras.getCharSequence("android.text");
            String text = textCs != null ? textCs.toString() : null;
            CharSequence[] textLinesArray = extras.getCharSequenceArray("android.textLines");
            List<String> textLines = null;
            if (textLinesArray != null) {
                textLines = new ArrayList<>();
                for (CharSequence line : textLinesArray) {
                    String lineStr = line.toString();
                    if (!StringsKt.isBlank(lineStr)) {
                        textLines.add(lineStr);
                    }
                }
            }
            String body;
            if (bigText == null || StringsKt.isBlank(bigText)) {
                if (textLines != null && !textLines.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < textLines.size(); i++) {
                        if (i > 0) {
                            sb.append("\n");
                        }
                        sb.append(textLines.get(i));
                    }
                    body = sb.toString();
                } else if (text != null && !StringsKt.isBlank(text)) {
                    body = text;
                } else {
                    body = "";
                }
            } else {
                body = bigText;
            }
            // ---- 操作按钮：最多取 3 个，标题非空白且带 intent ----
            List<IslandAction> actions = CollectionsKt.emptyList();
            if (notification.actions != null) {
                List<IslandAction> actionList = new ArrayList<>();
                int limit = Math.min(3, notification.actions.length);
                for (int i = 0; i < limit; i++) {
                    Notification.Action action = notification.actions[i];
                    if (action.title == null || action.actionIntent == null) {
                        continue;
                    }
                    String actionTitle = action.title.toString();
                    if (StringsKt.isBlank(actionTitle)) {
                        continue;
                    }
                    // 过滤无意义标题(纯标点/符号/省略号，无字母数字汉字)，避免展开卡片出现"..."无用按钮
                    boolean meaningful = false;
                    for (int ci = 0; ci < actionTitle.length(); ci++) {
                        if (Character.isLetterOrDigit(actionTitle.charAt(ci))) {
                            meaningful = true;
                            break;
                        }
                    }
                    if (!meaningful) {
                        continue;
                    }
                    actionList.add(new IslandAction(actionTitle, action.actionIntent));
                }
                actions = actionList;
            }
            // ---- subText：非空白才采用 ----
            String subText = "";
            CharSequence subTextCs = extras.getCharSequence("android.subText");
            if (subTextCs != null) {
                String subTextStr = subTextCs.toString();
                if (subTextStr != null && !StringsKt.isBlank(subTextStr)) {
                    subText = subTextStr;
                }
            }
            // ---- 进度：progressMax > 0 时换算为 0-100 百分比，否则 -1 ----
            int progressMax = extras.getInt("android.progressMax", 0);
            int progress = extras.getInt("android.progress", 0);
            boolean hasProgress = progressMax > 0;
            boolean progressIndeterminate = hasProgress && extras.getBoolean("android.progressIndeterminate", false);
            // ---- MessagingStyle 消息列表：反射读取每条消息的 sender 与 text ----
            List<String> messages = CollectionsKt.emptyList();
            if (isMessagingStyle(extras)) {
                try {
                    Parcelable[] messageArray = extras.getParcelableArray("android.messages");
                    if (messageArray != null) {
                        List<String> rawLines = new ArrayList<>();
                        for (Parcelable message : messageArray) {
                            // sender（反射失败时按 null 处理）
                            CharSequence sender = null;
                            try {
                                Object senderResult = senderMethod(message).invoke(message);
                                sender = senderResult instanceof CharSequence ? (CharSequence) senderResult : null;
                            } catch (Throwable th) {
                                sender = null;
                            }
                            // text（反射失败时按 null 处理）
                            CharSequence messageText = null;
                            try {
                                Object textResult = textMethod(message).invoke(message);
                                messageText = textResult instanceof CharSequence ? (CharSequence) textResult : null;
                            } catch (Throwable th) {
                                messageText = null;
                            }
                            if (messageText != null) {
                                String senderStr = null;
                                if (sender != null && !StringsKt.isBlank(sender.toString())) {
                                    senderStr = sender.toString();
                                }
                                String line = senderStr != null ? senderStr + ": " + messageText : messageText.toString();
                                rawLines.add(line);
                            }
                        }
                        List<String> filtered = new ArrayList<>();
                        for (String line : rawLines) {
                            if (!StringsKt.isBlank(line)) {
                                filtered.add(line);
                            }
                        }
                        messages = filtered;
                    }
                } catch (Throwable th) {
                    messages = CollectionsKt.emptyList();
                }
            }
            // ---- 构造岛通知（groupCount 默认 1，由调用方按同包计数更新）----
            return new IslandNotification(
                    sbn.getKey(),
                    sbn.getPackageName(),
                    appLabel,
                    appIcon,
                    title,
                    body,
                    notification.when,
                    notification.contentIntent,
                    actions,
                    subText,
                    hasProgress ? Math.max(0, Math.min(100, (progress * 100) / progressMax)) : -1,
                    progressIndeterminate,
                    messages,
                    1);
        }

        public final IslandNotification fromRequest(Context context, IslandRequest request) {
            Object m5221constructorimpl;
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(request, "request");
            try {
                Companion companion2 = this;
                Icon icon = request.getIcon();
                m5221constructorimpl = KResult.success(icon != null ? icon.loadDrawable(context) : null);
            } catch (Throwable th) {
                m5221constructorimpl = KResult.success(KResult.createFailure(th));
            }
            Drawable drawable = (Drawable) (KResult.isFailureimpl(m5221constructorimpl) ? null : m5221constructorimpl);
            String str = "dispatch:" + request.getId();
            String pkg = request.getPkg();
            if (StringsKt.isBlank(pkg)) {
                pkg = "external";
            }
            String str2 = pkg;
            String appLabel = request.getAppLabel();
            if (StringsKt.isBlank(appLabel)) {
                String pkg2 = request.getPkg();
                if (StringsKt.isBlank(pkg2)) {
                    pkg2 = "外部应用";
                }
                appLabel = pkg2;
            }
            return new IslandNotification(str, str2, appLabel, drawable, request.getTitle(), request.getText(), System.currentTimeMillis(), request.getContentIntent(), request.getActions(), request.getSubText(), 0, false, null, 0, 15360, null);
        }
    }

    public IslandNotification(String key, String pkg, String appLabel, Drawable drawable, String title, String text, long j, PendingIntent pendingIntent, List<IslandAction> actions, String subText, int i, boolean z, List<String> messages, int i2) {
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        Intrinsics.checkNotNullParameter(appLabel, "appLabel");
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(actions, "actions");
        Intrinsics.checkNotNullParameter(subText, "subText");
        Intrinsics.checkNotNullParameter(messages, "messages");
        this.key = key;
        this.pkg = pkg;
        this.appLabel = appLabel;
        this.appIcon = drawable;
        this.title = title;
        this.text = text;
        this.postTime = j;
        this.contentIntent = pendingIntent;
        this.actions = actions;
        this.subText = subText;
        this.progress = i;
        this.progressIndeterminate = z;
        this.messages = messages;
        this.groupCount = i2;
    }

    public /* synthetic */ IslandNotification(String str, String str2, String str3, Drawable drawable, String str4, String str5, long j, PendingIntent pendingIntent, List list, String str6, int i, boolean z, List list2, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, str3, drawable, str4, str5, j, pendingIntent, list, (i3 & 512) != 0 ? "" : str6, (i3 & 1024) != 0 ? -1 : i, (i3 & 2048) != 0 ? false : z, (i3 & 4096) != 0 ? CollectionsKt.emptyList() : list2, (i3 & 8192) != 0 ? 1 : i2);
    }

    public static /* synthetic */ IslandNotification copy$default(IslandNotification islandNotification, String str, String str2, String str3, Drawable drawable, String str4, String str5, long j, PendingIntent pendingIntent, List list, String str6, int i, boolean z, List list2, int i2, int i3, Object obj) {
        return islandNotification.copy((i3 & 1) != 0 ? islandNotification.key : str, (i3 & 2) != 0 ? islandNotification.pkg : str2, (i3 & 4) != 0 ? islandNotification.appLabel : str3, (i3 & 8) != 0 ? islandNotification.appIcon : drawable, (i3 & 16) != 0 ? islandNotification.title : str4, (i3 & 32) != 0 ? islandNotification.text : str5, (i3 & 64) != 0 ? islandNotification.postTime : j, (i3 & 128) != 0 ? islandNotification.contentIntent : pendingIntent, (i3 & 256) != 0 ? islandNotification.actions : list, (i3 & 512) != 0 ? islandNotification.subText : str6, (i3 & 1024) != 0 ? islandNotification.progress : i, (i3 & 2048) != 0 ? islandNotification.progressIndeterminate : z, (i3 & 4096) != 0 ? islandNotification.messages : list2, (i3 & 8192) != 0 ? islandNotification.groupCount : i2);
    }

    /* renamed from: component1, reason: from getter */
    public final String component1() {
        return this.key;
    }

    /* renamed from: component10, reason: from getter */
    public final String component10() {
        return this.subText;
    }

    /* renamed from: component11, reason: from getter */
    public final int component11() {
        return this.progress;
    }

    /* renamed from: component12, reason: from getter */
    public final boolean component12() {
        return this.progressIndeterminate;
    }

    public final List<String> component13() {
        return this.messages;
    }

    /* renamed from: component14, reason: from getter */
    public final int component14() {
        return this.groupCount;
    }

    /* renamed from: component2, reason: from getter */
    public final String component2() {
        return this.pkg;
    }

    /* renamed from: component3, reason: from getter */
    public final String component3() {
        return this.appLabel;
    }

    /* renamed from: component4, reason: from getter */
    public final Drawable component4() {
        return this.appIcon;
    }

    /* renamed from: component5, reason: from getter */
    public final String component5() {
        return this.title;
    }

    /* renamed from: component6, reason: from getter */
    public final String component6() {
        return this.text;
    }

    /* renamed from: component7, reason: from getter */
    public final long component7() {
        return this.postTime;
    }

    /* renamed from: component8, reason: from getter */
    public final PendingIntent component8() {
        return this.contentIntent;
    }

    public final List<IslandAction> component9() {
        return this.actions;
    }

    public final IslandNotification copy(String key, String pkg, String appLabel, Drawable appIcon, String title, String text, long postTime, PendingIntent contentIntent, List<IslandAction> actions, String subText, int r29, boolean progressIndeterminate, List<String> messages, int groupCount) {
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(pkg, "pkg");
        Intrinsics.checkNotNullParameter(appLabel, "appLabel");
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(actions, "actions");
        Intrinsics.checkNotNullParameter(subText, "subText");
        Intrinsics.checkNotNullParameter(messages, "messages");
        return new IslandNotification(key, pkg, appLabel, appIcon, title, text, postTime, contentIntent, actions, subText, r29, progressIndeterminate, messages, groupCount);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IslandNotification)) {
            return false;
        }
        IslandNotification islandNotification = (IslandNotification) other;
        return Intrinsics.areEqual(this.key, islandNotification.key) && Intrinsics.areEqual(this.pkg, islandNotification.pkg) && Intrinsics.areEqual(this.appLabel, islandNotification.appLabel) && Intrinsics.areEqual(this.appIcon, islandNotification.appIcon) && Intrinsics.areEqual(this.title, islandNotification.title) && Intrinsics.areEqual(this.text, islandNotification.text) && this.postTime == islandNotification.postTime && Intrinsics.areEqual(this.contentIntent, islandNotification.contentIntent) && Intrinsics.areEqual(this.actions, islandNotification.actions) && Intrinsics.areEqual(this.subText, islandNotification.subText) && this.progress == islandNotification.progress && this.progressIndeterminate == islandNotification.progressIndeterminate && Intrinsics.areEqual(this.messages, islandNotification.messages) && this.groupCount == islandNotification.groupCount;
    }

    public final List<IslandAction> getActions() {
        return this.actions;
    }

    public final Drawable getAppIcon() {
        return this.appIcon;
    }

    public final String getAppLabel() {
        return this.appLabel;
    }

    public final PendingIntent getContentIntent() {
        return this.contentIntent;
    }

    public final int getGroupCount() {
        return this.groupCount;
    }

    public final String getKey() {
        return this.key;
    }

    public final List<String> getMessages() {
        return this.messages;
    }

    public final String getPkg() {
        return this.pkg;
    }

    public final long getPostTime() {
        return this.postTime;
    }

    public final int getProgress() {
        return this.progress;
    }

    public final boolean getProgressIndeterminate() {
        return this.progressIndeterminate;
    }

    public final String getSubText() {
        return this.subText;
    }

    public final String getText() {
        return this.text;
    }

    public final String getTitle() {
        return this.title;
    }

    public int hashCode() {
        int hashCode = ((((this.key.hashCode() * 31) + this.pkg.hashCode()) * 31) + this.appLabel.hashCode()) * 31;
        Drawable drawable = this.appIcon;
        int hashCode2 = (((((((hashCode + (drawable == null ? 0 : drawable.hashCode())) * 31) + this.title.hashCode()) * 31) + this.text.hashCode()) * 31) + Long.hashCode(this.postTime)) * 31;
        PendingIntent pendingIntent = this.contentIntent;
        return ((((((((((((hashCode2 + (pendingIntent != null ? pendingIntent.hashCode() : 0)) * 31) + this.actions.hashCode()) * 31) + this.subText.hashCode()) * 31) + Integer.hashCode(this.progress)) * 31) + Boolean.hashCode(this.progressIndeterminate)) * 31) + this.messages.hashCode()) * 31) + Integer.hashCode(this.groupCount);
    }

    public String toString() {
        return "IslandNotification(key=" + this.key + ", pkg=" + this.pkg + ", appLabel=" + this.appLabel + ", appIcon=" + this.appIcon + ", title=" + this.title + ", text=" + this.text + ", postTime=" + this.postTime + ", contentIntent=" + this.contentIntent + ", actions=" + this.actions + ", subText=" + this.subText + ", progress=" + this.progress + ", progressIndeterminate=" + this.progressIndeterminate + ", messages=" + this.messages + ", groupCount=" + this.groupCount + ")";
    }
}
