package eu.neosurance.sdk.interactors.user;

import android.util.Log;

import eu.neosurance.sdk.NSRAuth;
import eu.neosurance.sdk.NSRUser;
import eu.neosurance.sdk.processors.auth.AuthProcessor;
import eu.neosurance.sdk.data.user.UserRepository;
import eu.neosurance.sdk.interactors.DefaultUseCase;

public class RegisterUser implements DefaultUseCase<NSRUser> {

    private static final String TAG = RegisterUser.class.getCanonicalName();

    private final UserRepository userRepository;
    private final AuthProcessor authProcessor;

    public RegisterUser(UserRepository userRepository, AuthProcessor authProcessor) {
        this.userRepository = userRepository;
        this.authProcessor = authProcessor;
    }

    @Override
    public void execute(NSRUser user) {
        Log.d(TAG, "registerUser");
        try {
            userRepository.setUser(user);
            authProcessor.authorize();
        } catch (Exception e) {
            Log.e(TAG, "registerUser", e);
        }
    }
}
