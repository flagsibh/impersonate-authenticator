package org.wso2.carbon.identity.custom.authenticator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticatorFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.LogoutFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authenticator.basicauth.BasicAuthenticator;
import org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorServiceComponentDataHolder;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.wso2.carbon.identity.custom.authenticator.ImpersonateAuthenticatorConstants.*;

/**
 * @author ibandera
 * @since Friday, 22 Dec 2023, 12:08:53
 */
public class ImpersonateAuthenticator extends BasicAuthenticator {

	private static final Log log = LogFactory.getLog(ImpersonateAuthenticator.class);

	@Override
	public boolean canHandle(HttpServletRequest request) {

		String impersonatee = request.getParameter(IMPERSONATEE);
		return impersonatee != null && super.canHandle(request);
	}

	@Override
	public AuthenticatorFlowStatus process(HttpServletRequest request, HttpServletResponse response,
			AuthenticationContext context) throws AuthenticationFailedException, LogoutFailedException {
		//check if it is a logout request
		if (context.isLogoutRequest()) {
			return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
		} else {
			// start a basic authentication flow
			boolean impersonateAuthRequest = false;
			AuthenticatorFlowStatus status = null;
			// check if it is an impersonate request
			if (request.getParameter(IMPERSONATEE) != null || context.getProperty(IMPERSONATEE) != null) {
				impersonateAuthRequest = true;
				// set it to the context, if it is not already set
				if (context.getProperty(IMPERSONATEE) == null) {
					context.setProperty(IMPERSONATEE, request.getParameter(IMPERSONATEE));
				}
				status = super.process(request, response, context);
			}

			if (impersonateAuthRequest) {
				if (log.isDebugEnabled()) {
					log.debug("Impersonate request received");
				}
				if (status.equals(AuthenticatorFlowStatus.SUCCESS_COMPLETED)) {
					// if the authentication is successful, get the authenticated user
					AuthenticatedUser authenticatedUser = context.getSubject();
					Map<String, String> configParams = getAuthenticatorConfig().getParameterMap();
					//get the uer real of the authenticated user
					try {
						UserRealm userRealm = getUserRealm(authenticatedUser.getAuthenticatedSubjectIdentifier());
						//get the roles of the authenticated user
						String[] roles = userRealm.getUserStoreManager()
						                          .getRoleListOfUser(
								                          MultitenantUtils.getTenantAwareUsername(
										                          authenticatedUser.getAuthenticatedSubjectIdentifier()));
						//check if the authenticated user has the impAdmin role
						boolean hasRole = false;
						String impAdminRole = configParams.get(IMPERSONATE_ADMIN_ROLE);
						if (impAdminRole == null || impAdminRole.isEmpty()) {
							impAdminRole = DEFAULT_IMPERSONATE_ADMIN_ROLE;
						}
						for (String role : roles) {
							if (role.equals(impAdminRole)) {
								hasRole = true;
								break;
							}
						}
						if (hasRole) {
							//get the impersonatee's roles
							UserRealm impUserRealm = getUserRealm((String) context.getProperty(IMPERSONATEE));
							String[] impUserRoles =
									impUserRealm.getUserStoreManager().getRoleListOfUser(MultitenantUtils
											.getTenantAwareUsername((String) context.getProperty(IMPERSONATEE)));

							// set default impuser role if not set in config params
							String impUserRole = configParams.get(IMPERSONATE_USER_ROLE);
							if (impUserRole == null || impUserRole.isEmpty()) {
								impUserRole = DEFAULT_IMPERSONATE_USER_ROLE;
							}
							for (String role : impUserRoles) {
								if (role.equals(impUserRole)) {
									log.debug("Impersonatee is identified");
									// set subject as the impersonatee
									context.setSubject(
											AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(
													(String) context.getProperty(IMPERSONATEE)));
									return status;
								}
							}

						}
					} catch (UserStoreException e) {
						String errorMessage = "Unable to get the user realm";
						log.error(errorMessage, e);
						throw new AuthenticationFailedException(errorMessage, e);
					}
				}
				return status;
			} else
				return super.process(request, response, context);
		}
	}

	@Override
	public String getName() {

		return ImpersonateAuthenticatorConstants.AUTHENTICATOR_NAME;
	}

	@Override
	public String getFriendlyName() {

		return ImpersonateAuthenticatorConstants.AUTHENTICATOR_FRIENDLY_NAME;
	}

	private UserRealm getUserRealm(String username) throws UserStoreException {
		//RealmService realmService = IdentityTenantUtil.getRealmService();
		RealmService realmService = ImpersonateAuthenticatorServiceComponentDataHolder.getInstance()
		                                                                              .getRealmService();

		// get the tenant domain
		// String tenantDomain = context.getTenantDomain();
		String tenantDomain = MultitenantUtils.getTenantDomain(username);
		int tenantId = realmService.getTenantManager().getTenantId(tenantDomain);
		// int tenantId = IdentityTenantUtil.getTenantId(tenantDomain);

		return (UserRealm) realmService.getTenantUserRealm(tenantId);

	}
}
