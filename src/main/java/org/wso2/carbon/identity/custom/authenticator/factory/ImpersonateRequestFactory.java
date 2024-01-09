package org.wso2.carbon.identity.custom.authenticator.factory;

import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityRequestFactory;

/**
 * This class represents a factory for custom IdentityRequest instances which will be passed to the framework for
 * authentication.
 * <p>
 * Essentially, the conversion of the protocol-specific HTTP request to the framework-understood IdentityRequest takes
 * place here.
 *
 * @author ibandera
 * @since Monday, 08 Jan 2024, 18:09:29
 */
public class ImpersonateRequestFactory extends HttpIdentityRequestFactory {

}
