package org.wso2.carbon.identity.custom.authenticator.util;

import org.wso2.carbon.identity.application.common.model.InboundProvisioningConnector;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.application.mgt.AbstractInboundAuthenticatorConfig;

import static org.wso2.carbon.identity.custom.authenticator.util.ImpersonateAuthenticatorConstants.*;

/**
 * This class is a subclass of {@link AbstractInboundAuthenticatorConfig} which can be used for populating the GUI
 * elements for the custom inbound authenticator in the Identity Server's SP configuration,
 *
 * @author ibandera
 * @since Monday, 08 Jan 2024, 16:22:04
 */
public class ImpersonateAuthenticatorConfig extends AbstractInboundAuthenticatorConfig implements
                                                                                       InboundProvisioningConnector {

	@Override
	public String getName() {

		return CONFIGURATION_NAME;
	}

	@Override
	public String getConfigName() {

		return CONFIGURATION_NAME;
	}

	/**
	 * The human-readable name that gets printed in the SP configuration.
	 *
	 * @return the friendly name of the configuration
	 */
	@Override
	public String getFriendlyName() {

		return CONFIGURATION_FRIENDLY_NAME;
	}

	/**
	 * Defines which property should be used against the value from the request in picking up what SP config to use
	 * against the request (see document).
	 *
	 * @return the name of the property in the property map defined in  {@link #getConfigurationProperties()} whose
	 * value will be unique for a protocol.
	 */
	@Override
	public String getRelyingPartyKey() {

		return RELYING_PARTY_KEY_PROPERTY;
	}

	/**
	 * This method helps define all property fields that will be shown in the SP config page.
	 *
	 * @return an array of properties to be populated in the SP GUI.
	 */
	@Override
	public Property[] getConfigurationProperties() {

		Property relyingPartyKey = new Property();
		relyingPartyKey.setName(RELYING_PARTY_KEY_PROPERTY);
		relyingPartyKey.setDisplayName("Relying Party Key");

		Property redirectUrl = new Property();
		redirectUrl.setName(REDIRECT_URL_PROPERTY);
		redirectUrl.setDisplayName("Redirect URL");
		return new Property[] { relyingPartyKey, redirectUrl };
	}
}
