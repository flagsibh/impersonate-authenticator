package org.wso2.carbon.identity.custom.oauth2.token.handlers;

import org.apache.oltu.oauth2.common.validators.AbstractValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static org.wso2.carbon.identity.custom.oauth2.token.handlers.ImpersonationGrantHandler.ACCESS_TOKEN;
import static org.wso2.carbon.identity.custom.oauth2.token.handlers.ImpersonationGrantHandler.TARGET_USER;

/**
 * @author ibandera
 * @since Thursday, 18 Jan 2024, 19:05:43
 */
public class ImpersonationGrantValidator extends AbstractValidator<HttpServletRequest> {

	public ImpersonationGrantValidator() {

		requiredParams.addAll(Arrays.asList(ACCESS_TOKEN, TARGET_USER));
	}

}
