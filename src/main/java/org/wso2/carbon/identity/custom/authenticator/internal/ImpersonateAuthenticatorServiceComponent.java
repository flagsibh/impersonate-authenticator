package org.wso2.carbon.identity.custom.authenticator.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityRequestFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.HttpIdentityResponseFactory;
import org.wso2.carbon.identity.application.mgt.AbstractInboundAuthenticatorConfig;
import org.wso2.carbon.identity.custom.authenticator.factory.ImpersonateRequestFactory;
import org.wso2.carbon.identity.custom.authenticator.factory.ImpersonateResponseFactory;
import org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorConfig;
import org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorServiceComponentDataHolder;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

import java.util.Hashtable;

/**
 * This is the OSGi service component for the custom inbound authenticator. This will enable the bundle (jar) to
 * activate the specified service and register themselves so that the Identity Server is able to see them and use them
 * when a matching request arrives.
 *
 * @author ibandera
 * @scr.component name="org.wso2.carbon.identity.custom.authenticator.impersonate.component" immediate="true"
 * @scr.reference name="config.context.service" immediate="true"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1" policy="dynamic"
 * bind="setConfigurationContextService" unbind="unsetConfigurationContextService"
 * @scr.reference name="user.realmservice.default" interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1" policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 * @scr.reference name="osgi.httpservice" interface="org.osgi.service.http.HttpService" cardinality="1..1"
 * policy="dynamic" bind="setHttpService" unbind="unsetHttpService"
 * @since Thursday, 21 Dec 2023, 18:46:41
 */
public class ImpersonateAuthenticatorServiceComponent {

	private static final Log log = LogFactory.getLog(ImpersonateAuthenticatorServiceComponent.class);

	@SuppressWarnings("java:S1149")
	protected void activate(ComponentContext componentContext) {

		try {
			ImpersonateAuthenticatorConfig config = new ImpersonateAuthenticatorConfig();
			Hashtable<String, String> dictionary = new Hashtable<>();
			componentContext.getBundleContext().registerService(AbstractInboundAuthenticatorConfig.class, config,
					dictionary);

			componentContext.getBundleContext()
			                .registerService(HttpIdentityRequestFactory.class.getName(),
					                new ImpersonateRequestFactory(), null);

			componentContext.getBundleContext()
			                .registerService(HttpIdentityResponseFactory.class.getName(),
					                new ImpersonateResponseFactory(), null);

			if (log.isDebugEnabled()) {
				log.debug("ImpersonateAuthenticator activated successfully");
			}
		} catch (Exception e) {
			log.error("Error while activating ImpersonateAuthenticator", e);
			throw new RuntimeException("Error while activating ImpersonateAuthenticator", e);
		}
	}

	protected void deactivate(ComponentContext componentContext) {

		ImpersonateAuthenticatorServiceComponentDataHolder.getInstance().setBundleContext(null);
		if (log.isDebugEnabled()) {
			log.debug("Deactivating ImpersonateAuthenticator");
		}
	}

	protected void setConfigurationContextService(ConfigurationContextService configurationContextService) {

		ImpersonateAuthenticatorServiceComponentDataHolder.getInstance()
		                                                  .setConfigurationContextService(configurationContextService);
		if (log.isDebugEnabled()) {
			log.debug("ConfigurationContextService bound successfully");
		}
	}

	protected void unsetConfigurationContextService(ConfigurationContextService configurationContextService) {

		ImpersonateAuthenticatorServiceComponentDataHolder.getInstance()
		                                                  .setConfigurationContextService(null);
		if (log.isDebugEnabled()) {
			log.debug("ConfigurationContextService unbound successfully");
		}
	}

	protected void setRealmService(RealmService realmService) {

		ImpersonateAuthenticatorServiceComponentDataHolder.getInstance().setRealmService(realmService);
		if (log.isDebugEnabled()) {
			log.debug("RealmService bound successfully");
		}
	}

	protected void unsetRealmService(RealmService realmService) {

		ImpersonateAuthenticatorServiceComponentDataHolder.getInstance().setRealmService(null);
		if (log.isDebugEnabled()) {
			log.debug("RealmService unbound successfully");
		}
	}

	protected void unsetHttpService(HttpService httpService) {

		ImpersonateAuthenticatorServiceComponentDataHolder.getInstance().setHttpService(null);
		if (log.isDebugEnabled()) {
			log.debug("HttpService unbound successfully");
		}
	}

	protected void setHttpService(HttpService httpService) {

		ImpersonateAuthenticatorServiceComponentDataHolder.getInstance().setHttpService(httpService);
		if (log.isDebugEnabled()) {
			log.debug("HttpService bound successfully");
		}
	}
}
