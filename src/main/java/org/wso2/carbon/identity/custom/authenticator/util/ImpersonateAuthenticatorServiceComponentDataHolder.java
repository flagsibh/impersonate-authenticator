package org.wso2.carbon.identity.custom.authenticator.util;

import org.osgi.service.http.HttpService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

/**
 * @author ibandera
 * @since Friday, 22 Dec 2023, 10:28:50
 */
public class ImpersonateAuthenticatorServiceComponentDataHolder {

    private static final ImpersonateAuthenticatorServiceComponentDataHolder instance = new ImpersonateAuthenticatorServiceComponentDataHolder();
    private ConfigurationContextService configurationContextService;
    private RealmService realmService;
    private HttpService httpService;

    private ImpersonateAuthenticatorServiceComponentDataHolder() {
        // private constructor to prevent instantiation
    }

    public static ImpersonateAuthenticatorServiceComponentDataHolder getInstance() {
        return instance;
    }

    public ConfigurationContextService getConfigurationContextService() {
        return configurationContextService;
    }

    public void setConfigurationContextService(ConfigurationContextService configurationContextService) {
        this.configurationContextService = configurationContextService;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    public HttpService getHttpService() {
        return httpService;
    }

    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }
}
