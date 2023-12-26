package org.wso2.carbon.identity.custom.authenticator;

/**
 * @author ibandera
 * @since Tuesday, 26 Dec 2023, 10:48:53
 */
public abstract class ImpersonateAuthenticatorConstants {

    public static final String AUTHENTICATOR_NAME = "ImpersonateAuthenticator";
    public static final String AUTHENTICATOR_FRIENDLY_NAME = "impersonate";
    public static final String IMPERSONATEE = "impersonatee";
    private static final String DEFAULT_IMPERSONATE_ADMIN_ROLE = "Internal/impadmin";
    private static final String DEFAULT_IMPERSONATE_USER_ROLE = "Internal/impuser";

    private ImpersonateAuthenticatorConstants() {
        // private constructor to prevent instantiation
    }
}
