package org.wso2.carbon.identity.custom.authenticator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticatorFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.LogoutFailedException;
import org.wso2.carbon.identity.application.authenticator.basicauth.BasicAuthenticator;
import org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorServiceComponentDataHolder;
import org.wso2.carbon.user.api.UserRealmService;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.wso2.carbon.identity.custom.authenticator.ImpersonateAuthenticatorConstants.IMPERSONATEE;

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
            }
            status = super.process(request, response, context);

            if (impersonateAuthRequest) {
                if (log.isDebugEnabled()) {
                    log.debug("Impersonate request received");
                }
                if (status == AuthenticatorFlowStatus.SUCCESS_COMPLETED) {
                    // if the authentication is successful, get the authenticated user
                    String authenticatedUser = context.getSubject().getAuthenticatedSubjectIdentifier();

                }
            }
            return status;
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

    private UserRealm getUserRealm(AuthenticationContext context, String username) {
        try {
            UserRealmService realmService = ImpersonateAuthenticatorServiceComponentDataHolder.getInstance()
                                                                                              .getRealmService();

            // get the tenant domain
            // String tenantDomain = context.getTenantDomain();
            String tenantDomain = MultitenantUtils.getTenantDomain(username);
            int tenantId = realmService.getTenantManager().getTenantId(tenantDomain);

            return (UserRealm) realmService.getTenantUserRealm(tenantId);

        } catch (UserStoreException e) {
            log.error("Error while retrieving UserRealm for the user", e);
            return null;
        }
    }
}
