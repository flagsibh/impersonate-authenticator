package org.wso2.carbon.identity.custom.authenticator.factory;

import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityResponse;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityResponseFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityResponse;

/**
 * This class represents a factory for custom IdentityResponse instances which will result from the framework, after the
 * authentication step.
 *
 * @author ibandera
 * @since Monday, 08 Jan 2024, 18:14:31
 */
public class ImpersonateResponseFactory extends HttpIdentityResponseFactory {

	@Override
	public boolean canHandle(IdentityResponse identityResponse) {

		return false;
	}

	@Override
	public HttpIdentityResponse.HttpIdentityResponseBuilder create(IdentityResponse identityResponse) {

		return null;
	}

	@Override
	public void create(HttpIdentityResponse.HttpIdentityResponseBuilder httpIdentityResponseBuilder,
			IdentityResponse identityResponse) {

	}
}
