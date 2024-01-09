package org.wso2.carbon.identity.custom.authenticator.message;

import org.wso2.carbon.identity.application.authentication.framework.inbound.FrameworkClientException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class represents a subclass of IdentityRequest, which can be used to inject additional properties and parameters
 * from the HTTP request coming from the servlet to the IdentityRequest bound for the authentication framework.
 *
 * @author ibandera
 * @since Monday, 08 Jan 2024, 13:51:49
 */
public class ImpersonateRequest extends IdentityRequest {

	private transient HttpServletRequest request;
	private transient HttpServletResponse response;

	protected ImpersonateRequest(IdentityRequestBuilder builder) throws FrameworkClientException {

		super(builder);
		this.request = ((ImpersonateIdentityRequestBuilder) (builder)).getRequest();
		this.response = ((ImpersonateIdentityRequestBuilder) (builder)).getResponse();
	}

	public HttpServletRequest getRequest() {

		return request;
	}

	public HttpServletResponse getResponse() {

		return response;
	}

	/**
	 * The builder for the request class is maintained here as a subclass. The builder is required because once it is
	 * built, the IdentityRequest object is treated as immutable within the framework and cannot be used for adding
	 * additional custom properties from the HTTP request.
	 */
	public static class ImpersonateIdentityRequestBuilder extends IdentityRequestBuilder {

		private HttpServletRequest request;
		private HttpServletResponse response;

		public ImpersonateIdentityRequestBuilder(HttpServletRequest request, HttpServletResponse response) {

			super(request, response);
			this.request = request;
			this.response = response;
		}

		@Override
		public ImpersonateRequest build() throws FrameworkClientException {

			return new ImpersonateRequest(this);
		}
	}
}
