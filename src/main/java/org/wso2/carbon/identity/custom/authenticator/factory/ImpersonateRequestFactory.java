package org.wso2.carbon.identity.custom.authenticator.factory;

import org.wso2.carbon.identity.application.authentication.framework.inbound.FrameworkClientException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityRequestFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityRequest;
import org.wso2.carbon.identity.custom.authenticator.message.ImpersonateRequest;
import org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	/**
	 * Checks whether an incoming request hitting the "/identity" servlet should be handled by this particular custom
	 * IdentityRequest factory.
	 *
	 * @param request  the request parameter coming from the servlet
	 * @param response the response parameter coming from the servlet
	 * @return {@code true} if the request is of a type which can be handled by this particular IdentityRequest factory
	 */
	@Override
	public boolean canHandle(HttpServletRequest request, HttpServletResponse response) {

		// Return true if the incoming request to the identity servlet contains the custom URL param
		return request.getParameter(ImpersonateAuthenticatorConstants.HTTP_PARAM_IMPERSONATEE) != null;
	}

	/**
	 * Returns a new instance of the custom IdentityRequest object, which will then be passed to the processor.
	 *
	 * @param request  the HTTP request from the servlet
	 * @param response the response parameter coming from the servlet
	 * @return a builder for {@link ImpersonateRequest}, which is a subclass of IdentityRequest
	 */
	@Override
	public IdentityRequest.IdentityRequestBuilder create(HttpServletRequest request, HttpServletResponse response)
			throws FrameworkClientException {

		return new ImpersonateRequest.ImpersonateIdentityRequestBuilder(request, response);
	}
}
