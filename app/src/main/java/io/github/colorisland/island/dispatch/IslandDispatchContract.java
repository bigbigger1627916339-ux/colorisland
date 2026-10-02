package io.github.colorisland.island.dispatch;

import kotlin.Metadata;

/* compiled from: IslandDispatchContract.kt */
/* loaded from: classes3.dex */
public final class IslandDispatchContract {
    public static final int $stable = 0;
    public static final String ACTION_CANCEL = "io.github.colorisland.action.CANCEL_ISLAND";
    public static final String ACTION_SHOW = "io.github.colorisland.action.SHOW_ISLAND";
    public static final long DEFAULT_TIMEOUT_MS = 5000;
    public static final String EXTRA_ACTION_INTENTS = "io.github.colorisland.extra.ACTION_INTENTS";
    public static final String EXTRA_ACTION_TITLES = "io.github.colorisland.extra.ACTION_TITLES";
    public static final String EXTRA_APP_LABEL = "io.github.colorisland.extra.APP_LABEL";
    public static final String EXTRA_CONTENT_INTENT = "io.github.colorisland.extra.CONTENT_INTENT";
    public static final String EXTRA_ICON = "io.github.colorisland.extra.ICON";
    public static final String EXTRA_ID = "io.github.colorisland.extra.ID";
    public static final String EXTRA_PERSISTENT = "io.github.colorisland.extra.PERSISTENT";
    public static final String EXTRA_PKG = "io.github.colorisland.extra.PKG";
    public static final String EXTRA_SUB_TEXT = "io.github.colorisland.extra.SUB_TEXT";
    public static final String EXTRA_TEXT = "io.github.colorisland.extra.TEXT";
    public static final String EXTRA_TIMEOUT_MS = "io.github.colorisland.extra.TIMEOUT_MS";
    public static final String EXTRA_TITLE = "io.github.colorisland.extra.TITLE";
    public static final IslandDispatchContract INSTANCE = new IslandDispatchContract();
    public static final int MAX_ACTIONS = 3;
    public static final int MAX_TEXT_LEN = 200;
    public static final long MAX_TIMEOUT_MS = 60000;
    public static final int MAX_TITLE_LEN = 50;
    public static final String PERMISSION = "io.github.colorisland.permission.SEND_ISLAND";
    public static final String TAG = "ColorIslandDispatch";

    private IslandDispatchContract() {
    }
}
