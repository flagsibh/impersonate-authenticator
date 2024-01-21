package org.wso2.carbon.identity.custom.oauth2.token.handlers;

import com.nimbusds.jwt.JWTParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.base.IdentityRuntimeException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth.common.OAuthConstants;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationResponseDTO;
import org.wso2.carbon.identity.oauth2.model.AccessTokenDO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.identity.oauth2.validators.TokenValidationHandler;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ibandera
 * @since Tuesday, 16 Jan 2024, 17:18:08
 */
public class ImpersonationGrantHandler extends AbstractAuthorizationGrantHandler {

	public static final String ACCESS_TOKEN = "access_token";
	public static final String TARGET_USER = "target_user";
	public static final String IMPERSONATE = "impersonate";
	private static final String TOKEN_TYPE = "jwt";
	private static final List<String> PERMITTED_ROLES =
			Arrays.asList("admin impersonator Internal/ Internal/impadmin".split(" "));
	private static final Log log = LogFactory.getLog(ImpersonationGrantHandler.class);

	@Override
	public boolean validateGrant(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

		if (!super.validateGrant(tokReqMsgCtx)) {
			return false;
		}

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

		validateAccessToken(accessToken, TOKEN_TYPE);
		//AccessTokenDO accessTokenDO = OAuth2Util.getAccessTokenDOfromTokenIdentifier(getTokenIdentifier(accessToken));

		//if (log.isDebugEnabled()) {
		//	log.debug("Access token: " + accessToken + "\n" + "Target user: " + targetUser + "\n" +
		//	          "User: " + accessTokenDO.getAuthzUser().getUserName() + "\n");
		//}

		boolean targetUserExists = targetUserExists(targetUser);
		boolean isUserAuthorized = true; //isUserAuthorized(accessToken);

		if (targetUserExists && isUserAuthorized) {
			tokReqMsgCtx.setAuthorizedUser(OAuth2Util.getUserFromUserName(targetUser));
			scopes.add(IMPERSONATE);
			tokReqMsgCtx.setScope(scopes.toArray(new String[0]));
		} else {
			throw new IdentityOAuth2Exception("Target user does not exist or user is not authorized to impersonate");
		}

		return true;
	}

	private void validateAccessToken(String accessToken, String accessTokenType) throws IdentityOAuth2Exception {

		TokenValidationHandler tokenValidationHandler = TokenValidationHandler.getInstance();
		OAuth2TokenValidationRequestDTO validationRequestDTO = new OAuth2TokenValidationRequestDTO();
		OAuth2TokenValidationRequestDTO.OAuth2AccessToken token = validationRequestDTO.new OAuth2AccessToken();
		token.setIdentifier(getTokenIdentifier(accessToken));
		token.setTokenType(accessTokenType);
		validationRequestDTO.setAccessToken(token);
		try {
			OAuth2TokenValidationResponseDTO validation = tokenValidationHandler.validate(validationRequestDTO);
			if (!validation.isValid()) {
				throw new IdentityOAuth2Exception("Invalid access token");
			}
		} catch (IdentityOAuth2Exception e) {
			log.error("Error while validating access token", e);
			throw e;
		}
	}

	private boolean targetUserExists(String targetUser) {

		if (targetUser == null) {
			return false;
		}

		String tenantDomain = MultitenantUtils.getTenantDomain(targetUser);
		String userName = MultitenantUtils.getTenantAwareUsername(targetUser);
		// get the full username containing domain
		String fullUserName = userName + "@" + tenantDomain;

		try {
			int tenantId = IdentityTenantUtil.getTenantIdOfUser(fullUserName);
			return IdentityTenantUtil.getRealmService()
			                         .getTenantUserRealm(tenantId)
			                         .getUserStoreManager()
			                         .isExistingUser(targetUser);
		} catch (IdentityRuntimeException | UserStoreException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	private boolean isUserAuthorized(String accessToken) {

		try {
			AccessTokenDO accessTokenDO =
					OAuth2Util.getAccessTokenDOfromTokenIdentifier(getTokenIdentifier(accessToken));
			String userName = accessTokenDO.getAuthzUser().getUserName();
			String fullUserName = MultitenantUtils.getTenantAwareUsername(userName) + "@" +
			                      MultitenantUtils.getTenantDomain(userName);
			int tenantId = IdentityTenantUtil.getTenantIdOfUser(fullUserName);

			List<String> roles = Arrays.asList(IdentityTenantUtil.getRealmService()
			                                                     .getTenantUserRealm(tenantId)
			                                                     .getUserStoreManager()
			                                                     .getRoleListOfUser(userName));
			roles.retainAll(PERMITTED_ROLES);
			if (!roles.isEmpty()) {
				return true;
			}
		} catch (IdentityOAuth2Exception | UserStoreException e) {
			log.error("Error while validating user", e);
		}
		return false;
	}

	private String getTokenIdentifier(String accessToken) {

		try {
			return JWTParser.parse(accessToken).getJWTClaimsSet().getJWTID();
		} catch (ParseException e) {
			log.error("Error while parsing access token", e);
			return null;
		}

	}

}
