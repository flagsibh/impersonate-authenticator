package org.wso2.carbon.identity.custom.authenticator.processor;

import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityMessageContext;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityProcessor;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityRequest;
import org.wso2.carbon.identity.application.authentication.framework.inbound.IdentityResponse;

/**
 * The processor class represents the core functionality of the inbound authenticator. Being a subclass of the
 * {@link IdentityProcessor} class, the developer is required to override certain methods which dictate the functional
 * elements.
 * <p>
 * In the process method, the developer is able to leverage standard methods offered by the framework to send the
 * request on to the framework (buildResponseForFrameworkLogin()) as well as handle the response after going through the
 * authentication step (processResponseFromFrameworkLogin()).
 * <p>
 * The getRelyingPartyId() method is used for correlating the protocol of the incoming authentication request (i.e. the
 * protocol represented by this processor) against a particular SP in the Identity Server which contains the information
 * related to the actual authentication, either through local and/or federated authenticators.
 *
 * @author ibandera
 * @since Monday, 08 Jan 2024, 17:50:01
 */
public class ImpersonateRequestProcessor extends IdentityProcessor {

	@Override
	public IdentityResponse.IdentityResponseBuilder process(IdentityRequest identityRequest) throws FrameworkException {

		return null;
	}

	@Override
	public String getCallbackPath(IdentityMessageContext identityMessageContext) {

		return null;
	}

	@Override
	public String getRelyingPartyId() {

		return null;
	}

	@Override
	public String getRelyingPartyId(IdentityMessageContext identityMessageContext) {

		return null;
	}

	@Override
	public boolean canHandle(IdentityRequest identityRequest) {

		return false;
	}
}
