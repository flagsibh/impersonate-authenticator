package org.wso2.carbon.identity.custom.authenticator.util;

/**
 * @author ibandera
 * @since Tuesday, 26 Dec 2023, 10:48:53
 */
public abstract class ImpersonateAuthenticatorConstants {

	public static final String AUTHENTICATOR_NAME = "ImpersonateAuthenticator";
	public static final String AUTHENTICATOR_TYPE_NAME = "impersonate-authenticator-type";
	public static final String AUTHENTICATOR_FRIENDLY_NAME = "impersonate";
	public static final String HTTP_PARAM_IMPERSONATEE = "impersonatee";
	public static final String HTTP_PARAM_TOKEN = "token";
	public static final String CONFIGURATION_NAME = AUTHENTICATOR_TYPE_NAME;
	public static final String CONFIGURATION_FRIENDLY_NAME = "WSO2 Custom Impersonate Authenticator Configuration";
	public static final String RELYING_PARTY_KEY_PROPERTY = "relying-party-key";
	public static final String REDIRECT_URL_PROPERTY = "redirect-url";
	public static final String IDENTITY_SERVLET_URL = "identity";
	private static final String DEFAULT_IMPERSONATE_ADMIN_ROLE = "Internal/impadmin";
	private static final String DEFAULT_IMPERSONATE_USER_ROLE = "Internal/impuser";

	private ImpersonateAuthenticatorConstants() {
		// private constructor to prevent instantiation
	}
}
