package eu.neosurance.sdk;

import android.util.Log;

import static eu.neosurance.sdk.utils.PrecheckUtils.isInvalidAndroidVersion;

public class InstanceManager {

    private static final String TAG = InstanceManager.class.getCanonicalName();

    public static NSR setupNewInstance(NSR instance) {
        NSR nsr = null;
        Log.d(TAG, "making instance...");
        if (!isInvalidAndroidVersion) {
            try {
                nsr = setupSecurityDelegate(instance);
                nsr = setupWorkflowDelegate(nsr);
                nsr.getJobManager().initJob();
            } catch (Exception e) {
                Log.e(TAG, "getInstance", e);
            }
        }

        return nsr;
    }

    private static NSR setupWorkflowDelegate(NSR instance) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        String s;
        s = instance.getDataManager().getWorkflowRepository().getClassName();
        if (s != null) {
            Log.d(TAG, "making workflowDelegate... " + s);
            instance.setWorkflowDelegate((NSRWorkflowDelegate) Class.forName(s).newInstance());
        }

        return instance;
    }

    private static NSR setupSecurityDelegate(NSR instance) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        String s = instance.getDataManager().getSecurityRepository().getClassName();
        if (s != null) {
            Log.d(TAG, "making securityDelegate... " + s);
            instance.setSecurityDelegate((NSRSecurityDelegate) Class.forName(s).newInstance());
        } else {
            Log.d(TAG, "making securityDelegate... NSRDefaultSecurity");
            instance.setSecurityDelegate(new NSRDefaultSecurity());
        }

        return instance;
    }
}
