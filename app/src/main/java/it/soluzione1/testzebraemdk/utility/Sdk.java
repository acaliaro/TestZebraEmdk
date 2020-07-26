package it.soluzione1.testzebraemdk.utility;

import com.symbol.emdk.personalshopper.CradleConfig;
import com.symbol.emdk.personalshopper.CradleException;

public abstract class Sdk {

    Sdk() {
    }

    public abstract void release();
    public abstract void init();
    public abstract void executeCommand(String profileName);
    public abstract String SdkVersion();
    public abstract String ExtensionVersion();
    public abstract String BarcodeVersion();
    public abstract boolean isSdkReady();

}
