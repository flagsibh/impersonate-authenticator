package org.wso2.carbon.identity.custom.authenticator.factory;

import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityResponse;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityResponseFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityResponse;
import org.wso2.carbon.identity.custom.authenticator.message.ImpersonateResponse;
import org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorConstants;

import javax.servlet.http.HttpServletResponse;

/**
 * This class represents a factory for custom IdentityResponse instances which will result from the framework, after the
 * authentication step.
 *
 * @author ibandera
 * @since Monday, 08 Jan 2024, 18:14:31
 */
public class ImpersonateResponseFactory extends HttpIdentityResponseFactory {

	/**
	 * Checks if an incoming IdentityResponse from the framework can be handled by this particular factory.
	 *
	 * @param identityResponse incoming IdentityResponse from the identity framework
	 * @return true if the incoming response is of the type handled by this factory
	 */
	@Override
	public boolean canHandle(IdentityResponse identityResponse) {

		return identityResponse instanceof ImpersonateResponse;
	}

	/**
	 * Converts the received IdentityResponse instance to an HTTPResponse so that it could be sent to the calling party.
	 * This is where the logic for picking up and setting any custom parameters/headers/cookies, etc., is written.
	 *
	 * @param identityResponse the received (and handle-able IdentityResponse instance
	 * @return a corresponding HTTPResponse in the form of a builder, so that it could be built on demand
	 */
	@Override
	public HttpIdentityResponse.HttpIdentityResponseBuilder create(IdentityResponse identityResponse) {

		HttpIdentityResponse.HttpIdentityResponseBuilder builder
				= new HttpIdentityResponse.HttpIdentityResponseBuilder();

		ImpersonateResponse impersonateResponse = (ImpersonateResponse) identityResponse;

		if (StringUtils.isBlank(impersonateResponse.getToken())) {
			builder.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			builder.setStatusCode(HttpServletResponse.SC_FOUND);
			builder.addParameter(ImpersonateAuthenticatorConstants.HTTP_PARAM_TOKEN, impersonateResponse.getToken());
		}
		builder.setRedirectURL(impersonateResponse.getRedirectUrl());
		return builder;
	}

	@Override
	public void create(HttpIdentityResponse.HttpIdentityResponseBuilder httpIdentityResponseBuilder,
			IdentityResponse identityResponse) {

		this.create(identityResponse);
	}
}
