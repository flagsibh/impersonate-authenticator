package org.wso2.carbon.identity.custom.authenticator.processor;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.application.authentication.framework.inbound.*;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticationResult;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkConstants;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.application.common.model.InboundAuthenticationConfig;
import org.wso2.carbon.identity.application.common.model.InboundAuthenticationRequestConfig;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.custom.authenticator.message.ImpersonateRequest;
import org.wso2.carbon.identity.custom.authenticator.message.ImpersonateResponse;
import org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorConfig;
import org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * The processor class represents the core functionality of the inbound authenticator. Being a subclass of the
 * {@link IdentityProcessor} class, the developer is required to override certain methods which dictate the functional
 * elements.
 * <p>
 * In the process method, the developer is able to leverage standard methods offered by the framework to send the
 * request on to the framework ({@link #buildResponseForFrameworkLogin(IdentityMessageContext)}) as well as handle the
 * response after going through the authentication step
 * ({@link #processResponseFromFrameworkLogin(IdentityMessageContext, IdentityRequest)}).
 * <p>
 * The {@link #getRelyingPartyId()} method is used for correlating the protocol of the incoming authentication request
 * (i.e. the protocol represented by this processor) against a particular SP in the Identity Server which contains the
 * information related to the actual authentication, either through local and/or federated authenticators.
 *
 * @author ibandera
 * @since Monday, 08 Jan 2024, 17:50:01
 */
public class ImpersonateRequestProcessor extends IdentityProcessor {

	private ImpersonateAuthenticatorConfig config = null;
	private String relyingPartyId;

	public ImpersonateRequestProcessor(ImpersonateAuthenticatorConfig config) {

		this.config = config;
	}

	@Override
	public String getRelyingPartyId() {

		return this.relyingPartyId;
	}

	@Override
	public String getRelyingPartyId(IdentityMessageContext identityMessageContext) {

		return this.relyingPartyId;
	}

	@Override
	public boolean canHandle(IdentityRequest identityRequest) {

		if (identityRequest instanceof ImpersonateRequest) {
			this.relyingPartyId = ((ImpersonateRequest) identityRequest).getRequest()
			                                                            .getParameter(
					                                                            FrameworkConstants.REQUEST_PARAM_SP);
		} else if (StringUtils.isNotBlank(identityRequest.getParameter(FrameworkConstants.REQUEST_PARAM_SP))) {
			this.relyingPartyId = identityRequest.getParameter(FrameworkConstants.REQUEST_PARAM_SP);
		}
		return StringUtils.isNotBlank(this.relyingPartyId);
	}

	@Override
	public IdentityResponse.IdentityResponseBuilder process(IdentityRequest identityRequest) throws FrameworkException {

		IdentityMessageContext<String, String> messageContext =
				new IdentityMessageContext<>(identityRequest, new HashMap<>());
		ImpersonateResponse.ImpersonateIdentityResponseBuilder responseBuilder =
				new ImpersonateResponse.ImpersonateIdentityResponseBuilder(messageContext);
		String sessionId = identityRequest.getParameter(InboundConstants.RequestProcessor.CONTEXT_KEY);
		if (sessionId != null) {
			AuthenticationResult authenticationResult =
					processResponseFromFrameworkLogin(messageContext, identityRequest);
			if (authenticationResult != null && authenticationResult.isAuthenticated()) {
				String userName = authenticationResult.getSubject().getUserName();
				responseBuilder.token(generateToken(userName));
			} else {
				responseBuilder.token(null);
			}
			responseBuilder.redirectUrl(
					getPropertyValue(identityRequest, ImpersonateAuthenticatorConstants.REDIRECT_URL_PROPERTY));
			return responseBuilder;
		} else {
			return buildResponseForFrameworkLogout(messageContext);
		}
	}

	@Override
	public String getCallbackPath(IdentityMessageContext identityMessageContext) {

		// get the identity servlet url from the identity utility class
		return IdentityUtil.getServerURL(ImpersonateAuthenticatorConstants.IDENTITY_SERVLET_URL, false, false);
	}

	@Override
	public String getName() {

		return ImpersonateAuthenticatorConstants.AUTHENTICATOR_TYPE_NAME;
	}

	private String getPropertyValue(IdentityRequest request, String property) {

		String propertyValue = null;
		Map<String, Property> props = getInboundAuthenticatorPropertyArray(request);
		for (Object obj : props.entrySet()) {
			Map.Entry pair = (Map.Entry) obj;
			if (property.equals(pair.getKey())) {
				Property prop = (Property) pair.getValue();
				propertyValue = prop.getValue();
			}
		}
		return propertyValue;
	}

	private String generateToken(String userName) {

		if (StringUtils.isNotBlank(userName)) {
			return Base64.encodeBase64String(userName.getBytes());
		}
		return null;
	}

	private Map<String, Property> getInboundAuthenticatorPropertyArray(IdentityRequest request) {

		Map<String, Property> properties = new HashMap<>();
		ApplicationManagementService applicationManagementService = ApplicationManagementService.getInstance();
		try {
			ServiceProvider serviceProvider =
					applicationManagementService.getServiceProviderByClientId(this.relyingPartyId, this.getName(),
							request.getTenantDomain());
			// iterate over the configs in the service provider
			InboundAuthenticationConfig inboundAuthConfig = serviceProvider.getInboundAuthenticationConfig();
			for (InboundAuthenticationRequestConfig requestConfig : inboundAuthConfig.getInboundAuthenticationRequestConfigs()) {
				if (StringUtils.equals(requestConfig.getInboundAuthType(), getName()) &&
				    StringUtils.equals(requestConfig.getInboundAuthKey(), relyingPartyId)) {
					for (Property property : requestConfig.getProperties()) {
						properties.put(property.getName(), property);
					}
				}
			}
		} catch (IdentityApplicationManagementException e) {
			throw new RuntimeException("Error while reading inbound authenticator properties");
		}

		return properties;
	}
}
