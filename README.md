# impersonate-authenticator

A implementation of a custom inbound authenticator. Compatible with WSO2 Identity Server 5.10.0.

What happens here is... person A is allowed to impersonate B, if person A has a special role to impersonate others and if person B let others with the special role to impersonate him.

With the WSO2 Identity Server, we can achieve this by writing a custom authenticator. We can implement by coupling to a existing authenticator or as a decoupled authenticator.
However I have implemented this as a coupled authenticator with basic authenticator. Reason behind this was the person I want to impersonate is sent in a query parameter (`?impersonatee=userb`).

## Implementation

Since I am coupling impersonation authentication with basic authenticator, I have extended the custom authenticator class by BasicAuthenticator. For this custom authenticator, I have override only public `AuthenticatorFlowStatus process(HttpServletRequest request, HttpServletResponse response, AuthenticationContext context)` method.

1. The authentication/authorization request redirects the user to the IAM's login page
2. The person being impersonated is passed as a query parameter, like `?impersonatee=userb`
3. Then, the impersonator must enters his/her credentials and hit **Enter**
4. The custom authenticator will check if it is a valid user and authenticates him/her
5. If it is a valid user, then it will extract its roles and check if it matched with the impersonator role. (By default impersonator has `Internal/impadmin` role)
6. If it is there, then it will check if impersonatee has the necessary role which allows others with `Internal/impadmin` role to impersonate him/her. (By default impersonatee has `Internal/impuser`)
7. If these are satisfied, the user is created and set as the subject of this authentication step.
8. The application exchange the code for the token, which will have the subject set as the impersonated user

## Building

To compile and package the authenticator up, all you need to do is `mvn clean package`.

## Deploying

To deploy the authenticator, copy the generated JAR to the IS, in the path `$IS_HOME/repository/components/dropins`.

## Configuring

1. In IAM, create the two needed roles: `Internal/impadmin` and `Internal/impuser`
2. Assign the `Internal/impadmin` to the user that is supposed to be impersonating other users
3. Assign the `Internal/impuser` to the users that are supposed to be impersonated
4. Select the *Service Provider* you want to use the authenticator with and then go to the *Local & Outbound Authentication Configuration* section and in the *Authentication Type* select **Local Authentication** and then select **impersonate** from the list (**impersonate** is the name of the authenticator we just created)
5. Select the *Service Provider* you want to use the authenticator with and then go to the *OAuth/OpenID Connect Configuration* section and click the *Edit* option and then set the *Callback Url* to the desired one. In our testing case, we set it to **https://localhost:9443/local/login**

Also, we need to modify the login page so that text boxes and buttons correctly show up when using this new custom authenticator. In order to do so, we need to edit the `login.jsp` file.

1. Create a new constant `IMPERSONATE_AUTHENTICATOR`:
   ```java
    private static final String FIDO_AUTHENTICATOR = "FIDOAuthenticator";
    private static final String IWA_AUTHENTICATOR = "IwaNTLMAuthenticator";
    private static final String IS_SAAS_APP = "isSaaSApp";
    private static final String BASIC_AUTHENTICATOR = "BasicAuthenticator";
    private static final String IDENTIFIER_EXECUTOR = "IdentifierExecutor";
    private static final String OPEN_ID_AUTHENTICATOR = "OpenIDAuthenticator";
    private static final String JWT_BASIC_AUTHENTICATOR = "JWTBasicAuthenticator";
    private static final String X509_CERTIFICATE_AUTHENTICATOR = "x509CertificateAuthenticator";
    private static final String MULTI_ATTRIBUTE_AUTHENTICATOR = "CustomMultiAttributeAuthenticator";
    private static final String IMPERSONATE_AUTHENTICATOR = "ImpersonateAuthenticator";
    private String reCaptchaAPI = null;
    private String reCaptchaKey = null;
   ```
2. Over the line **173**, add `localAuthenticatorNames.contains(IMPERSONATE_AUTHENTICATOR)` to the condition:
   ```java
   else if (localAuthenticatorNames.contains(JWT_BASIC_AUTHENTICATOR) ||
   localAuthenticatorNames.contains(BASIC_AUTHENTICATOR) || localAuthenticatorNames.contains(MULTI_ATTRIBUTE_AUTHENTICATOR)
   || localAuthenticatorNames.contains(IMPERSONATE_AUTHENTICATOR)) {
   ...
   }
   ```
3. Do the same over line **185**:
   ```java
   else if (localAuthenticatorNames.contains(BASIC_AUTHENTICATOR) || localAuthenticatorNames.contains(MULTI_ATTRIBUTE_AUTHENTICATOR) || 
   localAuthenticatorNames.contains(IMPERSONATE_AUTHENTICATOR)) {
   isBackChannelBasicAuth = false;
   if (TenantDataManager.isTenantListEnabled() && Boolean.parseBoolean(request.getParameter(IS_SAAS_APP))) {
   includeBasicAuth = false;
   ```
