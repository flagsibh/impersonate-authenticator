package org.wso2.carbon.identity.custom.oauth2.token.handlers.grant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.common.OAuthConstants;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.identity.oauth2.model.AccessTokenDO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.identity.oauth2.validators.TokenValidationHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ibandera
 * @since Tuesday, 16 Jan 2024, 17:18:08
 */
public class ImpersonationGrantHandler extends AbstractAuthorizationGrantHandler {

	private static final String ACCESS_TOKEN = "access_token";
	private static final String TARGET_USER = "target_user";
	private static final String BEARER = "Bearer";
	private static Log log = LogFactory.getLog(ImpersonationGrantHandler.class);

	@Override
	public boolean validateGrant(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

		if (!super.validateGrant(tokReqMsgCtx)) {
			return false;
		}
		boolean targetUserExists = false;
		boolean isUserAuthorized = false;
		String accessToken = null;
		String targetUser = null;
		List<String> scopes = new ArrayList<>();

		RequestParameter[] requestParameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();
		for (RequestParameter parameter : requestParameters) {
			if (ACCESS_TOKEN.equalsIgnoreCase(parameter.getKey().trim())) {
				accessToken = parameter.getValue()[0].trim();
			} else if (TARGET_USER.equalsIgnoreCase(parameter.getKey().trim())) {
				targetUser = parameter.getValue()[0].trim();
			} else if (OAuthConstants.OAuth20Params.SCOPE.equalsIgnoreCase(parameter.getKey().trim())) {
				scopes.addAll(Arrays.asList(parameter.getValue()[0].split(" ")));
			}
		}

		validateAccessToken(accessToken, BEARER);
		AccessTokenDO accessTokenDO = OAuth2Util.getAccessTokenDOfromTokenIdentifier(accessToken);

		if (log.isDebugEnabled()) {
			log.debug("Access token: " + accessToken + "\n" + "Target user: " + targetUser + "\n" +
			          "User: " + accessTokenDO.getAuthzUser().getUserName() + "\n");
		}

		return true;
	}

	private void validateAccessToken(String accessToken, String accessTokenType) throws IdentityOAuth2Exception {

		TokenValidationHandler tokenValidationHandler = TokenValidationHandler.getInstance();
		OAuth2TokenValidationRequestDTO validationRequestDTO = new OAuth2TokenValidationRequestDTO();
		OAuth2TokenValidationRequestDTO.OAuth2AccessToken token = validationRequestDTO.new OAuth2AccessToken();
		token.setIdentifier(accessToken);
		token.setTokenType(accessTokenType);
		try {
			if (!tokenValidationHandler.validate(validationRequestDTO).isValid()) {
				throw new IdentityOAuth2Exception("Invalid access token");
			}
		} catch (IdentityOAuth2Exception e) {
			log.error("Error while validating access token", e);
			throw e;
		}
	}
}
