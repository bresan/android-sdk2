package eu.neosurance.sdk.interactors;

import org.junit.Before;
import org.junit.Test;

import eu.neosurance.sdk.NSRAuth;
import eu.neosurance.sdk.NSRUser;
import eu.neosurance.sdk.data.user.UserRepository;
import eu.neosurance.sdk.interactors.user.RegisterUser;
import eu.neosurance.sdk.processors.auth.AuthProcessor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RegisterUserTest {


    private UserRepository userRepository;
    private AuthProcessor authProcessor;
    private RegisterUser registerUser;

    @Before
    public void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        authProcessor = mock(AuthProcessor.class);
        registerUser = new RegisterUser(userRepository, authProcessor);
    }

    @Test
    public void shouldCallSetUserOnExecute() throws Exception {
        NSRUser user = new NSRUser();
        registerUser.execute(user);

        verify(userRepository, times(1)).setUser(user);
    }


    @Test
    public void shouldCallAuthorizeOnExecute() throws Exception {
        NSRUser user = new NSRUser();
        registerUser.execute(user);

        verify(authProcessor, times(1)).authorize();
    }
}