4. Head over to **212** and change the lines:

   ```java
   <% if (localAuthenticatorNames.contains(BASIC_AUTHENTICATOR) || 
   localAuthenticatorNames.contains(IDENTIFIER_EXECUTOR) || localAuthenticatorNames.contains(MULTI_ATTRIBUTE_AUTHENTICATOR)) || 
   localAuthenticatorNames.contains(IMPERSONATE_AUTHENTICATOR)) { %>
   ```


## Testing

1. Make an authorization request by calling the `authorize` endpoint:
   ```shell
   curl -k -X POST -d "response_type=code&client_id=zdeSNFqBEQdvBXxffF1VWeUs0kka&redirect_uri=https://localhost:9443/local/login&scope=internal_login internal_humantask_view openid&impersonatee=flags" https://localhost:9443/oauth2/authorize
   ```
    > Parameters should be adjusted to meet the current application requirements.

2. The IAM should responde with redirecting to the login page. If you are sending the previous request from a browser, then it will follow the redirection header. In this page, the impersonator should enter its credentials. 
   The raw response should be like this:
   ```text
   HTTP/1.1 302
   Connection: keep-alive
   Content-Length: 0
   Date: Wed, 17 Jan 2024 10:43:58 GMT
   Keep-Alive: timeout=60
   Location: https://localhost:9443/authenticationendpoint/login.do?commonAuthCallerPath=%2Foauth2%2Fauthorize&forceAuth=false&passiveAuth=false&tenantDomain=carbon.super&sessionDataKey=24d22839-826a-41d8-96ff-d8b4598c06d6&relyingParty=zdeSNFqBEQdvBXxffF1VWeUs0kka&type=oidc&sp=local&isSaaSApp=false&authenticators=ImpersonateAuthenticator%3ALOCAL
   Server: WSO2 Carbon Server
   Set-Cookie: JSESSIONID=F9DF5EA51E3D7C8C1255703F66FCCD60C06D9366D54B60C0E9677F3F11E1E558C6F62EC491413B00436B9196871982FE71B32CF75620D5866DFFE80913C1E71A4E3D7D78217CC30E4FCCE07777151AA45D9F8A6A6619A85014710BCBBD65CEDFB849F9C356508ACD52AA12A88E3F67945A0EC2CB5070CC31FC7D66C7B7F259EF; Path=/oauth2; Secure; HttpOnly
   X-Content-Type-Options: nosniff
   X-Frame-Options: DENY
   ```   
3. Grab the `Location` header's value and use a browser (Edge, Chrome, Firefox, Brave, etc...) to navigate to that location, to navigate to that location.
4. After a successful authentication, IAM will return the code you need to get the final token sets. The raw response will be a redirection to the supplied URL:

   ```text
   https://localhost:9443/local/login?code=37e7f4c7-0a13-3466-9241-6161b4280d7d&session_state=9468b31a819ec96e569c8a4dd554dedb4fa81bdc0066e4552e684f9655a8ab64.4jS2Rizee0ZvPpkdpkdfVA
   ```

   > It's important to note that the redirect_uri **should** match the initial parameter passed then calling the `/oauth2/authorize` endpoint, in this case `https://localhost:9443/local/login`.
   
   Pickup the `code` parameter as you will need it to exchange it for the tokens.
5. Make the final call to the `/oauth2/token` endpoint to retrieve the actual token set, using the received code:

   ```shell
   curl -k -X POST -d "grant_type=authorization_code&client_id=zdeSNFqBEQdvBXxffF1VWeUs0kka&client_secret=qeIaf2BB64HtB9EmrogrbODR6P8a&redirect_uri=https://localhost:9443/local/login&code=37e7f4c7-0a13-3466-9241-6161b4280d7d" https://localhost:9443/oauth2/token
   ```

   ```json
   HTTP/1.1 200
   Cache-Control: no-store
   Connection: keep-alive
   Content-Length: 2305
   Content-Type: application/json
   Date: Wed, 17 Jan 2024 10:51:48 GMT
   Keep-Alive: timeout=60
   Pragma: no-cache
   Server: WSO2 Carbon Server
   X-Content-Type-Options: nosniff
   X-Frame-Options: DENY
   
   {
   "access_token": "eyJ4NXQiOiJNell4TW1Ga09HWXdNV0kwWldObU5EY3hOR1l3WW1NNFpUQTNNV0kyT...",
   "expires_in": 3600,
   "id_token": "eyJ4NXQiOiJNell4TW1Ga09HWXdNV0kwWldObU5EY3hOR1l3WW1NNFpUQTNNV0kyTkRBelpHUXpO...",
   "refresh_token": "bccf0245-138e-3b85-98f3-ebfaa459946a",
   "scope": "internal_humantask_view internal_login openid",
   "token_type": "Bearer"
   }
   ```