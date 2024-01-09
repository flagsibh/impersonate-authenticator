package org.wso2.carbon.identity.custom.authenticator.message;

import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityMessageContext;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityResponse;

/**
 * @author ibandera
 * @since Monday, 08 Jan 2024, 14:08:35
 */
public class ImpersonateResponse extends IdentityResponse {

	private String redirectUrl;
	private String token;

	protected ImpersonateResponse(IdentityResponseBuilder builder) {

		super(builder);
		this.redirectUrl = ((ImpersonateIdentityResponseBuilder) (builder)).redirectUrl;
		this.token = ((ImpersonateIdentityResponseBuilder) (builder)).token;
	}

	public String getRedirectUrl() {

		return redirectUrl;
	}

	public String getToken() {

		return token;
	}

	/**
	 * Here also, the builder class for the IdentityResponse subclass can be found within the class itself. Here, the
	 * parameters found in the HTTP response can be picked up and set to the response object as per the need.
	 */
	public static class ImpersonateIdentityResponseBuilder extends IdentityResponseBuilder {

		private String redirectUrl;
		private String token;

		public ImpersonateIdentityResponseBuilder(IdentityMessageContext messageContext) {

			super(messageContext);
		}

		public ImpersonateIdentityResponseBuilder redirectUrl(String redirectUrl) {

			this.redirectUrl = redirectUrl;
			return this;
		}

		public ImpersonateIdentityResponseBuilder token(String token) {

			this.token = token;
			return this;
		}

		@Override
		public ImpersonateResponse build() {

			return new ImpersonateResponse(this);

		}

	}
}
