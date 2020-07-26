package it.soluzione1.testzebraemdk.utility;

public interface ZebrEMDKApiWrapperCallbacks {

    void onSdkInitEnd(boolean success, String message);
    void onSdkCommandExecuted(boolean success, String profileName, String message, String xml);
}
